package bgu.spl171.net.impl.TFTP;

import java.util.Map;
import java.util.WeakHashMap;

import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.ConnectionHandler;

public class ConnectionsImpl<T> implements Connections<T> {
	
	private Map<Integer,ConnectionHandler<T>> connections = new WeakHashMap<Integer, ConnectionHandler<T>>();
	
	
	public boolean AddConnection(int id,ConnectionHandler<T> con){
		if (connections.containsKey(id)) return false;
		connections.put(id, con);
		return true;
	}
	@Override
	public boolean send(int connectionId, T msg) {
		if (connections.containsKey(connectionId)) return false;
		connections.get(connectionId).send(msg);
		return true;
	}

	@Override
	public void broadcast(T msg) {
		for(Integer id: connections.keySet()){
			send(id,msg);
		}
		
	}

	@Override
	public void disconnect(int connectionId) {
		connections.remove(connectionId);
		
	}

}
