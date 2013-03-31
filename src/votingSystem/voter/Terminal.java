package votingSystem.voter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import votingSystem.Constants;
import votingSystem.InvalidCheckSumException;
import votingSystem.InvalidSignatureException;
import votingSystem.VotingSecurityException;

public class Terminal {
	private int electionId;
	private Voter v;
	
	public Terminal() {
		v = new Voter();
	}
	
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Please enter the election ID for the election you would like to vote in");
			electionId = 1;//Integer.parseInt(br.readLine());
			System.out.println("Please enter you name");
			v.setName("a");//br.readLine();
			if (!v.isEligible()) {
				System.out.println("Sorry " + v.getName() + ", at this time you are not eligible to vote in election " + electionId);
				return;
			}
			System.out.println("Success! " + v.getName() + ". You are eligible to vote in " + electionId);
			System.out.println("To vote, please enter your password");
			v.setPassword("u9kvce864r"); //br.readLine();
			v.willVote();
			if(!v.isVoting()) {
				System.out.println("Sorry " + v.getName() + ", at this time we could not confirm your voting status");
				return;
			}
			System.out.println("Success! " + v.getName() + ". You are confirmed as voting in " + electionId);
			System.out.println("Please enter your vote");
			v.setVote(1);//Integer.parseInt(br.readLine());
			v.vote();
			Constants.VoteStatus status = v.voted();
			if (status == Constants.VoteStatus.ID_COLLISION) {
				System.out.println("You ID collides with an existing ID, you will have to pick a new one.");
				return;
			} else if(status == Constants.VoteStatus.NOT_RECORDED) {
				System.out.println("At this time, your vote could not be recorded");
				return;
			} 
			System.out.println("Success! Your vote has been recorded");
			v.processVote();
			if(!v.counted()) {
				System.out.println("Error processing your vote");
				return;
			}
			System.out.println("Success! Your vote has been processed");
			System.out.println("The results from the election are: " + Arrays.toString(v.results()));
				
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