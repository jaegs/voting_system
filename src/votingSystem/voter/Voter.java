package votingSystem.voter;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;

import votingSystem.AESEncryption;
import votingSystem.Constants;
import votingSystem.Message;
import votingSystem.Operation;
import votingSystem.Tools;


public class Voter {
	private int electionId;
	private String name;
	private String password;
	private byte[] voterId;
	private byte[] encryptedVote;
	private static SecureRandom random = new SecureRandom();
	

	private Message prepareMessage(Message send, Operation responseType) throws InvalidNonceException, UnknownHostException, IOException {
		return prepareMessage(send); //TODO: CHECK RESPONSE TYPE
	}
	
	public Message prepareMessage(Message send) throws InvalidNonceException, UnknownHostException, IOException  {
		send.electionId = electionId;
		int nonce = random.nextInt();
		send.nonce = nonce;
		byte[] msg = Tools.ObjectToByteArray(send);
		byte[] encryptedMsg = null;
		try {
			encryptedMsg = AESEncryption.encrypt(msg, Constants.CTF_PUBLIC_KEY);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} 
		byte[] responseBytes = Client.send(encryptedMsg);
		Message response = (Message) Tools.ByteArrayToObject(responseBytes);
		if (nonce + 1 != response.nonce) {
			throw new InvalidNonceException();
		}
		
		return response;
	}

	public boolean isEligible() throws UnknownHostException, InvalidNonceException, IOException {
		Message send = new Message(Operation.ISELIGIBLE);
		send.voter = name;
		Message response = prepareMessage(send, Operation.ISELIGIBLE_R);
		return response.eligible;
	}
	
	public void willVote() throws UnknownHostException, InvalidNonceException, IOException {
		Message send = new Message(Operation.WILLVOTE);
		send.voter = name;
		send.password = password;
		prepareMessage(send);
	}
	
	public boolean isVoting() throws UnknownHostException, InvalidNonceException, IOException {
		Message send = new Message(Operation.ISVOTING);
		send.voter = name;
		Message response = prepareMessage(send, Operation.ISVOTING_R);
		return response.isVoting;
	}
	
	public void vote() throws UnknownHostException, InvalidNonceException, IOException {
		Message send = new Message(Operation.VOTE);
		send.voterId = voterId;
		send.encryptedVote = encryptedVote;
		prepareMessage(send);
	}
	
	//public voted() throws InvalidNonceException {
		//
	//}
	
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Please enter the election ID for the election you would like to vote in");
			electionId = 1;//Integer.parseInt(br.readLine());
			System.out.println("Please enter you name");
			name = "a";//br.readLine();
			if (!isEligible()) {
				System.out.println("Sorry " + name + ", at this time you are not eligible to vote in election " + electionId);
				return;
			}
			System.out.println("Success! " + name + ". You are eligible to vote in " + electionId);
			System.out.println("To vote, please enter your password");
			password = "u9kvce864r"; //br.readLine();
			willVote();
			if(!isVoting()) {
				System.out.println("Sorry " + name + ", at this time we could not confirm your voting status");
				return;
			}
			System.out.println("Success! " + name + ". You are confirmed as voting in " + electionId);
		} catch (InvalidNonceException ine) {
			System.out.println("Error communication with server: invalid nonce");
		} catch (IOException ioe) {
			System.out.println("Error communicating with server: ");
			ioe.printStackTrace();
		}
	}
	
	
