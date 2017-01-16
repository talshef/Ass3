package bgu.spl171.net.impl.TFTP;

public class ERRORPacket extends Packet {
	private short errorCode;
	private String msg;
	
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
	

	
	public byte[] toBytes(){
		byte[] temp=mergeBytes(super.toBytes(), TFTPEncoderDecoder.shortToBytes(errorCode));
		return mergeBytes(temp, msg.getBytes());
	}
}
