package votingSystem;

import java.io.Serializable;

public class VoteIdPair implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3686982314452089425L;
	public String voterId;
	public int vote;
	
	public VoteIdPair(String voterId, int vote) {
		this.voterId = voterId;
		this.vote = vote;
	}
}
