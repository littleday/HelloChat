package main.java.hello;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Yuyu on 11/21/15.
 */
public class OnlineStatus {
    private Map<Socket, String> sockets = new ConcurrentHashMap<Socket, String>();
    private Map<String, Socket> users = new ConcurrentHashMap<String, Socket>();
    private Map<String, Set<String>> roomAndUsers = new Hashtable<String, Set<String>>();
    public OnlineStatus() {
    }

    public Map<Socket, String> getSockets() {
        return sockets;
    }

    public Map<String, Socket> getUsers() {
        return users;
    }

    public boolean addUser(Socket socket, String name) {
        if (users.containsKey(name)) {
            return false;
        }else {
            users.put(name, socket);
            sockets.put(socket, name);
            return true;
        }
    }

    public void removeUser(Socket socket, String user){
        sockets.remove(socket);
        users.remove(user);
    }

    public Map<String, Set<String>> getRoomAndUsers() {
        return roomAndUsers;
    }

    public void addRoom(String room, String user){
        if (roomAndUsers.get(room) != null){
            roomAndUsers.get(room).add(user);
        }else{
            Set<String> users = Collections.synchronizedSet(new HashSet<String>());
            users.add(user);
            roomAndUsers.put(room, users);
        }
    }

    public void leaveRoom(String room, String user){
        roomAndUsers.get(room).remove(user);
        if (roomAndUsers.get(room).size() == 0)
            roomAndUsers.remove(room);
    }
}
