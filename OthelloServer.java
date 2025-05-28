import java.io.*;
import java.net.*;

public class OthelloServer {
    private static final int PORT = 9999;

    public static void main(String[] args) {
        System.out.println("Othello Server is running...");
        try (ServerSocket serverSocket = new ServerSocket()) {
            // Bind the server to a specific IP and port
            serverSocket.bind(new InetSocketAddress("10.21.7.12", PORT)); // Replace with your server's IP
            System.out.println("Server is running on 10.21.7.12:" + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                OthelloGame game = new OthelloGame();
                out.println("Welcome to Othello!");
                while (true) {
                    String command = in.readLine();
                    if (command == null) {
                        break; // Handle client disconnection
                    }
                    if (command.startsWith("MOVE")) {
                        // Handle MOVE logic here
                        out.println("MOVE command received: " + command);
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected");
            }
        }
    }
}
