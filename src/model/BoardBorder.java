package model;

import java.awt.Image;

import model.enumeration.BoardElementType;
import model.enumeration.BorderType;
import util.AssetsUtil;

/**
 * Represents a border element on the game board.
 *
 * @author hamza-okutucu
 */
public class BoardBorder extends BoardElement {
    
    private BorderType borderType;

    /**
     * Initializes a new BoardBorder with the specified border type and number of rotations.
     *
     * @param borderType The type of border (CORNER or SIDE).
     * @param rotations  The number of clockwise 90-degree rotations.
     */
    public BoardBorder(BorderType borderType, int rotations) {
        super(BoardElementType.BORDER, rotations);
        this.borderType = borderType;
        updateImage();
    }
    
    /**
     * Copy constructor that creates a new BoardBorder from an existing one.
     *
     * @param boardBorder The BoardBorder to copy.
     */
    public BoardBorder(BoardBorder boardBorder) {
        super(boardBorder.getBoardElementType(), boardBorder.getRotations());
        this.borderType = boardBorder.borderType;
        updateImage();
    }
    
    /**
     * Gets the type of this border (CORNER or SIDE).
     *
     * @return The border type.
     */
    public BorderType getBorderType() {
        return borderType;
    }

    /**
     * Updates the image of the border based on its type and rotations.
     */
    @Override
    public void updateImage() {
        switch (borderType) {
            case CORNER:
                Image cornerImage = AssetsUtil.extractPipeImage(6, 3);
                image = AssetsUtil.rotateImage(cornerImage, rotations * 90);
                break;
            case SIDE:
                Image sideImage = AssetsUtil.extractPipeImage(6, 4);
                image = AssetsUtil.rotateImage(sideImage, rotations * 90);
                break;
            default:
                handleInvalidBorderType();
        }
    }

    /**
     * Handles an invalid border type by throwing an exception.
     */
    private void handleInvalidBorderType() {
        try {
            throw new Exception("Le type de bordure n'existe pas : " + borderType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a deep copy of the BoardBorder.
     *
     * @return A new BoardBorder with the same properties as this instance.
     */
    @Override
    public BoardBorder deepCopy() {
        return new BoardBorder(this);
    }
}