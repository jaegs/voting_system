package votingSystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MessageTemplate {
	private SortedMap<Integer, String> fieldPositions = new TreeMap<Integer, String>();
	private Map<String, Integer> fieldLengths = new HashMap<String, Integer>();
	private int endPos = 0;
	
	public void addField(String name, int length) {
		endPos += length;
		fieldPositions.put(endPos - 1, name);
		fieldLengths.put(name, length);
	}
	
	public Set<Integer> indexSet() {
		return fieldPositions.keySet();
	}
	
	public String getfieldName(int index) {
		return fieldPositions.get(index);
	}
	
	public Integer getFieldLength(String fieldName) {
		return fieldLengths.get(fieldName);
	}
	
	public static Map<Operation, MessageTemplate> loadTemplates(){
		/**
		 * Specify a template in a csv file
		 * templateName1,fieldName1,length1,fieldName2,length2,...
		 * templateName2,fieldName1,length1,fieldName2,length2,...
		 * ...
		 */
		Map<Operation, MessageTemplate> templates = new HashMap<Operation, MessageTemplate>();
		try {
			String filename = Constants.TEMPLATE_FILENAME;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
			   String[] columns = line.split(",");
			   String templateName = columns[0];
			   MessageTemplate template = new MessageTemplate();
			   for (int i = 1; i < columns.length - 1; i+=2){
				   String name = columns[i].replaceAll("[\\r\\n]+\\s", "");
				   int length = Integer.parseInt(columns[i+1].replaceAll("[\\r\\n]+\\s", ""));
				   template.addField(name, length);
			   }
			   Operation op = Operation.valueOf(templateName);
			   templates.put(op, template);
			}
			br.close();
		} catch (IOException e) {
			System.err.println("Error reading template file");
			e.printStackTrace();
		}
		return templates;
	}
}
