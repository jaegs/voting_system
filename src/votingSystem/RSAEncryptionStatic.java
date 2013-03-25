package votingSystem;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * RSAEncryptionStatic
 * 
 * Provides static methods so that various people can perform RSA encrpytion without having access to the full object.
 * This also supports padding/splitting of messages so messages of variable length can be sent
 * 
 * @author tel36
 *
 */
public class RSAEncryptionStatic {


  /**
	 * Encrypt
	 * Takes in a public key and returns the encrypted value of a message.
	 * If the message is too long, split it into multiple parts
	 * If the message is too short, append appropriate padding
	 * 
	 * @param pub - the public key used for encrpytion
	 * @param toEncrypt - the message to encrypt 
	 * 
	 * @return the encrypted message
	 */
	public static byte[] encrypt(PublicKey pub, byte[] toEncrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		int length = pub.getEncoded().length / 8;
		//new chunk of correct size
		byte[] curEncrypt = new byte[length];
		
		int split = toEncrypt.length / length;
		int remainder = toEncrypt.length % length;
		
		//get determines the number of chunks in the encrypted message
		int multiple = split;
		if(remainder > 0){
			multiple++;
		}
		
		//the byte array to  be returned!
		byte[] toRet = new byte[length * multiple];
		
		//perform encryption for all complete chunks
		for(int i = 0; i < split; i++){
			
			
			
			//copy data over!
			for(int y=0; y < length; y++){
				curEncrypt[y] = toEncrypt[(i*length) + y]; 				
			}
			
			//encrypt the chunk!
			Cipher cipher = Cipher.getInstance("RSA"); 
		    cipher.init(Cipher.ENCRYPT_MODE, pub);
		    curEncrypt = cipher.doFinal(curEncrypt);
		    
		    //copy data into the array to return
		    for(int y=0; y < length; y++){
				toRet[(i*length) + y] = curEncrypt[y]; 				
			}
		}
		
		//if necessary, pad and add!
		if(remainder > 0){
			
			//add a padding a perform encryption for that too
			for(int y=0; y < length; y++){
				if(y < remainder){
					curEncrypt[y] = toEncrypt[(split*length) + y]; 
				}
				else{
					curEncrypt[y] = 0;
				}
			}
			
			//encrypt the final chunk
			Cipher cipher = Cipher.getInstance("RSA"); 
		    cipher.init(Cipher.ENCRYPT_MODE, pub);
		    curEncrypt = cipher.doFinal(curEncrypt);
		    
		    //copy encrypted data into the array to return
		    for(int y=0; y < length; y++){
				toRet[(split*length) + y] = curEncrypt[y]; 				
			}
		}
		
		return toRet;
	}

	/**
	 * Encrypt
	 * Takes in a public key and returns the encrypted value of a message.
	 * If the message is too long, split it into multiple parts
	 * If the message is too short, append appropriate padding
	 * 
	 * @param pub
	 * @param toDecrypt - the message to decrypt
	 * 
	 * @return to decrypted message
	 * @throws BadPaddingException 
	 */
	public static byte[] decrypt(PrivateKey priv, byte[] toDecrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		int length = priv.getEncoded().length / 8;
		
		//used for determining padding/splits
		int split = toDecrypt.length / length;
		int remainder = toDecrypt.length % length;
		
		//new chunk of correct size
		byte[] curEncrypt = new byte[length];
		
		//if there is not an even number of blocks, throw an exception
		if(remainder > 0){
			throw new BadPaddingException("Cannot decrypt a message that is not of an even block size!");
		}
		//otherwise, perform decrpytion in chunks and compile them
		else{
			
			//the byte array to  be returned!
			byte[] toRet = new byte[length * split];
			
			//perform encryption for all complete chunks
			for(int i = 0; i < split; i++){
				
				//copy data over!
				for(int y=0; y < length; y++){
					curEncrypt[y] = toDecrypt[(i*length) + y]; 				
				}
				
				Cipher cipher = Cipher.getInstance("RSA");
		        cipher.init(Cipher.DECRYPT_MODE, priv);
		        curEncrypt = cipher.doFinal(curEncrypt);
		        
		        //copy decrypted data into the array to return
			    for(int y=0; y < length; y++){
					toRet[(i*length) + y] = curEncrypt[y]; 				
				}
		        
			}
			
			return toRet;
		}
	}
}
