package votingSystem.voter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Map;
import votingSystem.Constants;
import votingSystem.InvalidCheckSumException;
import votingSystem.InvalidSignatureException;
import votingSystem.Tools;
import votingSystem.cTF.Election;

public class Terminal {
	
	public void run() {
		try {
			int electionId = 1;
			@SuppressWarnings("unchecked")
			Map<String,String> passwords = (Map<String,String>) Tools.ReadObjectFromFile(Constants.PASSWORDS_FILENAME);
			Map.Entry<String, String> entry = passwords.entrySet().iterator().next();
			System.out.println("You knew apriori that your username is: " + entry.getKey());
			System.out.println("You knew apriori that your password is: " + entry.getValue());
			Voter v = new Voter(electionId);
			System.out.println("Election state is PENDING.\nSince no one is operating the Central Tabulating Facility, we'll change the Election state for you");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.println("Please enter 'vote' to vote.");	
			while (!br.readLine().equals("vote")) {
				System.out.println("Please enter 'vote' to vote.");	
			}
			System.out.println("OK, Election state is PREVOTE");
			System.out.println("Please enter your username");
			String username = br.readLine();
			System.out.println("Please enter your password");

			// TODO: change to char array, can't use br.readLine()
			byte[] password = br.readLine().getBytes();
			v.willVote(username, password);
			Thread.sleep(Constants.PASSWORD_DELAY * 2);
			
			// "Erase" password in memory by overwriting stored password with random bytes
			SecureRandom sr = new SecureRandom();
			sr.nextBytes(password);
			
			if(!v.isVoting()) {
				System.out.println("Sorry " + v.getName() + ", at this time we could not confirm your voting status.");
				return;
			}
			System.out.println("Success! " + v.getName() + ". You are confirmed as voting in election #" + electionId + ".");
			
			v.setState(Election.ElectionState.VOTE);
			System.out.println("OK, Election state is VOTE");
			System.out.println("Please enter your vote. Candidates are 0 through 4.");
			int vote = Integer.parseInt(br.readLine());
			v.vote(vote);
			System.out.println("Your anonymous voter ID is " + v.getId() + ". Don't tell anyone or else you won't be anonymous!");
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
			v.setState(Election.ElectionState.COMPLETED);
			System.out.println("OK, Election state is COMPLETED");
			System.out.println("Election results are: " + v.results());
		} catch (InvalidNonceException ine) {
			System.out.println("Error communication with server: invalid nonce");
		} catch (IOException ioe) {
			System.out.println("Error communicating with server: ");
			ioe.printStackTrace();
		} catch (InvalidCheckSumException e) {
			System.out.println("Invalid Check Sum");
		} catch (InvalidSignatureException ise) {
			System.out.println("Signature from Central Tabulating Facility is invalid");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Terminal().run();
	}	
}