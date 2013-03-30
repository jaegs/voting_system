package votingSystem;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CheckSum {

	private final static String HASH_FUNCTION = "SHA-256";
	private final static int HASH_LENGTH = 32;
  /**
	 * getCheckSum
	 * Returns a SHA256 checksum for a given message
	 * 
	 * @param msg - the message to digest
	 * @return the SHA256 hash of the message
	 */
	public static byte[] getCheckSum(byte[] msg){
		
		//sanity check
		if(msg == null){
			return null;
		}
		
		//digest the message and return that
		try{
			MessageDigest md = MessageDigest.getInstance(HASH_FUNCTION);
			md.update(msg);
			
			return md.digest();
			
		}
		//on exception, print stack and return null
		catch(NoSuchAlgorithmException e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * appendCheckSum
	 * Appends a SHA256 checksum to the front of a given message
	 * 
	 * @param msg - the message to digest
	 * @return the SHA256 hash of the message appended to the front of the message
	 */
	public static byte[] appendCheckSum(byte[] msg){
		
		//get the checksum for the message
		byte[] checkSum = getCheckSum(msg);
		
		//make sure checksum calculation worked
		if(checkSum == null){
			return null;
		}
		//otherwise, copy the checksum and message into a new array
		else{
			byte[] toRet = new byte[msg.length + checkSum.length];
			
			//copy data into the new array
			for(int i = 0; i < toRet.length; i++){
				
				if(i < checkSum.length){
					toRet[i] = checkSum[i];
				}
				else{
					toRet[i] = msg[(i - checkSum.length)];
				}
			}
			
			return toRet;
		}

	}
	
	/**
	 * checkCheckSum
	 * Determines whether or not a given checkSum is correct for a given message
	 * 
	 * @param checksum - The checksum to check
	 * @param msg - the message to check the checkSum of
	 * 
	 * @return - true or false depending on whether or not the checkSum is valid
	 */
	public static boolean checkCheckSum(byte[] checksum, byte[] msg){
		
		//sanity check
		if(msg == null || checksum == null){
			return false;
		}
		
		//digest the message and return whether or not the digest equals the 
		try{
			MessageDigest md = MessageDigest.getInstance(HASH_FUNCTION);
			md.update(msg);
				
			return Arrays.equals(checksum, md.digest());	
		}	
		//on exception, print stack and return false
		catch(NoSuchAlgorithmException e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	/**
	 * stripAndCheck
	 * 
	 * Given a byte array, strips off the first HASH_LENGTH characters, and checks 
	 * if they are a valid checksum for the other bytes. 
	 * 
	 * @param checkSumMessage - the message to check
	 * @return true or false if the checkSum is valid
	 */
	public static boolean stripAndCheck(byte[] checkSumMessage){
		
		byte[] checkSum = new byte[HASH_LENGTH];
		
		//sanity check
		if(checkSumMessage.length <= HASH_LENGTH){
			return false;
		}
		
		byte[] message = new byte[checkSumMessage.length - HASH_LENGTH];
		
		//copy the data into "checkSum" and "message"
		for(int i = 0; i < checkSumMessage.length; i++){
			
			if(i < HASH_LENGTH){
				checkSum[i] = checkSumMessage[i];
			}
			else{
				message[i - HASH_LENGTH] = checkSumMessage[i];
			}
			
		}
		
		//return whether or not the checksum is valid
		return checkCheckSum(checkSum, message);
		
	}
	
	
	public static void printByteArray(byte[] toPrint){
		
		System.out.print("[");
		for(int i = 0; i < toPrint.length; i++){
			
			System.out.print(toPrint[i] + ", ");
		}
		System.out.println("]");
	}
	
	
	/**
	 * Main - Test cases for checkSum
	 * @param args
	 */
	public static void main(String[] args){
		
		byte[] test = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k'};
		
		byte[] fakeCheckSum = {'a', 'b'};
		byte[] checkSum = getCheckSum(test);
		
		printByteArray(test);

		printByteArray(checkSum);
		
		//check success for a valid checksum
		if(checkCheckSum(checkSum, test)){
			System.out.println("Success!");
		}
		else {
			System.out.println("Failed!");
		}
		
		//check failure for an invalid checksum
		if(checkCheckSum(fakeCheckSum, test)){
			System.out.println("Failed!");
		}
		else {
			System.out.println("Success!");
		}
		
		
		//Check the appending and stripAndCheck methods
		byte[] appended = appendCheckSum(test);

		printByteArray(appended);
		
		if(stripAndCheck(appended)){
			System.out.println("Success!");
		}
		else{
			System.out.println("Failed!");
		}
		
	}
}
