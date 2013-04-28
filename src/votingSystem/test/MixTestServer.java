package votingSystem.test;

import mixNet.Server;

public class MixTestServer extends Server {

	@Override
	public byte[] processMessage(byte[] msg) {
		msg[0]++;
		return msg;
	}

}
