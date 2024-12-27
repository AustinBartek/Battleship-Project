package online;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import battleship.board.Board;

public class Server {
    private static Long currentBattleID = 0l;
    private ServerSocket server;
    private boolean done;
    private final ArrayList<ConnectionHandler> connections;
    private final ExecutorService pool;
    private final HashMap<ConnectionHandler, Board> pendingGames;
    private final HashMap<Long, Battle> activeBattles;

    public Server() {
        connections = new ArrayList<>();
        pool = Executors.newCachedThreadPool();
        pendingGames = new HashMap<>();
        activeBattles = new HashMap<>();

        try {
            server = new ServerSocket(420);
            System.out.println("Server Started");

            while (!done) {
                Socket newConnection = server.accept();
                System.out.println(newConnection.getInetAddress().getHostAddress() + " connected");
                ConnectionHandler newHandler = new ConnectionHandler(newConnection);
                connections.add(newHandler);
                pool.execute(newHandler);
            }
        } catch (Exception e) {
            shutdown();
        }
    }

    public void addBattle(Board host, Board join) {
        Long id = currentBattleID;
        currentBattleID++;
        //activeBattles.add(new Battle(host, join));
    }

    public void shutdown() {
        System.out.println("Shutting Down Server");

        try {
            done = true;
            if (!server.isClosed()) {
                server.close();
            }

            for (ConnectionHandler ch : connections) {
                ch.shutdown();
            }
        } catch (Exception e) {
        }
    }

    private class ConnectionHandler implements Runnable {
        private final Socket connection;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ConnectionHandler(Socket connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
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

        public void addPendingGame(Board game) {
            if (!alreadyPendingGame()) {
                pendingGames.put(this, game);
            }
        }

        public boolean alreadyPendingGame() {
            for (ConnectionHandler handler : pendingGames.keySet()) {
                if (handler.equals(this)) {
                    return true;
                } 
            }
            return false;
        }

        public void shutdown() {
            try {
                connection.shutdownInput();
                connection.shutdownOutput();
                if (!connection.isClosed()) {
                    connection.close();
                }
                connections.remove(this);

            } catch (Exception e) {
            }
        }
        
        public void handlePacket(Object packet) {

        }
    }

    private class Battle {
        Board hostGame, joinerGame;

        public Battle(Board hostGame, Board joinerGame) {
            this.hostGame = hostGame;
            this.joinerGame = joinerGame;
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}