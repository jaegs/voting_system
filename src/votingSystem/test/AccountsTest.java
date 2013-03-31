package votingSystem.test;

import java.util.List;
import java.util.Map;

import votingSystem.Constants;
import votingSystem.Tools;
import votingSystem.cTF.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class AccountsTest {
	@Test
	@SuppressWarnings("unchecked")
	public void testVerifyTrue() {
		Accounts acc = new Accounts(false);
		List<String> names = (List<String>)Tools.ReadObjectFromFile(Constants.VOTERS_FILENAME);
		Map<String, String> passwords = (Map<String, String>)Tools.ReadObjectFromFile(Constants.PASSWORDS_FILENAME);
		String name = names.get(0);
		assertTrue(acc.verify(name, passwords.get(name)));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testVerifyFalse() {
		Accounts acc = new Accounts(false);
		List<String> names = (List<String>)Tools.ReadObjectFromFile(Constants.VOTERS_FILENAME);
		Map<String, String> passwords = (Map<String, String>)Tools.ReadObjectFromFile(Constants.PASSWORDS_FILENAME);
		String name = names.get(0);
		assertTrue(!acc.verify(name, ""));
	}
}
