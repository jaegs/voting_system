package votingSystem;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

/**
 * RSA Encryption
 * @author Clover
 */
public class RSAEncryption {
	
	/**
	 * Generates an RSA public-private key pair.
	 * @return RSA public-private key pair
	 */
	public static KeyPair genKeys() {
		try {
			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
			keygen.initialize(Constants.RSA_KEY_SIZE);
			return keygen.genKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Encrypts a message using RSA.
	 * @param msg, the message to be encrypted
	 * @param key, the key to use for encryption
	 * @return the encrypted message
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 */
	public static byte[] encrypt(byte[] msg, Key key) throws InvalidKeyException, BadPaddingException {
		try {
			Cipher cipher = Cipher.getInstance(Constants.RSA_ALG);
		    cipher.init(Cipher.ENCRYPT_MODE, key);
		    return cipher.doFinal(msg);
		} catch (InvalidKeyException e) {
			throw new InvalidKeyException();
		} catch (BadPaddingException e) {
			throw new BadPaddingException();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Decrypts an RSA encrypted message.
	 * @param msg, the message to be decrypted
	 * @param key, the key to use for decrypting
	 * @return the decrypted message
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 */
	public static byte[] decrypt(byte[] msg, Key key) throws InvalidKeyException, BadPaddingException {
		try {
			Cipher cipher = Cipher.getInstance(Constants.RSA_ALG);
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        return cipher.doFinal(msg);
		} catch (InvalidKeyException e) {
			throw new InvalidKeyException();
		} catch (BadPaddingException e) {
			throw new BadPaddingException();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Same functionality as encrypt, but without padding. Necessary for oblivious transfer to work.
	 * @param msg, the message to be encrypted
	 * @param key, the key to use for encryption
	 * @return the encrypted message
	 * @throws InvalidKeyException
	 */
	public static byte[] encryptNoPadding(byte[] msg, Key key) throws InvalidKeyException {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
		    cipher.init(Cipher.ENCRYPT_MODE, key);
		    return cipher.doFinal(msg);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * Same functionality as decrypt, but without padding. Necessary for oblivious transfer to work.
	 * @param msg, the message to be decrypted
	 * @param key, the key to use for decrypting
	 * @return the decrypted message
	 * @throws InvalidKeyException
	 */
	public static byte[] decryptNoPadding(byte[] msg, Key key) throws InvalidKeyException {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        return cipher.doFinal(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}	