package votingSystem.cTF;

import java.util.Date;
import java.util.List;

/**
 * Contains all the state information for one election
 * @author Benjamin
 *
 */
public class Election {

	private String passwordsFilename;
	private Passwords passwords;
	private int id; //identifies the election and is transmitted between voter and ctf
	private Date prevoteStartTime; //users respond whether they are participating in election
	private Date votingStartTime;
	private Date endTime;
	private List<String> eligibleUsers;
	private List<String> votingUsers; //made from willVote responses
	//private List<EncryptedVote> encryptedVotes; //from vote
	private List<String> candidates;
	//private Results results;
	private ElectionState state;
	
	public enum ElectionState {
		PENDING, PREVOTE, VOTE, COMPLETED
	}
	
	public Passwords getPasswords() {
		return passwords;
	}
}
