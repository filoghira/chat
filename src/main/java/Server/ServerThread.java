package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ServerThread extends Thread {

    private Socket s;
    private Server server;
    private String name;
    private boolean connected;
    private BufferedReader br;

    public ServerThread(Server server, Socket s) {
        this.s = s;
        this.server = server;
        this.connected = false;

        // Create a buffered reader to read the input from the client
        br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            s.getInputStream()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        // For each client, send the message
        while (!s.isClosed()) {
            try {
                String input;
                // Read the input from the client
                while (((input = br.readLine()) != null) && !input.equals("")) {
                    // If it's the start of a connection
                    if (input.startsWith("CONNECT ")) {
                        name = input.substring("CONNECT ".length());
                        connected = true;
                    }
                    // If it's a message
                    else if (connected && input.equals("MESSAGE")) {
                        String msg = br.readLine();
                        // Send the message to all clients
                        server.broadcast(this, msg);
                        System.out.println(name + ": " + msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // If the client disconnected, remove it from the list of clients
        server.killMe(this);
    }

    // Send a message to the client
    public void forward(String message, ServerThread sender) throws IOException {
        // Prepare the message
        PrintWriter pw = new PrintWriter(
                s.getOutputStream(),
                true
        );

        // Send the message
        pw.println("MESSAGE");
        pw.println(message);
        pw.println("FROM: " + sender.name);
        pw.println("");
    }
}
