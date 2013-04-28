package mixNet;

import java.security.PublicKey;
import votingSystem.Constants;
import votingSystem.Tools;
/**
 * Implementation of:
 * Chaum, David L. 
 * "Untraceable electronic mail, return addresses, and digital pseudonyms."
 * Communications of the ACM 24.2 (1981): 84-90.
 * @author Benjamin Jaeger
 *
 */
public class MixNet {
	private Mix[] mixes = new Mix[Constants.NUM_MIXES];
	
	public MixNet() {
		/**
		 * Starts each mix on a thread and writes their public keys
		 * to file.
		 */
		PublicKey[] pubKeys = new PublicKey[Constants.NUM_MIXES]; 
		for(int m = 0; m < Constants.NUM_MIXES; m++) {
			Mix mix = new Node(Constants.MIX_PORTS[m]);
			pubKeys[m] = mix.getPubKey();
			mix.start();
			mixes[m] = mix;
		}
		Tools.WriteObjectToFile(pubKeys, Constants.MIXNET_FILE);
	}
	
	public static void main(String [] args) {
		new MixNet();
	}
}
