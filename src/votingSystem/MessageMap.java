package votingSystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import java.nio.ByteBuffer;

import votingSystem.cTF.Election;

public class MessageMap {
	private final Operation operation;
	private final MessageTemplate template;
	private final Map<String, byte[]> data = new HashMap<String, byte[]>();
	private static final Map<Operation, MessageTemplate> templates
		= MessageTemplate.loadTemplates();

	public MessageMap(Operation operation) {
		this.operation = operation;
		this.template = templates.get(operation);
	}
	
	public void set(String fieldName, byte[] fieldValue) {
		if (template.getFieldLength(fieldName) != fieldValue.length) {
			throw new IllegalArgumentException("Field Value is wrong length");
		}
		data.put(fieldName, fieldValue);
	}
	
	public void set(String fieldName, byte fieldValue) {
		data.put(fieldName, new byte[] {fieldValue});
	}
	
	public void set(String fieldName, boolean fieldValue) {
		if (fieldValue)
			set(fieldName, (byte) 1);
		else
			set(fieldName, (byte) 0);
	}
	
	public void set(String fieldName, int fieldValue) {
		set(fieldName, ByteBuffer.allocate(4).putInt(fieldValue).array());
	}
	
	public void set(String fieldName, String fieldValue) {
		set(fieldName, fieldValue.getBytes());
	}
	
	public byte[] get(String fieldName) {
		return data.get(fieldName);
	}
	
	public int getInt(String fieldname) {
		return ByteBuffer.wrap(get(fieldname)).getInt();
	}
	
	public String getString(String fieldname) {
		return new String(get(fieldname)); 
	}
	
	public MessageTemplate getTemplate() {
		return template;
	}
	
	public Operation getOperation() {
		return operation;
	}
	
	public static MessageMap fromByteArray(byte[] msg) {
		Operation op = Constants.OPERATION_VALUES[msg[0]];
		MessageMap mm = new MessageMap(op);
		MessageTemplate template = mm.getTemplate();
		mm.set("op", new byte[] {msg[0]});
		int startPos = 1; //op value is in position 0, response is 1;
		for (Integer endPos : template.indexSet()) {
			String fieldName = template.getfieldName(endPos);
			byte[] fieldValue = Arrays.copyOfRange(msg, startPos, endPos + 1);
			mm.set(fieldName, fieldValue);
			startPos = endPos + 1;
		}
		return mm;
	}
	
	public byte[] toByteArray() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			bos.write(get("op"));
			MessageTemplate template = getTemplate();
			for (Integer endPos : template.indexSet()) {
				String fieldName = template.getfieldName(endPos);
				byte[] fieldValue = get(fieldName);
				bos.write(fieldValue);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toByteArray();
	}
	
	
	
}
