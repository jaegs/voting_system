package votingSystem.cTF;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

import votingSystem.*;

/**
 * Contains all of the election state and methods for managing voters,
 *  processing votes, and responding to voter queries concerning the votersÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢
 *   eligibility and vote status. This class is thread safe.
 * @author Benjamin
 *
 */
public class Election {

	private final Accounts accounts;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	//identifies the election and is transmitted between voter and ctf
	private final int id;

	private ObliviousTransfer OT;
//	private Date prevoteStartTime; //users respond whether they are participating in election
//	private Date votingStartTime;
//	private Date endTime;
	
	//All of these concurrent sets and collections would normally be in a database.
	//Since we're only using the standard library, we keep this collections in memory instead.
	
	//Set of voters who are eligible to vote in this election
	
	private final Set<Group> eligibleGroups;
	
	
	//Set of eligible voters who say they vote in the election
	private final Set<String> votingUsers = Collections.newSetFromMap(new ConcurrentHashMap<String,Boolean>()); //made from willVote responses
	
	//Map of encrypted votes->voterId of voterId's that collide
	private final Map<String, String> IdCollisions = new ConcurrentHashMap<String, String>();
	
	//Map of voterId->encryptedVote for valid voterId's that don't collide
	private final Map<String, String> IdToencryptedVotes = new ConcurrentHashMap<String, String>();
	
	private final Map<String, Integer> OutstandingNonces = new ConcurrentHashMap<String, Integer>();
	
	//Value set of IdToencryptedVotes
	private final Set<String> encryptedVotes = Collections.newSetFromMap(new ConcurrentHashMap<String,Boolean>());
	
	//Map encryptedVote->vote for votes that are successfully decrypted
	private final Map<String, Integer> processedVotes = new ConcurrentHashMap<String, Integer>();
	
	//vote tally for each candidate
	private final AtomicIntegerArray results;
	
	
	private final Set<String> receivedIDs = Collections.newSetFromMap(new ConcurrentHashMap<String,Boolean>());

	
	private static SecureRandom random = new SecureRandom();
	
	private ElectionState state;
	
	
	public enum ElectionState {
		PENDING, PREVOTE, VOTE, COMPLETED
	}
	
	public Election(int id, int numCandidates, Accounts accounts, Set<Group> groups) {
		this.id = id;
		this.accounts = accounts;
		results = new AtomicIntegerArray(numCandidates);
		setState(ElectionState.PREVOTE); //TODO election should start as pending
		eligibleGroups = groups;
	}
	
	public int getId() {
		return id;
	}
	public Message isEligible(Message received) {
		/** 
		 * Step: #1
		 * Input: name
		 * Output: name, bool
		 * Checks whether voter is eligible to vote in given election.
		 */
		System.out.println("Checking Eligibility for voter: " + received.voter);
		Message response = new Message(Operation.ISELIGIBLE_R);
		response.eligible = accounts.verifyGroups(received.voter, eligibleGroups);
		
		if(response.eligible){
			System.out.println("Eligibility Confirmed\n");
		}else{
			System.out.println("Eligibility Denied\n");
		}
		return response;
	}
	
	public void willVote(final Message received) {
		/**
		 * Step: #2
		 * Input: name, password
		 * If the election state is PREVOTE, the user is eligible for the election, 
		 * and the user's password is verified, adds voter to list of voting users.
		 */
		
		scheduler.schedule(new Runnable() { public void run() {
			System.out.println("Adding voter " + received.voter + " to the list of voting users!\n");
			String voter = received.voter;
			//System.out.println(getState() == ElectionState.PREVOTE);
			//System.out.println(accounts.verify(voter, new String(received.password)));
			//System.out.println(accounts.verifyGroups(received.voter, eligibleGroups));
			if (getState() == ElectionState.PREVOTE && accounts.verify(voter, new String(received.password)) && accounts.verifyGroups(received.voter, eligibleGroups)) {
				votingUsers.add(voter);
		}}}, Constants.PASSWORD_DELAY, TimeUnit.MILLISECONDS);
	}
	
	public Message isVoting(Message received) {
		/**
		 * Step: #3
		 * Input: name
		 * Output: name, bool
		 * Checks whether users is voting.
		 */
		System.out.println("Checking if voter " + received.voter + " is planning to vote.");
		Message response = new Message(Operation.ISVOTING_R);
		response.isVoting = votingUsers.contains(received.voter);
		if(response.isVoting){
			System.out.println("Voting Confirmed\n");
		}else{
			System.out.println("Voting Denied\n");
		}
		return response;
	}
	
