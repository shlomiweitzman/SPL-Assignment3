package bgu.spl.net.srv;

import bgu.spl.net.Pair;
import bgu.spl.net.impl.rci.*;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<T> {
    private Connections<T> connections;
    private int connId;
    private boolean shouldTerminate = false;
    private Database database;

    public BidiMessagingProtocolImpl(Database database) {
        this.database = database;
    }

    public void start(int connectionId, Connections<T> connections) {
        this.connections = connections;
        this.connId = connectionId;
    }

    @Override
    public void process(T message) {
        if (message instanceof Pair && ((Pair) message).getL() instanceof Integer) {
            switch (((Integer) ((Pair) message).getL()).intValue()) {
                case 1: //register
                    ErrorImpl error1 = new ErrorImpl(1);
                    ACKImpl ack1 = new ACKImpl(1);
                    String[] message1 = (String[]) ((Pair) message).getR();
                    if (database.register(message1[0], message1[1])) {
                        connections.send(connId, (T) ack1);
                    } else connections.send(connId, (T) error1);
                    break;
                case 2: //login
                    ErrorImpl error2 = new ErrorImpl(2);
                    ACKImpl ack2 = new ACKImpl(2);
                    String[] message2 = (String[]) ((Pair) message).getR();
                    if (database.login(connId, message2[0], message2[1])) {
                        connections.send(connId, (T) ack2);
                        ConcurrentLinkedQueue<Notifications> notifications =
                                database.getUserInfoById(connId).getToPost();
                        ConcurrentLinkedQueue<PM> toPm =
                                database.getUserInfoById(connId).getToPM();
                        while (!notifications.isEmpty()) connections.send(connId, (T) notifications.poll());
                        while (!toPm.isEmpty()) connections.send(connId, (T) toPm.poll());
                    } else connections.send(connId, (T) error2);
                    break;
                case 3: //logout
                    ErrorImpl error3 = new ErrorImpl(3);
                    ACKImpl ack3 = new ACKImpl(3);
                    if (database.logout(connId)) {
                        connections.send(connId, (T) ack3);
                        shouldTerminate = true;
                    } else connections.send(connId, (T) error3);
                    break;
                case 4:{ //follow{
                    ErrorImpl error4 = new ErrorImpl(4);
                    ACK4O7 ack4 = new ACK4O7(4);
                    UserInfo user4 = database.getUserInfoById(connId);
                    int counter = 0;
                    String[] message4 = (String[]) ((Pair) message).getR();
                    if (user4 != null) {
                        for (int i = 2; i < message4.length; i++) {
                            if (message4[0].equals("follow")) {
                                if (database.follow(connId, message4[i])) {
                                    counter++;
                                    ack4.setUsersList(message4[i]);
                                }
                            } else if (message4[0].equals("unfollow")) {
                                if (database.unfollow(connId, message4[i])) {
                                    counter++;
                                    ack4.setUsersList(message4[i]);
                                }
                            }
                        }
                        if (counter != 0) {
                            ack4.setNumOfUsers(counter);
                            connections.send(connId, (T) ack4);
                            return;
                        }
                    }
                    connections.send(connId, (T) error4);
                    break;

                }
                case 5: //post
                    ErrorImpl error5 = new ErrorImpl(5);
                    ACKImpl ack5 = new ACKImpl(5);
                    Post post5 = ((Post) ((Pair) message).getR());
                    UserInfo message5 = database.getUserInfoById(connId);
                    if (message5 != null) { // if the user already logged in
                        Notifications n = new Notifications(message5.getName(), post5.getPostMsg(), "post");
                        synchronized (message5.getFollowers()) { //protects the case which a user follow/unfollow between
                            for (UserInfo u : message5.getFollowers()) {
                                synchronized (database.getUsersById()) {
                                    if (u.isConnected()) connections.send(u.getConnectionId(), (T) n);
                                    else u.addNotification(n);
                                }
                            }
                            for (String s : post5.getTaggedUsers()) {
                                UserInfo u = database.getUserByName(s);
                                if (u!= null &&!message5.getFollowers().contains(u)) {
                                    synchronized (database.getUsersById()) {
                                        if (u.isConnected()) connections.send(u.getConnectionId(), (T) n);
                                        else u.addNotification(n);
                                    }
                                }
                            }
                        }
                        database.addPosts(post5); // check if needed
                        message5.increaseNumOfPosts();
                        connections.send(connId, (T) ack5);
                    } else connections.send(connId, (T) error5);
                    break;

                case 6: //pm
                    ACKImpl ack6 = new ACKImpl(6);
                    ErrorImpl error6 = new ErrorImpl(6);
                    String[] strings = (String[]) ((Pair) message).getR();
                    Database database6 = database;
                    UserInfo receiver6 = database6.getUserByName(strings[0]);
                    UserInfo user6 = database6.getUserInfoById(connId);
                    synchronized (database.getUsersById()) { //protects the case that a user logout between
                        if (user6 != null && user6.isConnected() && receiver6 != null && receiver6.isRegistered()) {
                            Notifications n = new Notifications(user6.getName(), strings[1], "pm");
                            PM pm = new PM(strings[1], user6.getName(), receiver6.getName());
                            if (receiver6.isConnected()){
                                connections.send(receiver6.getConnectionId(), (T) n);
                                connections.send(connId,(T) ack6);
                            }
                            else receiver6.addToPM(pm);
                            database6.addPMmessages(pm); // check if needed
                        } else connections.send(connId, (T) error6);
                    }
                    break;

                case 7: //userlist
                    ErrorImpl error7 = new ErrorImpl(7);
                    ACK4O7 ack7 = new ACK4O7(7);
                    if (database.getUsersById().containsKey(connId)) {
                        ack7.setNumOfUsers(database.getUsersByName().size());
                        synchronized (database.getUsersByName()) { //protects the case which user register between
                            for (Map.Entry<String, UserInfo> u : database.getUsersByName().entrySet()) {
                                ack7.setUsersList(u.getKey());
                            }
                        }
                        connections.send(connId, (T) ack7);
                    } else connections.send(connId, (T) error7);
                    break;

                case 8: //stat
                    ErrorImpl error8 = new ErrorImpl(8);
                    ACK8 ack8 = new ACK8(8);
                    String[] message8 = (String[]) ((Pair) message).getR();
                    String string8 = message8[0];
                    UserInfo user8 = database.getUserByName(string8);
                    if (user8 != null && user8.isRegistered() && database.getUsersById().containsKey(connId)) {
                        ack8.setNumPosts(user8.getNumOfPosts());
                        ack8.setNumFollowers(user8.getFollowers().size());
                        ack8.setNumFollowing(user8.getFollowing().size());
                        connections.send(connId, (T) ack8);
                    } else connections.send(connId, (T) error8);
                    break;
            }
        }
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
