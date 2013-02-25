package votingSystem.voter;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;

import votingSystem.Constants;


public class Voter {

	//Public key and modulo hard coded into the program
	//We realize this is not an appropriate way to handle this, and will change in the next phase
	private static BigInteger CTFPublic = new BigInteger("7");
	private static BigInteger CTFModulo = new BigInteger("318682891574554640236911507202669852853");
	private static BigInteger CTFPrivate = new BigInteger("136578382103380560086232017154571694323");
	private static SecureRandom random = new SecureRandom();
	
	private byte[] encrypt(byte[] toEncrypt){
		
		BigInteger message = new BigInteger(toEncrypt);
		return (message.modPow(CTFPublic, CTFModulo)).toByteArray();
	}
	
	private byte[] decrypt(byte[] toDecrypt){
		
		BigInteger message = new BigInteger(toDecrypt);
		return (message.modPow(CTFPrivate, CTFModulo)).toByteArray();
	}
	
	
	
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
	    
	    	System.out.println("Welcome to VotingSystem! Type \'man\' or \'help\' for assistance at any point.");
			//VALIDATE USER AND PASSWORD
			while(!in.equals("exit")){
				System.out.print("\nEnter Command (\"exit\") to quit: ");
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
					//System.out.println("CheckElections -> prints a list of open elections");
					System.out.println("Register-> registers a voter for a given election");
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
							//sending constants here
							byte[] unBytes = un.getBytes();
							byte[] toSend = new byte[4];
							byte[] nonce = new byte[1];
							random.nextBytes(nonce);
							
							
							//Cooresponds to ISELIGIBLE OPERATION
							toSend[0] = 1;
							//Election ID (always 1 in this rendition)
							toSend[1] = 1;
							//A random nonce
							toSend[2] = nonce[0];
							
							//the name of the user (in this case only one byte)
							toSend[3] = unBytes[0];
							
						
							byte[] response = encrypt(Client.send(encrypt(toSend)));
							
							//Check the nonce value
							if(response[2] == nonce[0] + 1){
								if (response[toSend.length] == 1){
									System.out.println("The user \'" + un + "\' is eligible to vote");
								}
								else{
									System.out.println("The user \'" + un + "\' is not eligible to vote");
								}
							}
							else{
								System.out.println("Error in signal transmission, please try again!");
							}
							
						}
						
			
						//SEND THIS QUERY TO SERVER
					}
					
					
				}
				else if(split[0].equals("Register")){
					
					 //gets the voter's name from the console
					System.out.print("\nPlease enter your username: ");
					String username = br.readLine();
					
					//get the voter's password from the console
					System.out.print("\nPlease enter your password: ");
					String password = br.readLine();
					
					if (username.length() < 1 || password.length() < 1){
						System.out.println("Invalid Username/Password Combination!");
					}
					
					
					byte[] pw = password.getBytes();
					byte[] toSend = new byte[3 + pw.length];
					
					//The type of message to send
					toSend[0] = 2;
					//The election (hard coded to 1 at this phase)
					toSend[1] = 1;
					toSend[2] = (byte) username.charAt(0);
					for(int i = 0; i < pw.length; i++){
						toSend[3 + i] = pw[i];
					}
				
					for(int y = 0; y < toSend.length; y++){
						System.out.println("Byte Np: " + y + " = " + toSend[y]);
					}
					
					byte[] toSend2 = decrypt(encrypt(toSend));
					
					for(int y = 0; y < toSend2.length; y++){
						System.out.println("Byte Np: " + y + " = " + toSend2[y]);
					}
					
					encrypt(Client.send(encrypt(toSend)));
					
					System.out.println("Intent to vote sent!");
				}
				//else if(){
					
					
				/*}
				else{
					
					
				}*/
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
