package votingSystem.cTF;

import java.util.List;

/**
 * Maintains all of the Elections
 * @author test
 *
 */
public class CTF {
	private List<Election> elections;
	
	
	public CTF() {
		new Protocol(this);
	}
}
