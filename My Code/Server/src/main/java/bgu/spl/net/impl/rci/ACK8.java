package bgu.spl.net.impl.rci;

public class ACK8 implements ACK {
    private int messageOpcode;
    private int NumPosts=0;
    private int NumFollowers=0;
    private int NumFollowing=0;

    public ACK8(int opcode){
        messageOpcode=opcode;
    }

    public int getNumPosts() {
        return NumPosts;
    }

    public int getNumFollowers() {
        return NumFollowers;
    }

    public int getNumFollowing() {
        return NumFollowing;
    }

    public int getMessageOpcode() {
        return 8;
    }

    public void setNumPosts(int numPosts) {
        NumPosts = numPosts;
    }

    public void setNumFollowers(int numFollowers) {
        NumFollowers = numFollowers;
    }

    public void setNumFollowing(int numFollowing) {
        NumFollowing = numFollowing;
    }
}
