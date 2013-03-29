package votingSystem;

import java.io.Serializable;
import java.security.PrivateKey;

import votingSystem.cTF.Election;

public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -17866676568921223L;
	public Operation operation;
	public int nonce;
	public int electionId;
	public String voter;
	public boolean eligible;
	public byte[] password;
	public boolean isVoting;
	public byte[] voterId;
	public byte[] encryptedVote;
	public Constants.VoteStatus voted;
	public String error;
	public int[] results;
	public Constants.VoteStatus counted;
	public byte[] checksum;
	public PrivateKey voteKey;
	public int vote;
	public Election.ElectionState electionState;
	
	public Message(Operation operation) {
		this.operation = operation;
	}
}
