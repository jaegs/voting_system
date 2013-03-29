package votingSystem.cTF;

import votingSystem.Constants;
import votingSystem.RSAEncryption;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.*;

/**
 * Maintains all of the Elections
 * @author test
 *
 */
public class CTF {
	private Map<Integer, Election> elections;
	private RSAEncryption rsa;
	private PrivateKey privKey;
	
	public CTF() {
		elections = new HashMap<Integer, Election>();
		List<String> candidates = new ArrayList<String>();
		candidates.add("Ben Jaeger");
		candidates.add("Tim Lenardo");
		candidates.add("Clover Bobker");
		
		Election testElection = new Election(1, candidates);
		elections.put(1, testElection);
		
		rsa = new RSAEncryption();
		KeyPair keys = rsa.genKeys();
		Constants.CTF_PUBLIC_KEY = keys.getPublic();
		privKey = keys.getPrivate();
		
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
