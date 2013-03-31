package votingSystem.cTF;

import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;

import votingSystem.*;

/**
 * Contains all the state information for one election
 * @author Benjamin
 *
 */
public class Election {

	private final Accounts accounts;
	private final int id; //identifies the election and is transmitted between voter and ctf
//	private Date prevoteStartTime; //users respond whether they are participating in election
//	private Date votingStartTime;
//	private Date endTime;
	
	private final Set<String> votingUsers = Collections.newSetFromMap(new ConcurrentHashMap<String,Boolean>()); //made from willVote responses
	private final Map<String, String> IdCollisions = new ConcurrentHashMap<String, String>();
	private final Map<String, String> IdToencryptedVotes = new ConcurrentHashMap<String, String>();
	private final Set<String> encryptedVotes = Collections.newSetFromMap(new ConcurrentHashMap<String,Boolean>());
	private final Map<String, Integer> processedVotes = new ConcurrentHashMap<String, Integer>();
	private final Set<String> eligibleUsers;
	private final AtomicIntegerArray results;
	
	private ElectionState state;
	
	
	public enum ElectionState {
		PENDING, PREVOTE, VOTE, COMPLETED
	}
	
	public Election(int id, int numCandidates) {
		this.id = id;
		accounts = new Accounts(false);
		results = new AtomicIntegerArray(numCandidates);
		setState(ElectionState.PREVOTE);
		eligibleUsers = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(accounts.getNames())));
//		eligibleUsers.add("a");
//		eligibleUsers.add("b");
//		eligibleUsers.add("c");
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
		if (getState() == ElectionState.PREVOTE && accounts.verify(voter, received.password)) {
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
		//state = ElectionState.VOTE; //TODO: CHANGE!!!!!
		return response;
	}
	
	
	public void vote(Message received) {
		/**
		 * #5
		 * {I, {I,v}K_v}K_CTF
		 */
		String voterId = received.voterId;
		String encryptedVote = received.encryptedVote;
		if (getState() == ElectionState.VOTE) {
			//if someone else has already voted with that voterId
			if (IdToencryptedVotes.containsKey(voterId)) {
				IdCollisions.put(encryptedVote, voterId);
			} else {
				IdToencryptedVotes.put(voterId, encryptedVote);
				encryptedVotes.add(encryptedVote);
			}
		}
	}

	public Message voted(Message received) {
		/**
		 * #6
		 * in: {{I,v}K_v}K_CTF
		 * out: {{I,v}K_v, bool}k_CTF
		 */
		String encryptedVote = received.encryptedVote;
		Message response = new Message(Operation.VOTED_R);
		if (encryptedVote != null && 
				encryptedVotes.contains(encryptedVote)) {
			response.voted = Constants.VoteStatus.SUCCESS;
		}
		else if (encryptedVote != null && 
				IdCollisions.containsKey(encryptedVote)) {
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
		String voterId = received.voterId;
		PrivateKey voteKey = received.voteKey;
		//voter calls "counted" to check if vote actually processed.
		if(voterId == null 
				|| voteKey == null 
				|| getState() != ElectionState.VOTE 
				|| !IdToencryptedVotes.containsKey(voterId)) {
			return;
		}
		String encryptedVote = IdToencryptedVotes.get(voterId);
		try {
			byte[] voteArr = AESEncryption.decrypt(Base64Coder.decodeLines(encryptedVote), received.voteKey);
			VoteIdPair voteIdPair = (VoteIdPair) Tools.ByteArrayToObject(voteArr);
			if (!voteIdPair.voterId.equals(voterId)) {
				System.out.println("VoterId's not equal");
			}
			int vote = voteIdPair.vote;
			processedVotes.put(encryptedVote, vote);
			results.incrementAndGet(vote);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
	public Message counted(Message received) {
		/**
		 * #8
		 * in: {{I,v}K_v}K_CTF
		 * out: {{I,v}K_v, v}k_CTF
		 */
		String encryptedVote = received.encryptedVote;
		Message response = new Message(Operation.COUNTED_R);
		if(processedVotes.containsKey(encryptedVote)) {
			response.vote = processedVotes.get(encryptedVote);
			response.encryptedVote = encryptedVote;
		} else {
			response.error = "Vote not processed";
		}
		return response;
	}
	
	public Message results() {
		/**
		 * #8
		 * out: {(v1:count), (v2:count), ...}K_CTF
		 */
		//state = ElectionState.COMPLETED; //TODO: remove
		Message response = new Message(Operation.RESULTS_R);
		if (getState() == ElectionState.COMPLETED) {
			response.results = results.toString();
		} else {
			response.error = "Election not yet completed";
		}
		return response;
	}
	
	public Message getElectionState() {
		Message response = new Message(Operation.STATE_R);
		response.electionState = getState();
		return response;
	}
	
	public void setState(Message received) {
		setState(received.electionState);
	}
	
	public synchronized Election.ElectionState getState() {
		return state;
	}

	public synchronized void setState(ElectionState state) {
		this.state = state;
	}
	
}
