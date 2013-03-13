package votingSystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class MessageMap {
	private final MessageTemplate template;
	private final Map<String, byte[]> data = new HashMap<String, byte[]>();

	public MessageMap(MessageTemplate template) {
		this.template = template;
	}
	
	public void set(String fieldName, byte[] fieldValue) {
		if (template.getFieldLength(fieldName) != fieldValue.length) {
			throw new IllegalArgumentException("Field Value is wrong length");
		}
		data.put(fieldName, fieldValue);
	}
	
	public byte[] get(String fieldName) {
		return data.get(fieldName);
	}
	
	public MessageTemplate getTemplate() {
		return template;
	}
	
	public static MessageMap fromByteArray(byte[] msg, MessageTemplate template) {
		MessageMap mm = new MessageMap(template);
		int startPos = 0;
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
		MessageTemplate template = getTemplate();
		for (Integer endPos : template.indexSet()) {
			String fieldName = template.getfieldName(endPos);
			byte[] fieldValue = get(fieldName);
			try {
				bos.write(fieldValue);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bos.toByteArray();
	}
	
	
	
}
