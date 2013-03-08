package votingSystem.cTF;

import votingSystem.RSAEncryption;
import java.util.*;

/**
 * Maintains all of the Elections
 * @author test
 *
 */
public class CTF {
	private Map<Integer, Election> elections;
	//We know this isn't secure, it's just temporary!!!
	public RSAEncryption rsa = new RSAEncryption("7", 
			"136578382103380560086232017154571694323",
			"318682891574554640236911507202669852853");
	
	
	public CTF() {
		elections = new HashMap<Integer, Election>();
		List<String> candidates = new ArrayList();
		candidates.add("Ben Jaeger");
		candidates.add("Tim Lenardo");
		candidates.add("Clover CantRememberLastName");
		
		Election testElection = new Election(1, candidates);
		elections.put(1, testElection);
		
		new Protocol(this);
		
	}
	
	public Map<Integer, Election> getElections() {
		return Collections.unmodifiableMap(elections);
	}
}
