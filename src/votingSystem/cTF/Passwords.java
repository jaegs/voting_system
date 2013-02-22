package votingSystem.cTF;

import java.util.Map;

public class Passwords {
	/**
	 * This class manages password verification for Protocol.willVote()
	 * Use SHA-256
	 */
	private Map<String, String> passwords; //in memory because not using database
	
	public Passwords(String filename) {
		/**
		 * Loads maps from file
		 */
	}
	
	public String generate(String username) {
		/**
		 * Generates random password for user
		 * Adds user -> hash(pass) in map 
		 * The passwords need to be physically mailed to the voter...
		 */
		return null;
	}
	
	public boolean verify(String username, String password) {
		/**
		 * Verifies that user -> h(pass) in map
		 */
		return false;
	}
	
	public void backup() {
		/**
		 * Writes map to file
		 */
	}
}
