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
import javax.crypto.SecretKey;

import org.junit.Test;

import votingSystem.AESEncryption;
import votingSystem.RSAEncryption;

public class AESEncryptionTest {
	@Test	
	public void testEncryptDecrypt() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidParameterSpecException, UnsupportedEncodingException {
		System.out.println("Test showing a successful encryption/decryption:");
		
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
	
	@Test
	public void testAESEncryptDecrypt() {
		System.out.println("This test only uses AES and not RSA.");
		String s = "I am testing a long string to see if AES works, because if it doesn't, I will cry very very much. Please work. Don't make me cry.";
		byte[] msg = s.getBytes();
		System.out.println("Message: " + s);
		System.out.println("Message Bytes: " + Arrays.toString(msg));
		SecretKey k = AESEncryption.genKey();
		try {
			byte[] e = AESEncryption.encrypt(msg, k);
			System.out.println("Encrypted Message: " + new String(e));
			System.out.println("Encrypted Message Bytes: " + Arrays.toString(e));
			byte[] d = AESEncryption.decrypt(e, k);
			System.out.println("Decrypted Message: " + new String(d));
			System.out.println("Decrypted Message Bytes: " + Arrays.toString(d));		
			
			assertTrue(Arrays.equals(msg,d));	
		} catch (InvalidKeyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalBlockSizeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}
	
	
	@Test
	public void testInterceptor() throws InvalidKeyException {
		System.out.println();
		System.out.println("Test showing that an intercepter cannot successfully decrypt the encrypted message:");
		
		String s = "I am testing a long string to see if AES works, because if it doesn't, I will cry very very much. Please work. Don't make me cry.";
		byte[] msg = s.getBytes();
		System.out.println("Message: " + s);
		System.out.println("Message Bytes: " + Arrays.toString(msg));
		
		// Intended recipient's key pair
		KeyPair keys = RSAEncryption.genKeys();
		// Interceptor's key pair
		KeyPair keys2 = RSAEncryption.genKeys();
		
		byte[] encryptedMsg = AESEncryption.encrypt(s.getBytes(), keys.getPublic());
		System.out.println("Encrypted Message: " + new String(encryptedMsg));
		System.out.println("Encrypted Message Bytes: " + Arrays.toString(encryptedMsg));
		
		try {
			// Interceptor tries to decrypt with his private key
			byte[] decryptedMsg = AESEncryption.decrypt(encryptedMsg, keys2.getPrivate());
			System.out.println("Decrypted Message: " + new String(decryptedMsg));
			System.out.println("Decrypted Message Bytes: " + Arrays.toString(decryptedMsg));		
			assertTrue(Arrays.equals(msg,decryptedMsg));
		} catch (InvalidKeyException e) {
			System.out.println("Intercepter could not successfully decrypt the encrypted message.");
			assertTrue(true);
		} catch (BadPaddingException e) { 
			// BadPaddingException is almost always because of an improper decrypt key
			System.out.println("Intercepter could not successfully decrypt the encrypted message.");
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}