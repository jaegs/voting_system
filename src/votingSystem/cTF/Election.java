package votingSystem.cTF;

import java.math.BigInteger;
import java.util.*;

import votingSystem.ObliviousTransfer;
import votingSystem.RSAEncryption;

/**
 * Contains all the state information for one election
 * @author Benjamin
 *
 */
public class Election {

	private String passwordsFilename;
	private Passwords passwords;
	private int id; //identifies the election and is transmitted between voter and ctf
	private Date prevoteStartTime; //users respond whether they are participating in election
	private Date votingStartTime;
	private Date endTime;
	private Set<String> eligibleUsers = new HashSet<String>();
	private Set<String> votingUsers = new HashSet<String>(); //made from willVote responses


	private Map<byte[],byte[]> encryptedVotes = new HashMap<byte[],byte[]>();
	private List<String> candidates;
	//private Results results;
	private ElectionState state;
	public ObliviousTransfer obliviousTransfer;
	public BigInteger[] randomMessages;
	public RSAEncryption otRSA;
	
	
	public enum ElectionState {
		PENDING, PREVOTE, VOTE, COMPLETED
	}
	
	public Election(int id) {
		this.id = id;
		eligibleUsers.add("a");
		eligibleUsers.add("b");
		eligibleUsers.add("c");
		passwords = new Passwords("passwords.ser");
		obliviousTransfer = new ObliviousTransfer(20);
		otRSA = new RSAEncryption(128);
		randomMessages = obliviousTransfer.randomMessages();
	}
	
	public Passwords getPasswords() {
		return passwords;
	}
	
	public BigInteger[] getSecrets(BigInteger v) {
		return obliviousTransfer.calculateMs(randomMessages, v, otRSA.getSecret(), otRSA.getModulus());
	}
	
	public int getId() {
		return id;
	}

	
	public boolean isVoting(String voter) {
		return votingUsers.contains(voter);
	}
	
	public void addVoter(String voter, byte[] password) {
		if (passwords.verify(voter, password)) {
			votingUsers.add(voter);
		}
	}
	
	public void addEncyptedVote(byte[] voterId, byte[] encryptedVote) {
		if (encryptedVotes.containsKey(key))
		encryptedVotes.put(voterId, encryptedVote);
	}

	public boolean isEligible(String voter) {
		return eligibleUsers.contains(voter);
	}

	
}
