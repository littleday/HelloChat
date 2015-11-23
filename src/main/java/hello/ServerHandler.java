package main.java.hello;

import java.io.*;
import java.net.Socket;


/**
 * Created by Yuwen on 11/21/15.
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
            while(!onlineStatus.addUser(server, name)){
                pw.println("Sorry, name taken.");
                pw.println("Login Name?");
                name = br.readLine();
            }
            pw.println("Welcome " + name + "!");
            pw.println("Use /help to look more details!");
            String command = br.readLine();
            String room = null;
            while (command != null){
                command.trim();
                if (command.equals("/help")){
                    pw.println("XYZ Chat Room");
                    pw.println("We provide the following operations: ");
                    pw.println("/rooms: List current rooms.");
                    pw.println("/join: Join or create a room.");
                    pw.println("/names: List members in current room.");
                    pw.println("/leave: Leave the current room.");
                    pw.println("/quit: Close the connection.");
                }else if (command.equals("/rooms")){
                    pw.println("Active rooms are:");
                    for (String roomName : onlineStatus.getRoomAndUsers().keySet()){
                        pw.println("* " + roomName + "(" + onlineStatus.getRoomAndUsers().get(roomName).size() + ")");
                    }
                    pw.println("end of list.");
                }else if(command.startsWith("/join ")){
                    if (room == null) {
                        room = command.substring(6).trim();
                        onlineStatus.addRoom(room, name);
                        pw.println("entering room: " + room);
                        namesInRoom(pw, room, name);
                        messageDispatch.publishNewUser(room, name);
                    }else{
                        pw.println("Please leave current room first!");
                    }
                }else if (command.equals("/leave")){
                    if (room != null) {
                        leftRoom(room, name);
                        room = null;
                    }else{
                        pw.println("You are not in any room now!");
                    }
                }else if (command.equals("/quit")){
                    if (room != null)
                        leftRoom(room, name);
                    pw.println("BYE");
                    pw.close();
                    onlineStatus.removeUser(server, name);
                    server.close();
                    break;
                }else if(command.equals("/names")){
                    if (room != null) {
                        namesInRoom(pw, room, name);
                    }else{
                        pw.println("You are not in any room now!");
                    }
                } else{
                    if (room != null)
                        messageDispatch.sendMessage(name, room, command);
                }
                command = br.readLine();
            }
            pw.close();
            if (!server.isClosed())server.close();
        } catch (IOException ioe) {
            System.out.println("IOException on socket listen: " + ioe);
            ioe.printStackTrace();
        }
    }

    public void leftRoom(String room, String name){
        messageDispatch.publishUserLeft(room, name);
        onlineStatus.leaveRoom(room, name);
    }

    public void namesInRoom(PrintWriter pw, String room, String name){
        for (String member : onlineStatus.getRoomAndUsers().get(room)) {
            if (member.equals(name)) {
                pw.println("* " + member + " (** this is you)");
            } else
                pw.println("* " + member);
        }
        pw.println("end of list");
    }
}
