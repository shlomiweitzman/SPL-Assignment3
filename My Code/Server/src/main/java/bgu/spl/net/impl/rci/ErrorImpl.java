package bgu.spl.net.impl.rci;

public class ErrorImpl implements Error {
    private int messageOpcode;

    public ErrorImpl(int messageOpcode) {
        this.messageOpcode = messageOpcode;
    }

    @Override
    public int getMessageOpcode() {
        return messageOpcode;
    }
}
