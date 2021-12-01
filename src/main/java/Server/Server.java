package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    ArrayList<ServerThread> clients;

    public void start() throws IOException {
        // Create a new server socket
        ServerSocket server = new ServerSocket(8083);
        clients = new ArrayList<>() ;
        // Listen for a new connection request
        while(true){
            Socket s = server.accept();
            clients.add(new ServerThread(this, s));
            clients.get(clients.size()-1).start();
        }
    }

    /**
     * Send a message to all clients
     * @param self The client who sent the message
     * @param message The message to send
     * @throws IOException
     */
    public void broadcast(ServerThread self, String message) throws IOException {
        // For each client, send the message
        for (ServerThread client : clients)
            // But don't send it to the client who sent it
            if (client != self)
                client.forward(message, self);
    }

    /**
     * Stop the server
     * @param serverThread The thread that called this method
     */
    public void killMe(ServerThread serverThread) {
        // Remove the client from the list of clients
        serverThread.interrupt();
        clients.remove(serverThread);
    }
}