	/**
	 * Gets a nonce from the server to protect against replay
	 * @param received
	 * @return
	 */
	public Message getNonce(Message received){
		Message response = new Message(Operation.REQUEST_NONCE_R);
		
		
		
		//check to make sure the requestor is a valid voter
		if(!accounts.verify(received.voter, new String(received.password))){
			System.out.println("password");
			response.error = "Invalid username and password!";
			return response;
		}
		if((!accounts.verifyGroups(received.voter, eligibleGroups))){
			System.out.println("groups");
			response.error = "Invalid username and password!";
			return response;
		}
		
		//generate a nonce, add this to the outstanding nonce map
		int nonce = random.nextInt();
		response.ctfNonce = nonce;
		
		System.out.println("CTF NONCE: " + nonce);
		
		OutstandingNonces.put(received.voter, nonce);
		
		return response;
	}
	
	/**
	 * 
	 * @param received
	 * @return
	 */
	public Message changePassword(Message received){
		Message response = new Message(Operation.CHANGE_PASSWORD_R);
		
		//if username/password is invalid
		if(!accounts.verify(received.voter, new String(received.password))){
				response.error = "Invalid username and password!";
				System.out.println("Invalid User");
				response.passwordChanged = false;
				return response;
		}
		//if password do not match
		else if(!Arrays.equals(received.newPassword, received.confirmPassword)){
			response.error = "Password do not match!";
			System.out.println("Pass match");
			response.passwordChanged = false;
			return response;
		}
		//otherwise change the password
		else{
			
			//password
			String password = new String(received.newPassword);
			System.out.println("Password: " + password);
			
			
			if(accounts.changePassword(received.voter, password)){
				System.out.println("Change success");
				response.passwordChanged = true;
				return response;
			}
			else{
				System.out.println("Type violation");
				response.error = "Password must be at least 10 characters long, and must contain at least one of each: number, lowercase letter, uppercase letter, symbol";
				response.passwordChanged = false;
				return response;
			}
		}
		
		
	}
	
	public Message vote(Message received) {
		/**
		 * Step: #5
		 * Input: I, {I,v}K_v
		 * Each user generates a public/private key set (K_v and k_v). 
		 * User sends identification number and encrypted voted. 
		 * If election state is VOTE and the id is valid, the id is then checked for a collision.
		 *  If there is a collision, the encrypted vote and id number are added to a collisions lists.
		 *   Otherwise, the vote and id are added to a list of submitted votes.
		 */   
		Message response = new Message(Operation.VOTE_R);
		String voterId = new String(received.voterId);
		System.out.println("VOTE: "+voterId);
		String encryptedVote = received.encryptedVote;
		System.out.println("VOTE encvote: "+encryptedVote);
		//check
		if(getState() != ElectionState.VOTE
				|| !(OT.checkSecret(received.voterId))){
			response.error = "Invalid request!";
			return response;
		}
		
		
		//check the nonce!
		Integer nonce = new Integer(received.ctfNonce);
		Integer savedNonce = OutstandingNonces.get(received.voter);
		savedNonce++;
		if(!savedNonce.equals(nonce)){
			response.error = "Nonce's do not match!";
			return response;
		}
		
		
		//if someone else has already voted with that voterId
		if (IdToencryptedVotes.containsKey(received.voterId)) {
			IdCollisions.put(encryptedVote, voterId);
		} else {
			IdToencryptedVotes.put(voterId, encryptedVote);
			encryptedVotes.add(encryptedVote);
		}
		
		return response;
	}

	public Message voted(Message received) {
		/**
		 * Step: #6
		 * Input: {I,v}K_v
		 * Output: {I,v}K_v, voteStatus
		 * Users sends an encrpyted voted and CTF responds with a
		 *  vote status - SUCCESS, ID_COLLISION, or NOT_RECORDED.
		 */  
		
		System.out.println("Confirming Vote cast...");
		
		String encryptedVote = received.encryptedVote;
		System.out.println("VOTE: "+encryptedVote);
		Message response = new Message(Operation.VOTED_R);
		if (encryptedVote != null && 
				encryptedVotes.contains(encryptedVote)) {
			response.voted = Constants.VoteStatus.SUCCESS;
			System.out.println("Vote cast successfully!\n");
		}
		else if (encryptedVote != null && 
				IdCollisions.containsKey(encryptedVote)) {
			response.voted = Constants.VoteStatus.ID_COLLISION;
			System.out.println("Voter ID collision!\n");
		} 
		else {
			response.voted = Constants.VoteStatus.NOT_RECORDED;
			System.out.println("Voter Not Recorded!\n");
		}
		return response;
	}
	
