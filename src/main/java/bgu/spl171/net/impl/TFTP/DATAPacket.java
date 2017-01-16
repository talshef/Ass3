package bgu.spl171.net.impl.TFTP;

public class DATAPacket extends Packet {
	private short packetSize;
	private short blockNum;
	private byte[] data;
	
	public DATAPacket(short oppcode,short packetSize,short blockNum,byte[] data) {
		super(oppcode);
		this.packetSize=packetSize;
		this.blockNum=blockNum;
		this.data=data;
	}
	
	public short GetPacketSize(){
		return this.packetSize;
	}
	
	public short GetBlockNum(){
		return this.blockNum;
	}
	
	public byte[] GetData(){
		return this.data;
	}

	
	public byte[] tobytes(){
		byte[] temp=mergeBytes(super.toBytes(), TFTPEncoderDecoder.shortToBytes(packetSize));
		byte[] temp1=mergeBytes(temp, TFTPEncoderDecoder.shortToBytes(blockNum));
		return mergeBytes(temp1, data);
	}
}
