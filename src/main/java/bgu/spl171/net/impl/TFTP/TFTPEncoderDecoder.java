package bgu.spl171.net.impl.TFTP;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import bgu.spl171.net.api.MessageEncoderDecoder;

public class TFTPEncoderDecoder implements MessageEncoderDecoder<Packet> {
	
	private int state=0;
	private byte[] oppcodebyte = new byte[2];
	private byte[] bytes = new byte[1 << 10]; //start with 1k
	private int len = 0;
	private short tempShort1=0;
	private short tempShort2=0;
	private String tempString;
	private short oppcode;
	
	@Override
	public Packet decodeNextByte(byte nextByte) {
		if(state<2){
			this.oppcodebyte[state]=nextByte;
			if(state==1) this.oppcode=bytesToShort(this.oppcodebyte);
			state++;
		}
		else{
			switch(this.oppcode){
			case 1:
			case 2:
					if(nextByte=='\0'){
						Packet result=new RQPacket(this.oppcode, new String(this.bytes, 0, this.len, StandardCharsets.UTF_8));
						this.len=0;
						this.oppcode=0;
						return result;
					}
					else{
						pushByte(nextByte);
						return null;
					}
			case 3:
					
				
			case 4:
					if(this.len==1){
						pushByte(nextByte);
						Packet result=new ACKPacket(this.oppcode, bytesToShort(Arrays.copyOfRange(this.bytes, 0, this.len-1)));
						this.len=0;
						this.oppcode=0;
						return result;
					}
					else{
						pushByte(nextByte);
						return null;
					}
			
			case 5:
					if(this.len==1){
						pushByte(nextByte);
						this.tempShort1=bytesToShort(Arrays.copyOfRange(this.bytes, 0, this.len-1));
					}
					else if(this.len==3){
						pushByte(nextByte);
						this.tempString= new String(this.bytes,2,this.len-1);
					}
					
			
					
			
			
			}
			
			
			
			
			
		}
		return null;
	}

	@Override
	public byte[] encode(Packet message) {
		// TODO Auto-generated method stub
		return message.toBytes();
	}
   
	
	   private void pushByte(byte nextByte) {
	        if (this.len >= this.bytes.length) {
	            this.bytes = Arrays.copyOf(this.bytes, this.len * 2);
	        }

	        this.bytes[this.len++] = nextByte;
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
