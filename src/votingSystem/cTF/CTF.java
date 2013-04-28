package votingSystem.cTF;

import votingSystem.Constants;
import votingSystem.RSAEncryption;
import votingSystem.Tools;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of a Central Tabulating Facility. Keeps track of currently active elections.
 * At the moment, we create just one sample election.
 * @author Benjamin
 *
 */
public class CTF {
	private final Map<Integer, Election> elections;
	private PrivateKey privKey;
	protected final ExecutorService threadPool = Executors.newFixedThreadPool(Constants.CTF_POOL_THREADS);
	private final Accounts acc;
    
	
	/**
	 * Generates a new public / private key combination every time.
	 * The public key is written to a file so that it is accessible to the voters
	 * (which run in a different process). 
	 * Creates on sample election with 5 candidates  
	 */
	public CTF(Accounts acc) {
		this.acc = acc;
		KeyPair keys = RSAEncryption.genKeys();
		Tools.WriteObjectToFile(keys.getPublic(), Constants.CTF_PUBLIC_KEY_FILE);
		privKey = keys.getPrivate();
		
		if (acc.getSelectedGroup().size() == 0) {
			Set<Group> groups = new HashSet<Group>();
			groups.add(new Group("all"));
			acc.setSelectedGroup(groups);
		}
		Set<String> eligibleUsers = acc.getUsersInGroups(acc.getSelectedGroup());

		elections = new HashMap<Integer, Election>();
		Election testElection = new Election(1, 5, acc, acc.getSelectedGroup());
		elections.put(1, testElection);

		new Protocol(this);
	}
	
	public CTF() {
		this(new Accounts(false));
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
	
	public ExecutorService getThreadPool() {
		return threadPool;
	}
}
