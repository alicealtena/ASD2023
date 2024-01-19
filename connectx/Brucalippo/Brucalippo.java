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
            this.myWin = CXGameState.WINP2;
        }

        if (first) {
            this.yourWin = CXGameState.WINP2;
        } else{
            this.yourWin = CXGameState.WINP1;
        }

        if (first) {
            this.myPiece = CXCellState.P1;
        } else{
            this.myPiece = CXCellState.P2;
        }

        if (first) {
            this.yourPiece = CXCellState.P2;
        } else{
            this.yourPiece = CXCellState.P1;
        }
        
        this.TIMEOUT = timeout_in_secs;

    }


    public int selectColumn(CXBoard B){
        Time_End = false;
        START = System.currentTimeMillis();

        Integer[] L = B.getAvailableColumns();

        //The strongest move for the first turn is always the middle column
        if (B.numOfMarkedCells() <= 1) {
            return N/2; 
        }

        IterativeDeepening(B, 10);
        return this.BestMove;

    }

    public String playerName(){
        return "Brucalippo";
    }

    private boolean timeOut() {
        return ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (95.0 / 100.0));
    }

    private Long AlphaBeta(CXBoard B, int depthCurrent, int depthMax, long alpha, long beta, boolean maximizingPlayer){
        if (depthCurrent == 0 || B.gameState() != CXGameState.OPEN || Time_End) {
            // Evaluate the board position and return the heuristic value
            return evaluateBoard(B, depthCurrent); 
        }

        if (maximizingPlayer) {
            long value = Long.MIN_VALUE + 1;
            Integer[] L = B.getAvailableColumns();
            for (int i : L) {

                if (timeOut()) {
                    this.Time_End = true;
                    return value;
                }

                B.markColumn(i);

                // Rercusively call AlphaBeta for the child node
                value = Math.max(value, AlphaBeta(B, depthCurrent + 1, depthMax, alpha, beta, false));
                B.unmarkColumn();

                alpha = Math.max(alpha, value);

                

                if (beta <= alpha) {
                    // Beta pruning - prune the remaining branches
                    break;
                }
            }
            return value;
        } else { // Minimizing player
            long value = Long.MAX_VALUE - 1;
            Integer[] L = B.getAvailableColumns();
            for (int i : L) {

                if (timeOut()) {
                    this.Time_End = true;
                    return value;
                }

                B.markColumn(i);

                // Recursively call AlphaBeta for the child node
                value = Math.min(value, AlphaBeta(B, depthCurrent + 1, depthMax, alpha, beta, true));
                B.unmarkColumn();

                beta = Math.min(beta, value);

                if (beta <= alpha) {
                    // Alpha pruning - prune the remaining branches
                    break;
                }
            }
            return value;
        }
    }

    private long evaluateBoard(CXBoard B, int depth) {
        if (B.gameState() != CXGameState.OPEN) {
            if (B.gameState() == myWin) {
                return Long.MAX_VALUE - 1 - depth;
            } else if (B.gameState() == yourWin) {
                return Long.MIN_VALUE + 1 + depth;
            } else {
                return 0L;
            }
        }

        CXCellState[][] board = B.getBoard();
        CXCellState[] columnA = new CXCellState[M];
        CXCellState[] rowA = new CXCellState[N];
        CXCellState[] diagonalA = new CXCellState[X];
        boolean[] isEmpty = new boolean[M];
        Long score = 0L;
        
        // Evaluate vertically
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M - (X - 1); j++) {
                columnA[j] = board[j][i];
                if (columnA[j] != CXCellState.FREE) {
                    isEmpty[j] = false;
                }
                if (columnA[M-1] == CXCellState.FREE) {
                    continue;
                }
                for (int r = 0; r < M - (X - 1); r++) {
                    if (columnA[r+X-1] == CXCellState.FREE) {
                        continue;
                    }
                    score += calculateScore(columnA, j, j + X);
                }
            }
        }

        // Evaluate horizzontally
        for (int j = 0; j < M; j++) {
            if (isEmpty[j] == true) {
                continue;
            }
            rowA = board[j];
            for (int i = 0; i < N - (X - 1); i++) {
                score += calculateScore(rowA, i, i + X);
            }
        }

        // Evaluate diagonally (left to right)
        for (int i = 0; i < M - (X - 1); i++) {
            for (int j = 0; j < N - (X - 1); j++) {
                for (int k = 0; k < X; k++) {
                    diagonalA[k] = board[i + k][j + k];
                }
                
                score += calculateScore(diagonalA, 0, X);
            }
        }

        // Evaluate diagonally (right to left) 
        for (int i = 0; i < M - (X - 1); i++) {
            for (int j = 0; j < N - (X - 1); j++) {
                for (int k = 0; k < X; k++) {
                    diagonalA[k] = board[i + (X -1) - k][j + k];
                }
                
                score += calculateScore(diagonalA, 0, X);
            }
        }

        return score;
    }

    private Long calculateScore(CXCellState arr[], int start, int end) {
        int countMine = 0;
        int countEmpty = 0;
        int countYours = 0;

        Long score = 0L; 

        for (int i = start; i < end; i++) {
            if (arr[i] == myPiece) {
                countMine++;
            } else if (arr[i] == CXCellState.FREE) {
                countEmpty++;
            } else {
                countYours++;
            }
        }

        score += (countEmpty - countYours);

        if (countYours == 0) {
            score *= (long) Math.pow(2, countMine);
        }
        if (countMine == 0) {
            score *= (long) Math.pow(2, countYours);
        }

        return score;
    }


    private void IterativeDeepening(CXBoard B, int maxDepth) {
        Long alpha = Long.MIN_VALUE;
        Long beta = Long.MAX_VALUE;
        int prev;
        this.BestMove = - 1;

        for(int d = 1; d <= maxDepth; d++){
            if(Time_End) break;
            prev = this.BestMove; //Save the best move we found considering the previous maximum depth
            AlphaBeta(B, 0, d, alpha, beta, true);
            if(Time_End) this.BestMove = prev; //If we timed out with depth = d we couldn't establish a reliable best move, so we use the previous one
        }
    }
}
