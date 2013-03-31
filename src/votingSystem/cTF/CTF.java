package votingSystem.cTF;

import votingSystem.Constants;
import votingSystem.RSAEncryption;
import votingSystem.Tools;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.*;

/**
 * Implementation of a Central Tabulating Facility. Keeps track of currently active elections.
 * At the moment, we create just one sample election.
 * @author Benjamin
 *
 */
public class CTF {
	private final Map<Integer, Election> elections;
	private PrivateKey privKey;
	
	/**
	 * Generates a new public / private key combination every time.
	 * The public key is written to a file so that it is accessible to the voters
	 * (which run in a different process). 
	 * Creates on sample election with 5 candidates  
	 */
	public CTF() {
		KeyPair keys = RSAEncryption.genKeys();
		Tools.WriteObjectToFile(keys.getPublic(), Constants.CTF_PUBLIC_KEY_FILE);
		privKey = keys.getPrivate();
		
		elections = new HashMap<Integer, Election>();
		Election testElection = new Election(1, 5);
		elections.put(1, testElection);

		new Protocol(this);
		
	}
	public PrivateKey getPrivateKey() {
		return privKey;
	}
	public Map<Integer, Election> getElections() {
		return Collections.unmodifiableMap(elections);
	}
	
	public Election getElection(int electionId) {
		return elections.get(electionId);
	}
	
	public boolean isActiveElection(int electionId) {
		return elections.containsKey(electionId);
	}
}
