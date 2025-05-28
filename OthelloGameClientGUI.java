import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class OthelloGameClientGUI extends JFrame {
    private static final int BOARD_SIZE = 8;
    private JButton[][] boardButtons;
    private OthelloGame game;
    private BufferedReader in;
    private PrintWriter out;

    public OthelloGameClientGUI(OthelloGame game) {
        this.game = game;
        boardButtons = new JButton[BOARD_SIZE][BOARD_SIZE];

        // Establish connection to the server
        try {
            // Replace with the server's IP and port
            Socket socket = new Socket("10.21.7.12", 9999);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to server at 10.21.7.12:9999");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to connect to server.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Set up the GUI
        setTitle("Othello Client");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));

        // Initialize the board
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardButtons[i][j] = new JButton();
                boardButtons[i][j].setBackground(Color.GREEN); // Board squares are green
                boardButtons[i][j].setFocusPainted(false);
                boardButtons[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                boardButtons[i][j].addActionListener(new MoveListener(i, j));
                add(boardButtons[i][j]);
            }
        }

        updateBoard(); // Show the initial board state
        setVisible(true);
    }

    // Update the GUI to reflect the current board state
    public void updateBoard() {
        char[][] board = game.getBoard();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 'B') { // Black pieces
                    boardButtons[i][j].setText("●");
                    boardButtons[i][j].setForeground(Color.BLACK);
                    boardButtons[i][j].setFont(new Font("Arial", Font.BOLD, 36));
                } else if (board[i][j] == 'W') { // White pieces
                    boardButtons[i][j].setText("●");
                    boardButtons[i][j].setForeground(Color.WHITE);
                    boardButtons[i][j].setFont(new Font("Arial", Font.BOLD, 36));
                } else { // Empty spaces
                    boardButtons[i][j].setText("");
                }
            }
        }
    }

    // Listener for player moves
    private class MoveListener implements ActionListener {
        private int row;
        private int col;

        public MoveListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (game.getCurrentPlayer() == 'B') { // Player is Black
                if (game.makeMove(row, col, 'B')) {
                    updateBoard(); // Update GUI after a successful move
                    game.setCurrentPlayer('W'); // Switch to White's turn (Server)
                    out.println("MOVE " + row + " " + col); // Send move to the server
                    playServerTurn(); // Simulate server's move
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid move. Try again.");
                }
            }
        }
    }

    // Simulate server's (White) turn
    private void playServerTurn() {
        if (game.isGameOver()) {
            showGameOver();
            return;
        }

        int[] bestMove = game.getBestMove('W');
        if (bestMove != null) {
            game.makeMove(bestMove[0], bestMove[1], 'W'); // Server plays a move
            updateBoard(); // Update GUI after server's move
            game.setCurrentPlayer('B'); // Switch back to Black's turn
        } else {
            JOptionPane.showMessageDialog(null, "White has no valid moves. Passing turn to Black.");
            game.setCurrentPlayer('B'); // Pass turn back to Black
        }

        if (game.isGameOver()) {
            showGameOver();
        }
    }

    // Simulate client's (Black) turn
    private void playClientTurn() {
        if (game.isGameOver()) {
            showGameOver();
            return;
        }

        int[] bestMove = game.getBestMove('B');
        if (bestMove != null) {
            game.makeMove(bestMove[0], bestMove[1], 'W'); // Client plays a move
            updateBoard(); // Update GUI after client's move
            game.setCurrentPlayer('B'); // Switch back to White's turn
        } else {
            JOptionPane.showMessageDialog(null, "Black has no valid moves. Passing turn to White.");
            game.setCurrentPlayer('B'); // Pass turn back to White
        }

        if (game.isGameOver()) {
            showGameOver();
        }
    }

    // Display the game over dialog
    private void showGameOver() {
        char winner = game.getWinner();
        String message = (winner == 'D') ? "It's a draw!" : (winner == 'B' ? "Black wins!" : "White wins!");
        JOptionPane.showMessageDialog(null, "Game Over! " + message);
    }

    // Main method to launch the GUI
    public static void main(String[] args) {
        // Initialize the Othello game logic
        OthelloGame game = new OthelloGame();
        // Launch the GUI
        new OthelloGameClientGUI(game);
    }
}
