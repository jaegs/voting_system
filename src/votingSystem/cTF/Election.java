package votingSystem.cTF;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.util.*;

import votingSystem.Constants;
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
	private Set<byte[]> IdCollisions = new HashSet<byte[]>();


	private Map<byte[],byte[]> encryptedVotes = new HashMap<byte[],byte[]>();
	private Map<byte[], Integer> processedVotes = new HashMap<byte[], Integer>();
	private int[] voteTally;
	private List<String> candidates;
	//private Results results;
	private ElectionState state;
	public ObliviousTransfer obliviousTransfer;
	public BigInteger[] randomMessages;
	public RSAEncryption otRSA;
	
	
	public enum ElectionState {
		PENDING, PREVOTE, VOTE, COMPLETED
	}
	
	public Election(int id, int numCandidates) {
		this.id = id;
		eligibleUsers.add("a");
		eligibleUsers.add("b");
		eligibleUsers.add("c");
		passwords = new Passwords("passwords.ser");
		obliviousTransfer = new ObliviousTransfer(20);
		otRSA = new RSAEncryption(128);
		randomMessages = obliviousTransfer.randomMessages();
		voteTally = new int[numCandidates];
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

	public boolean isEligible(String voter) {
		return eligibleUsers.contains(voter);
	}
	
	public boolean isVoting(String voter) {
		return votingUsers.contains(voter);
	}
	
	public void addVoter(String voter, byte[] password) {
		if (state == ElectionState.PREVOTE && passwords.verify(voter, password)) {
			votingUsers.add(voter);
		}
	}
	
	public void addEncyptedVote(byte[] voterId, byte[] encryptedVote) {
		if (state == ElectionState.VOTE) {
			//if someone else has already voted with that voterId
			if (encryptedVotes.containsKey(voterId)) {
				IdCollisions.add(voterId);
			} else {
				encryptedVotes.put(voterId, encryptedVote);
			}
		}
	}

	public Constants.VoteStatus isVoting(byte[] voterId) {
		if (encryptedVotes.containsKey(voterId)) {
			return Constants.VoteStatus.SUCCESS;
		}
		if (IdCollisions.contains(voterId)) {
			return Constants.VoteStatus.ID_COLLISION;
		} 
		return Constants.VoteStatus.NOT_RECORDED;
	}
	
	public void processVote(byte[] voterId, PrivateKey voteKey) {
		if(state != ElectionState.VOTE && !encryptedVotes.containsKey(voterId)) {
			return; //voter calls "counted" to check if vote actually processed. 
		}
		byte[] encryptedVote = encryptedVotes.get(voterId);
		byte[] voteArr = RSAEncryption.decrypt(encryptedVote, voteKey); //decrypt with voteKey
		int vote = ByteBuffer.wrap(voteArr).getInt();
		processedVotes.put(voterId, vote);
		voteTally[vote]++;
	}
	
	public int[] results() {
		if (state == ElectionState.COMPLETED) {
			return voteTally;
		}
		return null;
	}
	
	public ElectionState getState() {
		return state;
	}

	
}
