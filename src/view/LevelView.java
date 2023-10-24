package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import model.BoardCell;
import model.BoardElement;
import model.LevelModel;
import model.enumeration.BoardElementType;
import model.enumeration.PathComponentColor;
import model.enumeration.PipeType;
import util.AssetsUtil;

/**
 * The graphical user interface for displaying a game board and controls.
 *
 * @author hamza-okutucu
 */
public class LevelView extends JFrame {
    
	private static final long serialVersionUID = 1L;
	private JPanel pipeStock;
    private JPanel gameBoard;
    private JPanel buttons;
    private ImageView[][] pipeStockImageViews;
    private ImageView[][] gameBoardImageViews;
    private JButton undo;
    private JButton redo;
    private JButton reset;
    private JButton levels;
    private LevelModel model;

    /**
     * Constructs a new LevelView for the given game model.
     *
     * @param model The game model to display.
     */
    public LevelView(LevelModel model) {
        this.model = model;

        setTitle("Level " + Integer.toString(model.getLevel()));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
         
        pipeStock = createPipeStock();
        gameBoard = createGameBoard();
        buttons = createButtons();

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.weighty = 1;

        gc.gridx = 0;
        gc.gridy = 0;
        add(gameBoard, gc);
        
        gc.gridx = 1;
        gc.gridy = 0;
        add(pipeStock, gc);
        
        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridwidth = 2;
        add(buttons, gc);
        
		pack();
        setMinimumSize(getPreferredSize());
		setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * Creates the panel for the pipe stock display.
     *
     * @return A panel displaying the available pipes in the stock.
     */
    private JPanel createPipeStock() {
        JPanel stock = new JPanel();
        
        stock.setBackground(Color.black);

        int gameBoardRows = model.getHeight();
        int pipeStockGridRows = Math.max(gameBoardRows, 6);
        
        stock.setLayout(new GridLayout(pipeStockGridRows, 2));
        
        pipeStockImageViews = new ImageView[pipeStockGridRows][2];

        for (int row = 0; row < pipeStockGridRows; row++) {
            for (int col = 0; col < 2; col++) {
            	if (row >= 6) {
            		ImageView imageView = new ImageView();
            		pipeStockImageViews[row][col] = imageView;
            		stock.add(imageView);
            	} else {
            		BoardCell cell = getBoardCellAt(row, col);
            		PipeType type = getPipeTypeAt(row, col);
            		int rotations = getPipeRotationsAt(row, col);
            		int quantity = getPipeQuantity(type, rotations);
                    Image image = cell.getImage();
                    image = writeQuantityOn(image, quantity);
                    ImageView imageView = new ImageView(image, cell, quantity == 0);
                    pipeStockImageViews[row][col] = imageView;
                    stock.add(imageView);
            	}
            }
        }

        int stockWidth = 70 * 2;
        int stockHeight = 70 * pipeStockGridRows;
        stock.setPreferredSize(new Dimension(stockWidth, stockHeight));

        return stock;
    }
    
    /**
     * Gets the ImageView objects representing the pipe stock.
     *
     * @return A 2D array of ImageView objects for the pipe stock.
     */
    public ImageView[][] getPipeStockImageViews() {
    	return pipeStockImageViews;
    }

    /**
     * Creates a BoardCell instance based on the given row and column, using information about the pipe type,
     * rotations, and color obtained from other methods.
     *
     * @param row The row in the grid.
     * @param col The column in the grid.
     * @return A new BoardCell instance representing a game board cell.
     */
    private BoardCell getBoardCellAt(int row, int col) {
        PipeType type = getPipeTypeAt(row, col);
        int rotations = getPipeRotationsAt(row, col);
        PathComponentColor color = getPipeColorAt(row, col);
        return new BoardCell(type, color, rotations, false);
    }
    
    /**
     * Determines and returns the PipeType for a given row and column on the game board.
     *
     * @param row The row in the grid.
     * @param col The column in the grid.
     * @return The PipeType for the specified position on the game board.
     */
    private PipeType getPipeTypeAt(int row, int col) {
        PipeType[] pipeTypes = {
            PipeType.CROSS, PipeType.OVER, PipeType.LINE, PipeType.LINE,
            PipeType.TURN, PipeType.TURN, PipeType.TURN, PipeType.TURN,
            PipeType.FORK, PipeType.FORK, PipeType.FORK, PipeType.FORK
        };
        int typeIndex = row * 2 + col;
        return pipeTypes[typeIndex];
    }
    
    /**
     * Retrieves the number of rotations needed for a pipe element at a specified position on the game board.
     *
     * @param row The row in the grid.
     * @param col The column in the grid.
     * @return The number of rotations required for the pipe element.
     */
    private int getPipeRotationsAt(int row, int col) {
    	int[] rotations = { 0, 0, 0, 1, 1, 2, 0, 3, 0, 1, 3, 2 };
    	return rotations[row * 2 + col];
    }
    
    /**
     * Determines the PathComponentColor based on the availability of pipes of a certain type and rotations.
     *
     * @param row The row in the grid.
     * @param col The column in the grid.
     * @return The PathComponentColor based on pipe availability.
     */
    private PathComponentColor getPipeColorAt(int row, int col) {
    	PipeType type = getPipeTypeAt(row, col);
    	int rotations = getPipeRotationsAt(row, col);
        int quantity = getPipeQuantity(type, rotations);
        return quantity > 0 ? PathComponentColor.GRAY : PathComponentColor.DARK_GRAY;
    }

    /**
     * Retrieves the quantity of pipes in the stock for a given PipeType and rotations.
     *
     * @param type The type of the pipe.
     * @param rotations The number of rotations.
     * @return The quantity of pipes in the stock for the specified PipeType and rotations.
     */
    private int getPipeQuantity(PipeType type, int rotations) {
        return model.getStock().getPipeQuantity(type, rotations);
    }
    
    /**
     * Writes the quantity value on top of the given image, indicating the number of pipes available.
     *
     * @param image The image to which the quantity is added.
     * @param quantity The quantity of pipes to display.
     * @return An image with the quantity displayed on it.
     */
    private Image writeQuantityOn(Image image, int quantity) {
    	return AssetsUtil.writeNumberOnImage(image, quantity, 0, image.getHeight(null) - 5);
    }
    
    /**
     * Returns the panel representing the game board.
     *
     * @return The JPanel containing the game board elements.
     */
    public JPanel getBoardPanel() {
    	return gameBoard;
    }

    /**
     * Creates the panel for the game board display.
     *
     * @return A panel displaying the game board elements.
     */
    private JPanel createGameBoard() {
        JPanel gameBoard = new JPanel();

        gameBoard.setBackground(Color.black);

        int stockGridRows = ((GridLayout) pipeStock.getLayout()).getRows();
        int gameBoardGridHeight = Math.max(stockGridRows, model.getHeight());

        gameBoard.setLayout(new GridLayout(gameBoardGridHeight, model.getWidth()));
        
        gameBoardImageViews = new ImageView[gameBoardGridHeight][model.getWidth()];
        
        for (int row = 0; row < gameBoardGridHeight; row++) {
			for (int col = 0; col < model.getWidth(); col++) {
				if (row >= model.getHeight()) {
            		ImageView imageView = new ImageView();
            		gameBoardImageViews[row][col] = imageView;
					gameBoard.add(imageView);
				} else {
	                BoardElement element = model.getElement(row, col);
	                Image pipeImage = element.getImage();
	                boolean moveable = true;
	                if (element.getBoardElementType().equals(BoardElementType.CELL)) {
	                	BoardCell cell = (BoardCell)element;
	                	if (!cell.isSource() && !cell.isEmpty() && !cell.isAttached()) {
	                		moveable = false;
	                	}
	                }
	                ImageView imageView = new ImageView(pipeImage, element, moveable);
	                gameBoardImageViews[row][col] = imageView;
	                gameBoard.add(imageView);
                }
			}
		}
        
        int stockWidth = 70 * model.getWidth();
        int stockHeight = 70 * gameBoardGridHeight;
        gameBoard.setPreferredSize(new Dimension(stockWidth, stockHeight));

        return gameBoard;
    }
    
    /**
     * Retrieves the panel representing the pipe stock.
     *
     * @return The JPanel containing the pipe stock elements.
     */
    public JPanel getStockPanel() {
    	return pipeStock;
    }
    
    /**
     * Gets the ImageView objects representing the game board elements.
     *
     * @return A 2D array of ImageView objects for the game board.
     */
    public ImageView[][] getGameBoardImageViews() {
    	return gameBoardImageViews;
    }
    
    /**
     * Creates and configures a JPanel that contains control buttons, such as "Undo," "Redo," "Reset," and "Levels."
     *
     * @return A JPanel with control buttons for game actions.
     */
    private JPanel createButtons() {
    	JPanel buttons = new JPanel();
    	Font buttonFont = new Font("Arial", Font.BOLD, 18);
    	
		undo = new JButton("undo");
		redo = new JButton("redo");
		reset = new JButton("reset");
		levels = new JButton("levels");
		
		undo.setBackground(new Color(245, 245, 220));
		redo.setBackground(new Color(245, 245, 220));
		reset.setBackground(new Color(245, 245, 220));
		levels.setBackground(new Color(245, 245, 220));
		undo.setFont(buttonFont);
		redo.setFont(buttonFont);
		reset.setFont(buttonFont);
		levels.setFont(buttonFont);
		
		buttons.setLayout(new GridLayout(1, 5));
		buttons.add(undo);
		buttons.add(redo);
		buttons.add(reset);
		buttons.add(levels);
		
        int buttonsWidth = 70 * gameBoard.getWidth();
        int buttonsHeight = 50;
		buttons.setPreferredSize(new Dimension(buttonsWidth, buttonsHeight));
		
		return buttons;
    }

	/**
	 * Retrieves the "Undo" button.
	 *
	 * @return The JButton representing the "Undo" action.
	 */
    public JButton getUndoButton() {
    	return undo;
    }
    
    /**
     * Retrieves the "Redo" button.
     *
     * @return The JButton representing the "Redo" action.
     */
    public JButton getRedoButton() {
    	return redo;
    }
    
    /**
     * Retrieves the "Reset" button.
     *
     * @return The JButton representing the "Reset" action.
     */
    public JButton getResetButton() {
    	return reset;
    }
    
    /**
     * Retrieves the "Levels" button.
     *
     * @return The JButton representing the "Levels" action.
     */
    public JButton getLevelsButton() {
    	return levels;
    }
    
    /**
     * Refreshes the view components to match the game model's state.
     */
    public void refresh() {
        refreshPipeStock();
        refreshGameBoard();
        repaint();
    }

	/**
	 * Refreshes the pipe stock display by updating the images, elements, and moveability states of the pipe stock cells.
	 */
    private void refreshPipeStock() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 2; col++) {
            	if (row >= 6) continue;
            	BoardCell cell = getBoardCellAt(row, col);
                PipeType type = getPipeTypeAt(row, col);
                int rotations = getPipeRotationsAt(row, col);
                int quantity = getPipeQuantity(type, rotations);
                Image image = cell.getImage();
                image = writeQuantityOn(image, quantity);
                pipeStockImageViews[row][col].setImage(image);
                pipeStockImageViews[row][col].setElement(cell);
                pipeStockImageViews[row][col].setMoveable(quantity == 0);
            }
        }
    }
    

	/**
	 * Refreshes the game board display by updating the images, elements, and moveability states of the game board cells.
	 */
    private void refreshGameBoard() {
        for (int row = 1; row < model.getHeight() - 1; row++) {
            for (int col = 1; col < model.getWidth() - 1; col++) {
                ImageView imageView = gameBoardImageViews[row][col];
            	BoardCell cell = (BoardCell) model.getElement(row, col);
                Image image = cell.getImage();
                imageView.setImage(image);
                imageView.setElement(cell);
                imageView.setMoveable(cell.isEmpty() || cell.isAttached());
            }
        }
    }
}