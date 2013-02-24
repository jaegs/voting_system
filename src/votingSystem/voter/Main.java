package votingSystem.voter;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Main {

	/**
	 * @param args
	 * 
	 * username: a pass: 2oki4icaffsabc256u2h
	 * username: b pass: 5h039git56rcbl0mptpe
     * username: c pass: lovdbggg9kkcv6r6opf8
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte[] msg = {4, 35, -67, 0, 3, 120, 0, -56};
		try {
			System.out.println(Arrays.toString(Client.send(msg)));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
