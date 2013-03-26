package votingSystem;

import java.security.PublicKey;

public final class Constants {
	// Voting Protocol Constants
	public static enum Operation {
		NOOP, //Zero value operation gets chopped off by RSA
		ISELIGIBLE,
		WILLVOTE, 
		ISVOTING, 
		GETIDENTIFICATION, 
		VOTE,
		VOTED, 
		CHECKIDCOLLISION, 
		PROCESSVOTE,
		RESULTS,
		COUNTED,
		PROTEST,
		CHANGE,
		OTGETRANDOMMESSAGES,
		OTGETSECRETS
	}
	public static final Operation[] OPERATION_VALUES = Constants.Operation.values();
	
	// Server Constants
	public static final int PORT = 9001;
	public static final int POOL_THREADS = 10;
	public static final int MESSAGE_SIZE = 2048;
	public static final int PASSWORD_LENGTH = 50; //Length of BigInteger
	public static final String HOST = null;
	public static final String CTF_PUBLIC_KEY = null;
	
	// Client Constants
	
	// Encryption Constants
	public static final int RSA_KEY_SIZE = 2048;
	public static final int AES_KEY_SIZE = 128;
	public static PublicKey ctfpublic;
}