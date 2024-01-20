package connectx.Brucalippo;

import java.util.concurrent.TimeoutException;
import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;

public class Brucalippo implements CXPlayer {
  private int N;
  private int TIMEOUT;
  private CXGameState WIN, LOSE;
  private long start;

  public Brucalippo() {}
  
  //Player initialization
  @Override
  public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
    this.N = N;
    this.TIMEOUT = timeout_in_secs;

    // Determine winning and losing states based on whether the player goes first or second
    this.WIN = first ? CXGameState.WINP1 : CXGameState.WINP2;
    this.LOSE = first ? CXGameState.WINP2 : CXGameState.WINP1;
  }

  //Select the best move
  @Override
  public int selectColumn(CXBoard B) {
    this.start = System.currentTimeMillis();
    
    // If only one or zero cells are marked, return the middle column
    if (B.numOfMarkedCells() <= 1) {
        return N/2; 
    }

    // Use iterative deepening to find the best move
    return iterativeDeepening(B);
  }
  
  @Override
  public String playerName() {
    return "Brucalippo";
  }
  
  private void checkTime() throws TimeoutException {
    // Check if the time limit has been reached
    if ((System.currentTimeMillis() - start) >= TIMEOUT * 995) throw new TimeoutException();
	}
  
  //The iterative deepening return the best move.
  private int iterativeDeepening(CXBoard B) {
    int bestMove = B.getAvailableColumns()[0], tmp = bestMove, tmpEval, eval;
    
    try {
      // Iterate over depths to perform iterative deepening
      for (int d = 1; d <= B.numOfFreeCells(); d++) {
        tmpEval = -1;
        eval = -1;

        // Iterate over available columns to evaluate possible moves
        for (Integer m : B.getAvailableColumns()) {
          
          B.markColumn(m); // Try making the move
          eval = alphabeta(B, false, -1, 1, d); // Evaluate the resulting board position using alpha-beta pruning
          B.unmarkColumn(); // Undo the move

          // Update the best move if the current move has a higher evaluation
          if (eval > tmpEval) {
            tmp = m;
            tmpEval = eval;
          }
          
        }
        // Update the best move at the current depth
        bestMove = tmp;
      }
      
    } catch (TimeoutException e) {}
    return bestMove;
  }

  

  //MinMax with alphabeta pruning
  private int alphabeta(CXBoard B, boolean maximize, int alpha, int beta, int depth) throws TimeoutException {
    int eval;
    checkTime();

    if (depth == 1 || isLeaf(B.gameState())) {
      // If at the specified depth or a leaf node, evaluate the board position
      eval = evaluate(B);
    } else if (maximize) {
      // maximize
      eval = -1;
      Integer[] ac = B.getAvailableColumns();
      for (Integer c : ac) {
        B.markColumn(c);
        // Recursively evaluate the resulting position for the minimizing player
        eval = Integer.max(eval, alphabeta(B, false, alpha, beta, depth - 1));
        B.unmarkColumn();
        alpha = Integer.max(eval, alpha); // Update alpha
        if (beta <= alpha) break; // Perform alpha-beta pruning if necessary
      }
    } else {
      // minimize
      eval = 1;
      Integer[] ac = B.getAvailableColumns();
      for (Integer c : ac) {
        B.markColumn(c);
        // Recursively evaluate the resulting position for the maximizing player
        eval = Integer.min(eval, alphabeta(B, true, alpha, beta, depth - 1));
        B.unmarkColumn();
        beta = Integer.min(eval, beta); // Update beta
        if (beta <= alpha) break; // Perform alpha-beta pruning if necessary
      }
    }
    return eval; 
  }

  private boolean isLeaf(CXGameState s) {
    // Check if the given game state is a leaf node (terminal state)
    return s == WIN || s == LOSE || s == CXGameState.DRAW;
  }

  private int evaluate(CXBoard B) {
    // Evaluate the board position based on the game state
    if (B.gameState() == WIN) {
      return 1;
    } else if (B.gameState() == LOSE) {
      return -1;
    } else {
      return 0;
    }
  }
  
}
