package votingSystem;


import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class ObliviousTransfer {

	
    private BigInteger[] secrets;
    private BigInteger[] randomMessages;
    private KeyPair keys;
    private static SecureRandom random;
    
        
    private Set<String> stringSecrets = new HashSet<String>();

    /**
     * Common constructor
     * 
     * Initializes a new ObliviousTransfer object
     * 
     * @param numKeys - the number of "secrets" to create
     */
    public ObliviousTransfer(int numKeys){
    	
    	//initialize instance variables. 
		random = new SecureRandom();
		secrets = new BigInteger[numKeys];//<BigInteger>();
    	
		//generate random keys
    	for(int i = 0; i < numKeys; i++){
   		 	
   		 	//Generate 128-bit voting keys and set the all as available...
   		 	BigInteger p = new BigInteger(128, 100, random);
   		 	
   		 	secrets[i] = p;
   		 	stringSecrets.add(Base64Coder.encodeLines(p.toByteArray()));
   		 	//Alternatively, we might want to use RSA keys here here
    	}

    	
    	//generate the random messages
		randomMessages = new BigInteger[numKeys];
	
		//create 128-bit random messages
		for(int i = 0; i < numKeys; i++){
			randomMessages[i] = new BigInteger(128, 100, random);
		}
		
		//Initialize keyPairs!
		keys = RSAEncryption.genKeys();
    }
    
    /**
     * Gets a the private/public keypair
     * If this does not already exist, initialize it
     * @return
     */
    public KeyPair getKeyPair(){
    	
    	return keys;
    }

    /**
     * randomMessages
     * 
     * @return - the list of random messages
     */
    public BigInteger[] getRandomMessages(){
    	
    	
    	return randomMessages;
    	
    }
    
    
    /**
     * Secretly chooses a random message out of a list
     * @param randomMessages - a list of random messages to choose from
     * @return the index of the random message
     */
    public static int chooseSecret(BigInteger[] randomMessages){
    	
    	//randomly choose one of the random messages
    	int size = randomMessages.length;
    	int b = random.nextInt(size);
    	
    	return b;
    }
    
    /**
     * generate K - generates a random value (used client side)
     * @return
     */
    public static byte[] generateK(){
    	BigInteger k = new BigInteger(128, random);
    	return k.toByteArray();
    }
    
    
    /**
     * Calculates the V value for oblivious transfer (used client side)
     * @param x - the random message produced by the server, chosen by the client
     * @param k - a random value chosen by the client
     * @param e - the e aspect of the public RSA key 
     * @param N - the N aspect of the public RSA key
     * @return the calculated V value
     * @throws InvalidKeyException 
     */
    public static BigInteger calculateV(BigInteger x, byte[] k, PublicKey pubk) throws InvalidKeyException{// BigInteger k, int e, BigInteger n){
    	
    	//encrypt k then blind it with X
    	byte[] encrypted = RSAEncryption.encryptNoPadding(k, pubk);	
    	BigInteger k_encrypted = new BigInteger(encrypted);
    	return k_encrypted.add(x);
    }
    
    /**
     * Calculates the K values for all of the random messages
     * @param randomMessages - the list of random messages initially generated
     * @param v - the V value generated by client
     * @param d - the d aspect of the private RSA KEY
     * @param n - the N aspect of the public RSA key
     * @return - the encrypted k values of the random mesages
     * @throws InvalidKeyException 
     */
    public BigInteger[] calculateMs(BigInteger v) throws InvalidKeyException{// BigInteger d, BigInteger n){
    	
    	BigInteger[] toRet = new BigInteger[randomMessages.length];
    	PrivateKey privk = keys.getPrivate();
    	
    	for(int i = 0; i < randomMessages.length; i++){
    		
    		BigInteger to_decrypt = (v.subtract(randomMessages[i]));
    		byte[] to_dec = to_decrypt.toByteArray();    		
    		
    		byte[] decrypted = RSAEncryption.decryptNoPadding(to_dec, privk);
    		
    		BigInteger bigI_decrypted = new BigInteger(decrypted);
    		
    		toRet[i] = secrets[i].add(bigI_decrypted);
    	}
    	
    	return toRet;
    	
    }
    
    /**
     * Determines the desired message from a list of encrypted k values
     * @param kValues - the list of k-values
     * @param index - the index of the message (chosen earlier)
     * @param k - the random K value (chosen earlier)
     * @return the decrypted message
     */
    public static BigInteger determineMessage(BigInteger[] mValues, int index, byte[] k){
    	return (mValues[index]).subtract(new BigInteger(k));
    }
    
    /**
     * checkSecret - checks the validity of a secret
     * @param toCheck - the integer to check
     * @return true or false based on the key's validity
     */
    public boolean checkSecret(String toCheck){	
    	
    	return stringSecrets.contains(toCheck);
    }
    
    public BigInteger getSecret(int index){
    	return secrets[index];
    }
    
    

	public static void printByteArray(byte[] toPrint){
		
		System.out.print("[");
		for(int i = 0; i < toPrint.length; i++){
			
			System.out.print(toPrint[i] + ", ");
		}
		System.out.println("]");
	}
	
	
    
    public static void main(String[] args) throws InvalidKeyException{
    	
    	ObliviousTransfer test = new ObliviousTransfer(10);
    	
    	KeyPair keypair = RSAEncryption.genKeys();
    	
    	//server side starting
    	BigInteger[] randomMessages = test.getRandomMessages();
    
    	//client side action
    	int b = chooseSecret(randomMessages);
    	System.out.println("b: " + b);
    	
    	byte[] k = generateK();

    	printByteArray(k);
    	
    	BigInteger v = calculateV(randomMessages[b], k, keypair.getPublic());
    	
    	//server side action
    	BigInteger[] ms = test.calculateMs(v);// rsa.getSecret(), rsa.getModulus());
    	
    	//client side action
    	BigInteger message = determineMessage(ms, b, k);
    	
    	
    	//These should be the same
    	System.out.println("Start: " + test.getSecret(b));
    	System.out.println("End: " + message);
    
    }

}
