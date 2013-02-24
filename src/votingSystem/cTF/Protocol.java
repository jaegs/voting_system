package votingSystem.cTF;

import java.util.Arrays;

import votingSystem.Constants;
import votingSystem.Constants.Operation;
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

	public byte[] processMessage(byte[] msg){
		/**
		 * Processes every received message.
		 * Calls one of the other methods based on operation type. 
		 * This method is invoked by ServerThread.
		 * #1 decrypt message
		 */
		//Assumes message is length < modulus
		msg = ctf.rsa.decrypt(msg);
		Constants.Operation op = Constants.OPERATION_VALUES[msg[0]];
		byte[] response = null;
		switch (op){
		case ISELIGIBLE:
			response = isEligible(msg);
		case WILLVOTE:
			willVote(msg);
		case ISVOTING:
			response = isVoting(msg);
		case GETIDENTIFICATION:
			response = getIdentification(msg);
		case VOTE:
			vote(msg);
		case VOTED:
			response = voted(msg);
		case CHECKIDCOLLISION:
			response = checkIDCollision(msg);
		case PROCESSVOTE:
			processVote(msg);
		case RESULTS:
			results();
		case COUNTED:
			response = counted(msg);
		case PROTEST:
			protest(msg);
		case CHANGE:
			willVote(msg);
		}
		//Assumes response length is < modulus
		return ctf.rsa.encrypt(response);
	}
	
	public byte[] isEligible(byte[] msg) {
		/** 
		 * #1
		 * in {Election, r, Voter name}K_CTF
		 * out {election, r+1, voter name, bool}k_CTF
		 * e = election
		 */
		byte[] response = Arrays.copyOf(msg, msg.length + 1);
		response[2] = (byte) (msg[2] + 1);
		if (msg[1] == ctf.election.id
				&& ctf.election.eligibleUsers.contains(String.valueOf((char) msg[3])) )
			response[msg.length] = 1;
		return response;
	}
	
	public void willVote(byte[] msg) {
		/**
		 * #2
		 * in: {e, name, password}K_CTF
		 * SEE: PASSWORDS.JAVA
		 */
		if(msg[1] == ctf.election.id) {
			byte[] pass = Arrays.copyOfRange(msg, 3,msg.length);
			String user = String.valueOf((char) msg[2]);
			if( ctf.election.passwords.verify(user, pass)) {
				ctf.election.votingUsers.add(user);
			}
		}
	}
	
	public byte[] isVoting(byte[] msg) {
		/**
		 * #3
		 * in: {e, r, name}K_CTF
		 * out: {e, r + 1, name, bool}k_CTF
		 */
		byte[] response = Arrays.copyOf(msg, msg.length + 1);
		response[2] = (byte) (msg[2] + 1);
		String user = String.valueOf((char) msg[3]);
		if (ctf.election.votingUsers.contains(user))
			response[msg.length + 1] = 1;
		return null;
	}
	
	public static byte[] getIdentification(byte[] msg)
	{
		/**
		 * #4
		 * ANDOS
		 */
		return null;
	}
	
	public static void vote(byte[] msg)
	{
		/**
		 * #5
		 * {e,op, I, {I,v}K_v}K_CTF
		 */
	}
	
	public static byte[] voted(byte[] msg)
	{
		/**
		 * #6
		 * in: {e, op, {I,v}K_v}K_CTF
		 * out: {e, op, {I,v}K_v, bool}k_CTF
		 */
		return null;
	}
	
	public static byte[] checkIDCollision(byte[] msg)
	{
		/**
		 * If #6 fails
		 * in: {e, op, {I,v}K_v}K_CTF
		 * out: {e, op, I', {I,v}K_v}k_CTF
		 */
		return null;
	}
	
	public static void processVote(byte[] msg) {
		/**
		 * #7
		 * in: {e, op, I, k_v}K_CTF
		 */
	}
	
	public static byte[] results() {
		/**
		 * #8
		 * out: {e, op, (v1:count), (v2:count), ...}K_CTF
		 */
		return null;
	}
	
	public static byte[] counted(byte msg[]) {
		/**
		 * #8
		 * in: {e, op, {I,v}K_v}K_CTF
		 * out: {e, op, {I,v}K_v, v}k_CTF
		 */
		return null;
	}
	
	public static void protest(byte msg[]) {
		/**
		 * #9
		 * {e, op, I, {I,v}K_v, k_v}K_CTF
		 */
	}
	
	public static void change(byte msg[]) {
		/**
		 * #10
		 * {e, op, I, (I, v'}K_v, k_v}K_CTF
		 */
	}
}
