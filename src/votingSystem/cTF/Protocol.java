package votingSystem.cTF;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import votingSystem.Constants;
import votingSystem.MessageMap;
import votingSystem.MessageTemplate;
import votingSystem.Operation;
/**
 * This class only has static methods, the CTF state will be in a different class.
 * @author test
 *
 */
public class Protocol {
	private CTF ctf;
	
	
	public Protocol(CTF ctf){
		this.ctf = ctf;
	}

	public byte[] processMessage(byte[] msg) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		/**
		 * Processes every received message.
		 * Calls one of the other methods based on operation type. 
		 * This method is invoked by ServerThread.
		 * #1 decrypt message
		 */
		msg = ctf.rsa.decrypt(msg); //CHANGE
		/*System.out.println(Arrays.toString(msg));
		System.out.println(msg[0]);*/
		MessageMap received = MessageMap.fromByteArray(msg);
		Operation op = received.getOperation();
		MessageMap response = null;
		int electionId = received.getInt("electionId"); 
		if (!ctf.isActiveElection(electionId)) {
			response = new MessageMap(Operation.R_ERROR);
			String error_msg = "Invalid Election ID";
			response.set("msg", error_msg.getBytes());
		}
		else {
			Election election = ctf.getElection(electionId);
			switch (op){
				case ISELIGIBLE:
					response = isEligible(received, election);
					break;
				case WILLVOTE:
					willVote(received, election);
					break;
				case ISVOTING:
					response = isVoting(received, election);
				case OTGETRANDOMMESSAGES:
					response = OTgetRandomMessages(received);
					break;
				case OTGETSECRETS:
					response = OTgetSecrets(received);
					break;
				case VOTE:
					vote(received, election);
					break;
				case VOTED:
					response = voted(received);
					break;
				case CHECKIDCOLLISION:
					response = checkIDCollision(received);
					break;
				case PROCESSVOTE:
					processVote(received);
					break;
				case RESULTS:
					results();
					break;
				case COUNTED:
					response = counted(received);
					break;
				case PROTEST:
					protest(received);
					break;
				case CHANGE:
					willVote(received);
					break;
			}
		}
		if (response == null) {
			response = new MessageMap(Operation.R_SUCCESS);
		}
		int nonce = received.getInt("nonce");
		response.set("nonce", nonce + 1);
		response.set("election", electionId);
		return ctf.rsa.decrypt(response.toByteArray()); //CHANGE
	}
	
	public MessageMap isEligible(MessageMap received, Election election) {
		/** 
		 * #1
		 * in {Election, r, Voter name}K_CTF
		 * out {election, r+1, voter name, bool}k_CTF
		 * e = election
		 */
		String voter = received.getString("voter");
		MessageMap response = new MessageMap(Operation.ISELIGIBLE_R);
		response.set("voter", voter);
		response.set("eligible", election.isEligible(voter));
		return response;
	}
	
	public void willVote(MessageMap received, Election election) {
		/**
		 * #2
		 * in: {e, name, password}K_CTF
		 * SEE: PASSWORDS.JAVA
		 */
		String voter = received.getString("voter");
		byte[] password = received.get("password");
		election.addVoter(voter, password);
	}
	
	public MessageMap isVoting(MessageMap received, Election election) {
		/**
		 * #3
		 * in: {e, r, name}K_CTF
		 * out: {e, r + 1, name, bool}k_CTF
		 */
		String voter = received.getString("voter");
		MessageMap response = new MessageMap(Operation.ISVOTING_R);
		response.set("voter", voter);
		response.set("isVoting", election.isVoting(voter));
		return response;
	}
	
	
	public MessageMap OTgetRandomMessages(MessageMap mm) {
		/**
		 * #4 Oblivious Transfer
		 * in {e, r}K_CTF
		 * out {e, r+1, N, exponent, x_0,...,x_n}
		 */
		Election e = getElection(msg);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(msg[0]);
			baos.write(msg[1]);
			baos.write((byte)(msg[2] + 1));
			baos.write(e.otRSA.getExponent().toByteArray());
			baos.write(e.otRSA.getModulus().toByteArray());
			for (BigInteger r : e.randomMessages) {
				baos.write(r.toByteArray());
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		return baos.toByteArray();
	}
	
	public MessageMap OTgetSecrets(MessageMap mm) {
		/**
		 * #4 Oblivious Transfer
		 * in {e, r, v}K_CTF
		 * out: {e, r+1, m'}
		 */
		Election e = getElection(msg);
		BigInteger[] ms = e.getSecrets(new BigInteger(Arrays.copyOfRange(msg, 3, msg.length)));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(msg[0]);baos.write(msg[1]);
		baos.write((byte) (msg[2] + 1));
		try {
			for (BigInteger r : ms) {
				baos.write(r.toByteArray());
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		return baos.toByteArray();
	}
	
	public void vote(MessageMap received, Election election)
	{
		/**
		 * #5
		 * {e,op, I, {I,v}K_v}K_CTF
		 */
		byte[] voterId = received.get("voterId");
		byte[] encryptedVote = received.get("encryptedVote");
		election.addEncyptedVote(voterId, encryptedVote);
	}
	
	public static MessageMap voted(MessageMap mm)
	{
		/**
		 * #6
		 * in: {e, op, {I,v}K_v}K_CTF
		 * out: {e, op, {I,v}K_v, bool}k_CTF
		 */
		byte[] voterId = received.get("voterId");
		byte[] encryptedVote = received.get("encryptedVote");
		
		election.addEncyptedVote(voterId, encryptedVote);
	}
	
	public static MessageMap checkIDCollision(MessageMap mm)
	{
		/**
		 * If #6 fails
		 * in: {e, op, {I,v}K_v}K_CTF
		 * out: {e, op, I', {I,v}K_v}k_CTF
		 */
		return null;
	}
	
	public static void processVote(MessageMap mm) {
		/**
		 * #7
		 * in: {e, op, I, k_v}K_CTF
		 */
	}
	
	public static MessageMap results() {
		/**
		 * #8
		 * out: {e, op, (v1:count), (v2:count), ...}K_CTF
		 */
		return null;
	}
	
	public static MessageMap counted(MessageMap mm) {
		/**
		 * #8
		 * in: {e, op, {I,v}K_v}K_CTF
		 * out: {e, op, {I,v}K_v, v}k_CTF
		 */
		return null;
	}
	
	public static void protest(MessageMap mm) {
		/**
		 * #9
		 * {e, op, I, {I,v}K_v, k_v}K_CTF
		 */
	}
	
	public static void change(MessageMap mm) {
		/**
		 * #10
		 * {e, op, I, (I, v'}K_v, k_v}K_CTF
		 */
	}
	
	private int getElectionID(MessageMap mm) {
		return msg[1];
	}
	
	private boolean checkElection(MessageMap mm) {
		return ctf.getElections().containsKey(getElectionID(msg));
	}
	
	
}
