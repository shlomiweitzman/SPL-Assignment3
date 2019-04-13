package bgu.spl.net.impl.rci;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UserInfo {
    private int connectionId = -1;
    private int numOfPosts = 0;
    private String password;
    private String name;
    private boolean isConnected = false;
    private boolean isRegistered = false;
    private Vector<UserInfo> followers = new Vector<>();
    private Vector<UserInfo> following = new Vector<>();
    private ConcurrentLinkedQueue<Notifications> toPost = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<PM> toPM = new ConcurrentLinkedQueue<>();

    public UserInfo(String name, String pw) {
        this.password = pw;
        this.name = name;
    }

    public void addNotification(Notifications toPost) {
        this.toPost.add(toPost);
    }

    public ConcurrentLinkedQueue<Notifications> getToPost() {
        return toPost;
    }

    public String getName() {
        return name;
    }

    public boolean follow(UserInfo user) {
        synchronized (following) { //protects the case that the user unfollow me between
            if (!following.contains(user)) {
                following.add(user);
                user.addFollower(this);
                return true;
            }
            return false;
        }
    }

    public boolean unfollow(UserInfo user) {
        synchronized (following) { // protects the case that the user follow me between
            if (following.contains(user)) {
                following.remove(user);
                user.removeFollower(user);
                return true;
            }
            return false;
        }
    }

    public void addFollower(UserInfo follower) {
        followers.add(follower);
    }

    public void removeFollower(UserInfo follower) {
        followers.remove(follower);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public Vector<UserInfo> getFollowers() {
        return followers;
    }

    public Vector<UserInfo> getFollowing() {
        return following;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void increaseNumOfPosts() {
        numOfPosts++;
    }

    public int getNumOfPosts() {
        return numOfPosts;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public String getPassword() {
        return password;
    }

    public ConcurrentLinkedQueue<PM> getToPM() {
        return toPM;
    }

    public void addToPM(PM pm) {
        toPM.add(pm);
    }
}