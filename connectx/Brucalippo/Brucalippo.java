package connectx.Brucalippo;

import java.util.concurrent.TimeoutException;
import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;

public class Brucalippo implements CXPlayer {
  private int N; //columns
  private int TIMEOUT;
  private CXGameState WIN, LOSE;
  private long start;

  public Brucalippo() {}
  
  @Override
  public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
    this.N = N;
    this.TIMEOUT = timeout_in_secs;
    this.WIN = first ? CXGameState.WINP1 : CXGameState.WINP2;
    this.LOSE = first ? CXGameState.WINP2 : CXGameState.WINP1;
  }

  @Override
  public int selectColumn(CXBoard B) {
    this.start = System.currentTimeMillis();
    
    if (B.numOfMarkedCells() <= 1) {
        return N/2; 
    }
    /* try {
      // controllo simile a L1 se usa checktime()
    } catch (TimeoutException e) {
      return B.getAvailableColumns()[0];
    } */

    return iterativeDeepening(B);
  }
  
  @Override
  public String playerName() {
    return "Brucalippo";
  }
  
  private void checkTime() throws TimeoutException {
    if ((System.currentTimeMillis() - start) >= TIMEOUT * 995) throw new TimeoutException();
	}
  
  private int iterativeDeepening(CXBoard B) {
    int bestMove = B.getAvailableColumns()[0], tmp = bestMove, tmpEval, eval;
    try {
      for (int d = 1; d <= B.numOfFreeCells(); d++) {
        tmpEval = -1;
        eval = -1;
        for (Integer m : B.getAvailableColumns()) {
          B.markColumn(m);
          eval = alphabeta(B, false, -1, 1, d);
          B.unmarkColumn();
          if (eval > tmpEval) {
            tmp = m;
            tmpEval = eval;
          }
        }
        bestMove = tmp;
      }
    } catch (TimeoutException e) {}
    return bestMove;
  }

  private int alphabeta(CXBoard B, boolean maximize, int alpha, int beta, int depth) throws TimeoutException {
    int eval;
    checkTime();
    if (depth == 1 || isLeaf(B.gameState())) {
      eval = evaluate(B);
    } else if (maximize) {
      // maximize
      eval = -1;
      Integer[] ac = B.getAvailableColumns();
      for (Integer c : ac) {
        B.markColumn(c);
        eval = Integer.max(eval, alphabeta(B, !maximize, alpha, beta, depth - 1));
        B.unmarkColumn();
        alpha = Integer.max(eval, alpha);
        if (beta <= alpha) break;
      }
    } else {
      // minimize
      eval = 1;
      Integer[] ac = B.getAvailableColumns();
      for (Integer c : ac) {
        B.markColumn(c);
        eval = Integer.min(eval, alphabeta(B, !maximize, alpha, beta, depth - 1));
        B.unmarkColumn();
        beta = Integer.min(eval, beta);
        if (beta <= alpha) break;
      }
    }
    return eval; 
  }

  private boolean isLeaf(CXGameState s) {
    return s == WIN || s == LOSE || s == CXGameState.DRAW;
  }

  private int evaluate(CXBoard B) {
    if (B.gameState() == WIN) {
      return 1;
    } else if (B.gameState() == LOSE) {
      return -1;
    } else {
      return 0;
    }
  }
  
}
