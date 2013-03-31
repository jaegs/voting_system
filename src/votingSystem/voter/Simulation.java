package votingSystem.voter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import votingSystem.Base64Coder;
import votingSystem.Constants;
import votingSystem.InvalidCheckSumException;
import votingSystem.InvalidSignatureException;
import votingSystem.Tools;
import votingSystem.VotingSecurityException;
import votingSystem.cTF.Election;

public class Simulation {
	protected ExecutorService threadPool = Executors.newFixedThreadPool(Constants.POOL_THREADS);

	@SuppressWarnings("unchecked")
	public void simulate() {
		try {
			int electionId = 1;
			Map<String,String> passwords = (Map<String,String>) Tools.ReadObjectFromFile(Constants.PASSWORDS_FILENAME);
			Voter[] voters = new Voter[Constants.NUM_VOTERS];
			int v = 0;
			for(Map.Entry<String, String> entry : passwords.entrySet()) {
				//String voterId = Base64Coder.encodeLines(ByteBuffer.allocate(4).putInt(v).array());
				voters[v] = new Voter(electionId, entry.getKey(), entry.getValue());
				v++;
			}
			for (Election.ElectionState state : Election.ElectionState.values()) {
				voters[0].setState(state);
				CountDownLatch latch = new CountDownLatch(Constants.NUM_VOTERS);
				for(v = 0; v < Constants.NUM_VOTERS; v++) {
					threadPool.execute(new VoterThread(state, latch, voters[v]));
				}
				try {
					  latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
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
		}
	}
	
	public static void main(String[] args){
		Simulation sim = new Simulation();
		sim.simulate();
	}
}
