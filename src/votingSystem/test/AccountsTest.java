package votingSystem.test;

import java.math.BigInteger;
import java.security.SecureRandom;
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
		String[] names = (String[]) Tools.ReadObjectFromFile(Constants.VOTERS_FILENAME);
		Map<String, String> passwords = (Map<String, String>) Tools.ReadObjectFromFile(Constants.PASSWORDS_FILENAME);
		String name = names[0];
		assertTrue(acc.verify(name, passwords.get(name)));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testVerifyFalse() {
		Accounts acc = new Accounts(false);
		String[] names = (String[]) Tools.ReadObjectFromFile(Constants.VOTERS_FILENAME);
		Map<String, String> passwords = (Map<String, String>)Tools.ReadObjectFromFile(Constants.PASSWORDS_FILENAME);
		String name = names[0];
		String password = passwords.get(name);
		String p = "";
		SecureRandom r = new SecureRandom();
		while (p == password) {
			p = new BigInteger(Constants.PASSWORD_LENGTH, r).toString(32);
		}
		assertTrue(!acc.verify(name, p));
	}
}
