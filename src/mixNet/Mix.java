package mixNet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

import votingSystem.AESEncryption;
import votingSystem.Constants;
import votingSystem.RSAEncryption;
import votingSystem.Tools;

public abstract class Mix extends Thread {
	protected PrivateKey privKey;
	protected PublicKey pubKey;
	protected int port;
	
	public Mix(int port) {
		this.port = port;
		KeyPair keys = RSAEncryption.genKeys();
		privKey = keys.getPrivate();
		pubKey = keys.getPublic();
	}
	
	public PublicKey getPubKey() {
		return pubKey;
	}
	
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(port);
			boolean stopped = false;
			Socket clientSocket = null;
			while(!stopped) {
				try {
					clientSocket = ss.accept();
					InputStream in = clientSocket.getInputStream();
					DataInputStream dis = new DataInputStream(in);
					int len = dis.readInt();
					byte[] input = new byte[len];
					//System.out.println(len);
					if (len > 0) {
						dis.readFully(input);
					}
					receive(input);
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print("Mixnet error");
		}
	}
	public void send(byte[] msg, int port) {
		try {
			Socket soc = new Socket(InetAddress.getByName(Constants.HOST), port);
			OutputStream out = soc.getOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(msg.length);
			dos.write(msg);
			soc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public abstract void receive(byte[] msg);	
		
}