	public void processVote(Message received) {
		/**
		 * Step: #7
		 * Input: I, k_v
		 * Voter sends identification number and the private key to their encrpyted vote.
		 * If the election state is VOTE and an encrypted vote was submitted with the given id,
		 * then the server retrieves that encrypted vote, and decrypts it.
		 * if the id in the encrypted vote matches the input id,
		 * the CTF adds the vote to the overall election tally.
		 */
		System.out.println("Processing vote...\n");
		String voterId = new String(received.voterId);
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
		 * Step: #8
		 * Input: {I,v}K_v
		 * Output:	{I,v}K_v, v
		 * A voter can check if his/her vote has been counted by sending their encrypted vote again.
		 *  The CTF responds with the unencrypted vote.
		 */
		System.out.println("Confirming that vote has been counted...");
		String encryptedVote = received.encryptedVote;
		Message response = new Message(Operation.COUNTED_R);
		if(processedVotes.containsKey(encryptedVote)) {
			System.out.println("Vote Confirmed!\n");
			response.vote = processedVotes.get(encryptedVote);
			response.encryptedVote = encryptedVote;
		} else {
			System.out.println("Vote Not Counted!\n");
			response.error = "Vote not processed";
		}
		return response;
	}
	
	public Message results() {
		/**
		 * Step #8
		 * Output: (candidate1:count), (candidate1:count), ...
		 * If election state is COMPLETED,
		 *  the CTF will list the results for each candidate as an integer array.
		 */
		System.out.println("Checking Results...");
		Message response = new Message(Operation.RESULTS_R);
		if (getState() == ElectionState.COMPLETED) {
			response.results = results.toString();
			for(int i = 0; i < results.length(); i++){
				System.out.println("Candidate #" + i + " received " + results.get(i) + " votes.");
			}
			System.out.println();
		} else {
			System.out.println("Election not yet complete!\n");
			response.error = "Election not yet completed";
		}
		return response;
	}
	
	public Message getElectionState() {
		/**
		 * Output: ElectionState
		 * At any time, the voter can query the CTF for the current election state.
		 *  States are PENDING, PREVOTE, VOTE, and COMPLETED
		 */
		Message response = new Message(Operation.STATE_R);
		response.electionState = getState();
		return response;
	}
	
	/**
	 * Input: ElectionState
	 * Normally a voter would not be able to set the election state but for simulation purposes,
	 * it's useful.
	 * @param received
	 */
	public void setState(Message received) {
		setState(received.electionState);
	}
	
	public synchronized Election.ElectionState getState() {
		return state;
	}

	public synchronized void setState(ElectionState state) {
		this.state = state;
		if (state == ElectionState.VOTE) {
			OT = new ObliviousTransfer(votingUsers.size() * 100);
		}
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
		
		System.out.println("Starting Oblivious Transfer");
		//get the random messages
		BigInteger[] randoms = OT.getRandomMessages();
		//KeyPair keys = OT.getKeyPair();
		
		//create a response Message with the random messages and return it
		Message response = new Message(Operation.OTGETPUBLICKEYANDRANDOMMESSAGES_R);
		response.OTMessages = randoms;
		
		//Print out the OTMessages
		//System.out.print("\nOTMessags:");
		//for(int i = 0; i < response.OTMessages.length; i++){
		//	System.out.print(response.OTMessages[i] + "  ");
		//}
		
		
		
		response.OTKey = OT.getPublicKey();
		return response;
	}
	
	
	/**
	 * OTGetSecretes
	 * 
	 * @param received
	 * @return
	 */
	public Message OTGetSecrets(Message received){
		
		System.out.println("Finishing Oblivious Transfer for voter: " + received.voter);
		//create response object
		Message response = new Message(Operation.OTGETSECRETS_R);
		
		//if the received message is not appropriate, add the response
		if(received.OTMessages == null 
				|| received.OTMessages[0] == null 
				|| received.voter == null 
				|| received.password == null
				|| received.nonce == null){
			response.error = "Invalid request!";
			return response;
		}
		
		//check the nonce!
		Integer nonce = new Integer(received.ctfNonce);
		Integer savedNonce = OutstandingNonces.get(received.voter);
		savedNonce++;
		if(!savedNonce.equals(nonce)){
			response.error = "Nonce's do not match!";
			return response;
		}
		
		//if username/password is invalid
		if(!accounts.verify(received.voter, new String(received.password)) || (!accounts.verifyGroups(received.voter, eligibleGroups))){
			response.error = "Invalid username and password!";
			return response;
		}
		
		//check if user has already gotten a secret!
		if(receivedIDs.contains(received.voter)){
			response.error = "User has already been issued a votingID!";
			return response;
		}
		
		//calculate the mValues based on v value passed in
		BigInteger v = received.OTMessages[0];
		BigInteger[] mValues;
		try {
			mValues = OT.calculateMs(v);
		} catch (InvalidKeyException e) {
			response.error = "Invalid Key exception thrown on server side!";
			return response;
		}
		
		//System.out.println("M! " + mValues[0]);
		//System.out.println("Length of M array: " + mValues.length);
		response.OTMessages = mValues;
		
		//add the voter to the set of voters that has already been issued IDs!
		receivedIDs.add(received.voter);
		return response;
		
	}
}
