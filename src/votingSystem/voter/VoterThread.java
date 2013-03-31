package votingSystem.voter;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.Random;

import votingSystem.*;
import votingSystem.cTF.Election;

/**
 * This class controls a Voter class for the Simulation.
 * A new VoterThread object is created for each election state and for each voter.
 * @author benjamin
 *
 */
public class VoterThread implements Runnable {
	private static Random random = new Random();
	private final Election.ElectionState electionState;
	private final CountDownLatch latch;
	private final Voter voter;
	
	public VoterThread(Election.ElectionState electionState, CountDownLatch latch, Voter voter) {
		this.electionState = electionState;
		this.latch = latch;
		this.voter = voter;
	}
	public void run() {
		try {
			switch(electionState) {
			case PENDING:
				break;
			case PREVOTE:
				if (!voter.isEligible()) {
					System.out.println("not eligible");
				}
				voter.willVote();
				if(!voter.isVoting())
					System.out.println("not voting");
				break;
			case VOTE:
				voter.vote(random.nextInt(5)); //Randomly chooses one of the 5 candidates
				Constants.VoteStatus status = voter.voted();
				if (status == Constants.VoteStatus.ID_COLLISION) {
					System.out.println("id collision");
					return;
				} else if(status == Constants.VoteStatus.NOT_RECORDED) {
					System.out.println("vote not be recorded");
					return;
				} 
				voter.processVote();
				if(!voter.counted()) {
					System.out.println("vote not processed");
					return;
				}
				break;
			case COMPLETED:
				break;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (VotingSecurityException e) {
			e.printStackTrace();
		} finally {
			latch.countDown();
		}
	}
}
