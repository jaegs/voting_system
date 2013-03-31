package votingSystem.test;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
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
	public void testEncryptDecrypt() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidParameterSpecException, UnsupportedEncodingException {
		String s = "I am testing a long string to see if AES works, because if it doesn't, I will cry very very much. Please work. Don't make me cry.";
		byte[] msg = s.getBytes();
		System.out.println("Message: " + s);
		System.out.println("Message Bytes: " + Arrays.toString(msg));
		
		KeyPair keys = RSAEncryption.genKeys();
		
		byte[] encryptedMsg = AESEncryption.encrypt(s.getBytes(), keys.getPublic());
		System.out.println("Encrypted Message: " + new String(encryptedMsg));
		System.out.println("Encrypted Message Bytes: " + Arrays.toString(encryptedMsg));
		
		byte[] decryptedMsg = AESEncryption.decrypt(encryptedMsg, keys.getPrivate());
		System.out.println("Decrypted Message: " + new String(decryptedMsg));
		System.out.println("Decrypted Message Bytes: " + Arrays.toString(decryptedMsg));		
		
		assertTrue(Arrays.equals(msg,decryptedMsg));						
	}
}