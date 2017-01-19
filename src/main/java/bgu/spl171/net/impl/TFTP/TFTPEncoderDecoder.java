package bgu.spl171.net.impl.TFTP;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import bgu.spl171.net.api.MessageEncoderDecoder;

public class TFTPEncoderDecoder<T> implements MessageEncoderDecoder<Packet> {
	
	private int state=0;

	private byte[] bytes = new byte[1 << 10]; //start with 1k
	private byte[] shortBytes=new byte[2];
	private int len = 0;
	private short blockNum=-1;
	private short packetSize=-1;
	private short oppcode=-1;
	private int shortIndex=0;
	private short byteLeft=-1;
	
	@Override
	public Packet decodeNextByte(byte nextByte) {
		if(oppcode==-1){
			oppcode=getShort(nextByte);
			if(this.oppcode==6||this.oppcode==10){
				Packet temp=new Packet(oppcode);
				this.oppcode=-1;
				return temp;
			}
		}
		else{
			switch(this.oppcode){
			case 1:
					return stringPakect(nextByte);
			case 2:
					return stringPakect(nextByte);
			case 3:
					return dataPakect(nextByte);
				
			case 4:
					return ackPakect(nextByte);
			case 5:
					return errorPacket(nextByte);
			case 7:
					return stringPakect(nextByte);
			case 8:
					return stringPakect(nextByte);
					
			 default:
				 return new ERRORPacket((short)5, (short)4, "Unkonown Oppcode");
			}
		}
		return null;
	}

	@Override
	public byte[] encode(Packet message) {
		return message.toBytes();
	}
	
	
	private short getShort(byte nextByte){
		shortBytes[shortIndex]=nextByte;
		if (shortIndex==1) {
			shortIndex=0;
			return bytesToShort(shortBytes);
		}
		shortIndex++;
		return -1;
		
	}
	
	
	
	private Packet dataPakect(byte nextByte){
		Packet result=null;
		if (byteLeft==-1) {
			byteLeft=getShort(nextByte);
		}
		else{
			if(blockNum==-1){
				blockNum=getShort(nextByte);
				if(byteLeft==0&&blockNum!=-1){
					byte[] data=new byte[0];
					result=new DATAPacket(oppcode, (short)(this.len), blockNum,data );
					this.len=0;
					this.oppcode=-1;
					this.blockNum=-1;
					this.byteLeft=-1;
				}
			}
			else{
				if(byteLeft==1) {
					
					pushByte(nextByte);
					
					result=new DATAPacket(oppcode, (short)(this.len), blockNum,getData() );
					this.len=0;
					this.oppcode=-1;
					this.blockNum=-1;
					this.byteLeft=-1;
					
				}
				else{
					pushByte(nextByte);
					byteLeft--;
				}
			}
		}
		return result;
			
		
	}
	
	private Packet errorPacket(byte nextByte){
		Packet result=null;
		if(blockNum==-1) blockNum=getShort(nextByte);
		else{
			if(nextByte=='\0'){
				result= new ERRORPacket(oppcode, blockNum, new String(this.bytes, 0, this.len, StandardCharsets.UTF_8));
				this.len=0;
				this.oppcode=-1;
				this.blockNum=-1;
			}
			else{
				pushByte(nextByte);
			}
		}
		
		return result;
	}
	
	
	
	
	private Packet stringPakect(byte nextByte){
		if(nextByte=='\0'){
			Packet result=new RQPacket(this.oppcode, new String(this.bytes, 0, this.len, StandardCharsets.UTF_8));
			this.len=0;
			this.oppcode=-1;
			return result;
		}
		else{
			pushByte(nextByte);
			return null;
		}
	}
	private Packet ackPakect(byte nextByte){
		Packet result=null;
		blockNum=getShort(nextByte);
		if (blockNum!=-1) {
			result=new ACKPacket(this.oppcode, blockNum);
			this.blockNum=-1;
			this.oppcode=-1;
		}
		return result;
		
	}
   
	
	private void pushByte(byte nextByte) {
		if (this.len >= this.bytes.length) {
			this.bytes = Arrays.copyOf(this.bytes, this.len * 2);
		}
		
		this.bytes[this.len++] = nextByte;
	}

	   
	   
	private byte[] getData(){
		//TODO check index
		return Arrays.copyOfRange(this.bytes, 0, len);
	}
	
	public static short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
	
    public static byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

}
