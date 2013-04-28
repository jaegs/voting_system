package mixNet;

import java.security.Key;
import java.security.PublicKey;


public class Message {
	public byte[] payload;
	public int sendAddr; 
	public byte[] response;
	public PublicKey senderKey;
	public Message(byte[] payload, int address) {
		this.payload = payload;
		this.sendAddr = address;
	}
	public Message(byte[] payload, int address, PublicKey senderKey, byte[] response) {
		this.response = response;
		this.payload = payload;
		this.sendAddr = address;
		this.senderKey = senderKey;
	}
	public static class Response {
		public Response(Key respKey, int address) {
			this.respAddr = address;
			this.respKey = respKey;
		}
		public Response(Key respKey, byte[] payload, int address) {
			this.respKey = respKey;
			this.respPayload = payload;
			this.respAddr = address;
		}
		public Key respKey;
		public byte[] respPayload;
		public int respAddr;
		
	}
}
