package votingSystem.test;

import votingSystem.CheckSum;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class CheckSumTest {
	@Test
	public void testCheckSum() {
		byte[] msg = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k'};		
		byte[] checkSum = CheckSum.getCheckSum(msg);
		assertTrue(CheckSum.checkCheckSum(checkSum, msg));
	}
	@Test
	public void testFalseCheckSum() {
		byte[] msg = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k'};		
		byte[] checkSum = {'r', 'a', 'w', 'r'};
		assertTrue(!CheckSum.checkCheckSum(checkSum, msg));
	}
}
