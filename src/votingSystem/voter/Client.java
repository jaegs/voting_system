package votingSystem.voter;
import votingSystem.Constants;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Client {
	/**
	 * Each message will be sent with a new connection.
	 */
	public static byte[] send(byte[] msg) throws UnknownHostException, IOException{
		Socket soc = new Socket(Constants.HOST, Constants.PORT);
		OutputStream out = soc.getOutputStream();
		out.write(msg);
		out.close();
		InputStream in = soc.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte buffer[] = new byte[1024];
		for(int s; (s=in.read(buffer)) != -1; ) {
		  baos.write(buffer, 0, s);
		}
		byte[] response = baos.toByteArray();
		in.close();
		soc.close();
		return response;
	}
}
