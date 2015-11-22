package main.HelloServer;

import java.io.*;
import java.net.Socket;
import java.util.Set;

/**
 * Created by Yuyu on 11/21/15.
 */
public class ServerHandler implements Runnable {
    private Socket server;
    private OnlineStatus onlineStatus;
    private MessageDispatch messageDispatch;
    ServerHandler(Socket server, OnlineStatus onlineStatus) {
        this.server=server;
        this.onlineStatus = onlineStatus;
        this.messageDispatch = new MessageDispatch(onlineStatus);
    }
    public void run () {
        try {
            OutputStream os = server.getOutputStream();
            PrintWriter pw = new PrintWriter(os, true);
            pw.println("Welcome to the XYZ chat server");
            pw.println("Login Name?");
            BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
            String name = br.readLine();
            if(onlineStatus.addUser(server, name)){
                pw.println("Welcome " + name + "!");
            }else{
                pw.println("Sorry, name taken.");
            }
            String command = br.readLine();
            String room = null;
            while (command != null){
                command.trim();
                if (command.equals("/rooms")){
                    pw.println("Active rooms are:");
                    for (String roomName : onlineStatus.getRoomAndUsers().keySet()){
                        pw.println("* " + roomName + "(" + onlineStatus.getRoomAndUsers().get(roomName).size() + ")");
                    }
                    pw.println("end of list.");
                }else if(command.startsWith("/join")){
                    room = command.substring(5).trim();
                    onlineStatus.addRoom(room, name);
                    pw.println("entering room: " + room);
                    for (String member : onlineStatus.getRoomAndUsers().get(room)){
                        if (member.equals(name)){
                            pw.println("* " + member + " (** this is you)");
                        }else
                            pw.println("* " + member);
                    }
                    pw.println("end of list");
                    messageDispatch.publishNewUser(room, name);
                }else if (command.equals("/leave")){
                    if (room != null) {
                        messageDispatch.publishUserLeft(room, name);
                        onlineStatus.leaveRoom(room, name);
                        room = null;
                    }
                }else if (command.equals("/quit")){
                    pw.println("BYE");
                    pw.close();
                    server.close();
                    onlineStatus.removeUser(server, name);
                    break;
                }else{
                    if (room != null)
                        messageDispatch.sendMessage(name, room, command);
                }
                command = br.readLine();
            }
            pw.close();
            server.close();
        } catch (IOException ioe) {
            System.out.println("IOException on socket listen: " + ioe);
            ioe.printStackTrace();
        }
    }
}
