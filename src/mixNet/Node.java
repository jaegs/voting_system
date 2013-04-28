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
			Object Omsg =  Tools.ByteArrayToObject(bmsg);
			Message msg;
			if(Omsg != null) {
				msg = (Message) Omsg;
				/*
				 * It's a message from server a to client x
				 * Ki(Ri,Ai+1,Ki+1(Ri+1,Ai+2...,Kn-1,(Rn-1,An,Kn(Rn,Ax)...)), Ri-1(Ri-2(...R2(R1(Kx(R0,M)))...)) ==>
				 * Ki+1(Ri+1,Ai+2...,Kn-1,(Rn-1,An,Kn(Rn,Ax)...)), Ri(Ri-1(Ri-2...R2(R1(Kx(R0,M)))...) to Ai+1 
				 */
				//Ki(Ri,Ai+1, Ki+1(...)) ==> Ri,Ai+1, Ki+1(...)
				byte[] decrp_resp = AESEncryption.decrypt(msg.response, privKey); 
				Response resp = (Response) Tools.ByteArrayToObject(decrp_resp);
				//Ri-1(...) ==> Ri(Ri-1(...))
				msg.payload = AESEncryption.encrypt(msg.payload, (SecretKey) resp.respKey);
				msg.response = resp.respPayload;
				send(Tools.ObjectToByteArray(msg), resp.respAddr);
			} else {
				/*
				 * It's a message from client x to Server a
				 * Kn(Rn,An-1, Kn-1(Rn-1,An,...,K2(R2,A1,K1(R1,Aa,Ka(...)))...) ==>
				 * Kn-1(Rn-1,An,...,K2(R2,A1,K1(R1,Aa,Ka(...)))...) to An-1
				 */
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
