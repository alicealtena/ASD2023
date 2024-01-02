package connectx;

public class YourAlgorithmPlayer implements CXPlayer {

    private int M; // Number of rows
    private int N; // Number of columns
    private int X; // Number of coins for a win
    private boolean isFirstPlayer; // Indicates if this player is the first player
    private int timeoutInSeconds; // Maximum time allowed for move selection

    @Override
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
        // Initialize player with game parameters
        this.M = M;
        this.N = N;
        this.X = X;
        this.isFirstPlayer = first;
        this.timeoutInSeconds = timeout_in_secs;

        // Additional initialization logic if needed
    }

    @Override
    public int selectColumn(CXBoard B) {
        // Implement your move selection algorithm here
        // You can access the current state of the game through the CXBoard object (B)

        // Placeholder: Select a random column for demonstration purposes
        return (int) (Math.random() * N);
    }

    @Override
    public String playerName() {
        // Return a name for your player (e.g., "YourAlgorithmPlayer")
        return "YourAlgorithmPlayer";
    }
}
