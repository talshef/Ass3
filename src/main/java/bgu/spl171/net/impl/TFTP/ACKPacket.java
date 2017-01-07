package bgu.spl171.net.impl.TFTP;

public class ACKPacket extends Packet {
	short blockNum;
	
	public ACKPacket(short oppcode,short blockNum) {
		super(oppcode);
		this.blockNum=blockNum;
	}
	
	public short GetBlockNum(){
		return this.blockNum;
	}
}
