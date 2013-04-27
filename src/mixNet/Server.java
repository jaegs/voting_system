package mixNet;

import java.security.InvalidKeyException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;

import votingSystem.AESEncryption;
import votingSystem.Tools;
import votingSystem.cTF.CTF;
import votingSystem.cTF.Protocol;

public class Server extends Mix{
	private final CTF ctf;
	private final Protocol protocol;

	public Server(int port, CTF ctf) {
		super(port);
		this.ctf = ctf;
		this.protocol = new Protocol(ctf);
	}

	@Override
	public void receive(byte[] bmsg) {
		byte[] decrp_msg;
		try {
			decrp_msg = AESEncryption.decrypt(bmsg, privKey);
			Message msg = (Message) Tools.ByteArrayToObject(decrp_msg);
			byte[] respMsg = protocol.processMessage(msg.payload);
			msg.payload = AESEncryption.encrypt(respMsg, msg.senderKey);
			int sendAddr = msg.sendAddr;
			msg.sendAddr = 0;
			send(Tools.ObjectToByteArray(msg), sendAddr);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
	}
}
