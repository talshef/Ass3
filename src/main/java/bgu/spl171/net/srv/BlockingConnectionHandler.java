package bgu.spl171.net.srv;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;


import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.TFTP.Packet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private Connections<T> connections;
    private int id;
   
    
    
    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol,Connections<T> connections, int id) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.connections=connections;
        this.id=id;
    }
    

    @Override
    public void run() {

        try (Socket sock = this.sock) { //just for automatic closing
            int read;
          

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
            	
            	
            	
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
//                    if (response != null) {
//                        out.write(encdec.encode(response));
//                        out.flush();
//                    }
                }
            }
            close();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        this.connections.disconnect(this.id);
        sock.close();
    }

	@Override
	public void send(T msg) {
		
		if(msg!=null){
			try {
				System.out.println("opp send : "+((Packet)msg).GetOppcode());
				out.write(encdec.encode(msg));
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else System.out.println("null msg : ");
		
	}
	
	
		


}
