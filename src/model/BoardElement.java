package model;

import java.awt.Image;

import model.enumeration.BoardElementType;

/**
 * An abstract class representing an element on the game board.
 *
 * @author hamza-okutucu
 */
public abstract class BoardElement {
    
    protected BoardElementType boardElementType;
    protected int rotations;
    protected Image image;

    /**
     * Initializes a new BoardElement with the specified board element type and number of rotations.
     *
     * @param boardElementType The type of board element (e.g., CELL, BORDER, etc.).
     * @param rotations The number of clockwise 90-degree rotations.
     */
    public BoardElement(BoardElementType boardElementType, int rotations) {
        this.boardElementType = boardElementType;
        this.rotations = rotations;
    }

    /**
     * Gets the type of the board element.
     *
     * @return The type of the board element (e.g., CELL, BORDER, etc.).
     */
    public BoardElementType getBoardElementType() {
        return boardElementType;
    }
    
    /**
     * Gets the number of rotations applied to the board element.
     *
     * @return The number of clockwise 90-degree rotations.
     */
    public int getRotations() {
        return rotations;
    }

    /**
     * Gets the image representation of the board element.
     *
     * @return The image representing the board element.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Updates the image representation of the board element.
     */
    public abstract void updateImage();
    
    /**
     * Creates a deep copy of the board element.
     *
     * @return A new board element with the same properties as this instance.
     */
    public abstract BoardElement deepCopy();
}
