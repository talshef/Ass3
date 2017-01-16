package bgu.spl171.net.impl.TFTP;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.TFTP.Packet;

public class TFTPProtocol implements BidiMessagingProtocol<Packet> {
	private static Map<Integer,String> users=new HashMap<Integer, String>();
	private static ConcurrentLinkedDeque<String> files=new ConcurrentLinkedDeque<String>();
	
	private FileOutputStream file=null;
	private String filename=null;
	private String filesFolder="/Files/";
	private Connections<Packet> connections;
	private int id;
	private LinkedList<Packet> sendQueue=new LinkedList<Packet>();
	private short blockCount;
	private int ackState;
	private boolean shouldTerminate;
	
	@Override
	public void start(int connectionId, Connections<Packet> connections) {
		this.id=id;
		this.connections=connections;
		
	}

	@Override
	public void process(Packet message) {
		
		switch(message.GetOppcode()){
			
		case 1: ReadRequest((RQPacket)message);
				break;
				
		case 2: WriteRequest((RQPacket)message);
				break;
				
		case 3: DataProcess((DATAPacket)message);
				break;
				
		case 4: ACKProcess((ACKPacket)message);
				break;
		
		case 5: ErrorProcess((ERRORPacket)message);
				break;
				
		case 6: DirRequest(message);
				break;
				
		case 7: LogRequest((RQPacket)message);
				break;
				
		case 8: DelRequest((RQPacket)message);
				break;
				
		case 9: BcastProcess((BCASTPacket)message);
				break;
				
		case 10: DisProcess(message);
				break;
		
		}
		
	}

	private void ReadRequest(RQPacket message){
		String s=this.filesFolder+message.GetString();
		
		
			 try {
				File f=new File(s);
				if(!f.exists()) this.connections.send(this.id, new ERRORPacket((short)5, (short)1, "File not found"));
				else{
					byte[] byteArray= Files.readAllBytes(f.toPath());
					this.ackState=1; //ACK of sending file
					this.blockCount=1;
					ByteToPacket(byteArray);
				}
			 } catch (IOException e) {
			
				e.printStackTrace();
			}

		
	}
		
	private void WriteRequest(RQPacket message){
		String s=this.filesFolder+message.GetString();
		this.filename=s;
		try {
			File f=new File(s);
			if(f.exists()) this.connections.send(this.id, new ERRORPacket((short)5, (short)5, "File already exists"));
			else{
				f.createNewFile();
				this.file=new FileOutputStream(f,true);
				this.connections.send(this.id, new ACKPacket((short)4, (short)0));
			}
		 } catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void DataProcess(DATAPacket message){
		try {
			this.file.write(message.GetData());
			this.file.flush();
			this.connections.send(this.id, new ACKPacket((short)4, (short)message.GetBlockNum()));
			if(message.GetPacketSize()<512){
				this.file.close();
				files.addLast(this.filename);
				this.file=null;
				this.filename=null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void ACKProcess(ACKPacket message){
		switch(this.ackState){
			case 1: if(this.blockCount!=message.blockNum) this.connections.send(this.id,new ERRORPacket((short)5, (short)1, "The ACK block not much to Data block"));
					else{
						if(!this.sendQueue.isEmpty()){
							this.connections.send(this.id, this.sendQueue.removeFirst());
							this.blockCount++;
						}
						else this.blockCount=0;
					}
			
		
		}
	}
	
	private void ErrorProcess(ERRORPacket message){
		
	}
	
	private void DirRequest(Packet message){
		
	}
	
	private void LogRequest(RQPacket message){
		
	}
	
	private void DelRequest(RQPacket message){
		
	}
	
	private void BcastProcess(BCASTPacket message){
		
	}
	
	private void DisProcess(Packet message){
		
	}
	
	private void ByteToPacket(byte[] array){
		
	}
	
	@Override
	public boolean shouldTerminate() {
		return this.shouldTerminate;
	}

}
