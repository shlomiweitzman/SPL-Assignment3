package bgu.spl.net.impl.rci;

public class PM {
    private String pm;
    private String sender;
    private String receiver;

    public PM(String pm, String sender, String receiver) {
        this.pm = pm;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getPm() {
        return pm;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
}
