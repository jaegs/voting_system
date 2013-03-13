package votingSystem.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import votingSystem.MessageMap;
import votingSystem.MessageTemplate;

public class MessageMapTest {
	static MessageTemplate mt;

	@BeforeClass
	public static void setUpBeforeClass() {
		mt = new MessageTemplate();
		mt.addField("one", 3);
		mt.addField("two", 4);
		mt.addField("three", 5);
	}
	
	@Test
	public void testGetFieldName() {
		assertEquals("two", mt.getfieldName(6));
	}
	
	@Test
	public void testfromByteArray() {
		byte[] msg = {1,2,3,4,5,6,7,8,9,10,11,12};
		MessageMap mm = MessageMap.fromByteArray(msg, mt);
		byte[] two = {4,5,6,7};
		byte[] three = {8,9,10,11,12};
		assertTrue(Arrays.equals(mm.get("two"), two));
		assertTrue(Arrays.equals(mm.get("three"), three));
	}
	
	@Test
	public void testToByteArray() {
		byte[] msg = {1,2,3,4,5,6,7,8,9,10,11,12};
		MessageMap mm = MessageMap.fromByteArray(msg, mt);
		byte[] msg2 = mm.toByteArray();
		assertTrue(Arrays.equals(msg, msg2));
	}
	
	@Test 
	public void testLoadTemplates() throws IOException {
		/*
		 * MessageTemplates.txt:
		 * verify,election,1,username,3,reddit,5
		 * vote,election,1,id,5,vote,1
		 */
		Map<String, MessageTemplate> templates = 
				MessageTemplate.loadTemplates("bin/votingSystem/test/MessageTemplates.txt");
		MessageTemplate vote = templates.get("vote");
		assertEquals(new Integer(5),vote.getFieldLength("id"));
	}

}
