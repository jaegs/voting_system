package votingSystem.cTF;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import votingSystem.Constants;
import votingSystem.Message;
import votingSystem.ObliviousTransfer;
import votingSystem.Operation;
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
	private int[] results;
	
	private ElectionState state;
	
	
	public enum ElectionState {
		PENDING, PREVOTE, VOTE, COMPLETED
	}
	
	public Election(int id, int numCandidates) {
		this.id = id;
		passwords = new Passwords("passwords.ser");
		randomMessages = obliviousTransfer.randomMessages();
		results = new int[numCandidates];
	}
	
	public Passwords getPasswords() {
		return passwords;
	}
	
	public int getId() {
		return id;
	}

	public Message isEligible(Message received) {
		/** 
		 * #1
		 * in {name}K_CTF
		 * out {name, bool}k_CTF
		 */
		Message response = new Message(Operation.ISELIGIBLE_R);
		response.eligible = eligibleUsers.contains(received.voter);
		return response;
	}
	
	public void willVote(Message received) {
		/**
		 * #2
		 * in: {name, password}K_CTF
		 * SEE: PASSWORDS.JAVA
		 */
		String voter = received.voter;
		if (state == ElectionState.PREVOTE && passwords.verify(voter, received.password)) {
			votingUsers.add(voter);
		}
	}
	
	public Message isVoting(Message received) {
		/**
		 * #3
		 * in: {name}K_CTF
		 * out: {name, bool}k_CTF
		 */
		Message response = new Message(Operation.ISVOTING_R);
		response.isVoting = votingUsers.contains(received.voter);
		return response;
	}
	
	
	public void vote(Message received) {
		/**
		 * #5
		 * {I, {I,v}K_v}K_CTF
		 */
		byte[] voterId = received.voterId;
		if (state == ElectionState.VOTE) {
			//if someone else has already voted with that voterId
			if (encryptedVotes.containsKey(voterId)) {
				IdCollisions.add(voterId);
			} else {
				encryptedVotes.put(voterId, received.encryptedVote);
			}
		}
	}

	public Message voted(Message received) {
		/**
		 * #6
		 * in: {{I,v}K_v}K_CTF
		 * out: {{I,v}K_v, bool}k_CTF
		 */
		byte[] voterId = received.voterId;
		Message response = new Message(Operation.VOTED_R);
		if (encryptedVotes.containsKey(voterId)) {
			response.voted = Constants.VoteStatus.SUCCESS;
		}
		else if (IdCollisions.contains(voterId)) {
			response.voted = Constants.VoteStatus.ID_COLLISION;
		} 
		else {
			response.voted = Constants.VoteStatus.NOT_RECORDED;
		}
		return response;
	}
	
	public void processVote(Message received) {
		/**
		 * #7
		 * in: {I, k_v}K_CTF
		 */
		byte[] voterId = received.voterId;
		byte[] encryptedVote = received.encryptedVote;
		if(state != ElectionState.VOTE && !encryptedVotes.containsKey(voterId)) {
			return; //voter calls "counted" to check if vote actually processed. 
		}
		try {
			byte[] voteArr = RSAEncryption.decrypt(encryptedVote, received.voteKey);
			int vote = ByteBuffer.wrap(voteArr).getInt();
			processedVotes.put(encryptedVote, vote);
			results[vote]++;
		} catch (Exception e){}
	}
	
	public Message results() {
		/**
		 * #8
		 * out: {(v1:count), (v2:count), ...}K_CTF
		 */
		Message response = new Message(Operation.RESULTS_R);
		if (state == ElectionState.COMPLETED) {
			response.results = results;
		} else {
			response.error = "Election not yet completed";
		}
		return response;
	}
	
	public Message counted(Message received) {
		/**
		 * #8
		 * in: {{I,v}K_v}K_CTF
		 * out: {{I,v}K_v, v}k_CTF
		 */
		byte[] encryptedVote = received.encryptedVote;
		Message response = new Message(Operation.COUNTED_R);
		if(processedVotes.containsKey(encryptedVote)) {
			response.vote = processedVotes.get(encryptedVote);
		} else {
			response.error = "Vote not processed";
		}
		return response;
	}
	
	public Message getState() {
		Message response = new Message(Operation.STATE_R);
		response.electionState = state;
		return response;
	}

	
}
