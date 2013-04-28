package votingSystem.cTF;

public class Group {
  private String groupName;
	
	
	public Group(String name){
		
		groupName = name;
	}
	
	public String getName(){
		return groupName;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Group))
            return false;
		return ((Group)obj).getName().equals(groupName);
	}
	
	public int hashCode() {
		return groupName.hashCode();
	}
}
