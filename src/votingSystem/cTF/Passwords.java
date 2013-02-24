package votingSystem.cTF;

import votingSystem.Constants;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Passwords {
	/**
	 * This class manages password verification for Protocol.willVote()
	 * Use SHA-256
	 */
	private Map<String, byte[]> passwords; //in memory because not using database. keys need to be Strings
	private SecureRandom random = new SecureRandom();
	private File file;
	private MessageDigest hasher = null; 
	
	@SuppressWarnings("unchecked")
	public Passwords(String filename) {
		/**
		 * Loads maps from file
		 */
		file = new File(filename);

		try {
			if (file.exists()) {
				ObjectInputStream ois = new ObjectInputStream(
						new FileInputStream(file));
				passwords = (HashMap<String, byte[]>) ois.readObject();
				ois.close();
			} else {
				file.createNewFile();
				passwords = new HashMap<String, byte[]>();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();			
		}
		try {
			hasher = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public String generate(String username) {
		/**
		 * Generates random password for user
		 * Adds user -> hash(pass) in map 
		 * The passwords need to be physically mailed to the voter...
		 */
		//Make an alphanumeric password
		String pass = new BigInteger(Constants.PASSWORD_LENGTH, random).toString(32);
		System.out.println("username: " + username + " pass: " + pass);
        hasher.update(pass.getBytes());
 
        byte[] hashedPass = hasher.digest();

		passwords.put(username, hashedPass);
		return pass;
	}
	
	public boolean verify(String username, byte[] password) {
		/**
		 * Verifies that user -> h(pass) in map
		 * 
		 */
		if (!passwords.containsKey(username))
			return false;
		
		hasher.update(password);	 
        byte[] hashedPass = hasher.digest();
		return Arrays.equals(hashedPass, passwords.get(username)); 
	}
	
	public void backup() {
		/**
		 * Writes map to file
		 */
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(passwords);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public static void main(String args[]) {
		new File("passwords.ser").delete();
		Passwords passwords = new Passwords("passwords.ser");
		passwords.generate("a");
		passwords.generate("b");
		passwords.generate("c");
	}
}
