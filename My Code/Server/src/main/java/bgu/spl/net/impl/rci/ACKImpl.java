package bgu.spl.net.impl.rci;

public class ACKImpl implements ACK {
    private int messageOpcode;

    public ACKImpl(int messageOpcode) {
        this.messageOpcode = messageOpcode;
    }

    @Override
    public int getMessageOpcode() {
        return messageOpcode;
    }
}
