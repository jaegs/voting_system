package votingSystem.cTF;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
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
		msg = ctf.rsa.encrypt(msg);
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
		case OTGETRANDOMMESSAGES:
			response = OTgetRandomMessages(msg);
		case OTGETSECRETS:
			response = OTgetSecrets(msg);
		}
		if (response.length < ctf.rsa.getModulus().toByteArray().length) {
			return ctf.rsa.decrypt(response);
		}
		System.out.println(op + ": Not encrypting - message longer than modulus");
		return response;
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
		if (checkElection(msg)
				&& ctf.getElections().get(msg[1]).eligibleUsers.contains(String.valueOf((char) msg[3])) )
			response[msg.length] = 1;
		return response;
	}
	
	public void willVote(byte[] msg) {
		/**
		 * #2
		 * in: {e, name, password}K_CTF
		 * SEE: PASSWORDS.JAVA
		 */
		if(checkElection(msg)) {
			Election election = ctf.getElections().get(msg[1]);
			byte[] pass = Arrays.copyOfRange(msg, 3,msg.length);
			String user = String.valueOf((char) msg[2]);
			if( election.passwords.verify(user, pass)) {
				election.votingUsers.add(user);
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
		if (checkElection(msg)
				&& ctf.getElections().get(msg[1]).votingUsers.contains(user))
			response[msg.length + 1] = 1;
		return null;
	}
	
	
	public byte[] OTgetRandomMessages(byte[] msg) {
		/**
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
	
	public byte[] OTgetSecrets(byte[] msg) {
		/**
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
	
	private int getElectionID(byte[] msg) {
		return msg[1];
	}
	
	private boolean checkElection(byte[] msg) {
		return ctf.getElections().containsKey(getElectionID(msg));
	}
	
	private Election getElection(byte[] msg) {
		return ctf.getElections().get(getElectionID(msg));
	}
}
