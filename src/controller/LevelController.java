package controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import model.BoardCell;
import model.BoardElement;
import model.LevelModel;
import model.MenuModel;
import model.Pipe;
import model.enumeration.BoardElementType;
import model.enumeration.PathComponentColor;
import model.enumeration.PipeType;
import view.DraggedImage;
import view.ImageView;
import view.LevelView;
import view.MenuView;

/**
 * The controller for the game level, responsible for managing interactions
 * between the model and the view, as well as handling game elements manipulation.
 *
 * @author hamza-okutucu
 */
public class LevelController {

	private LevelModel model;
	private LevelView view;
	private ImageView selectedImageView;
	private DraggedImage dragImage;
	
    /**
     * Initializes a new level controller with the associated model and view.
     *
     * @param model The level model.
     * @param view  The level view.
     */
	public LevelController(LevelModel model, LevelView view) {
		this.model = model;
		this.view = view;
		selectedImageView = null;
		dragImage = null;
		
		addActionListenersToButtons();
        addMouseListenerToPipeStock();
        addMouseListenerToGameBoard();
	}
	
    /**
     * Adds action listeners to UI buttons. The buttons include "Undo," "Redo,"
     * "Reset," and "Levels."
     */
    private void addActionListenersToButtons() {
        JButton undoButton = view.getUndoButton();
        JButton redoButton = view.getRedoButton();
        JButton resetButton = view.getResetButton();
        JButton levelsButton = view.getLevelsButton();

        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.performUndo();
                view.refresh();
            }
        });

        redoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.performRedo();
                view.refresh();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.resetLevel();
                view.refresh();
            }
        });

        levelsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                
                MenuModel menuModel = new MenuModel();
                MenuView menuView = new MenuView();
                new MenuController(menuModel, menuView);
                view.dispose();
            }
        });
    }
    
    /**
     * Adds mouse listeners to pipe stock elements in the view. These listeners handle
     * drag-and-drop of pipes into the game.
     */
    private void addMouseListenerToPipeStock() {
        ImageView[][] components = view.getPipeStockImageViews();
        
        for (ImageView[] imageViews : components) {
        	for (ImageView imageView : imageViews) {
        		
                imageView.addMouseListener(new MouseAdapter() {
                	
                    @Override
                    public void mousePressed(MouseEvent e) {
                    	if (imageView.isMoveable()) return;
                    	
                    	selectedImageView = imageView;
                		
                        BoardCell boardCell = (BoardCell)selectedImageView.getElement();
                        Pipe pipe = boardCell.getPipe();
                        
                        int x = e.getXOnScreen();
                        int y = e.getYOnScreen();
                        
                		dragImage = new DraggedImage(pipe.getImage(), new Point(x, y));
                		dragImage.setLocation(x - dragImage.getWidth() / 2, y - dragImage.getHeight() / 2);
                    }
                    
                    @Override
                    public void mouseReleased(MouseEvent e) {
                    	if (selectedImageView == null) return;
                    	
                    	Point clickedBoardPos = getClickedBoardPos(e.getXOnScreen(), e.getYOnScreen());
                    	
                        if (clickedBoardPos != null) {
                            BoardCell boardCell = (BoardCell) selectedImageView.getElement();
                            int clickedRow = (int) clickedBoardPos.getY();
                            int clickedCol = (int) clickedBoardPos.getX();
                            BoardCell clickedCell = (BoardCell) model.getElement(clickedRow, clickedCol);

                            if (clickedCell.isEmpty()) {
                                model.setElement(clickedRow, clickedCol, boardCell);
                                model.getStock().removePipe(boardCell.getPipe().getType(), boardCell.getPipe().getRotations());
                                view.refresh();
                            } else {
                                dragImage.translateToOriginLocation();
                            }
                        } else {
                            dragImage.translateToOriginLocation();
                        }
                    	
                		selectedImageView = null;
                		dragImage.dispose();
                		dragImage = null;
                    }
                });
                
                imageView.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                    	if (selectedImageView == null) return;
                    	
                    	dragImage.setLocation(e.getXOnScreen() - dragImage.getWidth() / 2, e.getYOnScreen() - dragImage.getHeight() / 2);
                        dragImage.repaint();
                    }
                });
        	}
        }
    }
    
    /**
     * Gets the board position coordinates from the provided screen coordinates.
     *
     * @param x The X coordinate on the screen.
     * @param y The Y coordinate on the screen.
     * @return A Point object representing the coordinates on the game board,
     *         or null if the coordinates do not correspond to a valid position.
     */
    private Point getClickedBoardPos(int x, int y) {
    	Point pointOnBoard = new Point(x, y);
    	SwingUtilities.convertPointFromScreen(pointOnBoard, view.getBoardPanel());

        int boardWidth = view.getBoardPanel().getWidth();
        int boardHeight = view.getBoardPanel().getHeight();
        
        if (pointOnBoard.getX() < 0 || pointOnBoard.getY() < 0 ||
        		pointOnBoard.getX() >= boardWidth || pointOnBoard.getY() >= boardHeight) {
            return null;
        }
        
        int col = (int) (pointOnBoard.getX() / view.getGameBoardImageViews()[0][0].getWidth());
        int row = (int) (pointOnBoard.getY() / view.getGameBoardImageViews()[0][0].getHeight());
        
        if (row >= model.getHeight() || col >= model.getWidth()) {
        	return null;
        }
        
        
        BoardElement boardElement = model.getElement(row, col);
        
        if (!boardElement.getBoardElementType().equals(BoardElementType.CELL)) return null;
        
        BoardCell cell = (BoardCell) boardElement;
        
        if (cell.isAttached() || cell.isSource()) return null;
        
        return new Point(col, row);
    }
    
    /**
     * Adds mouse listeners to game board cells. These listeners handle interactions
     * with game cells, including dragging and dropping pipes and swapping pipes.
     */
    private void addMouseListenerToGameBoard() {
        ImageView[][] components = view.getGameBoardImageViews();
        
        for (int row = 0; row < components.length; row++) {
            for (int col = 0; col < components[row].length; col++) {
            	final int finalRow = row;
            	final int finalCol = col;
            	
            	ImageView imageView = components[row][col];
            	
                imageView.addMouseListener(new MouseAdapter() {
                	
                    @Override
                    public void mousePressed(MouseEvent e) {
                    	if (imageView.isMoveable()) return;
                        
                    	selectedImageView = imageView;
                    	
                        BoardCell boardCell = (BoardCell)selectedImageView.getElement();
                        
                        Pipe pipe = boardCell.getPipe();
                        
                        int x = e.getXOnScreen();
                        int y = e.getYOnScreen();
                        
                		dragImage = new DraggedImage(pipe.getImage(), new Point(x, y));
                		dragImage.setLocation(x - dragImage.getWidth() / 2, y - dragImage.getHeight() / 2);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (selectedImageView == null) return;
                        
                        int x = e.getXOnScreen();
                        int y = e.getYOnScreen();
                        
                        if (isClickedOnPipeStock(x, y)) {
                        	BoardCell clickedCell = (BoardCell) model.getElement(finalRow, finalCol);
                        	
                        	if (!clickedCell.isEmpty()) {
                        		BoardCell emptyCell = new BoardCell(PipeType.EMPTY, PathComponentColor.GRAY, 0, false);
                        		Pipe clickedPipe = clickedCell.getPipe();
                        		
                        		model.setElement(finalRow, finalCol, emptyCell);
                        		model.getStock().addPipe(clickedPipe.getType(), clickedPipe.getRotations());
                        		view.refresh();
                        	}
                        } else {
                        	Point clickedBoardPos = getClickedBoardPos(x, y);
                        	
                        	if (clickedBoardPos != null) {
                            	model.switchElements(finalRow, finalCol, (int)clickedBoardPos.getY(), (int)clickedBoardPos.getX());
                            	view.refresh();
                        	} else {
                        		dragImage.translateToOriginLocation();
                        	}
                        }
                        
                		selectedImageView = null;
                		dragImage.dispose();
                		dragImage = null;
                    }
                });

                imageView.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        if (selectedImageView == null) return;

                        dragImage.setLocation(e.getXOnScreen() - dragImage.getWidth() / 2, e.getYOnScreen() - dragImage.getHeight() / 2);
                        dragImage.repaint();
                    }
                });
            }
        }
    }
    
    /**
     * Checks if the given coordinates correspond to a position in the pipe stock.
     *
     * @param x The X coordinate on the screen.
     * @param y The Y coordinate on the screen.
     * @return true if the coordinates are in the pipe stock, otherwise false.
     */
    private boolean isClickedOnPipeStock(int x, int y) {
        Point pointOnStock = new Point(x, y);
        SwingUtilities.convertPointFromScreen(pointOnStock, view.getStockPanel());

        int stockWidth = view.getStockPanel().getWidth();
        int stockHeight = view.getStockPanel().getHeight();

        if (pointOnStock.getX() < 0 || pointOnStock.getY() < 0 ||
                pointOnStock.getX() >= stockWidth || pointOnStock.getY() >= stockHeight) {
            return false;
        }

        return true;
    }
}
