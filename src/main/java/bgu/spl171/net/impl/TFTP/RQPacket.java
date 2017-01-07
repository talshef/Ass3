package bgu.spl171.net.impl.TFTP;

public class RQPacket extends Packet {
	String s;
	

	public RQPacket(short oppcode, String s) {
		super(oppcode);
		this.s=s;
	}
	
	public String GetString(){
		return this.s;
	}
}
