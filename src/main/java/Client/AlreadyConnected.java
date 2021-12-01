package Client;

public class AlreadyConnected extends Exception {
    public AlreadyConnected() { super("Client.Client already connected"); }
    public AlreadyConnected(String message) { super(message); }
    public AlreadyConnected(String message, Throwable cause) { super(message, cause); }
    public AlreadyConnected(Throwable cause) { super(cause); }
}
