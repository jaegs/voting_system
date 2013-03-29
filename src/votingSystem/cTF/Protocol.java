package votingSystem.cTF;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import votingSystem.Constants;
import votingSystem.Message;
import votingSystem.MessageMap;
import votingSystem.MessageTemplate;
import votingSystem.Operation;
import votingSystem.RSAEncryption;
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

	public byte[] processMessage(byte[] msg) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		/**
		 * Processes every received message.
		 * Calls one of the other methods based on operation type. 
		 * This method is invoked by ServerThread.
		 * #1 decrypt message
		 */
		msg = RSAEncryption.decrypt(msg, ctf.ctfPrivKey); //CHANGE
		/*System.out.println(Arrays.toString(msg));
		System.out.println(msg[0]);*/
		Message received = (Message) Tools.ByteArrayToObject(msg);
		Operation op = received.operation;
		Message response = null;
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
				case OTGETRANDOMMESSAGES:
					response = OTgetRandomMessages(received);
					break;
				case OTGETSECRETS:
					response = OTgetSecrets(received);
					break;
				case VOTE:
					election.vote(received);
					break;
				case VOTED:
					response = election.voted(received);
					break;
				case CHECKIDCOLLISION:
					response = election.checkIDCollision(received);
					break;
				case PROCESSVOTE:
					election.processVote(received);
					break;
				case RESULTS:
					response = election.results();
					break;
				case COUNTED:
					response = election.counted(received);
					break;
				case PROTEST:
					protest(received);
					break;
				case CHANGE:
					//election.willVote(received);
					break;
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
		return ctf.rsa.decrypt(Tools.ObjectToByteArray(response)); //CHANGE
	}

	
	
}
