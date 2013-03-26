package votingSystem;

import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
		
	public RSAEncryption() {
	}
		
	public KeyPair genKeys() throws NoSuchAlgorithmException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
		keygen.initialize(Constants.RSA_KEY_SIZE);
		return keygen.genKeyPair();
}
	
	public byte[] encrypt(byte[] msg, PublicKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		 Cipher cipher = Cipher.getInstance("RSA"); 
	     cipher.init(Cipher.ENCRYPT_MODE, key);
	     return cipher.doFinal(msg);
	}
	
	public byte[] decrypt(byte[] msg, PrivateKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(msg);
	}
}	