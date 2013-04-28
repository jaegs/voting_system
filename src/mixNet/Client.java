package mixNet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

import votingSystem.AESEncryption;
import votingSystem.Constants;
import votingSystem.RSAEncryption;
import votingSystem.Tools;

public class Client extends Thread{
	private SecretKey[] responseKeys;
	private PublicKey pubKey;
	private PrivateKey privKey;
	private int[] mixIDs = new int[Constants.NUM_MIXES];
	private int port;
	private PublicKey[] mixKeys;
	private PublicKey ctfMixKey;
	
	public Client(int port) {
		mixKeys = (PublicKey[]) Tools.ReadObjectFromFile(Constants.MIXNET_FILE);
		ctfMixKey = (PublicKey) Tools.ReadObjectFromFile(Constants.MIX_SERVER_KEY_FILE);
		for (int i=0; i<mixIDs.length; i++) mixIDs[i] = i;
		this.port = port;
		
 	}
	
	public byte[] send(byte[] msg) {
		byte[] toSend = prepareMixMessge(msg);
		try {
			//get port of first mix.
			int mixPort = Constants.MIX_PORTS[mixIDs[0]];
			Socket soc = new Socket(InetAddress.getByName(Constants.HOST), mixPort);
			OutputStream out = soc.getOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(toSend.length);
			dos.write(toSend);
			soc.close();
			//Should loop until gets correct message.
			ServerSocket ss = new ServerSocket(port);
			Socket clientSocket = ss.accept();
			InputStream in = clientSocket.getInputStream();
			DataInputStream dis = new DataInputStream(in);
			int len = dis.readInt();
			byte[] input = new byte[len];
			//System.out.println(len);
			if (len > 0) {
				dis.readFully(input);
			}
			clientSocket.close();
			return decryptMixResponse(input);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] prepareMixMessge(byte[] msg) {
		KeyPair keys = RSAEncryption.genKeys();
		privKey = keys.getPrivate();
		pubKey = keys.getPublic();
		mixIDs = Tools.shuffle(mixIDs);
		for(int i = 0; i < Constants.MIXES_PER_MSG; i++)
			responseKeys[i] = AESEncryption.genKey();
		
		int lastMsgAddr = mixIDs[Constants.MIXES_PER_MSG-1];
		
		//Make the response object
		Message.Response firstResponse = new Message.Response(responseKeys[mixIDs[0]], port); 
		byte[] response = Tools.ObjectToByteArray(firstResponse);
		try {
			response = AESEncryption.encrypt(response, mixKeys[mixIDs[0]]);
			for(int i = 1; i < Constants.MIXES_PER_MSG; i++) {
				SecretKey responseKey = responseKeys[mixIDs[i]];
				int responsePort = Constants.MIX_PORTS[mixIDs[i]];
				Message.Response nextResponse = new Message.Response(responseKey, response, responsePort);
				response = Tools.ObjectToByteArray(nextResponse);
				response = AESEncryption.encrypt(response, mixKeys[mixIDs[0]]);
			}
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message message = new Message(msg, lastMsgAddr, pubKey, response);
		PublicKey lastKey = mixKeys[mixIDs[Constants.MIXES_PER_MSG - 1]];
		byte[] toSend;
		try {
			toSend = AESEncryption.encrypt(Tools.ObjectToByteArray(message), lastKey);
			for(int i = Constants.MIXES_PER_MSG - 2; i >= 0; i--) {
				int nextAddr = Constants.MIX_PORTS[mixIDs[i]];
				PublicKey nextKey = mixKeys[mixIDs[i]];
				message = new Message(toSend, nextAddr);
				toSend = AESEncryption.encrypt(Tools.ObjectToByteArray(message), nextKey);
			}
			return toSend;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	public byte[] decryptMixResponse(byte[] bmsg) {
		try {
			Message msg = (Message) Tools.ByteArrayToObject(bmsg);
			byte[] payload = msg.payload;
			for(int i = 0; i < Constants.NUM_MIXES; i++) {
				payload = AESEncryption.decrypt(payload, responseKeys[i]);
			}
			return AESEncryption.decrypt(payload, privKey);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
