package bgu.spl171.net.impl.TFTPreactor;

import bgu.spl171.net.impl.TFTP.TFTPEncoderDecoder;
import bgu.spl171.net.impl.TFTP.TFTPProtocol;
import bgu.spl171.net.srv.Server;

public class ReactorMain {
	 public static void main(String[] args) {


	        Server.reactor(5,Integer.parseInt(args[0]), ()-> new TFTPProtocol() , ()->new TFTPEncoderDecoder()).serve();

	    }
}
