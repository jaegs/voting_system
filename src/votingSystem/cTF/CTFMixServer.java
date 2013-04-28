package votingSystem.cTF;

public class CTFMixServer extends mixNet.Server{
	private final CTF ctf;
	private final Protocol protocol;
	
	public CTFMixServer(CTF ctf) {
		super();
		this.ctf = ctf;
		this.protocol = new Protocol(ctf);
	}
	@Override
	public byte[] processMessage(byte[] payload) {
		return protocol.processMessage(payload);
	}
}
