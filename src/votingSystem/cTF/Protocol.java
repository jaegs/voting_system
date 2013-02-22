/*
 * Questions
 * How does ANDOS work?
 * Passwords for #2 a good idea?
 * How to network CTF and voters?
 * How to send anonymous messages?
 * Query CTF instead of "publishing" - maybe use 3rd party
 * How to format byte messages? xform?
 * Why is d (k_v) sent in same message for steps 9 and 10?
 * What are the expectations for phase III?
 * Good example of Java RSA
 */
public class CTF {

	public byte[] isEligible(byte[] msg) {
		/** 
		 * #1
		 * in {Election, Voter name}K_CTF
		 * out {election, voter name, bool}k_CTF
		 * e = election
		 * op = operation
		 */
		return null;
	}
	
	public void willVote(byte[] msg) {
		/**
		 * #2
		 * in: {e, op, name, password}K_CTF
		 */
	}
	
	public byte[] isVoting(byte[] msg) {
		/**
		 * #3
		 * in: {e, op, name}K_CTF
		 * out: {e, op, name, bool}k_CTF
		 */
		return null;
	}
	
	public byte[] getIdentification(byte[] msg)
	{
		/**
		 * #4
		 * ANDOS
		 */
		return null;
	}
	
	public void vote(byte[] msg)
	{
		/**
		 * #5
		 * {e,op, I, {I,v}K_v}K_CTF
		 */
	}
	
	public byte[] voted(byte[] msg)
	{
		/**
		 * #6
		 * in: {e, op, {I,v}K_v}K_CTF
		 * out: {e, op, {I,v}K_v, bool}k_CTF
		 */
		return null;
	}
	
	public byte[] checkIDCollision(byte msg[])
	{
		/**
		 * If #6 fails
		 * in: {e, op, {I,v}K_v}K_CTF
		 * out: {e, op, I', {I,v}K_v}k_CTF
		 */
	}
	
	public void processVote(byte[] msg) {
		/**
		 * #7
		 * in: {e, op, I, k_v}K_CTF
		 */
	}
	
	public byte[] results() {
		/**
		 * #8
		 * out: {e, op, (v1:count), (v2:count), ...}K_CTF
		 */
		return null;
	}
	
	public byte[] counted(byte msg[]) {
		/**
		 * #8
		 * in: {e, op, {I,v}K_v}K_CTF
		 * out: {e, op, {I,v}K_v, v}k_CTF
		 */
		return null;
	}
	
	public void protest(byte msg[]) {
		/**
		 * #9
		 * {e, op, I, {I,v}K_v, k_v}K_CTF
		 */
	}
	
	public void change(byte msg[]) {
		/**
		 * #10
		 * {e, op, I, (I, v'}K_v, k_v}K_CTF
		 */
	}
}
