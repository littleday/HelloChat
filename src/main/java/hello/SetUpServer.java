package main.java.hello;

/**
 * Created by Yuyu on 11/21/15.
 */
public class SetUpServer {
    private static int port = 9399;
    private static int maxConnections = 1000;
    public static void main(String[] args) {
        HelloServer server = new HelloServer(port, maxConnections);
        server.runServer();
    }
}
