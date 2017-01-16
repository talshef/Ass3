package bgu.spl171.net.impl.TFTP;

public class Packet {
	private short oppcode;
	
	public Packet(short oppcode) {
		this.oppcode=oppcode;
	}
	
	public short GetOppcode(){
		return this.oppcode;
	}
	

	public byte[] toBytes(){
		return TFTPEncoderDecoder.shortToBytes(oppcode);
	}
	
	public static byte[] mergeBytes(byte[] a,byte[] b){
		byte[] result=new byte[a.length+b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
		
	}
}

