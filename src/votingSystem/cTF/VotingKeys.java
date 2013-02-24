package votingSystem.cTF;

//import votingSystem.RSAEncryption;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

public class VotingKeys {

    
    private HashSet<BigInteger> availableKeys;
    private HashSet<BigInteger> takenKeys;
    private SecureRandom random;
    //private RSAEncryption rsa;
    
    /*private byte[] privateRSAKey;
    private byte[] publicRSAModulo;
    private byte[] publicRSAExponent;*/
    
    //private RSAEncrpytion rsa;
    
    public VotingKeys(int numKeys){
    	
    	//initialize instance variables. 
		random = new SecureRandom();
		availableKeys = new HashSet<BigInteger>();
		takenKeys = new HashSet<BigInteger>();
    	
		//generate random keys
    	for(int i = 0; i < numKeys; i++){
   		 	
   		 	//Generate 128-bit voting keys and set the all as available...
   		 	BigInteger p = new BigInteger(128, 100, random);
   		 	availableKeys.add(p);
   		 	//Alternatively, we might want to use RSA keys here here
    	}
    }

  
    
    public BigInteger[] randomMessages(){
    	
    	//the number of keys left available
    	int size = availableKeys.size();
    	
    	//a list of random messages cooresponding to private keys
    	BigInteger[] toRet = new BigInteger[size];
    	
    	//create 128-bit random messages
    	for(int i = 0; i < size; i++){
    		toRet[i] = new BigInteger(128, 100, random);
    	}
    
    	return toRet;
    }
    
    
    public static void main(String[] args){
    	
    	VotingKeys vk = new VotingKeys(100);

    }
//    
//    public generateK(){
//        
//    }
//    
//    public sendMessages(){
//        
//    }
}
