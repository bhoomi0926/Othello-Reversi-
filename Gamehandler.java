import java.io.*;
import java.net.*;

public class Gamehandler {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9999); // Start the server
        System.out.println("Othello Server is running...");

        try {
            Socket clientSocket = serverSocket.accept(); // Accept client connection
            System.out.println("Client connected.");

            // Set up communication streams
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Initialize the game
            OthelloGame game = new OthelloGame();
            game.printBoard(out); // Print the initial board to the client

            // Game loop
            while (!game.isGameOver()) {
                // Debugging: print the current player
                System.out.println("Current Player: " + game.getCurrentPlayer());

                // Handle Black's turn
                if (game.getCurrentPlayer() == 'B') {
                    int[] bestMoveB = game.getBestMove('B'); // Check for valid moves
                    if (bestMoveB == null) {
                        out.println("Black has no valid moves and passes its turn.");
                        game.setCurrentPlayer('W'); // Pass turn to White
                        continue; // Skip to White's turn
                    }

                    out.println("Black's turn. Enter your move (row column):");
                    String input = in.readLine(); // Read player's input
                    if (input == null || input.isEmpty()) {
                        out.println("Invalid input. Try again.");
                        continue;
                    }

                    String[] tokens = input.split(" ");
                    if (tokens.length != 2) {
                        out.println("Invalid input format. Enter row and column.");
                        continue;
                    }

                    try {
                        int row = Integer.parseInt(tokens[0]);
                        int col = Integer.parseInt(tokens[1]);

                        if (game.makeMove(row, col, 'B')) {
                            game.printBoard(out); // Print the updated board
                            game.setCurrentPlayer('W'); // Pass turn to White
                        } else {
                            out.println("Invalid move. Try again.");
                        }
                    } catch (NumberFormatException e) {
                        out.println("Invalid input. Row and column must be integers.");
                    }
                }

                // Handle White's turn (Server)
                if (game.getCurrentPlayer() == 'W') {
                    int[] bestMoveW = game.getBestMove('W'); // Check for valid moves
                    if (bestMoveW == null) {
                        out.println("White has no valid moves and passes its turn.");
                        game.setCurrentPlayer('B'); // Pass turn to Black
                        continue; // Skip to Black's turn
                    }

                    System.out.println("Server's turn as White");
                    game.makeMove(bestMoveW[0], bestMoveW[1], 'W');
                    out.println("White (Server) played at: " + bestMoveW[0] + " " + bestMoveW[1]);
                    game.printBoard(out);
                    game.setCurrentPlayer('B'); // Pass turn to Black
                }
            }

            // Game over
            char winner = game.getWinner();
            out.println("Game Over! Winner: " + (winner == 'D' ? "Draw" : (winner == 'W' ? "White" : "Black")));
        } finally {
            // Ensure proper socket closing after the game ends
            serverSocket.close();
            System.out.println("Server socket closed.");
        }
    }
}
