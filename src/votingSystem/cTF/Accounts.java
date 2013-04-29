package votingSystem.cTF;

import votingSystem.Constants;
import votingSystem.Tools;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Accounts{
/**
	 * Generates username and passwords and stores them in a file.
	 * Also used by the CTF to check whether a given password is valid for a given username.
	 */
	private final Map<String, String> userToPass; //in memory because not using database. keys need to be Strings
	private final SecureRandom random = new SecureRandom();
	private Map<String, Set<Group>> userToGroups;
	private Set<Group> activeGroups = Collections.newSetFromMap(new ConcurrentHashMap<Group,Boolean>());
	private Set<Group> selectedGroups = Collections.newSetFromMap(new ConcurrentHashMap<Group,Boolean>());
	
	/**
	 * 
	 * @param load - true reads user information from a file.
	 * false generates users with random names and random passwords
	 */
	@SuppressWarnings("unchecked")
	public Accounts(boolean load) {
		if (load) {
			userToPass = (Map<String, String>) Tools.ReadObjectFromFile(Constants.PASSWORDS_FILENAME);
			userToGroups = (Map<String, Set<Group>>) Tools.ReadObjectFromFile(Constants.GROUPS_FILENAME);
		} else {
			userToPass = new ConcurrentHashMap<String, String>();
			userToGroups = new ConcurrentHashMap<String, Set<Group>>();
			//Create a default group
			Group all = new Group("all");
			this.addGroup(all, new HashSet<String>());
			
			for(int i = 0; i < Constants.NUM_VOTERS; i++) {
				String username = new BigInteger(Constants.VOTER_NAME_LENGTH, random).toString(32);
				String pass = new BigInteger(Constants.PASSWORD_LENGTH, random).toString(32);
				Set<Group> userGroup = new HashSet<Group>();
				userGroup.add(all);
				userToGroups.put(username, userGroup);
				userToPass.put(username, pass);	
				if(Constants.DEBUG) System.out.println("USER: " + username +  " PASS: " + pass);
			}
			writeUsersToFile();
		}
	}
	
	/**
	 * Creates Accounts object with no users or groups.
	 */
	public Accounts() {
		userToPass = new ConcurrentHashMap<String, String>();
		userToGroups = new ConcurrentHashMap<String, Set<Group>>();
	}
	
	//For demonstration purposes only
	protected void printUsers() {
		for(Map.Entry<String, String> entry: userToPass.entrySet()) {
			System.out.println("Username: " + entry.getKey() + " Password: " + entry.getValue());
		}
	}
	
	private void writeUsersToFile() {
		Tools.WriteObjectToFile(userToPass, Constants.PASSWORDS_FILENAME);
		Tools.WriteObjectToFile(userToPass, Constants.GROUPS_FILENAME);
	}
	
	public boolean createUser(String user, Set<Group> groups, boolean writeToFile) {
		//Don't create duplicate users
		if (this.userToGroups.keySet().contains(user)) {
			return false;
		}
		String pass = new BigInteger(Constants.PASSWORD_LENGTH, random).toString(32);
		userToPass.put(user, pass);
		
		//only add active groups
		groups.retainAll(activeGroups); 
		Set<Group> groupsConcurrent = Collections.newSetFromMap(new ConcurrentHashMap<Group,Boolean>());
		groupsConcurrent.addAll(groups);
		userToGroups.put(user, groupsConcurrent);
		if (writeToFile) {
			writeUsersToFile();
		}
		return true;
	}
	
	/**
	 * Verifies the given username and password.
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean verify(String username, String password) {
		if (!userToPass.containsKey(username))
			return false;		
		boolean result = password.equals(userToPass.get(username));
		return result;
	}
	
	
// NEW STUFF TIM TIM TIM TIM
	public boolean verifyGroups(String username, Set<Group> eligibleGroups){
		eligibleGroups = new HashSet<Group>(eligibleGroups);
		eligibleGroups.retainAll(activeGroups);
		if (!userToGroups.containsKey(username)) return false;
		eligibleGroups.retainAll(userToGroups.get(username));
		return (eligibleGroups.size() > 0);
	}
	
	public boolean verifyGroup(String user, Group group) {
		return userToGroups.keySet().contains(user) 
				&&userToGroups.get(user).contains(group);
	}
	
	public boolean changePassword(String username, String password){
		
		//33-47 Symbols
		//48-57 Numbers
		//58-64 More symbols
		//65-90 Upper case
		//91-96 More symbols
		//97-122 Lower case
		//122-126 Final symbols
		
		int symbols = 0;
		int lowercase = 0;
		int uppercase = 0;
		int numbers = 0;
		
		//if the string is too short
		if(password.length() < 10){
			return false;
		}
		
		//run through all of the password characters, and count the number of uppercase vs lowercase vs numbers vs symbols
		for(int i = 0; i < password.length(); i++){
			int current = (int) password.charAt(i);
			
			//if not a legal character, return false
			if(current < 33 || current > 126){
				return false;
			}
			
			if(48 >= current && current <= 57){
				numbers++;
			}
			else if(current >=65 && current <=90){
				uppercase++;
			}
			else if(current >= 97 && current <= 122){
				lowercase++;
			}
			else{
				symbols++;
			}
			
		}
		
		//if the password does not have at least one of the subsets, don't allow the change
		if(numbers == 0 || uppercase == 0 || lowercase == 0 || symbols == 0){
			return false;
		}
		userToPass.put(username, password);
		return true;
	}
	
	/**
	 * Return the groups associated with a given username
	 * @param username
	 * @return
	 */
	public Set<Group> getGroups(String username){
		return userToGroups.get(username);
	}
	
	/**
	 * Export the ability to
	 * @param username
	 * @param group
	 */
	public void addGroupsToUser(String username, Set<Group> groups){
		groups = new HashSet<Group>(groups);
		groups.retainAll(activeGroups);
		userToGroups.get(username).addAll(groups);
	}
	
	public String[] getNames() {
		return (String []) userToPass.keySet().toArray(new String[userToPass.size()]);
	}
	
	public void deleteGroupsFromUser(String username, Set<Group> groups) {
		groups = new HashSet<Group>(groups);
		groups.retainAll(activeGroups);
		userToGroups.get(username).removeAll(groups);
	}
	
	
	public void addGroup(Group group, Set<String> usernames) {
		activeGroups.add(group);
		usernames = new HashSet<String>(usernames);
		usernames.retainAll(userToPass.keySet());
		for(String user : usernames) {
			userToGroups.get(user).add(group);
		}
	}
	
	public void deleteGroupAll(Group g) {
		for(Map.Entry<String, Set<Group>> entry: userToGroups.entrySet()) {
			entry.getValue().remove(g);
		}
		activeGroups.remove(g);
	}
	
	public void deleteGroupFromUsers(Set<String> users, Group g) {
		users = new HashSet<String>(users);
		users.retainAll(userToGroups.entrySet());
		for (String u: users) {
			userToGroups.get(u).remove(g);
		}
	}
	
	
	public Set<Group> getActiveGroups() {
		return Collections.unmodifiableSet(activeGroups);
	}

	public void deleteUser(String user) {
		userToPass.remove(user);
		userToGroups.remove(user);
	}
	
	public Set<String> getUsersInGroups(Set<Group> groups) {
		groups = new HashSet<Group>(groups);
		groups.retainAll(activeGroups);
		Set<String> eligibleUsers = new HashSet<String>();
		for (Map.Entry<String, Set<Group>> entry: userToGroups.entrySet()) {
			Set<Group> interX = new HashSet<Group>(groups);
			interX.retainAll(entry.getValue());
			if (interX.size() > 0) {
				eligibleUsers.add(entry.getKey());
			}
		}
		return eligibleUsers;
	}
	
	public void setSelectedGroup(Set<Group> selected) {
		selectedGroups.clear();
		selectedGroups.addAll(selected);
	}
	
	public Set<Group> getSelectedGroup() {
		return selectedGroups;
	}
}