//	/**
//	 * Define the Client Interface!
//	 * 
//	 * "CheckEligible -username" -> returns
//	 * "CheckElections" -> prints a list of open elections
//	 * "Register -ElectionNo" -> registers a voter for a given election
//	 * "ConfirmRegistration" -> confirms that a voter has successfully registered
//	 * "GetCandidates -ElectionNo" -> prints the list of candidates for a given election
//	 * "Vote -ElectionNo -CandidateNo" -> vote for a given candidate in a given election
//	 * "ConfirmVote -ElectionNo -CandidateNo" -> confirms a vote in a given election
//	 * "Revote - ElectionNo -CandidateNo" -> revotes in a new election
//	 * 
//	 */
//	public void run(){
//		String in = "";
//		
//	    try{
//	    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//	    
//	    	System.out.println("Welcome to Voting System! Type \'man\' or \'help\' for assistance at any point.");
//			//VALIDATE USER AND PASSWORD
//			while(!in.equals("exit")){
//				System.out.print("\nEnter Command (\"exit\") to quit: ");
//				in = br.readLine();
//			
//				String[] split = in.split(" ");
//				
//				//check to make sure the length is at least 1
//				if(split.length == 0){
//					System.out.println("Invalid Command!");
//				}
//				//check to see if the user has requested the Manual Page
//				else if (split[0].equals("man") || split[0].equals("help")){
//					
//					System.out.println("CheckEligible -username -> returns whether or not a given user is eligible to vote");
//					//System.out.println("CheckElections -> prints a list of open elections");
//					System.out.println("Register-> registers a voter for a given election");
//					System.out.println("ConfirmRegistration -> confirms that a voter has successfully registered");
//					System.out.println("GetCandidates-> prints the list of candidates for a given election");
//					System.out.println("Vote -CandidateNo -> vote for a given candidate in a given election");
//					System.out.println("ConfirmVote -CandidateNo -> confirms a vote in a given election");
//	  				System.out.println("Revote -CandidateNo -> revotes in a new election");
//	  				
//				}
//				else if (split[0].equals("CheckEligible")){
//					if(split.length < 2){
//						System.out.println("Invalid Command!");
//					}
//					else{
//						
//						String un = split[1];
//						
//						if(un.length() > 1){
//							System.out.println("Invalid Command!");
//						}
//						else{
//							//sending constants here
//							byte[] unBytes = un.getBytes();
//							byte[] toSend = new byte[4];
//							byte[] nonce = new byte[1];
//							random.nextBytes(nonce);
//							
//							
//							//Cooresponds to ISELIGIBLE OPERATION
//							toSend[0] = 1;
//							//Election ID (always 1 in this rendition)
//							toSend[1] = 1;
//							//A random nonce
//							toSend[2] = nonce[0];
//							
//							//the name of the user (in this case only one byte)
//							toSend[3] = unBytes[0];
//							
//						
//							byte[] response = encrypt(Client.send(encrypt(toSend)));
//							
//							//Check the nonce value
//							if(response[2] == nonce[0] + 1){
//								if (response[toSend.length] == 1){
//									System.out.println("The user \'" + un + "\' is eligible to vote");
//								}
//								else{
//									System.out.println("The user \'" + un + "\' is not eligible to vote");
//								}
//							}
//							else{
//								System.out.println("Error in signal transmission, please try again!");
//							}
//							
//						}
//						
//			
//						//SEND THIS QUERY TO SERVER
//					}
//					
//					
//				}
//				else if(split[0].equals("Register")){
//					
//					 //gets the voter's name from the console
//					System.out.print("Please enter your username: ");
//					String username = br.readLine();
//					
//					//get the voter's password from the console
//					System.out.print("Please enter your password: ");
//					String password = br.readLine();
//					
//					if (username.length() < 1 || password.length() < 1){
//						System.out.println("Invalid Username/Password Combination!");
//					}
//					
//					
//					byte[] pw = password.getBytes();
//					byte[] toSend = new byte[3 + pw.length];
//					
//					//The type of message to send
//					toSend[0] = 2;
//					//The election (hard coded to 1 at this phase)
//					toSend[1] = 1;
//					toSend[2] = (byte) username.charAt(0);
//					for(int i = 0; i < pw.length; i++){
//						toSend[3 + i] = pw[i];
//					}
//				
//					/*for(int y = 0; y < toSend.length; y++){
//						System.out.println("Byte Np: " + y + " = " + toSend[y]);
//					}
//					
//					byte[] toSend2 = (encrypt(toSend));
//					
//					for(int y = 0; y < toSend2.length; y++){
//						System.out.println("Byte Np: " + y + " = " + toSend2[y]);
//					}
//					
//					byte[] toSend3 = decrypt(encrypt(toSend));
//					
//					for(int y = 0; y < toSend3.length; y++){
//						System.out.print( toSend[y] + "  ");
//					}
//					*/
//					
//					byte[] response = encrypt(Client.send(encrypt(toSend)));
//					
//					
//					
//					
//					if(response[0] == 0){
//						System.out.println("Invalid username and password!");
//					}
//					
//					if(response[0] == 1){
//						System.out.println("Valid username and password, intent to vote is sent!");
//					}
//					
//				}
//				else if(split[0].equals("ConfirmRegistration")){
//					if(split.length < 2){
//						System.out.println("Invalid Command!");
//					}
//					else{
//						
//						String un = split[1];
//						
//						if(un.length() > 1){
//							System.out.println("Invalid Username!");
//						}
//						else{
//							//sending constants here
//							byte[] unBytes = un.getBytes();
//							byte[] toSend = new byte[4];
//							byte[] nonce = new byte[1];
//							random.nextBytes(nonce);
//							
//							
//							//Cooresponds to ISVOTING OPERATION
//							toSend[0] = 3;
//							//Election ID (always 1 in this rendition)
//							toSend[1] = 1;
//							//A random nonce
//							toSend[2] = nonce[0];
//							
//							//the name of the user (in this case only one byte)
//							toSend[3] = unBytes[0];
//							
//						
//							byte[] response = encrypt(Client.send(encrypt(toSend)));
//							
//							//Check the nonce value
//							if(response[2] == nonce[0] + 1){
//								if (response[toSend.length] == 1){
//									System.out.println("The user \'" + un + "\' is registered to vote :)");
//								}
//								else{
//									System.out.println("The user \'" + un + "\' is not registered to vote :(");
//								}
//							}
//							else{
//								System.out.println("Error in signal transmission, please try again!");
//							}
//							
//						}
//					}
//					
//				}
//				else if(split[0].equals("GetCandidates")){
//					if(split.length < 2){
//						System.out.println("Invalid Command!");
//					}
//					else{
//						String electionNumber = split[1];
//						try{
//							Integer electionNum = Integer.parseInt(electionNumber);
//							byte byteNum = electionNum.byteValue();
//							byte[] nonce = new byte[1];
//							random.nextBytes(nonce);
//							
//							byte[] toSend = new byte[2];
//							toSend[0] = 4;
//							toSend[1] = nonce[0];
//							toSend[2] = byteNum;
//							
//							byte[] response = encrypt(Client.send(encrypt(toSend)));
//							
//							
//						}catch(NumberFormatException e){
//							System.out.println("Invalid Election Number!");
//						}
//					}
//				//	
//				}
//			}
//			
//	    }
//		catch(IOException e)
//		{
//			e.printStackTrace();
//		}
//	    
//		
//		
//		
//	}
		
	
	public static void main(String[] args){
		
		Voter voter = new Voter();
		voter.run();
		
	}
}
