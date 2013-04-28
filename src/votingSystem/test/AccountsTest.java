package votingSystem.test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
		Map<String, String> passwords = (Map<String, String>) Tools.ReadObjectFromFile(Constants.PASSWORDS_FILENAME);
		String name = (String) passwords.keySet().toArray()[0]; 
		assertTrue(acc.verify(name, passwords.get(name)));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testVerifyFalse() {
		Accounts acc = new Accounts(false);
		Map<String, String> passwords = (Map<String, String>)Tools.ReadObjectFromFile(Constants.PASSWORDS_FILENAME);
		String name = (String) passwords.keySet().toArray()[0]; 
		String password = passwords.get(name);
		String p = "";
		SecureRandom r = new SecureRandom();
		while (p == password) {
			p = new BigInteger(Constants.PASSWORD_LENGTH, r).toString(32);
		}
		assertTrue(!acc.verify(name, p));
	}
	
	@Test
	public void testGroups() {
		Group national = new Group("National");
		Group state = new Group("State");
		Group local = new Group("local");
		Group fake = new Group("fake");
		
		Accounts acc = new Accounts();
		acc.addGroup(local, new HashSet<String>());
		acc.addGroup(state, new HashSet<String>());
		acc.addGroup(national, new HashSet<String>());
		//Don't add fake
		
		String userA = "userA";
		String userB = "userB";
		String userC = "userC";
		String userFake = "fake";
		
		Set<Group> userAGroups = 
				new HashSet<Group>(Arrays.asList(new Group[] {national, fake}));
		acc.createUser(userA, userAGroups, false);
		assertTrue(acc.verifyGroup(userA, national));
		assertFalse(acc.verifyGroup(userA, fake));
		
		Set<Group> userBGroups = 
				new HashSet<Group>(Arrays.asList(new Group[] {national, state}));
		acc.createUser(userB, userBGroups, false);
		
		Set<Group> userCGroups = 
				new HashSet<Group>(Arrays.asList(new Group[] {national, state, local}));
		acc.createUser(userC, userCGroups, false);
		
		Set<Group> moreUserAGroups = 
				new HashSet<Group>(Arrays.asList(new Group[] {local, fake}));
		acc.addGroupsToUser(userA, moreUserAGroups);
		assertTrue(acc.verifyGroup(userA, local));
		assertFalse(acc.verifyGroup(userA, fake));
		
		assertTrue(acc.verifyGroup(userB, national));
		Set<Group> userBGroupsToDelete = 
				new HashSet<Group>(Arrays.asList(new Group[] {national, fake}));
		acc.deleteGroupsFromUser(userB, userBGroupsToDelete);
		assertFalse(acc.verifyGroup(userB, national));
		
		
		Group school = new Group("school");
		Set<String> usersInSchool = 
				new HashSet<String>(Arrays.asList(new String[] {userA, userB, userFake}));
		acc.addGroup(school, usersInSchool);
		assertTrue(acc.verifyGroup(userB, school));
		assertFalse(acc.verifyGroup(userFake, school));
		
		
		acc.deleteGroupAll(school);
		assertFalse(acc.verifyGroup(userB, school));
		
		assertTrue(acc.verifyGroup(userB, state));
		Set<String> userToRemove = 
				new HashSet<String>(Arrays.asList(new String[] {userB, userFake}));
		acc.deleteGroupFromUsers(userToRemove, state);
		assertFalse(acc.verifyGroup(userB, national));
		
		acc.deleteUser(userA);
		acc.deleteUser(userB);
		String userD = "userD";
		String userE = "userE";
		Set<Group> userDGroups = 
				new HashSet<Group>(Arrays.asList(new Group[] {state}));
		acc.createUser(userD, userDGroups, false);
		Set<Group> userEGroups = 
				new HashSet<Group>(Arrays.asList(new Group[] {national}));
		acc.createUser(userE, userEGroups, false);
		Set<Group> votingGroups = 
				new HashSet<Group>(Arrays.asList(new Group[] {national, state}));
		Set<String> expectedUsers = 
				new HashSet<String>(Arrays.asList(new String[] {userC, userD, userE}));
		assertEquals(expectedUsers, acc.getUsersInGroups(votingGroups));
	}
}
