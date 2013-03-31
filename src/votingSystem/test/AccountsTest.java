package votingSystem.test;

import votingSystem.cTF.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class AccountsTest {
	@Test
	public void testVerifyTrue() {
		Accounts acc = new Accounts(false);
		assertTrue(acc.verify("", ""));
	}
}
