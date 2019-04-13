package bgu.spl.net.impl.rci;

import java.util.Vector;

public class Post {
    private String postMsg;
    private Vector<String> taggedUsers=new Vector<>();

    public Post(String post) {
        postMsg=post;
        String tmp="";
        boolean reachtagg=false;
        for(int i=0;i<post.length();i++){
            if(reachtagg&&post.charAt(i)!=' '){
                tmp=tmp+post.charAt(i);

            }
            if(post.charAt(i)=='@'){
                reachtagg=true;
            }
            if(reachtagg&&(post.charAt(i)==' '||i==post.length()-1)){
                taggedUsers.add(tmp);
                tmp="";
                reachtagg=false;
            }
        }
    }

    public String getPostMsg() {
        return postMsg;
    }

    public Vector<String> getTaggedUsers() {
        return taggedUsers;
    }


}
