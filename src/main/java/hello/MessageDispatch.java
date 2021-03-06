package main.java.hello;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

/**
 * Created by Yuwen on 11/21/15.
 */
public class MessageDispatch {
    private OnlineStatus onlineStatus;

    public MessageDispatch(OnlineStatus onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
    public void publishNewUser(String room, String name){
        final String n = name;
        final String r = room;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Set<String> users = onlineStatus.getRoomAndUsers().get(r);
                if (users != null) {
                    for (String user : onlineStatus.getRoomAndUsers().get(r)) {
                        if (!user.equals(n)) {
                            try {
                                Socket socket = onlineStatus.getUsers().get(user);
                                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                                pw.println("* new user joined chat: " + n);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    public void publishUserLeft(String room, String name){
        final String n = name;
        final String r = room;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Set<String> users = onlineStatus.getRoomAndUsers().get(r);
                if (users != null) {
                    for (String user : onlineStatus.getRoomAndUsers().get(r)) {
                        if (!user.equals(n)) {
                            try {
                                Socket socket = onlineStatus.getUsers().get(user);
                                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                                pw.println("* user has left chat: " + n);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }
    public void sendMessage(String sender, String room, String message){
        final String s = sender;
        final String r = room;
        final String m = message;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Set<String> users = onlineStatus.getRoomAndUsers().get(r);
                if (users != null) {
                    for (String user : users) {
                        try {
                            Socket socket = onlineStatus.getUsers().get(user);
                            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                            pw.println(s + ": " + m);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public void sendMessageToSingle(String sender, String receiver, String message, PrintWriter s_pw){
        final String s = sender;
        final String r = receiver;
        final String m = message;
        final PrintWriter p = s_pw;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = onlineStatus.getUsers().get(r);
                    if (socket != null && !socket.isClosed()) {
                        PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                        pw.println("You got a new private message from " + s + ": " + m);
                    }else {
                        p.println("The user is not existed or online!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
