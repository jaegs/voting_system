package votingSystem;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class RSAEncryption {

	private BigInteger modulus, secret, exponent;
	private static SecureRandom random = new SecureRandom();
	private MessageDigest hasher = null; 
	
	public RSAEncryption(int bitlen)
	{
	    BigInteger p = new BigInteger(bitlen / 2, 100, random);
	    BigInteger q = new BigInteger(bitlen / 2, 100, random);
	    modulus = p.multiply(q);
	    BigInteger m = (p.subtract(BigInteger.ONE))
	                   .multiply(q.subtract(BigInteger.ONE));
	    exponent = new BigInteger("3");
	    while(m.gcd(exponent).intValue() > 1) exponent = exponent.add(new BigInteger("2"));
	    secret = exponent.modInverse(m);
	}
	
	public RSAEncryption(String privFilename, String pubFilename) {
		//TODO
	}
	
	public RSAEncryption(String exponent, String secret, String modulus) {
		this.exponent = new BigInteger(exponent);
		this.secret = new BigInteger(secret);
		this.modulus = new BigInteger(modulus);
	}
	
	public BigInteger encrypt(BigInteger message) {
	    return message.modPow(exponent, modulus);
	}
	  
	public BigInteger decrypt(BigInteger message) {
	    return message.modPow(secret, modulus);
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

	public BigInteger getSecret() {
		return secret;
	}

	public BigInteger getExponent() {
		return exponent;
	}
	
	/**
	 * Save public and private keys. Private key is password protected.
	 * @param privFilename
	 * @param pubFilename
	 */
	public void backup(String privFilename, String pubFilename) {
		//TODO
	}
	
	public static void main(String args[]) {
		RSAEncryption rsa = new RSAEncryption(128);
		System.out.println("Modulus: " + rsa.modulus);
		System.out.println("Exponent: " + rsa.exponent);
		System.out.println("Secret: " + rsa.secret);
	}
}


	