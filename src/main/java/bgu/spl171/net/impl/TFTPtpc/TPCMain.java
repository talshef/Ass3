package bgu.spl171.net.impl.TFTPtpc;

import bgu.spl171.net.impl.TFTP.TFTPEncoderDecoder;
import bgu.spl171.net.impl.TFTP.TFTPProtocol;
import bgu.spl171.net.impl.newsfeed.NewsFeed;
import bgu.spl171.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl171.net.impl.rci.RemoteCommandInvocationProtocol;
import bgu.spl171.net.srv.Server;
import bgu.spl171.net.impl.TFTP.*;

public class TPCMain {
	 public static void main(String[] args) {

	// you can use any server... 
//	        Server.fixedThreadPool(
//	                8,
//	                7777, //port
//	                () -> new RemoteCommandInvocationProtocol<>(feed), //protocol factory
//	                ObjectEncoderDecoder::new //message encoder decoder factory
//	        ).serve();

	        Server.threadPerClient(Integer.parseInt(args[0]), ()-> new TFTPProtocol() , ()->new TFTPEncoderDecoder()).serve();

	    }
}
