package votingSystem;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CheckSum {

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
			MessageDigest md = MessageDigest.getInstance("SHA-256");
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
		
		byte[] checkSum = getCheckSum(msg);
		if(checkSum == null){
			return null;
		}
		else{
			byte[] toRet = new byte[msg.length + checkSum.length];
			
		}
		
		
	}
}
