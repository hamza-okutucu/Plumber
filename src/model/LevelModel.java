package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import model.enumeration.BoardElementType;
import model.enumeration.BorderType;
import model.enumeration.PathComponentColor;
import model.enumeration.PipeType;
import model.enumeration.Position;

/**
 * Represents the model of a game level, including the game board, available pipes, and game state.
 *
 * @author hamza-okutucu
 */
public class LevelModel {
    
    private int level;
    private int height;
    private int width;
    private BoardElement[][] board;
    private PipeStock stock;
    private File levelFile;
    private Stack<GameState> undoStack;
    private Stack<GameState> redoStack;
    
    /**
     * Initializes a new level model by loading a level from the provided level file.
     *
     * @param levelFile The file containing the level's configuration.
     */
    public LevelModel(File levelFile) {
    	stock = new PipeStock();
    	this.levelFile = levelFile;
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        loadLevelFromFile(levelFile);
    }
    
    /**
     * Loads the level configuration from a given level file.
     *
     * @param levelFile The file containing the level's configuration.
     */
    public void loadLevelFromFile(File levelFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(levelFile))) {
        	String fileName = levelFile.getName();
            String fileNameWithoutExtension = fileName.substring(0, fileName.length() - 2);
            String[] fileNameWithoutExtensionParts = fileNameWithoutExtension.split(" ");
            level = Integer.parseInt(fileNameWithoutExtensionParts[1]);
            String[] dimensions = reader.readLine().split(" ");
            int h = Integer.parseInt(dimensions[0]);
            int w = Integer.parseInt(dimensions[1]);
            this.height = h;
            this.width = w;
            this.board = new BoardElement[h][w];

            for (int row = 0; row < h; row++) {
                String[] elements = reader.readLine().split("\\s+");
                for (int col = 0; col < w; col++) {
                    String elementStr = elements[col];
                    boolean isAttached = elementStr.startsWith("*");
                    if (isAttached) {
                        elementStr = elementStr.substring(1);
                    }
                    updateBoardAndStock(elementStr, isAttached, row, col);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Updates the game board and pipe stock based on the provided configuration string.
     *
     * @param elementStr The configuration string for a board element.
     * @param isAttached  A boolean indicating if the element is attached.
     * @param row        The row index where the element is located.
     * @param col        The column index where the element is located.
     */
    private void updateBoardAndStock(String elementStr, boolean isAttached, int row, int col) {
        char type = elementStr.charAt(0);
        int rotations = elementStr.length() > 1 ? Character.getNumericValue(elementStr.charAt(1)) : 0;

        switch (type) {
            case 'X':
                handleBorder(row, col);
                break;
            case 'R':
                handleSource(PipeType.SOURCE, PathComponentColor.RED, rotations, isAttached, row, col);
                break;
            case 'G':
                handleSource(PipeType.SOURCE, PathComponentColor.GREEN, rotations, isAttached, row, col);
                break;
            case 'B':
                handleSource(PipeType.SOURCE, PathComponentColor.BLUE, rotations, isAttached, row, col);
                break;
            case 'Y':
                handleSource(PipeType.SOURCE, PathComponentColor.YELLOW, rotations, isAttached, row, col);
                break;
            case '.':
                handleEmpty(rotations, isAttached, row, col);
                break;
            case 'L':
                handlePipe(PipeType.LINE, rotations, isAttached, row, col);
                break;
            case 'F':
                handlePipe(PipeType.FORK, rotations, isAttached, row, col);
                break;
            case 'C':
                handlePipe(PipeType.CROSS, rotations, isAttached, row, col);
                break;
            case 'T':
                handlePipe(PipeType.TURN, rotations, isAttached, row, col);
                break;
            case 'O':
            	handlePipe(PipeType.OVER, rotations, isAttached, row, col);
            	break;
            default:
                handleInvalidPipeType(type);
        }
    }

    /**
     * Handles the creation of a board border element at the specified location.
     *
     * @param row The row index where the border element is located.
     * @param col The column index where the border element is located.
     */
    private void handleBorder(int row, int col) {
        if (row == 0) {
            if (col == 0) board[row][col] = new BoardBorder(BorderType.CORNER, 0);
            else if (col == width - 1) board[row][col] = new BoardBorder(BorderType.CORNER, 1);
            else board[row][col] = new BoardBorder(BorderType.SIDE, 0);
        } else if (row == height - 1) {
            if (col == 0) board[row][col] = new BoardBorder(BorderType.CORNER, 3);
            else if (col == width - 1) board[row][col] = new BoardBorder(BorderType.CORNER, 2);
            else board[row][col] = new BoardBorder(BorderType.SIDE, 2);
        } else if (col == 0) board[row][col] = new BoardBorder(BorderType.SIDE, 3);
        else if (col == width - 1) board[row][col] = new BoardBorder(BorderType.SIDE, 1);
    }

    /**
     * Handles the creation of a source pipe element at the specified location.
     *
     * @param pipeType   The type of the source pipe.
     * @param color      The color of the source pipe.
     * @param rotations  The number of rotations for the source pipe.
     * @param isAttached A boolean indicating if the source pipe is attached.
     * @param row        The row index where the source pipe is located.
     * @param col        The column index where the source pipe is located.
     */
    private void handleSource(PipeType pipeType, PathComponentColor color, int rotations, boolean isAttached, int row, int col) {
        board[row][col] = new BoardCell(pipeType, color, rotations, isAttached);
    }

    /**
     * Handles the creation of an empty pipe element at the specified location.
     *
     * @param rotations  The number of rotations for the empty pipe.
     * @param isAttached A boolean indicating if the empty pipe is attached.
     * @param row        The row index where the empty pipe is located.
     * @param col        The column index where the empty pipe is located.
     */
    private void handleEmpty(int rotations, boolean isAttached, int row, int col) {
        board[row][col] = new BoardCell(PipeType.EMPTY, PathComponentColor.GRAY, rotations, isAttached);
    }

    /**
     * Handles the creation of a pipe element at the specified location and manages the pipe stock.
     *
     * @param pipeType   The type of the pipe.
     * @param rotations  The number of rotations for the pipe.
     * @param isAttached A boolean indicating if the pipe is attached.
     * @param row        The row index where the pipe is located.
     * @param col        The column index where the pipe is located.
     */
    private void handlePipe(PipeType pipeType, int rotations, boolean isAttached, int row, int col) {
    	if(isAttached) {
    		board[row][col] = new BoardCell(pipeType, PathComponentColor.GRAY, rotations, isAttached);
    	} else {
    		stock.addPipe(pipeType, rotations);
    		board[row][col] = new BoardCell(PipeType.EMPTY, PathComponentColor.GRAY, rotations, isAttached);
    	}
    }

    /**
     * Handles an invalid pipe type by throwing an exception with an error message.
     *
     * @param type The invalid pipe type.
     */
    private void handleInvalidPipeType(char type) {
        try {
            throw new Exception("Le tuyau n'existe pas : " + type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the current level number.
     *
     * @return The level number.
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Gets the height of the game board.
     *
     * @return The height of the game board.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the width of the game board.
     *
     * @return The width of the game board.
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Gets the pipe stock containing available pipes for the game.
     *
     * @return The pipe stock with available pipes.
     */
    public PipeStock getStock() {
    	return stock;
    }

    /**
     * Gets the board element at the specified row and column.
     *
     * @param row The row index.
     * @param col The column index.
     * @return The board element at the specified position.
     */
    public BoardElement getElement(int row, int col) {
        return board[row][col];
    }

    /**
     * Sets the board element at the specified row and column.
     *
     * @param row The row index.
     * @param col The column index.
     * @param element The board element to be set at the specified position.
     */
    public void setElement(int row, int col, BoardElement element) {
    	undoStack.push(saveGameState());
        board[row][col] = element;
        propagateColor(element, row, col);
    	redoStack.clear();
    }
    
    /**
     * Switches the positions of two board elements.
     *
     * @param row1 The row index of the first element.
     * @param col1 The column index of the first element.
     * @param row2 The row index of the second element.
     * @param col2 The column index of the second element.
     */
    public void switchElements(int row1, int col1, int row2, int col2) {
    	if (row1 == row2 && col1 == col2) return;
    	
    	BoardCell cell1 = (BoardCell) getElement(row1, col1);
    	BoardCell cell2 = (BoardCell) getElement(row2, col2);
    	
    	if (cell1.isAttached() || cell2.isAttached()) return;
    	
    	undoStack.push(saveGameState());
    	board[row1][col1] = cell2;
    	board[row2][col2] = cell1;
    	propagateColor(cell1, row2, col2);
    	propagateColor(cell2, row1, col1);
    	redoStack.clear();
    }
    
    /**
     * Propagates the color through the connected pipe components starting from the placed element.
     *
     * @param placedElement The board element where the color propagation starts.
     * @param row           The row index of the placed element.
     * @param col           The column index of the placed element.
     */
    private void propagateColor(BoardElement placedElement, int row, int col) {
        if (placedElement.getBoardElementType() != BoardElementType.CELL) {
            return;
        }
        
        BoardCell placedCell = (BoardCell) placedElement;
        
        if (placedCell.isEmpty()) {
            int[][] neighborOffsets = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}
            };

            for (int[] offset : neighborOffsets) {
                int newRow = row + offset[0];
                int newCol = col + offset[1];
                
                if (isValidPosition(newRow, newCol)) {
                    BoardElement neighbor = getElement(newRow, newCol);
                    
                    if (neighbor.getBoardElementType() == BoardElementType.CELL && !((BoardCell) neighbor).isSource()) {
                        PathComponent pathComponent = (row == newRow) ?
                            PathComponent.getPathComponents(PipeType.LINE, PathComponentColor.GRAY, 1).get(0) :
                            PathComponent.getPathComponents(PipeType.LINE, PathComponentColor.GRAY, 0).get(0);

                        Map<PathComponent, int[]> pathComponents = getConnectedPathComponents(pathComponent, row, col);
                        
                        removeSources(pathComponents);
                        updateColorBasedOnConnectedColors(pathComponents);
                    }
                }
            }
        } else {
        	Map<PathComponent, int[]> pathComponents = placedCell.getPipe().getPathComponents().stream()
        	        .collect(Collectors.toMap(pc -> pc, pc -> new int[] {row, col}));
        	updateColorBasedOnConnectedColors(pathComponents);
        }
    }
    
    /**
     * Removes source pipe components from the provided map of connected path components.
     *
     * @param pathComponents A map of connected path components with their coordinates.
     */
    private void removeSources(Map<PathComponent, int[]> pathComponents) {
        Iterator<Map.Entry<PathComponent, int[]>> iterator = pathComponents.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<PathComponent, int[]> entry = iterator.next();
            int[] coordinates = entry.getValue();

            int neighborRow = coordinates[0];
            int neighborCol = coordinates[1];

            BoardCell connectedNeighbor = (BoardCell) getElement(neighborRow, neighborCol);

            if (connectedNeighbor.isSource()) {
                iterator.remove();
            }
        }
    }
    
    /**
     * Updates the color of connected path components based on the colors of their neighbors.
     *
     * @param pathComponents A map of connected path components with their coordinates.
     */
    private void updateColorBasedOnConnectedColors(Map<PathComponent, int[]> pathComponents) {
        for (Map.Entry<PathComponent, int[]> entry : pathComponents.entrySet()) {
            PathComponent pathComponent = entry.getKey();
            int[] coordinates = entry.getValue();
            
            int row = coordinates[0];
            int col = coordinates[1];

            Set<PathComponentColor> connectedColors = new HashSet<>();
            Set<PathComponent> visited = new HashSet<>();
            getConnectedColors(visited, connectedColors, pathComponent, row, col);

            PathComponentColor newColor;

            if (connectedColors.isEmpty()) {
                newColor = PathComponentColor.GRAY;
            } else if (connectedColors.size() == 1) {
                newColor = connectedColors.iterator().next();
            } else {
                newColor = PathComponentColor.DARK_GRAY;
            }
            
            visited.clear();
            
            updateColor(visited, pathComponent, newColor, coordinates[0], coordinates[1]);
        }
    }
    
    /**
     * Recursively collects connected colors for a given path component.
     *
     * @param visited         A set of visited path components.
     * @param connectedColors A set of connected colors.
     * @param currentPathComponent The current path component being analyzed.
     * @param currentRow      The row index of the current path component.
     * @param currentCol      The column index of the current path component.
     */
    private void getConnectedColors(
    		Set<PathComponent> visited,
    		Set<PathComponentColor> connectedColors,
    		PathComponent currentPathComponent,
    		int currentRow,
    		int currentCol
    ) {
		if (visited.contains(currentPathComponent)) {
			return;
		}
		
		visited.add(currentPathComponent);
    	
        Map<PathComponent, int[]> connectedPathComponents = getConnectedPathComponents(currentPathComponent, currentRow, currentCol);

        for (Map.Entry<PathComponent, int[]> entry : connectedPathComponents.entrySet()) {
            PathComponent pathComponent = entry.getKey();
            int[] coordinates = entry.getValue();

            int neighborRow = coordinates[0];
            int neighborCol = coordinates[1];

            BoardCell connectedNeighbor = (BoardCell) getElement(neighborRow, neighborCol);

            if (connectedNeighbor.isSource()) {
            	PathComponent connectedNeighBorPathComponent = connectedNeighbor.getPipe().getPathComponentAt(0);
            	connectedColors.add(connectedNeighBorPathComponent.getColor());
            	visited.add(connectedNeighBorPathComponent);
            } else {
            	getConnectedColors(visited, connectedColors, pathComponent, neighborRow, neighborCol);
            }
        }
	}
    
    /**
     * Updates the color of a path component and propagates the change to connected path components.
     *
     * @param visited       A set of visited path components.
     * @param currentPathComponent The current path component to update.
     * @param color         The new color for the path component.
     * @param currentRow    The row index of the current path component.
     * @param currentCol    The column index of the current path component.
     */
    private void updateColor(Set<PathComponent> visited, PathComponent currentPathComponent, PathComponentColor color, int currentRow, int currentCol) {
        if (visited.contains(currentPathComponent)) {
            return;
        }

        visited.add(currentPathComponent);
        currentPathComponent.setColor(color);

        BoardCell currentCell = (BoardCell) getElement(currentRow, currentCol);
        currentCell.getPipe().updateImage();
        currentCell.updateImage();

        Map<PathComponent, int[]> connectedPathComponents = getConnectedPathComponents(currentPathComponent, currentRow, currentCol);
        
        for (Map.Entry<PathComponent, int[]> entry : connectedPathComponents.entrySet()) {
            PathComponent pathComponent = entry.getKey();
            int[] coordinates = entry.getValue();

            int neighborRow = coordinates[0];
            int neighborCol = coordinates[1];

            BoardCell connectedNeighbor = (BoardCell) getElement(neighborRow, neighborCol);

            if (!connectedNeighbor.isSource()) {
                updateColor(visited, pathComponent, color, neighborRow, neighborCol);
            }
        }
    }
    
    /**
     * Retrieves a map of connected path components for a given path component.
     *
     * @param currentPathComponent The path component for which to find connected path components.
     * @param currentRow    The row index of the current path component.
     * @param currentCol    The column index of the current path component.
     * @return A map of connected path components with their coordinates.
     */
    private Map<PathComponent, int[]> getConnectedPathComponents(PathComponent currentPathComponent, int currentRow, int currentCol) {
        Map<PathComponent, int[]> connectedPathComponents = new HashMap<>();
        int[][] neighborOffsets = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        };

        for (int[] offset : neighborOffsets) {
            int newRow = currentRow + offset[0];
            int newCol = currentCol + offset[1];

            if (isValidPosition(newRow, newCol)) {
                BoardElement neighbor = getElement(newRow, newCol);

                if (neighbor.getBoardElementType() == BoardElementType.CELL) {
                    BoardCell neighborCell = (BoardCell) neighbor;

                    if (!neighborCell.isEmpty()) {
                        List<PathComponent> neighborPathComponents = neighborCell.getPipe().getPathComponents();

                        for (PathComponent neighborPathComponent : neighborPathComponents) {
                            if (areConnectedNeighbors(currentRow, currentCol, newRow, newCol, currentPathComponent, neighborPathComponent)) {
                                connectedPathComponents.put(neighborPathComponent, new int[] {newRow, newCol});
                            }
                        }
                    }
                }
            }
        }

        return connectedPathComponents;
    }

    /**
     * Checks if a row and column pair represents a valid position on the game board.
     *
     * @param row The row index to check.
     * @param col The column index to check.
     * @return True if the position is valid; otherwise, false.
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < height && col >= 0 && col < width;
    }

    /**
     * Determines whether two path components are connected neighbors.
     *
     * @param currentRow    The row index of the current path component.
     * @param currentCol    The column index of the current path component.
     * @param newRow        The row index of the neighboring path component.
     * @param newCol        The column index of the neighboring path component.
     * @param currentPathComponent The current path component.
     * @param neighbor       The neighboring path component.
     * @return True if the components are connected neighbors; otherwise, false.
     */
    private boolean areConnectedNeighbors(int currentRow, int currentCol, int newRow, int newCol, PathComponent currentPathComponent, PathComponent neighbor) {
        return ((currentRow == newRow && Math.abs(currentCol - newCol) == 1) ||
               (currentCol == newCol && Math.abs(currentRow - newRow) == 1)) &&
               currentPathComponent.isConnectedTo(neighbor, getRelativePosition(currentRow, currentCol, newRow, newCol));
    }

    /**
     * Determines the relative position of a neighboring cell with respect to the current cell.
     *
     * @param currentRow The row index of the current cell.
     * @param currentCol The column index of the current cell.
     * @param newRow     The row index of the neighboring cell.
     * @param newCol     The column index of the neighboring cell.
     * @return The relative position (e.g., TOP, BOTTOM, LEFT, RIGHT) of the neighboring cell.
     */
    private Position getRelativePosition(int currentRow, int currentCol, int newRow, int newCol) {
        if (currentRow == newRow) {
            if (currentCol < newCol) {
                return Position.RIGHT;
            } else {
                return Position.LEFT;
            }
        } else {
            if (currentRow < newRow) {
                return Position.BOTTOM;
            } else {
                return Position.TOP;
            }
        }
    }
    
    /**
     * Resets the level to its initial state.
     */
    public void resetLevel() {
    	stock = new PipeStock();
        loadLevelFromFile(levelFile);
        undoStack.clear();
        redoStack.clear();
    }
    
    /**
     * Undoes the last action in the game.
     */
    public void performUndo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(saveGameState());
            GameState previousState = undoStack.pop();
            restoreGameState(previousState);
        }
    }

    /**
     * Redoes the last undone action in the game.
     */
    public void performRedo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(saveGameState());
            GameState nextState = redoStack.pop();
            restoreGameState(nextState);
        }
    }

    /**
     * Saves the current game state, including a deep copy of the game board and the pipe stock.
     *
     * @return A GameState object representing the saved game state.
     */
    private GameState saveGameState() {
        return new GameState(deepCopyBoard(board), deepCopyPipeStock(stock));
    }

    /**
     * Restores the game state from a provided GameState object, replacing the current board and pipe stock.
     *
     * @param gameState The GameState object to restore the game state from.
     */
    private void restoreGameState(GameState gameState) {
        board = gameState.getBoard();
        stock = gameState.getPipeStock();
    }
    
    /**
     * Creates a deep copy of the game board, preserving the state of each board element.
     *
     * @param originalBoard The original game board to be deep-copied.
     * @return A new BoardElement array representing the deep copy of the game board.
     */
    private BoardElement[][] deepCopyBoard(BoardElement[][] originalBoard) {
        int height = originalBoard.length;
        int width = originalBoard[0].length;
        BoardElement[][] newBoard = new BoardElement[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (originalBoard[row][col] != null) {
                    newBoard[row][col] = originalBoard[row][col].deepCopy(); 
                }
            }
        }

        return newBoard;
    }
    
    /**
     * Creates a deep copy of the pipe stock, including all the available pipes.
     *
     * @param originalStock The original PipeStock to be deep-copied.
     * @return A new PipeStock object representing the deep copy of the pipe stock.
     */
    private PipeStock deepCopyPipeStock(PipeStock originalStock) {
        return originalStock.deepCopy();
    }
}