package votingSystem.cTF;

import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.util.*;

import votingSystem.*;

/**
 * Contains all the state information for one election
 * @author Benjamin
 *
 */
public class Election {

	private String passwordsFilename;
	private Accounts accounts;
	private int id; //identifies the election and is transmitted between voter and ctf
	private Date prevoteStartTime; //users respond whether they are participating in election
	private Date votingStartTime;
	private Date endTime;
	
	private Set<String> eligibleUsers = new HashSet<String>();
	private Set<String> votingUsers = new HashSet<String>(); //made from willVote responses
	private HashMap<String, String> IdCollisions = new HashMap<String, String>();
	private Map<String, String> IdToencryptedVotes = new HashMap<String, String>();
	private Set<String> encryptedVotes = new HashSet<String>();
	private Map<String, Integer> processedVotes = new HashMap<String, Integer>();
	private int[] results;
	
	private ElectionState state;
	
	
	public enum ElectionState {
		PENDING, PREVOTE, VOTE, COMPLETED
	}
	
	public Election(int id, int numCandidates) {
		this.id = id;
		accounts = new Accounts(false);
		results = new int[numCandidates];
		state = ElectionState.PREVOTE;
		
		eligibleUsers.add("a");
		eligibleUsers.add("b");
		eligibleUsers.add("c");
	}
	
	public Accounts getAccounts() {
		return accounts;
	}
	
	public int getId() {
		return id;
	}
	
	public void setState(ElectionState state) {
		this.state = state;
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
		if (state == ElectionState.PREVOTE && accounts.verify(voter, received.password)) {
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
		state = ElectionState.VOTE; //TODO: CHANGE!!!!!
		return response;
	}
	
	
	public void vote(Message received) {
		/**
		 * #5
		 * {I, {I,v}K_v}K_CTF
		 */
		String voterId = received.voterId;
		String encryptedVote = received.encryptedVote;
		if (state == ElectionState.VOTE) {
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
				|| state != ElectionState.VOTE 
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
			results[vote]++;
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
		state = ElectionState.COMPLETED; //TODO: remove
		Message response = new Message(Operation.RESULTS_R);
		if (state == ElectionState.COMPLETED) {
			response.results = results;
		} else {
			response.error = "Election not yet completed";
		}
		return response;
	}
	
	public Message getState() {
		Message response = new Message(Operation.STATE_R);
		response.electionState = state;
		return response;
	}

		/**
	 * OTGetRandomMessages
	 * 
	 * Gets the random messages required for ObliviousTransfer. 
	 * If the ObliviousTransfer object does not exist, create it.
	 * 
	 * @return a Message with the random messages in it
	 */	
	public Message OTGetPublicKeyAndRandomMessages(){
		
		//On the first OT request, create the ObliviousTransferObject
		if(OT == null){
			OT = new ObliviousTransfer(votingUsers.size());
		}
		
		//get the random messages
		BigInteger[] randoms = OT.getRandomMessages();
		KeyPair keys = OT.getKeyPair();
		
		//create a response Message with the random messages and return it
		Message response = new Message(Operation.OTGETPUBLICKEYANDRANDOMMESSAGES_R);
		response.OTMessages = randoms;
		response.OTKey = keys.getPublic();
		return response;
	}
	
	
	/**
	 * OTGetSecretes
	 * 
	 * @param received
	 * @return
	 */
	public Message OTGetSecrets(Message received){
		
		//create response object
		Message response = new Message(Operation.OTGETSECRETS_R);
		
		//if the received message is not appropriate, add the response
		if(received.OTMessages == null || received.OTMessages[0] == null){
			response.error = "Invalid V-value passed in!";
			return response;
		}
		
		//calculate the mValues based on v value passed in
		BigInteger v = received.OTMessages[0];
		BigInteger[] mValues = OT.calculateMs(v);
		response.OTMessages = mValues;
		return response;
		
	}
}
