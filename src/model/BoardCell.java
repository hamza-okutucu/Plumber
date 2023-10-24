package model;

import java.awt.Image;

import model.enumeration.BoardElementType;
import model.enumeration.PathComponentColor;
import model.enumeration.PipeType;
import util.AssetsUtil;

/**
 * Represents a cell on the game board containing a pipe.
 *
 * @author hamza-okutucu
 */
public class BoardCell extends BoardElement {
    
    private boolean attached;
    private Pipe pipe;

    /**
     * Initializes a new BoardCell with the specified pipe type, pipe color, rotations, and attachment status.
     *
     * @param pipeType   The type of the pipe (e.g., LINE, FORK, CROSS, TURN, OVER, SOURCE, or EMPTY).
     * @param pipeColor  The color of the pipe component.
     * @param rotations  The number of clockwise 90-degree rotations.
     * @param attached   Indicates if the cell is attached to the board.
     */
    public BoardCell(PipeType pipeType, PathComponentColor pipeColor, int rotations, boolean attached) {
        super(BoardElementType.CELL, rotations);
        pipe = new Pipe(pipeType, pipeColor, rotations);
        this.attached = attached;
        updateImage();
    }
    
    /**
     * Copy constructor that creates a new BoardCell from an existing one.
     *
     * @param boardCell The BoardCell to copy.
     */
    public BoardCell(BoardCell boardCell) {
        super(boardCell.getBoardElementType(), boardCell.getRotations());
        this.attached = boardCell.attached;
        this.pipe = new Pipe(boardCell.pipe);
        updateImage();
    }

    /**
     * Checks if the cell is attached to the board.
     *
     * @return true if the cell is attached, false otherwise.
     */
    public boolean isAttached() {
        return attached;
    }
    
    /**
     * Checks if the cell contains a source pipe.
     *
     * @return true if the cell contains a source pipe, false otherwise.
     */
    public boolean isSource() {
        return pipe.getType().equals(PipeType.SOURCE);
    }

    /**
     * Checks if the cell is empty (no pipe).
     *
     * @return true if the cell is empty, false otherwise.
     */
    public boolean isEmpty() {
        return pipe.getType().equals(PipeType.EMPTY);
    }

    /**
     * Gets the pipe contained within the cell.
     *
     * @return The pipe object.
     */
    public Pipe getPipe() {
        return pipe;
    }

    /**
     * Updates the image representation of the cell based on its pipe type and attachment status.
     */
    @Override
    public void updateImage() {
    	Image emptyImage = AssetsUtil.extractPipeImage(6, 0);
    	
    	if (attached) {
    		Image screwsImage = AssetsUtil.extractPipeImage(6, 5);
    		emptyImage = AssetsUtil.combineImages(emptyImage, screwsImage);
    	}
    	
        switch (pipe.getType()) {
            case LINE:
            case FORK:
            case CROSS:
            case TURN:
            case OVER:
                image = AssetsUtil.combineImages(emptyImage, pipe.getImage());
                break;
            case SOURCE:
            	Image borderImage = AssetsUtil.extractPipeImage(6, 4);
            	borderImage = AssetsUtil.rotateImage(borderImage, (pipe.getRotations() + 2) * 90);
            	image = AssetsUtil.combineImages(borderImage, pipe.getImage());
                break;
            case EMPTY:
                image = emptyImage;
                break;
            default:
                handleInvalidPipeType();
        }
    }
    
    /**
     * Handles an invalid pipe type by throwing an exception.
     */
    private void handleInvalidPipeType() {
        try {
            throw new Exception("Le type de tuyau n'existe pas : " + pipe.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a deep copy of the BoardCell.
     *
     * @return A new BoardCell with the same properties as this instance.
     */
    @Override
    public BoardCell deepCopy() {
        return new BoardCell(this);
    }
}
