package votingSystem.cTF;

public class Group {
  private String groupName;
	
	
	public Group(String name){
		
		groupName = name;
	}
	
	public String getName(){
		return groupName;
	}
	
	public boolean equals(Group other){
		
		return other.getName().equals(groupName);
	}
}
