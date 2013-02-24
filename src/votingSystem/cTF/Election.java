package votingSystem.cTF;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contains all the state information for one election
 * @author Benjamin
 *
 */
public class Election {

	private String passwordsFilename;
	public Passwords passwords;
	public int id; //identifies the election and is transmitted between voter and ctf
	private Date prevoteStartTime; //users respond whether they are participating in election
	private Date votingStartTime;
	private Date endTime;
	public List<String> eligibleUsers;
	public List<String> votingUsers; //made from willVote responses
	//private List<EncryptedVote> encryptedVotes; //from vote
	private List<String> candidates;
	//private Results results;
	private ElectionState state;
	
	
	public enum ElectionState {
		PENDING, PREVOTE, VOTE, COMPLETED
	}
	
	public Election() {
		id = 1;
		eligibleUsers = new ArrayList<String>();
		eligibleUsers.add("a");
		eligibleUsers.add("b");
		eligibleUsers.add("c");
		passwords = new Passwords("passwords.ser");
		votingUsers = new ArrayList<String>();
	}
	public Passwords getPasswords() {
		return passwords;
	}
	
	
}
