package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.rci.Database;
import bgu.spl.net.impl.rci.EncoderDecoder;
import bgu.spl.net.srv.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String args[]){
        Database database=new Database();
        Server server = Server.threadPerClient(Integer.parseInt(args[0]),()->new BidiMessagingProtocolImpl<>(database),()-> new EncoderDecoder());
        server.serve();
    }
}
