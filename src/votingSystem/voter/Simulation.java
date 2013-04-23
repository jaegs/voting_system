package votingSystem.voter;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import votingSystem.Constants;
import votingSystem.Tools;
import votingSystem.VotingSecurityException;
import votingSystem.cTF.Election;

/**
 * Multi-threaded simulation of a number of voters voting randomly.
 * Prints out election results at the end.
 * @author Benjamin
 *
 */
public class Simulation {
	protected ExecutorService threadPool = Executors.newFixedThreadPool(Constants.POOL_THREADS);

	/**
	 * Creates v voters, reads password and username from file
	 * (in reality, assume that each user only has access to their own username and password)
	 * Using a thread pool and the VoterThread class, simultaneously allows each voter to go through 
	 * voting protocol.
	 */
	@SuppressWarnings("unchecked")
	public void simulate() {
		try {
			int electionId = 1;
			Map<String,String> passwords = (Map<String,String>) Tools.ReadObjectFromFile(Constants.PASSWORDS_FILENAME);
			Voter[] voters = new Voter[Constants.NUM_VOTERS];
			int v = 0;
			for(Map.Entry<String, String> entry : passwords.entrySet()) {
				voters[v] = new Voter(electionId, entry.getKey(), entry.getValue().getBytes());
				v++;
			}
			for (Election.ElectionState state : Election.ElectionState.values()) {
				//In reality voter would not be allowed to set CTF state
				// but its useful here to make the simulation autonomous.
				voters[0].setState(state); 
				CountDownLatch latch = new CountDownLatch(Constants.NUM_VOTERS);
				for(v = 0; v < Constants.NUM_VOTERS; v++) {
					//Executes each voter once for each CTF state
					threadPool.execute(new VoterThread(state, latch, voters[v]));
				}
				try {
					//wait until every voter has finished before going to next CTF state
					latch.await(); 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (state == Election.ElectionState.PREVOTE) {
					Thread.sleep(Constants.PASSWORD_DELAY*2);
				}
			}
			System.out.println(voters[0].results());
			threadPool.shutdown();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (VotingSecurityException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		Simulation sim = new Simulation();
		sim.simulate();
	}
}
