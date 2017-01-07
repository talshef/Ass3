package bgu.spl171.net.impl.TFTP;

public class Packet {
	short oppcode;
	
	public Packet(short oppcode) {
		this.oppcode=oppcode;
	}
	
	public short GetOppcode(){
		return this.oppcode;
	}
	
}

