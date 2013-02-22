package votingSystem.cTF;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class ServerThread implements Runnable {
	private Socket soc;

	public ServerThread(Socket clientSocket) throws IOException {
		soc = clientSocket;
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
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte buffer[] = new byte[1024];
			for(int s; (s=in.read(buffer)) != -1; )
			{
			  baos.write(buffer, 0, s);
			}
			byte input[] = baos.toByteArray();
			in.close();
			byte[] response = Protocol.processMessage(input);
			OutputStream out = soc.getOutputStream();
			out.write(response);
			out.close();
			soc.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
