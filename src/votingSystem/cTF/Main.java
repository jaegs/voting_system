package votingSystem.cTF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Benjamin Jaeger
 * Starts up the CTF Server.
 */
public class Main {
	public static void main(String [] args)
	{
		CTF ctf = new CTF();
		Server server = new Server(ctf);
		new Thread(server).start();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			ctf.getElections().get(1).setState(Election.ElectionState.PREVOTE); //TEMPORARY
			while(true) {
				System.out.println("Press enter to stop server or an electionId and a state {PENDING, PREVOTE, VOTE, COMPLETED}");
				String input = br.readLine();
				if(input == "") {
					break;
				}
				String[] inputs = input.split(" ");
				int electionId = Integer.parseInt(inputs[0]);
				Election.ElectionState state = Election.ElectionState.valueOf(inputs[1]);
				ctf.getElections().get(electionId).setState(state);
				System.out.println("Election "+ electionId + " changed to state: " + state);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Stopping Server");
		server.stop();
	}
}
