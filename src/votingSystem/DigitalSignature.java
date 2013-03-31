package votingSystem;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * Digital Message Signing
 * @author Clover
 */
public class DigitalSignature {

	/**
	 * Signs a message using an RSA private key.
	 * @param msg, the message to be signed
	 * @param key, the private key used to sign
	 * @return the signed message if successful, null otherwise
	 */
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
	
	/**
	 * Verifies a digitally signed message using the corresponding public key to the private key that
	 * should have been used to sign the message.
	 * @param msg, the digitally signed message to verify
	 * @param key, the public key to verify with
	 * @return the unsigned message
	 * @throws InvalidSignatureException
	 */
	public static byte[] verifySignature(byte[] msg, PublicKey key) throws InvalidSignatureException {
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
				throw new InvalidSignatureException();
			}
		} catch (Exception e) {
			throw new InvalidSignatureException();
		}		
	}
	
}