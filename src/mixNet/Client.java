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
	/**
	 * A send message goes from X, N, N-1, ... , 2, 1, A
	 * Where X is the client and A is the server
	 * For a response its A, 1, 2, ..., N, N-1, X
	 */
	private SecretKey[] responseKeys = new SecretKey[Constants.MIXES_PER_MSG];
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
			//get port of last mix to send to first.
			int mixPort = Constants.MIX_PORTS[mixIDs[Constants.MIXES_PER_MSG-1]];
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
	
	private byte[] prepareMixMessge(byte[] msg) {
		//Make Kx, Kx^-1, these are one time use
		KeyPair keys = RSAEncryption.genKeys();
		privKey = keys.getPrivate();
		PublicKey pubKey = keys.getPublic();
		//Shuffle the mixIDs to get a random order of mixes.
		//Will only use the first MIXES_PER_MSG mixes
		mixIDs = Tools.shuffle(mixIDs);
		//Make R1...Rn
		for(int i = 0; i < Constants.MIXES_PER_MSG; i++)
			responseKeys[i] = AESEncryption.genKey();
		try {
			//Make the response object K1(R1,A2,K2(R2,A3,...,Kn-1(Rn-1,An,Kn(Rn,Ax))...))
			byte[] response = null; //base case is Kn(Rn,Ax)
			int respAddr = port;
			for(int i = Constants.MIXES_PER_MSG - 1; i >= 0 ; i--) {
				SecretKey responseKey = responseKeys[i];
				Response nextResponse = new Response(responseKey, response, respAddr);
				response = Tools.ObjectToByteArray(nextResponse);
				response = AESEncryption.encrypt(response, mixKeys[mixIDs[i]]);
				respAddr = Constants.MIX_PORTS[mixIDs[i]];
			}
					
			//This is the message object sent to the CTF server containing the message M to send,
			//The address of where it should send the response message to, A1
			//The entire response object, and the clients one time use public key Kx
			Message message = new Message(msg, Constants.MIX_PORTS[mixIDs[0]], pubKey, response);
			//==> Ka(R0, M, Kx, A1, K1(R1,A2,K2(R2,A3,...,Kn-1(Rn-1,An,Kn(Rn,Ax))...)))
			//Since the purpose of R0 for sent messages is just padding, it's included implicitly
			//by our AES encryption algorithm
			byte[] toSend = AESEncryption.encrypt(Tools.ObjectToByteArray(message), ctfMixKey);
			//Message sent to last Mix: K1(R1,Aa,Ka(...))
			int sendAddr = Constants.MIX_CTF_PORT;
			//Kn(Rn,An-1, Kn-1(Rn-1,An,...,K2(R2,A1,K1(R1,Ax,Ka(...)))...)
			for(int i = 0; i < Constants.MIXES_PER_MSG; i++) {
				message = new Message(toSend, sendAddr);
				toSend = AESEncryption.encrypt(Tools.ObjectToByteArray(message), mixKeys[mixIDs[i]]);
				sendAddr = Constants.MIX_PORTS[mixIDs[i]];
			}
			return toSend;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private byte[] decryptMixResponse(byte[] bmsg) {
		try {
			Message msg = (Message) Tools.ByteArrayToObject(bmsg);
			// Rn(Rn-1,...,R2(R1(Kx(R0,M)))...)
			byte[] payload = msg.payload;
			for(int i = Constants.MIXES_PER_MSG - 1; i >= 0; i--) {
				payload = AESEncryption.decrypt(payload, responseKeys[i]);
			}
			//System.out.println("at client");
			//Tools.printByteArray(payload);
			return AESEncryption.decrypt(payload, privKey);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
