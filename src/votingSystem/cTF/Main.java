package votingSystem.cTF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Benjamin Jaeger
 * Starts the CTF and socket server. Provides a terminal interface to change the election state.
 */
public class Main {
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	public static void main(String [] args)
	{
		Accounts acc = accountsExample();
		CTF ctf = null;
		if (acc != null) {
			ctf = new CTF(acc);
		} else {
			ctf = new CTF();
		}
		Server server = new Server(ctf);
		new Thread(server).start();
		
		try {
			//ctf.getElections().get(1).setState(Election.ElectionState.PREVOTE); //TODO
			while(true) {
				System.out.println("Press enter to stop server or an electionId and a state {PENDING, PREVOTE, VOTE, COMPLETED}");
				String input = br.readLine();
				if(input.equals("")) {
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
	
	public static void print(String str) {
		System.out.println(str);
	}
	
	public static Accounts accountsExample() {
		//Create sample Accounts
		Accounts acc = new Accounts();
		Group national = new Group("National");
		Group state = new Group("State");
		Group local = new Group("Local");
		
		acc.addGroup(local, new HashSet<String>());
		acc.addGroup(state, new HashSet<String>());
		acc.addGroup(national, new HashSet<String>());
		
		String userA = "userA";
		String userB = "userB";
		String userC = "userC";
		String userD = "userD";
		String userE = "userE";
		
		Set<Group> userAGroups = 
				new HashSet<Group>(Arrays.asList(new Group[] {state, local}));
		acc.createUser(userA, userAGroups, false);
		
		Set<Group> userBGroups = 
				new HashSet<Group>(Arrays.asList(new Group[] {national, state}));
		acc.createUser(userB, userBGroups, false);
		
		Set<Group> userCGroups = 
				new HashSet<Group>(Arrays.asList(new Group[] {national, state, local}));
		acc.createUser(userC, userCGroups, true);
		
		Set<Group> userDGroups = 
				new HashSet<Group>(Arrays.asList(new Group[] {local}));
		acc.createUser(userD, userDGroups, true);
		
		Set<Group> userEGroups = 
				new HashSet<Group>(Arrays.asList(new Group[] {national, local}));
		acc.createUser(userE, userEGroups, true);
		try {
			print("enter \"1\" if you would like to set eligible groups yourself, \"0\" for random");
			String input = "1";//br.readLine();
			if (!input.equals("1")) return null;
			
			print("For testing purposes only, these are the passwords for every user");
			acc.printUsers();
			
			print("Please enter the election type: \n\"1\" National\n\"2\" State\n\"3\" Local. You can enter more than one, ie. \"12\"");
			input = "123";//br.readLine();
			Set<Group> selected = new HashSet<Group>();
			acc.setSelectedGroup(selected);
			if(input.contains("1")) {
				selected.add(national);
			}
			if(input.contains("2")) {
				selected.add(state);
			}
			if(input.contains("3")) {
				selected.add(local);
			}
			print("Voters eligible for this election are:");
			acc.setSelectedGroup(selected);
			Set<String> eligibleVoters = acc.getUsersInGroups(selected);
			print(Arrays.toString(eligibleVoters.toArray()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally{}
		return acc;
	}
}

