package votingSystem.voter;

import java.io.IOException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.SecureRandom;


import votingSystem.*;
import votingSystem.cTF.Election;

/**
 * Implements election voting protocol on the client side.
 * All byte arrays are converted to Strings using a Base64 encoder before they are sent 
 * to the server. 
 * @author Benjamin
 *
 */
public class Voter {
	private int electionId;
	private String name;
	private byte[] password;
	private byte[] voterId = new byte[] {1,2,3,2};
	private int vote;
	private String encryptedVote;
	private KeyPair voteKeys;
	private mixNet.Client mixClient = null;
	private boolean voteAnonymous = false;
	private static SecureRandom random = new SecureRandom();
	
	public Voter(int electionId) {
	   this.electionId = electionId;
	}
	
	public Voter(int electionId, String name, byte[] password) {
		this.electionId = electionId;
		this.name = name;
		this.password = password;
	}
	
	private Message prepareMessage(Message send, Operation responseType) 
			throws UnknownHostException, IOException, VotingSecurityException {
		return prepareMessage(send); //TODO: CHECK RESPONSE TYPE
	}
	
	/**
	 * All messages sent to CTF call this method
	 * Adds electionId to message.
	 * Adds a fresh randomly generated nonce.
	 * Appends a checksum
	 * Encrypts the message
	 * Sends the message
	 * Receives the response
	 * Verifies the checksum
	 * Checks that the nonce is incremented
	 * Returns the response.
	 * @param send
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws VotingSecurityException
	 */
	public Message prepareMessage(Message send, boolean anonymous) 
			throws UnknownHostException, IOException, VotingSecurityException  {
		send.electionId = electionId;
		int nonce = random.nextInt();
		send.nonce = nonce;
		byte[] checkedMsg = CheckSum.appendCheckSum(Tools.ObjectToByteArray(send));
		byte[] encryptedMsg = null;
		try {
			encryptedMsg = AESEncryption.encrypt(checkedMsg, Constants.CTF_PUBLIC_KEY);
			// "Erase" unecrypted message in memory by overwriting with random bytes
			random.nextBytes(checkedMsg);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} 
		byte[] signedResponse;
		if (anonymous) {
			if(mixClient == null) {
				mixClient = new mixNet.Client(Constants.MIX_CLIENT_PORT);
			}
			signedResponse = mixClient.send(encryptedMsg);
		} else {
			signedResponse = votingSystem.voter.Client.send(encryptedMsg);
		}
		byte[] checkedResponse = DigitalSignature.verifySignature(signedResponse, Constants.CTF_PUBLIC_KEY);
		byte[] responseArr = CheckSum.stripAndCheck(checkedResponse);
		if(responseArr == null) {
			throw new InvalidCheckSumException();
		}
		Message response = (Message) Tools.ByteArrayToObject(responseArr);
		if (nonce + 1 != response.nonce) {
			throw new InvalidNonceException();
		}
		
		return response;
	}
	
	Message prepareMessage(Message send) 
			throws UnknownHostException, IOException, VotingSecurityException  {
		return prepareMessage(send, false);
	}

	public boolean isEligible() 
			throws UnknownHostException, IOException, VotingSecurityException {
		
		System.out.println("Voter " + name + " requesting eligibility check!");
		Message send = new Message(Operation.ISELIGIBLE);
		send.voter = name;
		Message response = prepareMessage(send, Operation.ISELIGIBLE_R);
		if(response.eligible){
			System.out.println("Eligibility Confirmed.\n");
		}else{
			System.out.println("Eligibility Denied.\n");
		}
		return response.eligible;
	}
	
	public void willVote() 
			throws UnknownHostException, IOException, VotingSecurityException {
		System.out.println("Voter " + name + " is registering to vote!\n");
		Message send = new Message(Operation.WILLVOTE);
		send.voter = name;
		send.password = password;
		prepareMessage(send);
	}
	
