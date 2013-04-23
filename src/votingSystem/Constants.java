package votingSystem;

import java.security.PublicKey;

public final class Constants {
	/**
	 * System-wide constants
	 */

	public static final Operation[] OPERATION_VALUES = Operation.values();
	public static final int NUM_VOTERS = 10;
	public static boolean DEBUG = false;
	public static boolean VERBOSE = false;

	//Server Constants
	public static final int PORT = 9001;
	public static final int POOL_THREADS = 10;
	public static final int MESSAGE_SIZE = 2048;
	public static final int RSA_SMALL_KEY = 512; //bits
	public static final int PASSWORD_LENGTH = 60; //Length of BigInteger
	public static final int VOTER_NAME_LENGTH = 100; //Length of BigInteger
	public static final String HOST = null; //this is the localhost.
	public static final String PASSWORDS_FILENAME = "bin/votingSystem/passwords.ser";
	public static final String VOTERS_FILENAME = "bin/votingSystem/voters.ser";
	public static final int PASSWORD_DELAY = 2000;
	
	public static enum VoteStatus {NOT_RECORDED, SUCCESS,ID_COLLISION, INVALID_ELECTION_STATE};
		
	// Encryption Constants
	public static final int RSA_KEY_SIZE = 2048; // bits
	public static final int RSA_ENCRYPTED_SIZE = 256; //bytes
	public static final int AES_KEY_SIZE = 128; // bits
	public static final int AES_IV_SIZE = 16; // bytes
	public static final int SIG_SIZE = 256; // bytes
	public static final String RSA_ALG = "RSA";
	public static final String AES_ALG = "AES/CBC/PKCS5PADDING";
	public static final String SIG_ALG = "SHA1withRSA";
	public static final String CTF_PUBLIC_KEY_FILE = "bin/votingSystem/ctfpubkey.ser";
	public static PublicKey CTF_PUBLIC_KEY = (PublicKey) Tools.ReadObjectFromFile(CTF_PUBLIC_KEY_FILE);
}
