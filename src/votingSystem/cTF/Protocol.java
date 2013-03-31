package votingSystem.cTF;


import java.security.InvalidKeyException;

import votingSystem.AESEncryption;
import votingSystem.CheckSum;
import votingSystem.DigitalSignature;
import votingSystem.Message;
import votingSystem.Operation;
import votingSystem.Tools;
/**
 * This class only has static methods, the CTF state will be in a different class.
 * @author test
 *
 */
public class Protocol {
	private CTF ctf;
	
	
	public Protocol(CTF ctf){
		this.ctf = ctf;
	}

	public byte[] processMessage(byte[] msg) {
		/**
		 * Processes every received message.
		 * Calls one of the other methods based on operation type. 
		 * This method is invoked by ServerThread.
		 * #1 decrypt message
		 */
		Message response = null;
		try {
			msg = AESEncryption.decrypt(msg, ctf.getPrivateKey());
		} catch (InvalidKeyException e) {
			response = new Message(Operation.OTHER);
			response.error = "Cannot decrypt message";
		} catch (Exception e) {
			e.printStackTrace();
		}
		msg = CheckSum.stripAndCheck(msg);
		if (msg == null) {
			response = new Message(Operation.OTHER);
			response.error = "Invalid Checksum";
		}
		/*System.out.println(Arrays.toString(msg));
		System.out.println(msg[0]);*/
		else {
			Message received = (Message) Tools.ByteArrayToObject(msg);
		
			Operation op = received.operation;
			int electionId = received.electionId; 
			if (!ctf.isActiveElection(electionId)) {
				response = new Message(Operation.OTHER);
				response.error = "Invalid Election ID";
			}
			else {
				Election election = ctf.getElection(electionId);
				switch (op){
					case ISELIGIBLE:
						response = election.isEligible(received);
						break;
					case WILLVOTE:
						election.willVote(received);
						break;
					case ISVOTING:
						response = election.isVoting(received);
					case OTGETPUBLICKEYANDRANDOMMESSAGES:
						response = election.OTGetPublicKeyAndRandomMessages();
						break;
					case OTGETSECRETS:
						response = election.OTGetSecrets(received);
						break;
					case VOTE:
						election.vote(received);
						break;
					case VOTED:
						response = election.voted(received);
						break;
	//				case CHECKIDCOLLISION:
	//					response = election.checkIDCollision(received);
	//					break;
					case PROCESSVOTE:
						election.processVote(received);
						break;
					case RESULTS:
						response = election.results();
						break;
					case COUNTED:
						response = election.counted(received);
						break;
	//				case PROTEST:
	//					protest(received);
	//					break;
	//				case CHANGE:
	//					//election.willVote(received);
	//					break;
					case GETELECTIONSTATE:
						election.getState();
						break;
				}
			}
			if (response == null) {
				response = new Message(Operation.OTHER);
			}
			response.nonce = received.nonce + 1;
			response.electionId =  electionId;
		}
		byte[] responseArr =  Tools.ObjectToByteArray(response);
		byte[] checkedResponse = CheckSum.appendCheckSum(responseArr);
		byte[] signedResponse = DigitalSignature.signMessage(checkedResponse, ctf.getPrivateKey()); //CHANGE TO SIGNATURE
		return signedResponse;
	}

	
	
}
