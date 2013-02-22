package votingSystem.cTF;

import java.io.IOException;

/**
 * @author Benjamin Jaeger
 * Starts up the CTF Server.
 */
public class Main {
	public static void main(String [] args)
	{
		Server server = new Server();
		new Thread(server).start();
		
		System.out.println("Press enter to stop server");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Stopping Server");
		server.stop();
	}
}
