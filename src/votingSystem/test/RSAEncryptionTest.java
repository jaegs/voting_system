package votingSystem.test;

import votingSystem.RSAEncryption;

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
	public void testEncryptDecrypt() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		KeyPair keys = RSAEncryption.genKeys();
		String s = "Hello, World!";
		byte[] msg = s.getBytes();
		assertTrue(Arrays.equals(msg,RSAEncryption.decrypt(RSAEncryption.encrypt(msg, keys.getPublic()),keys.getPrivate())));
	}
}
