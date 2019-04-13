package bgu.spl.net.impl.rci;

import java.io.UnsupportedEncodingException;
import java.util.Vector;


public class ACK4O7 implements ACK {

    private Vector<String> usersList=new Vector<>();
    private int numOfUsers=0;
    private int messageOpcode;

    public ACK4O7(int opcode) {
        messageOpcode=opcode;
    }

    public int getMessageOpcode() {
        return messageOpcode;
    }

    public void setUsersList(String users) {
            usersList.add(users);
    }

    public void setNumOfUsers(int numOfUsers) {
        this.numOfUsers = numOfUsers;
    }

    public Vector<String> getUsersList() {
        return usersList;
    }

    public int getNumOfUsers() {
        return numOfUsers;
    }
    public byte[] getUserListBytes() throws UnsupportedEncodingException {
        Vector<Byte> users=new Vector<>();

        for(int i=0;i<numOfUsers;i++){
            byte[] u=getUsersList().get(i).getBytes("UTF-8");
            for(byte b:u){
                users.add(b);
            }
            users.add((byte)0);

        }
        byte [] output =new byte[users.size()];
        int i=0;
        for (byte b:users){
            output[i]=b;
            i++;
        }
        return output;


    }

}
