package votingSystem.voter;

import java.io.Console;

public class Voter {
	
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
		
		Voter voter = new Voter();
		voter.run();
		
	}
}
