package votingSystem.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import mixNet.Client;
import mixNet.MixNet;
import mixNet.Server;

import org.junit.Test;

import votingSystem.Constants;
import votingSystem.Tools;

public class TestMixNet {

	@Test
	public void testSendAndReceive() {
		System.out.println("The tests whether a message can be sent and received anonymously through the mixnet\n The server increments the first byte by one");
		new MixNet();
		Server serv = new MixTestServer();
		Tools.WriteObjectToFile(serv.getPubKey(), Constants.MIX_SERVER_KEY_FILE);
		serv.start();
		Client client = new Client(8083);
		System.out.println("Message to send is:");
		byte[] msg = new byte[] {1,2,3,4, 5, 6, 7, 8, 9, 10, 11,12,13,14,15,16,17,18,19};
		Tools.printByteArray(msg);
		byte[] response = client.send(msg);
		System.out.println("Message received is:");
		Tools.printByteArray(response);
		msg[0]++;
		assertArrayEquals(msg, response);
	}

}
