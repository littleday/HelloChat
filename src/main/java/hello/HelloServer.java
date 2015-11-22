package main.HelloServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Yuyu on 11/19/15.
 */
public class HelloServer {
    private int port;
    private int maxConnections;
    private OnlineStatus onlineStatus = new OnlineStatus();

    public HelloServer(int port, int maxConnections){
        this.port = port;
        this.maxConnections = maxConnections;
    }
    // Listen for incoming connections and handle them
    public void initServer(){
        int i = 0;
        try{
            ServerSocket listener = new ServerSocket(port);
            while(i < maxConnections){
                Socket server = listener.accept();
                ServerHandler conn_c= new ServerHandler(server, onlineStatus);
                Thread t = new Thread(conn_c);
                t.start();
                i++;
            }
        } catch (IOException e) {
            System.out.println("IOException on socket listen: " + e);
            e.printStackTrace();
        }
    }
}