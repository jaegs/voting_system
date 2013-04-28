package mixNet;

import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

import votingSystem.AESEncryption;
import votingSystem.Tools;

public class Node extends Mix {
	public Node(int port) {
		super(port);
	}

	public void receive(byte[] bmsg) {
		try {
			Message msg = (Message) Tools.ByteArrayToObject(bmsg);
			if(msg != null) { //Response message
				byte[] decrp_resp = AESEncryption.decrypt(msg.response, privKey);
				Message.Response resp = (Message.Response) Tools.ByteArrayToObject(decrp_resp);
				msg.payload = AESEncryption.encrypt(msg.payload, (SecretKey) resp.respKey);
				msg.response = resp.respPayload;
				send(Tools.ObjectToByteArray(msg), resp.respAddr);
			} else { //Send message
				byte[] decrp_msg = AESEncryption.decrypt(bmsg, privKey);
				msg = (Message) Tools.ByteArrayToObject(decrp_msg);
				send(msg.payload, msg.sendAddr);
			}
				
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
	}
}
