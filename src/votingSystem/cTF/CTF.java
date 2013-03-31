package votingSystem.cTF;

import votingSystem.Constants;
import votingSystem.RSAEncryption;
import votingSystem.Tools;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.*;

/**
 * Maintains all of the Elections
 * @author test
 *
 */
public class CTF {
	private final Map<Integer, Election> elections;
	private PrivateKey privKey;
	
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
