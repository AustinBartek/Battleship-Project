package online;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    //public 24.49.208.72
    private static final String address = "192.168.1.175";
    private Socket connection;
    private Thread mainThread;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Client() {
    }

    public void runClient() {
        Runnable clientRunner = new Runnable() {
            @Override
            public void run() {
                try {
                    connection = new Socket(address, 420);
                    out = new ObjectOutputStream(connection.getOutputStream());
                    in = new ObjectInputStream(connection.getInputStream());

                    Object newObject;
                    while ((newObject = in.readObject()) != null) {
                        handlePacket(newObject);
                    }

                } catch (Exception e) {
                    shutdown();
                }
            }
        };

        mainThread = new Thread(clientRunner);
        mainThread.start();
    }

    public void shutdown() {
        try {
            if (connection != null) {
                if (!connection.isClosed()) {
                    connection.close();
                }
                connection.shutdownInput();
                connection.shutdownOutput();
            }

        } catch (Exception e) {
        }
    }

    public void handlePacket(Object packet) {

    }

    public static void main(String[] args) {
        Client test = new Client();
        test.runClient();
    }
}
