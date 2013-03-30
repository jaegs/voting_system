package votingSystem.cTF.test;

import java.io.File;
import java.util.Arrays;

import votingSystem.cTF.*;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class PasswordsTest {
	static Passwords passwords = null;
	static String filename = "bin/votingSystem/cTF/test/passwords.ser";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		passwords = new Passwords(filename);
	}

	@Test
	public void testVerifyTrue() {
		String username = "u1";
		String pass = passwords.generate(username);
		assertTrue(passwords.verify(username, pass));
	}
	
	@Test	
	public void testVerifyFalseWrongPass() {
		String username = "u2";
		passwords.generate(username);
		assertFalse(passwords.verify(username, "hi mom"));
	}

	@Test	
	public void testVerifyFalseWrongUser() {
		String username = "u3";
		assertFalse(passwords.verify(username, "hi mom"));
	}

	@Test	
	public void testBackup() {
		String username = "u4";
		String pass = passwords.generate(username);
		passwords.backup();
		Passwords passwords2 = new Passwords(filename);
		boolean result = passwords2.verify(username, pass);
		assertTrue(result);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		new File(filename).delete();
	}
}
