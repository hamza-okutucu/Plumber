package model;

/**
 * Represents the state of the game board and pipe stock at a specific point in the game.
 *
 * @author hamza-okutucu
 */
public class GameState {
	
    private BoardElement[][] board;
    private PipeStock pipeStock;

    /**
     * Initializes a new game state with the provided game board and pipe stock.
     *
     * @param board The 2D array representing the game board's elements.
     * @param pipeStock The pipe stock containing available pipes for the game.
     */
    public GameState(BoardElement[][] board, PipeStock pipeStock) {
        this.board = board;
        this.pipeStock = pipeStock;
    }

    /**
     * Gets the 2D array representing the game board's elements.
     *
     * @return The current state of the game board.
     */
    public BoardElement[][] getBoard() {
        return board;
    }

    /**
     * Gets the pipe stock containing available pipes for the game.
     *
     * @return The pipe stock with available pipes.
     */
    public PipeStock getPipeStock() {
        return pipeStock;
    }
}
