package votingSystem.voter;
import votingSystem.Constants;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.Console;
import java.io.*;


public class Client {
	
	
	/**
	 * Each message will be sent with a new connection.
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
	
		/**
	 * Define the Client Interface!
	 * 
	 * "CheckElections" -> prints a list of open elections
	 * "Register -ElectionNo" -> registers a voter for a given election
	 * "ConfirmRegistration" -> confirms that a voter has successfully registered
	 * "GetCandidates -ElectionNo" -> prints the list of candidates for a given election
	 * "Vote -ElectionNo -CandidateNo" -> vote for a given candidate in a given election
	 * "ConfirmVote -ElectionNo -CandidateNo" -> confirms a vote in a given election
	 * "Revote - ElectionNo -CandidateNo" -> revotes in a new election
	 * 
	 */
	public void run(){
		String input = "";
		//create a console instance
		Console console = System.console();
	    if (console == null) {
	        System.out.println("Could not create Console instance");
	        System.exit(-1);
	    }
	    
	    //gets the voter's name from the console
		System.out.println("\nPlease enter your username: ");
		String username = console.readLine();
		System.out.println(username);
		
		//get the voter's password from the console
		System.out.println("\nPlease enter your password: ");
		char password[] = console.readPassword();
		System.out.println(password);
		
		//VALIDATE USER AND PASSWORD
		
		while(!input.equals("exit")){
			
			System.out.println("\nEnter Command (\"exit\" to quit: ");
			String in = console.readLine();
			System.out.println(in);
			
			
			
		}
		
		
	}
		
	
	public static void main(String[] args){
		
		Client voter = new Client();
		voter.run();
		
	}
}
