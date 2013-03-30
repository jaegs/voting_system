package votingSystem;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES Encryption
 * @author Clover
 * Adapted from http://crypto.stackexchange.com/a/15. However, the RSA encrypted AES key and the AES encrypted message 
 * are sent as one message with the RSA encrypted AES key prepended to the AES encrypted message. In addition, the 
 * non-encrypted IV is prepended to the AES encrypted message as well.
 */
public class AESEncryption {
	
	public static byte[] encrypt(byte[] msg, PublicKey pubk) throws InvalidKeyException {
		try {
			Cipher cipher = Cipher.getInstance(Constants.AES_ALG);
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(Constants.AES_KEY_SIZE);
			SecretKey key = keygen.generateKey();
			SecretKeySpec keyspec = new SecretKeySpec(key.getEncoded(), "AES");
			cipher.init(Cipher.ENCRYPT_MODE, keyspec);
			IvParameterSpec ivspec = cipher.getParameters().getParameterSpec(IvParameterSpec.class);		
			byte[] encMsg = cipher.doFinal(msg);

			byte[] k = key.getEncoded();
			byte[] encKey = RSAEncryption.encrypt(k, pubk);
			byte[] iv = ivspec.getIV();

			byte[] message = new byte[encKey.length + iv.length + encMsg.length];
			System.arraycopy(encKey, 0, message, 0, encKey.length);
			System.arraycopy(iv, 0, message, encKey.length, iv.length);
			System.arraycopy(encMsg, 0, message, encKey.length + iv.length, encMsg.length);

			return message;
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return null;
	}
	
	public static byte[] decrypt(byte[] msg, PrivateKey pk) throws InvalidKeyException {
		try {
			Cipher cipher = Cipher.getInstance(Constants.AES_ALG);
		    int prepend = Constants.RSA_ENCRYPTED_SIZE + Constants.AES_IV_SIZE;
			int msglen = msg.length - prepend;
			
			byte[] key = new byte[Constants.RSA_ENCRYPTED_SIZE];
			byte[] iv = new byte[Constants.AES_IV_SIZE];
			byte[] message = new byte[msglen];
			
			System.arraycopy(msg, 0, key, 0, Constants.RSA_ENCRYPTED_SIZE);
			System.arraycopy(msg, Constants.RSA_ENCRYPTED_SIZE, iv, 0, Constants.AES_IV_SIZE);
			System.arraycopy(msg, prepend, message, 0, msglen);
			
			byte[] decKey = RSAEncryption.decrypt(key, pk);
			SecretKeySpec k = new SecretKeySpec(decKey, "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv);
		    cipher.init(Cipher.DECRYPT_MODE, k, ivspec);
			
			return cipher.doFinal(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}