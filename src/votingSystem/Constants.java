package votingSystem;

import java.security.PublicKey;

public final class Constants {

	public static final Operation[] OPERATION_VALUES = Operation.values();

	//Server Constants
	public static final int PORT = 9001;
	public static final int POOL_THREADS = 10;
	public static final int MESSAGE_SIZE = 2048;
	public static final int PASSWORD_LENGTH = 50; //Length of BigInteger
	public static final String HOST = null;
	public static final String TEMPLATE_FILENAME = "blah";
	
	public static enum VoteStatus {NOT_RECORDED, SUCCESS,ID_COLLISION, INVALID_ELECTION_STATE};
		
	// Encryption Constants
	public static final int RSA_KEY_SIZE = 2048; // bits
	public static final int RSA_ENCRYPTED_SIZE = 256; // bytes
	public static final int AES_KEY_SIZE = 128; // bits
	public static final int AES_IV_SIZE = 16; // bytes
	public static final String CTF_PUBLIC_KEY_FILE = "bin/votingSystem/ctfpubkey.ser";
	public static PublicKey CTF_PUBLIC_KEY = (PublicKey) Tools.ReadObjectFromFile(CTF_PUBLIC_KEY_FILE);
}
