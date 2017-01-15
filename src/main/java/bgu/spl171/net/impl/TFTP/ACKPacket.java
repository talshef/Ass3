package bgu.spl171.net.impl.TFTP;

import java.lang.reflect.Array;
import java.util.Arrays;

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
