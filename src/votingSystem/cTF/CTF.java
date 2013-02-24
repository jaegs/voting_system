package votingSystem.cTF;

import votingSystem.RSAEncryption;
import java.util.List;

/**
 * Maintains all of the Elections
 * @author test
 *
 */
public class CTF {
	private List<Election> elections;
	public RSAEncryption rsa = new RSAEncryption("7", 
			"136578382103380560086232017154571694323",
			"318682891574554640236911507202669852853");
	public Election election = new Election();
	
	
	public CTF() {
		new Protocol(this);
		
	}
}
