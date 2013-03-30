package votingSystem.cTF;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;


public class ServerThread implements Runnable {
	private Socket soc;
	private Protocol protocol;

	public ServerThread(Socket clientSocket, CTF ctf) throws IOException {
		soc = clientSocket;
		protocol = new Protocol(ctf);
	}


	@Override
	public void run() {
		/**
		 * Reads socket, passes message to Protocol
		 * and responds to client
		 */
		//http://stackoverflow.com/questions/8274966/reading-a-byte-array-from-socket
		try {	
			InputStream in = soc.getInputStream();
			OutputStream out = soc.getOutputStream();
			
			DataInputStream dis = new DataInputStream(in);
			int len = dis.readInt();
			byte[] input = new byte[len];
			//System.out.println(len);
			if (len > 0) {
				dis.readFully(input);
			}
			//System.out.println(Arrays.toString(input));
			byte[] response = protocol.processMessage(input);
			if (response != null) {
				DataOutputStream dos = new DataOutputStream(out);
				dos.writeInt(response.length);
				dos.write(response);
			}
			soc.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
