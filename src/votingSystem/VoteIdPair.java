package votingSystem;

import java.io.Serializable;

public class VoteIdPair implements Serializable{
	/**
	 * A tuple containing a vote and a voter identification number. This is part of the voting protocol.
	 */
	private static final long serialVersionUID = 3686982314452089425L;
	public String voterId;
	public int vote;
	
	public VoteIdPair(String voterId, int vote) {
		this.voterId = voterId;
		this.vote = vote;
	}
}
