package bgu.spl171.net.impl.TFTP;

import bgu.spl171.net.api.MessageEncoderDecoder;

public class TFTPEncoderDecoder implements MessageEncoderDecoder<Packet> {

	@Override
	public Packet decodeNextByte(byte nextByte) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] encode(Packet message) {
		// TODO Auto-generated method stub
		return null;
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
