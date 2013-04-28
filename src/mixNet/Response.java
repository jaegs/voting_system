package mixNet;

import java.io.Serializable;
import java.security.Key;

import javax.crypto.spec.IvParameterSpec;

public class Response implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Key respKey;
	public byte[] respPayload;
	public int respAddr;
	
	public Response(Key respKey, int address) {
		this.respAddr = address;
		this.respKey = respKey;
	}
	public Response(Key respKey, byte[] payload, int address) {
		this.respKey = respKey;
		this.respPayload = payload;
		this.respAddr = address;
	}
}
