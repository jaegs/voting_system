package votingSystem.test;

import votingSystem.RSAEncryption;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.Test;

public class RSAEncryptionTest {
	@Test	
	public void testEncryptDecrypt() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		String s = "Hello, World!";
		byte[] msg = s.getBytes();
		System.out.println("Message: " + s);
		System.out.println("Message Bytes: " + Arrays.toString(msg));
		
		KeyPair keys = RSAEncryption.genKeys();

		byte[] encryptedMsg = RSAEncryption.encrypt(msg, keys.getPublic());
		System.out.println("Encrypted Message: " + new String(encryptedMsg));
		System.out.println("Encrypted Message Bytes: " + Arrays.toString(encryptedMsg));
		
		byte[] decryptedMsg = RSAEncryption.decrypt(encryptedMsg,keys.getPrivate());
		System.out.println("Decrypted Message: " + new String(decryptedMsg));
		System.out.println("Decrypted Message Bytes: " + Arrays.toString(decryptedMsg));
		
		assertTrue(Arrays.equals(msg,decryptedMsg));
	}
}
