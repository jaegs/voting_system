package votingSystem.voter;
import votingSystem.Constants;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.Console;
import java.io.*;


public class Client {
	
	
	/**
	 * Each message is sent with a new connection.
	 */
	public static byte[] send(byte[] msg) throws UnknownHostException, IOException{
		Socket soc = new Socket( InetAddress.getByName(Constants.HOST), Constants.PORT);
		OutputStream out = soc.getOutputStream();
		InputStream in = soc.getInputStream();
		DataOutputStream dos = new DataOutputStream(out);
		dos.writeInt(msg.length);
		dos.write(msg);
		
		DataInputStream dis = new DataInputStream(in);
		int len = dis.readInt();
		byte[] response = new byte[len];
		if (len > 0)
			dis.readFully(response);
		
		soc.close();
		return response;
	}
	
	
}
