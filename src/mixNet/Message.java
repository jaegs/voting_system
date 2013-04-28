package mixNet;

import java.io.Serializable;
import java.security.Key;
import java.security.PublicKey;


public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1111783159477103875L;
	public byte[] payload;
	public int sendAddr; 
	public byte[] response;
	public PublicKey senderKey;
	public Message(byte[] payload, int address) {
		this.payload = payload;
		this.sendAddr = address;
	}
	public Message(byte[] payload, byte[] resp) {
		this.payload = payload;
		this.response = resp;
	}
	public Message(byte[] payload, int address, PublicKey senderKey, byte[] response) {
		this.response = response;
		this.payload = payload;
		this.sendAddr = address;
		this.senderKey = senderKey;
	}
}
