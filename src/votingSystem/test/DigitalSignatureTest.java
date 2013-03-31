package votingSystem.test;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import votingSystem.DigitalSignature;
import votingSystem.InvalidSignatureException;
import votingSystem.RSAEncryption;

public class DigitalSignatureTest {
	@Test	
	public void testSigning() throws NoSuchAlgorithmException, InvalidSignatureException {
		String s = "Hello, world!";
		byte[] msg = s.getBytes();
		System.out.println("Message: " + s);
		System.out.println("Message Bytes: " + Arrays.toString(msg));
		
		KeyPair keys = RSAEncryption.genKeys();
		
		byte[] signedMsg = DigitalSignature.signMessage(msg, keys.getPrivate());
		System.out.println("Signed Message: " + new String(signedMsg));
		System.out.println("Signed Message Bytes: " + Arrays.toString(signedMsg));
		
		byte[] verifiedMsg = DigitalSignature.verifySignature(signedMsg, keys.getPublic());
		System.out.println("Verified Message: " + new String(verifiedMsg));
		System.out.println("Verified Message Bytes: " + Arrays.toString(verifiedMsg));
		
		assertTrue(Arrays.equals(msg, verifiedMsg));
	}
}
