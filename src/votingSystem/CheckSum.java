package votingSystem;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CheckSum {

	private final static String HASH_FUNCTION = "SHA-256";
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
}
