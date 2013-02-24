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
		assertTrue(passwords.verify(username, pass.getBytes()));
	}
	
	@Test	
	public void testVerifyFalseWrongPass() {
		String username = "u2";
		passwords.generate(username);
		assertFalse(passwords.verify(username, "hi mom".getBytes()));
	}

	@Test	
	public void testVerifyFalseWrongUser() {
		String username = "u3";
		assertFalse(passwords.verify(username, "hi mom".getBytes()));
	}

	@Test	
	public void testBackup() {
		System.out.println("backup");
		String username = "u4";
		String pass = passwords.generate(username);
		passwords.backup();
		Passwords passwords2 = new Passwords(filename);
		byte [] pb = pass.getBytes();
		boolean result = passwords2.verify(username, pb);
		System.out.println(result);
		assertTrue(result);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		new File(filename).delete();
	}
}
