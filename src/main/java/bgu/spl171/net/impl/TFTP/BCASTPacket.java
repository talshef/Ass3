package bgu.spl171.net.impl.TFTP;

public class BCASTPacket extends Packet {
	byte state;
	String fileName;
	
	public BCASTPacket(short oppcode,byte state,String fileName) {
		super(oppcode);
		this.state=state;
		this.fileName=fileName;
	}
	
	public byte GetState(){
		return this.state;
	}
	
	public String GetFileName(){
		return this.fileName;
	}
}
