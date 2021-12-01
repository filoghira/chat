package Client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class RootController implements Initializable {

    @FXML private TextField message;
    @FXML private ListView<String> messagesList;
    @FXML private Label status;
    @FXML private TextField username;
    private static Client client;
    MessageListener messageListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    /**
     * Sends a message to the server
     * @throws IOException
     */
    @FXML
    public void send() throws IOException {
        client.send(message.getText());
    }

    /**
     * Connects to the server
     * @throws IOException
     */
    @FXML
    public void connect() throws IOException {
        try {
            client.connect();
            status.setTextFill(Color.GREEN);
            status.setText("Successful");

            messageListener = new MessageListener(client.getSocket(), this);
            messageListener.start();
        } catch (AlreadyConnected e) {
            status.setTextFill(Color.RED);
            status.setText("Already connected");
        }
    }

    /**
     * Sets the username
     */
    @FXML
    public void setUsername() {
        client.setUsername(username.getText());
    }

    /**
     * Sets the client
     * @param client
     */
    public void setClient(Client client) {
        RootController.client = client;
    }

    /**
     * Adds a message to the list
     * @param message
     */
    public void print(String message) {
        Platform.runLater(() -> messagesList.getItems().add(message));
    }
}

class MessageListener extends Thread{

    private Socket socket;
    private BufferedReader br;
    private RootController controller;

    public MessageListener(Socket socket, RootController controller) {
        this.socket = socket;
        this.controller = controller;

        // Initialize the reader
        br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();

        // Read the messages
        while (!socket.isClosed()) {
            String input, msg = "";
            try {
                // Read the message
                while ((((input = br.readLine()) != null) && !input.equals(""))) {
                    if (input.startsWith("MESSAGE"))
                        msg = br.readLine();
                    else if (input.startsWith("FROM: "))
                        msg += " (from: " + input.substring("FROM: ".length()) + ")";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            controller.print(msg);
        }
    }
}