package bgu.spl171.net.impl.TFTP;

public class RQPacket extends Packet {
	private String s;
	

	public RQPacket(short oppcode, String s) {
		super(oppcode);
		this.s=s;
	}
	
	public String GetString(){
		return this.s;
	}
	
	public byte[] toBytes(){
		return mergeBytes(super.toBytes(),(s+'\0').getBytes());
	}
}
