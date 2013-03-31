package votingSystem;

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
	
	public static byte[] encrypt(byte[] msg, Key key) throws InvalidKeyException {
		try {
			Cipher cipher = Cipher.getInstance(Constants.RSA_ALG);
		    cipher.init(Cipher.ENCRYPT_MODE, key);
		    return cipher.doFinal(msg);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public static byte[] decrypt(byte[] msg, Key key) throws InvalidKeyException {
		try {
			Cipher cipher = Cipher.getInstance(Constants.RSA_ALG);
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        return cipher.doFinal(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
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
