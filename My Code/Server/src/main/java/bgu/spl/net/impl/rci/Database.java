package bgu.spl.net.impl.rci;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private ConcurrentHashMap<String, UserInfo> usersByName = new ConcurrentHashMap<>(); //registered
    private ConcurrentHashMap<Integer, UserInfo> usersById = new ConcurrentHashMap<>(); // logged in
    private Vector<Post> posts = new Vector<>();
    private Vector<PM> PMmessages = new Vector<>();

    public boolean register(String username, String pw) {
        synchronized (usersByName) { //protects the case that users with the same name try to register simultaneously
            if (usersByName.containsKey(username)) return false;
            UserInfo tmp = new UserInfo(username, pw);
            usersByName.put(username, tmp);
            tmp.setRegistered(true);
            return true;
        }
    }

    public boolean login(int conid, String name, String pw) {
        UserInfo u = usersByName.get(name);
        if (u != null) {
            synchronized (usersById) { //protects the case which many threads tries to log the user in
                if (u.getPassword().equals(pw)) {
                    if (usersById.contains(u))
                        return false;
                    usersById.putIfAbsent(conid, u);
                    u.setConnected(true);
                    u.setConnectionId(conid);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean logout(int id) {
        synchronized (usersById) {
            UserInfo u = usersById.get(id);
            if (u != null) {
                u.setConnected(false);
                usersById.remove(id);
                return true;
            }
            return false;
        }
    }

    public boolean follow(int id, String name) {
        UserInfo me = usersById.get(id);
        UserInfo toFollow = usersByName.get(name);
        if (me != null && toFollow != null && me.follow(toFollow)) {
            return true;
        }
        return false;
    }

    public boolean unfollow(int conid, String name) {
        UserInfo me = usersById.get(conid);
        UserInfo toUnFollow = usersByName.get(name);
        if (me != null && toUnFollow != null && me.unfollow(toUnFollow)) {
            return true;
        }
        return false;
    }

    public ConcurrentHashMap<String, UserInfo> getUsersByName() {
        return usersByName;
    }

    public ConcurrentHashMap<Integer, UserInfo> getUsersById() {
        return usersById;
    }

    public UserInfo getUserByName(String s) {
        return usersByName.get(s);
    }

    public UserInfo getUserInfoById(int conid) {
        return usersById.get(conid);
    }

    public void addPosts(Post post) {
        posts.add(post);
    }

    public void addPMmessages(PM PMmessage) {
        PMmessages.add(PMmessage);
    }
}