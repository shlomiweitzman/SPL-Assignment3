package bgu.spl.net.srv;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, ConnectionHandler<T>> connectionHandlers;

    public ConnectionsImpl() {
        connectionHandlers = new ConcurrentHashMap<>();
    }

    @Override
    public boolean send(int connectionId, T msg) {
        if(connectionHandlers.containsKey(connectionId)) {
            ConnectionHandler<T> connectionHandler = connectionHandlers.get(connectionId);
            connectionHandler.send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for (Map.Entry<Integer, ConnectionHandler<T>> entry : connectionHandlers.entrySet()) {
            entry.getValue().send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        connectionHandlers.remove(connectionId);
    }

    public void addClient(ConnectionHandler<T> co,int connId){
        connectionHandlers.put(connId,co);
    }
}
