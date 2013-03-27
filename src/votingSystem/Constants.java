package votingSystem;

public final class Constants {

	public static final Operation[] OPERATION_VALUES = Operation.values();
	//Server
	public static final int PORT = 9001;
	public static final int POOL_THREADS = 10;
	public static final int MESSAGE_SIZE = 2048;
	public static final int PASSWORD_LENGTH = 50; //Length of BigInteger
	public static final String HOST = null;
	public static final String CTF_PUBLIC_KEY = null;
	public static final String TEMPLATE_FILENAME = "blah";
	//Client
	
	public static enum VoteStatus {SUCCESS,ID_COLLISION, NOT_RECORDED};
	}
}
