package mixNet;

import java.security.PublicKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import votingSystem.Constants;
import votingSystem.Tools;

public class MixNet {
	private Mix[] mixes;
	
	public MixNet() {
		/**
		 * Starts each mix on a thread and writes their public keys
		 * to file.
		 */
		PublicKey[] pubKeys = new PublicKey[Constants.NUM_MIXES + 1]; 
		for(int m = 0; m < Constants.NUM_MIXES; m++) {
			Mix mix = new Node(Constants.MIX_PORTS[m]);
			pubKeys[m] = mix.getPubKey();
			mix.start();
			mixes[m] = mix;
		}
		Tools.WriteObjectToFile(pubKeys, Constants.MIXNET_FILE);
	}
}
