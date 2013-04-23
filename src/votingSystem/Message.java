package votingSystem;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

import votingSystem.cTF.Election;

public class Message implements Serializable{
	/**
	 * The object that is sent back and forth between CTF server and Voter client.
	 * It is serialized and turned into a byte array so that it can be encrypted.
	 * Most of the fields will be uninitialized which does take up more memory and slow down
	 * encryption. Future work will focus on making a more compact representation.
	 */
	private static final long serialVersionUID = -17866676568921223L;
	public final Operation operation;
	public int nonce;
	public int electionId;
	public String voter;
	public boolean eligible;
	public byte[] password;
	public boolean isVoting;
	public String voterId;
	public String encryptedVote;
	public Constants.VoteStatus voted;
	public String error;
	public String results;
	public Constants.VoteStatus counted;
	public PrivateKey voteKey;
	public int vote;
	public Election.ElectionState electionState;
	public PublicKey OTKey;
	public BigInteger[] OTMessages;
	
	
	public Message(Operation operation) {
		this.operation = operation;
	}
}
