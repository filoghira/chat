package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Application{

    private Stage primaryStage;
    private AnchorPane rootLayout;
    private RootController controller;
    private String username = "defaultUser";
    private Socket socket;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage){

        controller = new RootController();
        controller.setClient(this);

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Socket chat");

        initRootLayout();
    }

    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader(Client.class.getResource("RootLayout.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects to the server.
     * @throws IOException
     * @throws AlreadyConnected
     */
    void connect() throws IOException, AlreadyConnected {
        // If already connected, throw exception
        if(socket != null)
            throw new AlreadyConnected();

        // Get the socket
        socket = new Socket(InetAddress.getByName("localhost"), 8083);

        // Connect to the server
        PrintWriter pw = new PrintWriter(
                socket.getOutputStream(),
                true
        );

        pw.println("CONNECT "+username);
        pw.println();
    }

    /**
     * Sends a message to the server.
     * @param message Message to send
     * @throws IOException
     */
    public void send(String message) throws IOException {
        PrintWriter pw = new PrintWriter(
                socket.getOutputStream(),
                true
        );

        pw.println("MESSAGE");
        pw.println(message);
        pw.println();
    }

    /**
     * Gets the socket.
     * @return Socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Sets the username.
     * @param username Username
     */
    void setUsername(String username){
        if(username.equals(""))
            this.username = "defaultUser";
        else
            this.username = username;
    }
}