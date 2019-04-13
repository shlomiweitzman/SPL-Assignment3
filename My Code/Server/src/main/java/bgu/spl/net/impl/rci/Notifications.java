package bgu.spl.net.impl.rci;

public class Notifications {
    private String postingUser;
    private String content;
    private String msg;

    public Notifications(String postingUser, String content,String postOpm) {
        this.postingUser = postingUser;
        this.content = content;
        msg=postOpm;
    }

    public String getMsg() {
        return msg;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }
}

