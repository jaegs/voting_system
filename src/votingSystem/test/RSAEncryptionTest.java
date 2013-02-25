package votingSystem.test;

import java.math.BigInteger;

import votingSystem.RSAEncryption;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class RSAEncryptionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testDecryptEncrypt() {
		RSAEncryption RSA = new RSAEncryption(128);
		BigInteger msg = new BigInteger("12345678901234567890");
		assertTrue(msg.equals(RSA.encrypt(RSA.decrypt(msg))));
	}
	
	@Test
	public void testEncryptDecrypt() {
		RSAEncryption RSA = new RSAEncryption(128);
		BigInteger msg = new BigInteger("12345678901234567890");
		assertTrue(msg.equals(RSA.decrypt(RSA.encrypt(msg))));
	}
	
	@Test
	public void testLength() {
		RSAEncryption RSA = new RSAEncryption(128);
		assertEquals(RSA.getModulus().bitCount(), 128);
		assertEquals(RSA.getSecret().bitCount(), 128);
	}

}
