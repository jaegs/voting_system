package votingSystem.test;

import static org.junit.Assert.assertTrue;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
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
		KeyPair keys = RSAEncryption.genKeys();
		String s = "I am testing a long string to see if AES works, because if it doesn't, I will cry very very much. Please work. Don't make me cry.";
		byte[] msgEnc = AESEncryption.encrypt(s.getBytes(), keys.getPublic());
		byte[] msgDec = AESEncryption.decrypt(msgEnc, keys.getPrivate());
		assertTrue(Arrays.equals(s.getBytes(),msgDec));						
	}
}