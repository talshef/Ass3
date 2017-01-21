package bgu.spl171.net.impl.TFTP;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.TFTP.Packet;

public class TFTPProtocol<T> implements BidiMessagingProtocol<Packet> {
	
	private static Map<Integer,String> users=new HashMap<Integer, String>();
	private static ConcurrentLinkedDeque<String> tempfiles=new ConcurrentLinkedDeque<String>();
	
	private FileOutputStream file=null;
	private String filename=null;
	private String filesFolder=System.getProperty("user.dir")+"/Files/";
	private Connections<Packet> connections;
	private int id;
	private LinkedList<Packet> sendQueue=new LinkedList<Packet>();
	private short blockCount;
	private boolean shouldTerminate=false;
	private boolean loggedIn=false;
	private short dataNum;
	
	@Override
	public void start(int connectionId, Connections<Packet> connections) {
		this.id=connectionId;
		this.connections=connections;
		
	}

	@Override
	public void process(Packet message) {
		
		if(!loggedIn){
			switch(message.GetOppcode()){
			case 7: LogRequest((RQPacket)message);
					break;
			case 1:
			case 2:
			case 3:
			case 4:
			case 6:
			case 8:
			case 10:
				this.connections.send(this.id, new ERRORPacket((short)5, (short)6, "User not logged in"));
				break;
			default: this.connections.send(this.id, new ERRORPacket((short)5, (short)4, "Illegal TFTP opp"));
			}	
		}
		else{
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
					
			case 10: DisProcess(message);
					break;
			
			default:this.connections.send(this.id, new ERRORPacket((short)5, (short)4, "Illegal TFTP opp"));
			}
		}
		
	}

	private void ReadRequest(RQPacket message){
		String s=this.filesFolder+message.GetString();
			 try {
				File f=new File(s);
				if(!f.exists() || tempfiles.contains(message.GetString())) this.connections.send(this.id, new ERRORPacket((short)5, (short)1, "File not found"));
				else{
					byte[] byteArray= Files.readAllBytes(f.toPath());
					ByteToPacket(byteArray);
				}
			 } catch (IOException e) {
				 this.connections.send(this.id, new ERRORPacket((short)5, (short)2, "File cannot be read"));
			}

		
	}
		
	private void WriteRequest(RQPacket message){
		String s=this.filesFolder+message.GetString();
		this.filename=message.GetString();
		try {
			File f=new File(s);
			if(f.exists()||tempfiles.contains(message.GetString())) this.connections.send(this.id, new ERRORPacket((short)5, (short)5, "File already exists"));
			else{
				tempfiles.addLast(message.GetString());
				f.createNewFile();
				this.file=new FileOutputStream(f,true);
				this.dataNum=1;
				this.connections.send(this.id, new ACKPacket((short)4, (short)0));
			}
		 } catch (IOException e) {
			 this.connections.send(this.id, new ERRORPacket((short)5, (short)2, ""));
		}
	}
	
	
	private void DataProcess(DATAPacket message){
		if(this.file==null) this.connections.send(this.id, new ERRORPacket((short)5, (short)0, "Unknown data packet have arrived"));
		if(this.dataNum!=message.GetBlockNum()){
			try{
				this.file.close();
				new File(this.filesFolder+this.filename).delete();
				this.connections.send(this.id, new ERRORPacket((short)5, (short)0, "Disorder data packet"));
				tempfiles.remove(this.filename);
				this.file=null;
				this.filename=null;
			}catch (IOException e) {
				
			}
			
		}
		else{ 
			try {
				dataNum++;
				this.connections.send(this.id, new ACKPacket((short)4, (short)message.GetBlockNum()));
				this.file.write(message.GetData());		
				this.file.flush();
				
				if(message.GetPacketSize()<512){
					this.file.close();
					tempfiles.remove(this.filename);
					Bcast((byte)1, this.filename);
					this.file=null;
					this.filename=null;
				}
			} catch (IOException e) {
				new File(this.filename).delete();
				tempfiles.remove(this.filename);
				this.connections.send(this.id, new ERRORPacket((short)5, (short)2, "File cannot be written"));
				this.file=null;
				this.filename=null;
			}
		}
	}
	
	private void ACKProcess(ACKPacket message){
		if(this.blockCount!=message.GetBlockNum()) this.connections.send(this.id,new ERRORPacket((short)5, (short)0, "Mismatch block num"));
		else{
			if(!this.sendQueue.isEmpty()){
				this.connections.send(this.id, this.sendQueue.removeFirst());
				
				
				this.blockCount++;
			}
			else this.blockCount=0;
		}		
		
	}
	
	private void ErrorProcess(ERRORPacket message){
		
	}
	
	private void DirRequest(Packet message){
		int counter=0;
		File folder = new File(filesFolder);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
		   if (!tempfiles.contains(listOfFiles[i].getName())) {
		     counter+=listOfFiles[i].getName().length()+1;
		    }
		}
		
		byte[] res=new byte[counter];
		ByteBuffer buffer=ByteBuffer.wrap(res);
		byte[] zeroArray={0x00};
		 for (int i = 0; i < listOfFiles.length; i++) {
		      if (!tempfiles.contains(listOfFiles[i].getName())) {
		    	  buffer.put(listOfFiles[i].getName().getBytes());
		    	  buffer.put(zeroArray);
		      }
		    }
		
		ByteToPacket(res);
	}
	
	private void LogRequest(RQPacket message){
		
		if(users.containsKey(this.id) || users.containsValue(message.GetString())) this.connections.send(this.id,new ERRORPacket((short)5, (short)7, "User already logged in"));
		
		else{
			

			users.put(this.id, message.GetString());
			this.loggedIn=true;
			this.connections.send(this.id, new ACKPacket((short)4, (short)0));
		}
	}
	
	private void DelRequest(RQPacket message){
		
		File f= new File(this.filesFolder+message.GetString());
		if(!f.exists() || tempfiles.contains(message.GetString())) this.connections.send(this.id,new ERRORPacket((short)5, (short)1, "File not found"));
		else{
			if(f.delete()){
				this.connections.send(this.id, new ACKPacket((short)4, (short)0));
				Bcast((byte)0, message.GetString());
			}
			else{
				this.connections.send(this.id,new ERRORPacket((short)5, (short)2, "File can't be deleted"));
			}
			
			
		}
	}
	

	
	private void DisProcess(Packet message){
		this.connections.send(this.id, new ACKPacket((short)4, (short)0));
		users.remove(this.id);
		this.shouldTerminate=true;
	}
	
	private void ByteToPacket(byte[] array){
		int numOfBlock=array.length/512;
		for(int i=0;i<numOfBlock;i++){
			byte[] data=Arrays.copyOfRange(array, i*512, (i+1)*512);
			this.sendQueue.addLast(new DATAPacket((short)3, (short)512, (short)(i+1), data));
		}
		byte[] data=Arrays.copyOfRange(array, numOfBlock*512, array.length);
		this.sendQueue.addLast(new DATAPacket((short)3, (short)data.length, (short)(numOfBlock+1), data));
		this.blockCount=1;
		this.connections.send(this.id, this.sendQueue.removeFirst());
		
		
	}
	
	private void Bcast(byte i,String filename){
		Packet res=new BCASTPacket((short)9, i, filename);
		for(Integer userId: users.keySet()){
			this.connections.send(userId, res);
		}
	}
	
	@Override
	public boolean shouldTerminate() {
		return this.shouldTerminate;
	}
	

}
