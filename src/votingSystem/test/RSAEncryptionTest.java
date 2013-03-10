package votingSystem.test;

import votingSystem.RSAEncryption;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.Test;

public class RSAEncryptionTest {

	@Test
	public void testEncryptDecrypt() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		RSAEncryption RSA = new RSAEncryption(512);
		String s = "Hello, World!";
		byte[] msg = s.getBytes();
		assertTrue(Arrays.equals(msg,RSA.decrypt(RSA.encrypt(msg))));
	}

	@Test
	// This probably needs to be a Java signature instead
	public void testDecryptEncrypt() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		RSAEncryption RSA = new RSAEncryption(512);
		String s = "Hello, World!";
		byte[] msg = s.getBytes();
		assertTrue(Arrays.equals(msg,RSA.encrypt(RSA.decrypt(msg))));
	}
}
