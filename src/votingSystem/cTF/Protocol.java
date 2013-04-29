package votingSystem.cTF;


import java.security.InvalidKeyException;

import votingSystem.AESEncryption;
import votingSystem.CheckSum;
import votingSystem.DigitalSignature;
import votingSystem.Message;
import votingSystem.Operation;
import votingSystem.Tools;
/**
 * @author Benjamin
 * Decrypts incoming message and checks checksum.
 * Analyzes incoming messages from clients and calls the correct method in the specified active Election.
 * Computes nonce, appends a checksum, and signs outgoing message. 
 */
public class Protocol {
	private final CTF ctf;
	
	
	public Protocol(CTF ctf){
		this.ctf = ctf;
	}

	/**
	 * This method is invoked by ServerThread.
	 * 
	 * Every message sent from the voter client to the CTF server:
	 * - Contains the election ID
	 * - Has a fresh randomly generate (SecureRandom.java) nonce
	 * - Has a checksum (cryptographic hash)
	 * - Is encrypted with the CTFÃ¢â‚¬â„¢s public key
	 * 
	 * Every message sent from the CTF server to the voter client:
	 * - Includes nonce + 
	 * - Has a checksum
	 * - Is signed with the CTFÃ¢â‚¬â„¢s private key
	 * @param msg
	 * @return
	 */
	public byte[] processMessage(byte[] msg) {
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
						break;
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
					case CHANGE_PASSWORD:
						response = election.changePassword(received);
						break;
	//				case PROTEST:
	//					protest(received);
	//					break;
	//				case CHANGE:
	//					//election.willVote(received);
	//					break;
					case REQUEST_NONCE:
						response = election.getNonce(received);
						break;
					case REQUEST_NONCE_ANON:
						response = election.getNonceAnon(received);
						break;
					case GETELECTIONSTATE:
						election.getElectionState();
						break;
					case SET_STATE:
						election.setState(received);
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
