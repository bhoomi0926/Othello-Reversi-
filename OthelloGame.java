import java.io.PrintWriter;

public class OthelloGame {
    private static final int BOARD_SIZE = 8;
    private char[][] board;
    private char currentPlayer;

    public OthelloGame() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
        currentPlayer = 'B'; // Black starts
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = '.';
            }
        }
        board[3][3] = 'W';
        board[3][4] = 'B';
        board[4][3] = 'B';
        board[4][4] = 'W';
    }

    public char[][] getBoard() {
        return board;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(char player) {
        this.currentPlayer = player;
    }

    public boolean isGameOver() {
        return !hasValidMoves('B') && !hasValidMoves('W');
    }

    public char getWinner() {
        int blackCount = 0, whiteCount = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 'B') blackCount++;
                if (board[i][j] == 'W') whiteCount++;
            }
        }
        if (blackCount > whiteCount) return 'B';
        if (whiteCount > blackCount) return 'W';
        return 'D'; // Draw
    }

    public boolean makeMove(int row, int col, char player) {
        if (!isValidMove(row, col, player)) {
            return false;
        }
        board[row][col] = player; // Place the disc
        flipDiscs(row, col, player); // Flip opponent's discs
        currentPlayer = (player == 'B') ? 'W' : 'B'; // Switch player
        return true;
    }

    public boolean isValidMove(int row, int col, char player) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE || board[row][col] != '.') {
            return false;
        }
        char opponent = (player == 'B') ? 'W' : 'B';
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue; // Skip the current position
                if (checkDirection(row, col, dr, dc, player, opponent)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkDirection(int row, int col, int dr, int dc, char player, char opponent) {
        int r = row + dr, c = col + dc, count = 0;
        while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == opponent) {
            r += dr;
            c += dc;
            count++;
        }
        return count > 0 && r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == player;
    }

    private void flipDiscs(int row, int col, char player) {
        char opponent = (player == 'B') ? 'W' : 'B';
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue; // Skip the current position
                if (checkDirection(row, col, dr, dc, player, opponent)) {
                    flipDirection(row, col, dr, dc, player);
                }
            }
        }
    }

    private void flipDirection(int row, int col, int dr, int dc, char player) {
        int r = row + dr, c = col + dc;
        char opponent = (player == 'B') ? 'W' : 'B';
        while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == opponent) {
            board[r][c] = player;
            r += dr;
            c += dc;
        }
    }

    public void printBoard(PrintWriter out) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                out.print(board[i][j] + " ");
            }
            out.println();
        }
        out.flush();
    }

    private boolean hasValidMoves(char player) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (isValidMove(i, j, player)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int[] getBestMove(char player) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (isValidMove(i, j, player)) {
                    return new int[]{i, j};
                }
            }
        }
        return null; // No valid moves
    }
}
