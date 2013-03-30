package votingSystem;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class DigitalSignature {

	public static byte[] signMessage(byte[] msg, PrivateKey key) {
		try {
			Signature sig = Signature.getInstance(Constants.SIG_ALG);
			sig.initSign(key);
			sig.update(msg);
			byte[] signature = sig.sign();
			byte[] message = new byte[msg.length + signature.length];
			System.arraycopy(msg, 0, message, 0, msg.length);
			System.arraycopy(signature, 0, message, msg.length, signature.length);
			return message;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] verifySignatrue(byte[] msg, PublicKey key) {
		try {
			Signature sig = Signature.getInstance(Constants.SIG_ALG);
			sig.initVerify(key);
			byte[] message = new byte[msg.length - Constants.SIG_SIZE];
			byte[] signature = new byte[Constants.SIG_SIZE];			
			System.arraycopy(msg, 0, message, 0, msg.length - Constants.SIG_SIZE);
			System.arraycopy(msg, msg.length - Constants.SIG_SIZE, signature, 0, Constants.SIG_SIZE);
			sig.update(message);
			if (sig.verify(signature)) {
				return message;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}
	
}