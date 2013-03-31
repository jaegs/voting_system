package votingSystem.cTF;

import votingSystem.Constants;
import votingSystem.Tools;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Accounts{
	/**
	 * Generates username and passwords and stores them in a file.
	 * Also used by the CTF to check whether a given password is valid for a given username.
	 */
	private final Map<String, String> passwords; //in memory because not using database. keys need to be Strings
	private final String[] names;
	private final SecureRandom random = new SecureRandom();
	
	/**
	 * 
	 * @param load - true reads user information from a file.
	 * false generates users with random names and random passwords
	 */
	@SuppressWarnings("unchecked")
	public Accounts(boolean load) {
		if (load) {
			names = (String[]) Tools.ReadObjectFromFile(Constants.VOTERS_FILENAME);
			passwords = (Map<String, String>) Tools.ReadObjectFromFile(Constants.PASSWORDS_FILENAME);
		} else {
			passwords = new HashMap<String, String>();
			names = new String[Constants.NUM_VOTERS];
			for(int i = 0; i < Constants.NUM_VOTERS; i++) {
				String username = new BigInteger(Constants.VOTER_NAME_LENGTH, random).toString(32);
				String pass = new BigInteger(Constants.PASSWORD_LENGTH, random).toString(32);
				passwords.put(username, pass);	
				names[i] = username;
				if(Constants.DEBUG) System.out.println("USER: " + username +  " PASS: " + pass);
			}
			Tools.WriteObjectToFile(names, Constants.VOTERS_FILENAME);
			Tools.WriteObjectToFile(passwords, Constants.PASSWORDS_FILENAME);
		}
	}
	
	/**
	 * Verifies the given username and password.
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean verify(String username, String password) {
		if (!passwords.containsKey(username))
			return false;		
		return password.equals(passwords.get(username)); 
	}
	
	public String[] getNames() {
		return names;
	}
}
