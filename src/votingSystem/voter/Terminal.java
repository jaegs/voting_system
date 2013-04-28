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
			
			//TIM
			//PASSWORD CHANGING
			boolean passwordChanged = true;
			while(!passwordChanged){
				System.out.println("Please change your password!");
				System.out.print("Enter your username: ");
				String username = br.readLine();
				System.out.print("Enter old password: ");
				String oldPassword = br.readLine();
				System.out.print("Enter new password: ");
				String newPassword = br.readLine();
				System.out.print("Enter confirm password: ");
				String confirmPassword = br.readLine();
				passwordChanged = v.changePassword(username, oldPassword, newPassword, confirmPassword);
				if(!passwordChanged){
					
					System.out.println("Password change failed. Please try again.");
				}
			}
			
			
			System.out.println("Please enter 'vote' to vote.");	
//			while (!br.readLine().equals("vote")) {
//				System.out.println("Please enter 'vote' to vote.");	
//			}
			System.out.println("OK, Election state is PREVOTE");
			System.out.println("Please enter your username");
			String username = entry.getKey();//br.readLine();
			System.out.println("Please enter your password");

			char[] pass = new char[Constants.MAX_PASS_LENGTH];
			int passlen = br.read(pass) - 1;
			byte[] password = new byte[passlen];
			for (int i = 0; i < passlen; i++) {
				password[i] = (byte) pass[i];
			}
			
			System.out.println("Entered pass: " + new String(password));
			
			v.willVote(username, password);
			Thread.sleep(Constants.PASSWORD_DELAY * 4);
			
			
			
			if(!v.isVoting()) {
				System.out.println("Sorry " + v.getName() + ", at this time we could not confirm your voting status.");
				return;
			}
			System.out.println("Success! " + v.getName() + ". You are confirmed as voting in election #" + electionId + ".");
			
			v.setState(Election.ElectionState.VOTE);
			System.out.println("OK, Election state is VOTE");
			System.out.println("Please enter your vote. Candidates are 0 through 4.");
			int vote = Integer.parseInt(br.readLine());
			System.out.println("Sending your vote through an anonymous mixnet so the Goverment won't know how you voted :)");
			v.setVoteAnonymous(false);
			
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
			v.setState(Election.ElectionState.COMPLETED);
			System.out.println("OK, Election state is COMPLETED");
			System.out.println("Election results are: " + v.results());
			
			
			// "Erase" password in memory by overwriting stored password with random bytes
			SecureRandom sr = new SecureRandom();
			sr.nextBytes(password);
			v.eraseInfo();
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
