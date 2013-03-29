package votingSystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class Tools {

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
		}
		return obj;
	}
	
}
