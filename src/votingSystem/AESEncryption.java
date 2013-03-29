package votingSystem;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
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
	
	public AESEncryption() {
	}
	
	public byte[] encrypt(byte[] msg, PublicKey pubk) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException {
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	    KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(Constants.AES_KEY_SIZE);
	    SecretKey key = keygen.generateKey();
	    SecretKeySpec keyspec = new SecretKeySpec(key.getEncoded(), "AES");
	    cipher.init(Cipher.ENCRYPT_MODE, keyspec);
	    IvParameterSpec ivspec = cipher.getParameters().getParameterSpec(IvParameterSpec.class);		
	    byte[] encMsg = cipher.doFinal(msg);
	    
		byte[] k = key.getEncoded();
		RSAEncryption rsa = new RSAEncryption();
		byte[] encKey = rsa.encrypt(k, pubk);
	    byte[] iv = ivspec.getIV();
	    
	    byte[] message = new byte[encKey.length + iv.length + encMsg.length];
	    System.arraycopy(encKey, 0, message, 0, encKey.length);
	    System.arraycopy(iv, 0, message, encKey.length, iv.length);
	    System.arraycopy(encMsg, 0, message, encKey.length + iv.length, encMsg.length);
	    
	    return message;
	}
	
	public byte[] decrypt(byte[] msg, PrivateKey pk) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		
	    int prepend = Constants.RSA_ENCRYPTED_SIZE + Constants.AES_IV_SIZE;
		int msglen = msg.length - prepend;
		
		byte[] key = new byte[Constants.RSA_ENCRYPTED_SIZE];
		byte[] iv = new byte[Constants.AES_IV_SIZE];
		byte[] message = new byte[msglen];
		
		System.arraycopy(msg, 0, key, 0, Constants.RSA_ENCRYPTED_SIZE);
		System.arraycopy(msg, Constants.RSA_ENCRYPTED_SIZE, iv, 0, Constants.AES_IV_SIZE);
		System.arraycopy(msg, prepend, message, 0, msglen);
		
		RSAEncryption rsa = new RSAEncryption();
		byte[] decKey = rsa.decrypt(key, pk);
		SecretKeySpec k = new SecretKeySpec(decKey, "AES");
		IvParameterSpec ivspec = new IvParameterSpec(iv);
	    cipher.init(Cipher.DECRYPT_MODE, k, ivspec);
		
		return cipher.doFinal(message);
	}
}