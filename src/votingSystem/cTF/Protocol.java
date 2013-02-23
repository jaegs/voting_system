package votingSystem.cTF;

/**
 * This class only has static methods, the CTF state will be in a different class.
 * @author test
 *
 */
public class Protocol {

	public static byte[] processMessage(byte[] msg){
		/**
		 * Processes every received message.
		 * Calls one of the other methods based on operation type. 
		 * This method is invoked by ServerThread.
		 * #1 decrypt message
		 */
		return null;
	}
	
	public static byte[] isEligible(byte[] msg) {
		/** 
		 * #1
		 * in {Election, Voter name}K_CTF
		 * out {election, voter name, bool}k_CTF
		 * e = election
		 * op = operation
		 */
		return null;
	}
	
	public static void willVote(byte[] msg) {
		/**
		 * #2
		 * in: {e, op, name, password}K_CTF
		 * SEE: PASSWORDS.JAVA
		 */
	}
	
	public static byte[] isVoting(byte[] msg) {
		/**
		 * #3
		 * in: {e, op, name}K_CTF
		 * out: {e, op, name, bool}k_CTF
		 */
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