	public void willVote(String username, byte[] password) 
	      throws UnknownHostException, IOException, VotingSecurityException {
	    this.name = username;
	    this.password = password;
	    willVote();
	}
	
	public boolean isVoting() 
			throws UnknownHostException, IOException, VotingSecurityException {
		System.out.println("Voter " + name + " checking successful registration!");
		Message send = new Message(Operation.ISVOTING);
		send.voter = name;
		Message response = prepareMessage(send, Operation.ISVOTING_R);
		if(response.isVoting){
			System.out.println("Registration Confirmed.\n");
		}else{
			System.out.println("Registration Denied.\n");
		}
		return response.isVoting;
	}
	
	public void setVoteAnonymous(boolean anon) {
		voteAnonymous = anon;
	}
	
	public void vote(int vote)
			throws UnknownHostException, IOException, VotingSecurityException {
		this.vote = vote;
		vote();
	}
	public void vote() 
			throws UnknownHostException, IOException, VotingSecurityException {
			
		System.out.println("Starting voting process!");
		System.out.println("Oblivious Transfer initiated.");
		//ObliviousTransfer Step1
		Message OTRequest = new Message(Operation.OTGETPUBLICKEYANDRANDOMMESSAGES);
		OTRequest.voter = name;
		OTRequest.password = password;
		Message response = prepareMessage(OTRequest, Operation.OTGETPUBLICKEYANDRANDOMMESSAGES_R);
		
		//check for server error
		if(response.error != null){
			System.out.println(response.error);
			return;
		}
		
		//sanity check
		if(response.OTMessages == null || response.OTKey == null){
			System.out.println("Error in the voting, please try again!");
			return;
		}
		
		//Print out the OTMessages
		//System.out.print("\nOTMessags:");
		//for(int i = 0; i < response.OTMessages.length; i++){
		//	System.out.print(response.OTMessages[i] + "  ");
		//}
		//System.out.println();
		
		//choose a b value
		int b_val = ObliviousTransfer.chooseSecret(response.OTMessages);
		//System.out.println("B" + b_val);
		byte[] k = ObliviousTransfer.generateK();
		// BigInteger test = new BigInteger(k);
		//System.out.println("K-> " + test);
		BigInteger v;
		
		//try to calculate V
		try {
			v = ObliviousTransfer.calculateV(response.OTMessages[b_val], k, response.OTKey);
		} catch (InvalidKeyException e1) {
			System.out.println("Error in the voting, please try again!");
			e1.printStackTrace();
			return;
		}
		
		//request the a nonce from the server
		Message nonceRequest = new Message(Operation.REQUEST_NONCE);
		nonceRequest.voter = name;
		nonceRequest.password = password;
		
		//send the message and check the response for errors
		response = prepareMessage(nonceRequest, Operation.REQUEST_NONCE);
		if(response.error != null){
			System.out.println(response.error);
			return;
		}
		
		//Create a second ObliviousTransfer Request, with the nonce from the server
		Message OTRequest2 = new Message(Operation.OTGETSECRETS);
		OTRequest2.nonce = (response.nonce + 1);
		OTRequest2.voter = name;
		OTRequest2.password = password;
		OTRequest2.OTMessages = new BigInteger[1];
		OTRequest2.OTMessages[0] = v;
		
		
		Message response2 = prepareMessage(OTRequest2, Operation.OTGETSECRETS_R);
		
		//check for server error
		if(response2.error != null){
			System.out.println(response2.error);
			return;
		}
	
		//sanity check pt.2
		if(response2.OTMessages == null){
			System.out.println("Error in the voting, please try again!");
			return;
		}
		
		//System.out.println("M-> " + response2.OTMessages[0]);
		//calculate voterID
		BigInteger vId = ObliviousTransfer.determineMessage(response2.OTMessages, b_val, k); 
		voterId = vId.toByteArray();
		System.out.println("Oblivious Transfer completed");
		//System.out.println("VoterId: " + voterId);
		
		
		//send the message and check the response for errors
		response = prepareMessage(nonceRequest, Operation.REQUEST_NONCE);
		if(response.error != null){
			System.out.println(response.error);
			return;
		}
		
		
		
		//create the VOTe to Send
		Message send = new Message(Operation.VOTE);
		send.voterId = voterId;
		send.nonce = (response.nonce + 1);
		VoteIdPair voteIdPair = new VoteIdPair(voterId, vote);
		byte[] voteIdPairArr = Tools.ObjectToByteArray(voteIdPair);
		voteKeys = RSAEncryption.genKeys();
		try {
			byte[] encryptedVoteArr = AESEncryption.encrypt(voteIdPairArr, voteKeys.getPublic());
			encryptedVote = Base64Coder.encodeLines(encryptedVoteArr);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		send.encryptedVote = encryptedVote;
		//prepareMessage(send);
		prepareMessage(send, voteAnonymous);
		System.out.println("Vote casted!\n");
	}
	
	/**
	 * Handles changing the password for a user. 
	 * User's are forced to do this on their first login
	 * 
	 * @param oldPassword - old password
	 * @param newPassword - the user's new password
	 * @param confirmPassword - the user enters their passwrod a second time
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws VotingSecurityException
	 */
	public boolean changePassword(String username, String oldPassword, String newPassword, String confirmPassword)
		throws UnknownHostException, IOException, VotingSecurityException {
		
		//Client-side checks
		if(!newPassword.equals(confirmPassword)){
			System.out.println("New password and confirmed password do not match.");
			return false;
		}
		
		//
		Message send = new Message(Operation.CHANGE_PASSWORD);
		send.password = oldPassword.getBytes();
		send.newPassword = newPassword.getBytes();
		send.confirmPassword = confirmPassword.getBytes();
		send.voter = username;
		
		Message response = prepareMessage(send);
		if (response.error != null) {
			System.out.println("Failure: " + response.error);			
		}
		return response.passwordChanged;
	}
	
	public Constants.VoteStatus voted()
			throws UnknownHostException, IOException, VotingSecurityException {
		System.out.println("Checking successful vote casted.");
		Message send = new Message(Operation.VOTED);
		send.encryptedVote = encryptedVote;
		Message response = prepareMessage(send, Operation.VOTED_R);
		if(response.voted == Constants.VoteStatus.SUCCESS){
			System.out.println("Vote Confirmed!\n");
		}else{
			System.out.println("Vote Error!\n");
		}
		return response.voted;
	}
	
	public void processVote() 
			throws UnknownHostException, IOException, VotingSecurityException {
		Message send = new Message(Operation.PROCESSVOTE);
		send.voterId = voterId;
		send.voteKey = voteKeys.getPrivate();
		prepareMessage(send);
	}
	
	public boolean counted() 
			throws UnknownHostException, IOException, VotingSecurityException {
		System.out.println("Confirming vote counted...\n");
		Message send = new Message(Operation.COUNTED);
		send.encryptedVote = encryptedVote;
		Message response = prepareMessage(send, Operation.COUNTED_R);
		return response.vote == vote && response.encryptedVote.equals(encryptedVote);
	}
	
	public String results() 
			throws UnknownHostException, IOException, VotingSecurityException {
		Message send = new Message(Operation.RESULTS);
		Message response = prepareMessage(send, Operation.RESULTS_R);
		System.out.println("Results: ");
		return response.results;
	}
	
	public void setState(Election.ElectionState state)
		throws UnknownHostException, IOException, VotingSecurityException {
		Message send = new Message(Operation.SET_STATE);
		send.electionState = state;
		prepareMessage(send);
	}
	
	public String getName() {
		return name;
	}
	
	// Erases password, voterID and vote from system memory
	public void eraseInfo() {
		random.nextBytes(password);
		random.nextBytes(voterId);
		vote = random.nextInt();
	}	
}