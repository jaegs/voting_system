package votingSystem;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * RSA Encryption
 * @author Clover
 */
public class RSAEncryption {
		
	public static KeyPair genKeys() {
		KeyPairGenerator keygen;
		try {
			keygen = KeyPairGenerator.getInstance("RSA");
			keygen.initialize(Constants.RSA_KEY_SIZE);
			return keygen.genKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
}
	
	public static byte[] encrypt(byte[] msg, PublicKey key) throws InvalidKeyException {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("RSA");
		    cipher.init(Cipher.ENCRYPT_MODE, key);
		    return cipher.doFinal(msg);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public static byte[] decrypt(byte[] msg, PrivateKey key) throws InvalidKeyException {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("RSA");
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        return cipher.doFinal(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}	