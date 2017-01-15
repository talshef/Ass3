package bgu.spl171.net.impl.TFTP;

public class ERRORPacket extends Packet {
	short errorCode;
	String msg;
	public ERRORPacket(short oppcode,short errorCode,String msg) {
		super(oppcode);
		this.errorCode=errorCode;
		this.msg=msg;
	}
	
	public short GetErrorCode(){
		return this.errorCode;
	}
	public String GetMsg(){
		return this.msg;
	}
	

}
