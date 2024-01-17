

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
    private Random Random;
    private int BestMove;
    private CXGameState myWin, yourWin; //Indicates the winner (Player1 or Player2)
    private CXCellState myPiece, yourPiece; //Indicates if the cell is player's 1 or 2
    

    public Brucalippo() {
    }


    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs){
        this.Random = new Random(System.currentTimeMillis());
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
        Start = System.currentTimeMillis();

        if (B.numOfMarkedCells() <= 1) return N/2; //The strongest move for the first turn is always the middle column

        IterativeDeepening(B, 1);
        return this.BestMove;

    }

    public String playerName(){
        return "Brucalippo";
    }

    private Long AlphaBeta(){
        Long val;
        return val;
    }

    private void IterativeDeepening(CXBoard B, int depth){
        Long alpha = Long.MIN_VALUE;
        Long beta = Long.MAX_VALUE;

        for (int d = 1; d < depth; d++){
            if(Time_End) break;
            AlphaBeta();
        }
    }
}
