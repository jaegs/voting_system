package votingSystem;

import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

public class RSAEncryption {
	
	private int keysize;
	private KeyPair keys;
	
	public RSAEncryption(int size)  
	{
		KeyPairGenerator keygen;
		try {
			keygen = KeyPairGenerator.getInstance("RSA");
			keygen.initialize(size);
			keysize = size;
			keys = keygen.genKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}		
	}
	
	public int maxLength() {
		return (keysize/8) - 11;
	}
	
	public int messageSize() {
		return keysize/8;
	}
	
	public byte[] encrypt(byte[] msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		 Cipher cipher = Cipher.getInstance("RSA"); 
	     cipher.init(Cipher.ENCRYPT_MODE, keys.getPublic());
	     return cipher.doFinal(msg);
	}
	
	public byte[] decrypt(byte[] msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, keys.getPrivate());
        return cipher.doFinal(msg);
	}
	
	public static void main(String args[]) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String s = "Hello, World!";
		byte[] m = s.getBytes();
		RSAEncryption test = new RSAEncryption(512);
		byte[] enc = test.encrypt(m);
		byte[] dec = test.decrypt(enc);
		System.out.println(new String(dec));
		System.out.println(test.maxLength());
		System.out.println(test.messageSize());
	}
}	