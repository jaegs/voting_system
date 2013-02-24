package votingSystem.voter;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

import votingSystem.Constants;


public class Voter {

	
	/**
	 * Define the Client Interface!
	 * 
	 * "CheckEligible -username" -> returns
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
		String in = "";
		
	    try{
	    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    	 //gets the voter's name from the console
			System.out.print("\nPlease enter your username: ");
			String username = br.readLine();
			System.out.println(username);
		
			//get the voter's password from the console
			System.out.print("\nPlease enter your password: ");
			String password = br.readLine();
			System.out.println(password);
	    
	    
			//VALIDATE USER AND PASSWORD
			while(!in.equals("exit")){
				System.out.println("\nEnter Command (\"exit\") to quit: ");
				in = br.readLine();
				System.out.println(in);
			
				String[] split = in.split(" ");
				
				//check to make sure the length is at least 1
				if(split.length == 0){
					System.out.println("Invalid Command!");
				}
				//check to see if the user has requested the Manual Page
				else if (split[0].equals("man") || split[0].equals("help")){
					
					System.out.println("CheckEligible -username -> returns whether or not a given user is eligible to vote");
					System.out.println("CheckElections -> prints a list of open elections");
					System.out.println("Register -ElectionNo -> registers a voter for a given election");
					System.out.println("ConfirmRegistration -> confirms that a voter has successfully registered");
					System.out.println("GetCandidates -ElectionNo -> prints the list of candidates for a given election");
					System.out.println("Vote -ElectionNo -CandidateNo -> vote for a given candidate in a given election");
					System.out.println("ConfirmVote -ElectionNo -CandidateNo -> confirms a vote in a given election");
	  				System.out.println("Revote - ElectionNo -CandidateNo -> revotes in a new election");
	  				
				}
				else if (split[0].equals("CheckEligible")){
					if(split.length < 2){
						System.out.println("Invalid Command!");
					}
					else{
						
						String un = split[1];
						
						if(un.length() > 1){
							System.out.println("Invalid Command!");
						}
						else{
							byte[] unBytes = un.getBytes();
							byte[] toSend = new byte[4];
							toSend[0] = 0;
							toSend[1] = 1;
							toSend[2] = 2;
							toSend[3] = 'a';
							
							byte[] response = Client.send(toSend);
						}
						
			
						//SEND THIS QUERY TO SERVER
					}
				}
			}
			
	    }
		catch(IOException e)
		{
			e.printStackTrace();
		}
	    
		
		
		
	}
		
	
	public static void main(String[] args){
		
		Voter voter = new Voter();
		voter.run();
		
	}
}
