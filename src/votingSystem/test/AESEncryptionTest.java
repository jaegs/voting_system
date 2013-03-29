package votingSystem.test;

import static org.junit.Assert.assertTrue;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

import votingSystem.AESEncryption;
import votingSystem.RSAEncryption;

public class AESEncryptionTest {
	@Test	
	public void testEncryptDecrypt() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidParameterSpecException {
		RSAEncryption RSA = new RSAEncryption();
		KeyPair keys = RSA.genKeys();
		String s = "I am testing a long string to see if AES works, because if it doesn't, I will cry very very much. Please work. Don't make me cry.";
		AESEncryption AES = new AESEncryption();
		byte[] msgEnc = AES.encrypt(s.getBytes(), keys.getPublic());
		byte[] msgDec = AES.decrypt(msgEnc, keys.getPrivate());
		assertTrue(Arrays.equals(s.getBytes(),msgDec));						
	}
}