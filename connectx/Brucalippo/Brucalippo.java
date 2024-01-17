package connectx.Brucalippo;

import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXCell;
import connectx.CXCellState;
import connectx.CXGameState;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Brucalippo implements CXPlayer {
    private int M, N, X;
    private long START; //Hold starting time (in milliseconds)
    private int TIMEOUT; //Hold the time limit (in seconds)
    private boolean Time_End;
    private Random rand;
    private int BestMove;
    private CXGameState myWin, yourWin; //Indicates the winner (Player1 or Player2)
    private CXCellState myPiece, yourPiece; //Indicates if the cell is player's 1 or 2
    

    public Brucalippo() {
    }


    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs){
        this.rand = new Random(System.currentTimeMillis());
        this.M = M;
        this.N = N;
        this.X = X;

        if (first) {
            this.myWin = CXGameState.WINP1;
        } else{
            this.yourWin = CXGameState.WINP2;
        }

        if (first) {
            this.myWin = CXGameState.WINP2;
        } else{
            this.yourWin = CXGameState.WINP1;
        }

        if (first) {
            this.myPiece = CXCellState.P1;
        } else{
            this.yourPiece = CXCellState.P2;
        }

        if (first) {
            this.myPiece = CXCellState.P2;
        } else{
            this.yourPiece = CXCellState.P1;
        }
        
        this.TIMEOUT = timeout_in_secs;

    }


    public int selectColumn(CXBoard B){
        Time_End = false;
        START = System.currentTimeMillis();

        if (B.numOfMarkedCells() <= 1) return N/2; //The strongest move for the first turn is always the middle column

        IterativeDeepening(B, 10);
        return this.BestMove;

    }

    public String playerName(){
        return "Brucalippo";
    }

    private Long AlphaBeta(CXBoard B, int depth, long alpha, long beta, boolean maximizingPlayer){
        if (depth == 0 || B.gameState() != CXGameState.OPEN) {
            // Evaluate the board position and return the heuristic value
            return evaluateBoard(B, maximizingPlayer ? myPiece : yourPiece); 
        }

        if (maximizingPlayer) {
            long value = Long.MIN_VALUE;
            for (int move : getLegalMoves(B)) {
                CXBoard newBoard = B.copy();
                // Make the move
                newBoard.markColumn(move);

                // Rercusively call AlphaBeta for the child node
                value = Math.max(value, AlphaBeta(newBoard, depth - 1, alpha, beta, false));
                alpha = Math.max(alpha, value);

                if (beta <= alpha) {
                    // Beta pruning - prune the remaining branches
                    break;
                }
            }
            return value;
        } else {
            long value = Long.MAX_VALUE;
            for (int move : getLegalMoves(B)) {
                CXBoard newBoard = B.copy();
                // Make the move 
                newBoard.markColumn(move);

                // Recursively call AlphaBeta for the child node
                value = Math.min(value, AlphaBeta(newBoard, depth - 1, alpha, beta, true));
                beta = Math.min(beta, value);

                if (beta <= alpha) {
                    // Alpha pruning - prune the remaining branches
                    break;
                }
            }
            return value;
        }
    }

    private List<Integer> getLegalMoves(CXBoard B) {
        List<Integer> legalMoves = new ArrayList<>();
        for (int col = 0; col < N; col++) {
            if (!B.fullColumn(col)) {
                legalMoves.add(col);
            }
        }
        return legalMoves;
    }

    private long evaluateBoard(CXBoard B, CXCellState player) {
        int score = 0;

        // Evaluate horizzontally
        for (int i = 0; i < B.M; i++) {
            for (int j = 0; j <= B.N - X; j++) {
                int consecutiveCount = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(i, j + k) == player) {
                        consecutiveCount++;
                    }
                }
                score += calculateScore(consecutiveCount);
            }
        }

        // Evaluate vertically
        for (int j = 0; j < B.N; j++) {
            for (int i = 0; i <= B.M - X; i++) {
                int consecutiveCount = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(i + k, j) == player) {
                        consecutiveCount++;
                    }
                }
                score += calculateScore(consecutiveCount);
            }
        }

        // Evaluate diagonally (left to right)
        for (int i = 0; i <= B.M - X; i++) {
            for (int j = 0; j <= B.N - X; j++) {
                int consecutiveCount = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(i + k, j + k) == player) {
                        consecutiveCount++;
                    }
                }
                score += calculateScore(consecutiveCount);
            }
        }

        // Evaluate diagonally (right to left) 
        for (int i = 0; i <= B.N - X; i++) {
            for (int j = X - 1; j < B.N; j++) {
                int consecutiveCount = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(i + k, j - k) == player) {
                        consecutiveCount++;
                    }
                }
                score += calculateScore(consecutiveCount);
            }
        }

        return score;
    }

    private int calculateScore(int consecutiveCount) {
        if (consecutiveCount == X) {
            return 100;
        } else if (consecutiveCount == X - 1) {
            return 10;
        } else if (consecutiveCount >= 2) {
            return 1;
        } else {
            return 0;
        }
    }

    private void IterativeDeepening(CXBoard B, int depth){
        Long alpha = Long.MIN_VALUE;
        Long beta = Long.MAX_VALUE;

        for (int d = 1; d < depth; d++){
            if(Time_End) break;
            AlphaBeta(B, depth, alpha, beta, true);
        }
    }
}
