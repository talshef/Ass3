package bgu.spl171.net.impl.TFTP;

public class BCASTPacket extends Packet {
	private byte state;
	private String fileName;
	
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
	
	public byte[] toBytes(){
		byte[] stateByte=new byte[1];
		stateByte[0]=state;
		byte[] temp=mergeBytes(super.toBytes(), stateByte);
		return mergeBytes(temp, (fileName+'\0').getBytes());
		
	}
}
