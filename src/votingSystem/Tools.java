package votingSystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Utility class for converting byte arrays to/from objects and reading/writing from files.
 * @author benjamin
 *
 */
public class Tools {
	private static SecureRandom random = new SecureRandom();

	/**
	 * Takes a Serializable object and converts it to a byte array.
	 * @param obj
	 * @return
	 */
	public static byte[] ObjectToByteArray(Object obj) {
		byte[] arr = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = null;
			try {
			  out = new ObjectOutputStream(bos);   
			  out.writeObject(obj);
			  arr = bos.toByteArray();
			} finally {
			  out.close();
			  bos.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return arr;
	}
	
	/**
	 * Takes a byte array representing a serialized object and converts to an object
	 * @param arr
	 * @return
	 */
	public static Object ByteArrayToObject(byte[] arr){
		Object obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(arr);
			ObjectInput in = null;
			try {
			  in = new ObjectInputStream(bis);
			  obj = in.readObject(); 
			} finally {
			  bis.close();
			  in.close();
			}
		} catch(IOException e1) {
			e1.printStackTrace();
		} catch(ClassNotFoundException e2) {
			e2.printStackTrace();
		} catch(NullPointerException e3) {
			return null;
		}
		return obj;
	}
	
	/**
	 * Takes an object that implements Serializable and writes it to a file.
	 * @param obj
	 * @param filename
	 */
	public static void WriteObjectToFile(Object obj, String filename) {
		try {
			File file = new File(filename);
			file.createNewFile();
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(obj);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Reads a file containing a serialized object and converts it to an object
	 * @param filename
	 * @return
	 */
	public static Object ReadObjectFromFile(String filename) {
		File file = new File(filename);
		Object obj = null;
		try {
			if (file.exists()) {
				ObjectInputStream ois = new ObjectInputStream(
						new FileInputStream(file));
				obj = ois.readObject();
				ois.close();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();			
		}
		return obj;
	}
	
	/**
	 * http://stackoverflow.com/questions/8700278/java-securerandom-internal-state
	 * @param input
	 * @return
	 */
	public static int[] shuffle(int[] input) {
		int[] output = Arrays.copyOf(input, input.length);
		for(int i = input.length-1; i>0; i--) {
			int j = random.nextInt(i+1);
			int temp = output[i];
			output[i] = output[j];
			output[j] = temp;
		}
		return output;
	}
	
	public static void printByteArray(byte[] in) {
		System.out.println(Arrays.toString(in));
	}
}
