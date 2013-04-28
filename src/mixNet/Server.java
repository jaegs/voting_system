package mixNet;

import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;

import votingSystem.AESEncryption;
import votingSystem.Constants;
import votingSystem.Tools;

public abstract class Server extends Mix{
	public Server() {
		super(Constants.MIX_CTF_PORT);
	}

	@Override
	public void receive(byte[] bmsg) {
		/*
		 * Ka(R0, M, Kx, A1, K1(R1,A2,K2(R2,A3,...,Kn-1(Rn-1,An,Kn(Rn,Ax))...))) ==>
		 * K1(R1,A2,K2(R2,A3,...,Kn-1(Rn-1,An,Kn(Rn,Ax))...))), Kx(R0,M') to A1
		 */	
		try {
			/*
			 * Ka(R0, M, Kx, A1, K1(...)) ==>
			 * R0, M, Kx, A1, K1(...)
			 */
			byte[] decrp_msg = AESEncryption.decrypt(bmsg, privKey);
			Message msg = (Message) Tools.ByteArrayToObject(decrp_msg);
			//M ==> M'
			byte[] respMsg = processMessage(msg.payload);
			//==> Kx(R0,M')
			byte[] payload = AESEncryption.encrypt(respMsg, msg.senderKey);
			//Tools.printByteArray(payload);
			Message toSend = new Message(payload, msg.response);
			//K1(R1,A2,K2(R2,A3,...,Kn-1(Rn-1,An,Kn(Rn,Ax))...))), Kx(R0,M') to A1
			send(Tools.ObjectToByteArray(toSend), msg.sendAddr);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
	}
	
	public abstract byte[] processMessage(byte[] msg);
}
