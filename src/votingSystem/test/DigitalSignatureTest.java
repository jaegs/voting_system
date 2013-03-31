package votingSystem.test;

import static org.junit.Assert.assertTrue;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.junit.Test;

import votingSystem.Constants;
import votingSystem.DigitalSignature;
import votingSystem.InvalidSignatureException;

public class DigitalSignatureTest {
	@Test	
	public void testSigning() throws NoSuchAlgorithmException, InvalidSignatureException {
		String s = "Hello, world!";
		byte[] msg = s.getBytes();
		System.out.println("Message: " + Arrays.toString(msg));
		
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
		keygen.initialize(Constants.RSA_KEY_SIZE);
		KeyPair keys = keygen.genKeyPair();
		
		byte[] signedMsg = DigitalSignature.signMessage(msg, keys.getPrivate());
		System.out.println("Signed Message: " + Arrays.toString(signedMsg));
		byte[] verifiedMsg = DigitalSignature.verifySignature(signedMsg, keys.getPublic());
		System.out.println("Verified Message: " + Arrays.toString(verifiedMsg));
		
		assertTrue(Arrays.equals(msg, verifiedMsg));
	}
}
