package battleship;
import battleship.ui.BattleshipWindow;
import online.Client;

//Testing 1 2 3!!

public class App {
    private static Client client;

    public static void main(String[] args) throws Exception {
        new BattleshipWindow();
    }

    public static Client getClient() {
        return client;
    }

    public static boolean isOnline() {
        return (client != null);
    }

    public static void disconnect() {
        client.shutdown();
        System.out.println("disconnected client because window closed");
    }
}