package votingSystem.voter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import votingSystem.Constants;
import votingSystem.InvalidCheckSumException;
import votingSystem.InvalidSignatureException;
import votingSystem.Tools;
import votingSystem.VotingSecurityException;
import votingSystem.cTF.Accounts;

public class Terminal {
	private int electionId;
	private Voter v;
	
	public Terminal() {
		electionId = 1;
		Map<String,String> passwords = (Map<String,String>) Tools.ReadObjectFromFile(Constants.PASSWORDS_FILENAME);
		Set<Map.Entry<String, String>> s = passwords.entrySet();
		Iterator<Entry<String, String>> sIt = s.iterator();
		Entry<String, String> e = sIt.next();
		v = new Voter(electionId, e.getKey(), e.getValue());
	}
	
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Your username is: " + v.getName());
			System.out.println("Your password is: " + v.getPassword());
			
			System.out.println("Please enter 'vote' to vote.");	
			while (!br.readLine().equals("vote")) {
				System.out.println("Please enter 'vote' to vote.");	
			}
			v.willVote();
			if(!v.isVoting()) {
				System.out.println("Sorry " + v.getName() + ", at this time we could not confirm your voting status.");
				return;
			}
			System.out.println("Success! " + v.getName() + ". You are confirmed as voting in " + electionId + ".");
			
			System.out.println("Please enter your vote.");
			int vote = Integer.parseInt(br.readLine());
			v.vote(vote);
			Constants.VoteStatus status = v.voted();
			if (status == Constants.VoteStatus.ID_COLLISION) {
				System.out.println("You ID collides with an existing ID, you will have to pick a new one.");
				return;
			} else if(status == Constants.VoteStatus.NOT_RECORDED) {
				System.out.println("At this time, your vote could not be recorded.");
				return;
			} 
			System.out.println("Success! Your vote has been recorded.");
			
			v.processVote();
			if(!v.counted()) {
				System.out.println("Error processing your vote.");
				return;
			}
			System.out.println("Success! Your vote has been processed.");			
		} catch (InvalidNonceException ine) {
			System.out.println("Error communication with server: invalid nonce");
		} catch (IOException ioe) {
			System.out.println("Error communicating with server: ");
			ioe.printStackTrace();
		} catch (InvalidCheckSumException e) {
			System.out.println("Invalid Check Sum");
		} catch (InvalidSignatureException ise) {
			System.out.println("Signature from Central Tabulating Facility is invalid");
		} catch (VotingSecurityException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Terminal t = new Terminal();
		t.run();
	}	
}