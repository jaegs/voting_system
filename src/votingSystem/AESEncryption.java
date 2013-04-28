package votingSystem;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

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
 * Adapted from http://crypto.stackexchange.com/a/15
 */
public class AESEncryption {
	public static SecretKey genKey() {
		KeyGenerator keygen = null;
		try {
			keygen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keygen.init(Constants.AES_KEY_SIZE);
		return keygen.generateKey();	
	}
	 
	public static byte[] encrypt(byte[] msg, SecretKey key) throws InvalidKeyException, IllegalBlockSizeException {
		try {
			Cipher cipher = Cipher.getInstance(Constants.AES_ALG);
			SecretKeySpec keyspec = new SecretKeySpec(key.getEncoded(), "AES");
			cipher.init(Cipher.ENCRYPT_MODE, keyspec);
			IvParameterSpec ivspec = cipher.getParameters().getParameterSpec(IvParameterSpec.class);
			byte[] encMsg = cipher.doFinal(msg);
			byte[] iv = ivspec.getIV();
			byte[] output = new byte[iv.length + encMsg.length];
			System.arraycopy(iv, 0, output, 0, iv.length);
			System.arraycopy(encMsg, 0, output, iv.length, encMsg.length);
			return output;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidParameterSpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] decrypt(byte[] input, SecretKey key) throws InvalidKeyException, IllegalBlockSizeException {
		try {
			Cipher cipher = Cipher.getInstance(Constants.AES_ALG);
			SecretKeySpec keyspec = new SecretKeySpec(key.getEncoded(), "AES");
			byte[] iv = new byte[Constants.AES_IV_SIZE];
			int msglen = input.length - Constants.AES_IV_SIZE;
			byte[] msg = new byte[msglen];
			
			System.arraycopy(input, 0, iv, 0, Constants.AES_IV_SIZE);
			System.arraycopy(input, Constants.AES_IV_SIZE, msg, 0, msglen);
			IvParameterSpec ivspec = new IvParameterSpec(iv);
		    cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			return cipher.doFinal(msg);
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}
	
	
	
	/**
	 * Encrypts a message using an AES symmetric key. The AES key is encrypted using RSA with the provided public key. 
	 * The encrypted AES key is prepended and the non-encrypted IV are prepended to the encrypted message.
	 * @param msg, the message to be encrypted
	 * @param pubk, the public key to use to encrypt the AES key
	 * @return byte array of the form [RSA encrypted AES KEY | IV | AES encrypted message] if successful, null otherwise
	 * @throws InvalidKeyException
	 */
	public static byte[] encrypt(byte[] msg, PublicKey pubk) throws InvalidKeyException {
		try {
			Cipher cipher = Cipher.getInstance(Constants.AES_ALG);
			SecretKey key = genKey();
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
	
	/**
	 * Decrypts a message using RSA to decrypt the AES key needed to decrypt the AES encrypted message,
	 * and decrypts the message with the AES key and IV.
	 * @param msg, byte array of the form [RSA encrypted AES KEY | IV | AES encrypted message]
	 * @param pk, the private key corresponding to the public key used to encrypt the AES key
	 * @return the non-encrypted message if successful, null otherwise
	 * @throws InvalidKeyException
	 * @throws BadPaddingException 
	 */
	public static byte[] decrypt(byte[] msg, PrivateKey pk) throws InvalidKeyException, BadPaddingException {
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
		} catch (InvalidKeyException e) {
			throw new InvalidKeyException();
		} catch (BadPaddingException e) {
			throw new BadPaddingException();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}