package votingSystem;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class RSAEncryption {

	private BigInteger modulus, pubKey, privKey;
	private static SecureRandom random = new SecureRandom();
	private MessageDigest hasher = null; 
	
	public RSAEncryption(int bitlen)
	{
	    BigInteger p = new BigInteger(bitlen / 2, 100, random);
	    BigInteger q = new BigInteger(bitlen / 2, 100, random);
	    modulus = p.multiply(q);
	    BigInteger m = (p.subtract(BigInteger.ONE))
	                   .multiply(q.subtract(BigInteger.ONE));
	    privKey = new BigInteger("3");
	    while(m.gcd(privKey).intValue() > 1) privKey = privKey.add(new BigInteger("2"));
	    pubKey = privKey.modInverse(m);
	}
	
	public RSAEncryption(String privFilename, String pubFilename) {
		//TODO
	}
	
	public BigInteger encrypt(BigInteger message) {
	    return message.modPow(privKey, modulus);
	}
	  
	public BigInteger decrypt(BigInteger message) {
	    return message.modPow(pubKey, modulus);
	}	
	
	public byte[] encrypt(byte[] msg) {
		return encrypt(new BigInteger(msg)).toByteArray();
	}
	
	public byte[] decrypt(byte[] msg) {
		return decrypt(new BigInteger(msg)).toByteArray();
	}
	
	public BigInteger getModulus() {
		return modulus;
	}

	public BigInteger getPubKey() {
		return pubKey;
	}

	public BigInteger getPrivKey() {
		return privKey;
	}
	
	/**
	 * Save public and private keys. Private key is password protected.
	 * @param privFilename
	 * @param pubFilename
	 */
	public void backup(String privFilename, String pubFilename) {
	}
	
}


	