package model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import model.enumeration.PathComponentColor;
import model.enumeration.PipeType;
import util.AssetsUtil;

/**
 * Represents a pipe element in the game board.
 *
 * @author hamza-okutucu
 */
public class Pipe {
    
    private PipeType type;
    private int rotations;
    private List<PathComponent> pathComponents;
    private Image image;
    
    /**
     * Constructs a new Pipe instance with the specified type, color, and rotations.
     *
     * @param type      The type of the pipe.
     * @param color     The color of the pipe.
     * @param rotations The number of clockwise rotations for the pipe.
     */
    public Pipe(PipeType type, PathComponentColor color, int rotations) {
        this.type = type;
        this.rotations = rotations;
        pathComponents = PathComponent.getPathComponents(type, color, rotations);
        updateImage();
    }
    
    /**
     * Constructs a copy of an existing Pipe instance.
     *
     * @param pipe The Pipe instance to copy.
     */
    public Pipe(Pipe pipe) {
        this.type = pipe.type;
        this.rotations = pipe.rotations;
        this.pathComponents = new ArrayList<>();
        for (PathComponent component : pipe.pathComponents) {
            this.pathComponents.add(new PathComponent(component));
        }
        updateImage();
    }

    /**
     * Gets the type of the pipe.
     *
     * @return The type of the pipe.
     */
    public PipeType getType() {
        return type;
    }

    /**
     * Gets the number of clockwise rotations for the pipe.
     *
     * @return The number of rotations.
     */
    public int getRotations() {
        return rotations;
    }

    /**
     * Gets the list of path components that make up the pipe.
     *
     * @return The list of path components.
     */
    public List<PathComponent> getPathComponents() {
        return pathComponents;
    }

    /**
     * Gets the path component at the specified index.
     *
     * @param index The index of the path component.
     * @return The path component at the specified index.
     */
    public PathComponent getPathComponentAt(int index) {
        return pathComponents.get(index);
    }
    
    /**
     * Gets the image representation of the pipe.
     *
     * @return The image of the pipe.
     */
    public Image getImage() {
    	return image;
    }
    
    /**
     * Updates the image representation of the pipe based on its type and path components.
     */
    public void updateImage() {
        Image emptyImage = AssetsUtil.extractPipeImage(6, 0);

        switch (type) {
            case LINE:
            case FORK:
            case CROSS:
            case TURN:
                updateImageForStandardPipe();
                break;
            case OVER:
                updateImageForOverPipe();
                break;
            case SOURCE:
                updateImageForSourcePipe();
                break;
            case EMPTY:
                image = emptyImage;
                break;
            default:
                handleInvalidPipeType();
        }
    }
    
    /**
     * Updates the image representation for a standard pipe.
     * Determines the pipe's image based on its type and color, and applies rotations.
     */
    private void updateImageForStandardPipe() {
        int pipeTypeColumn = type.ordinal();
        int pipeColorLine = getPathComponentAt(0).getColor().ordinal();
        
        Image pipeImage = AssetsUtil.extractPipeImage(pipeColorLine, pipeTypeColumn);

        image = rotatePipeImage(pipeImage);
    }
    
    /**
     * Updates the image representation for an over pipe.
     * Combines vertical and horizontal images to represent an over pipe.
     */
    private void updateImageForOverPipe() {
        Image verticalLineImage = getRotatedPipeImage(1, getPathComponentAt(0));
        Image horizontalLineImage = getRotatedPipeImage(2, getPathComponentAt(1));

        image = AssetsUtil.combineImages(verticalLineImage, horizontalLineImage);
    }
    
    /**
     * Updates the image representation for a source pipe.
     * Represents a source pipe with a rotated image.
     */
    private void updateImageForSourcePipe() {
    	image = getRotatedPipeImage(type.ordinal(), getPathComponentAt(0));
    }
    
    /**
     * Retrieves a rotated pipe image based on type and path component color.
     *
     * @param type      The type of pipe for image selection.
     * @param component The path component providing color information.
     * @return The rotated image representing the pipe.
     */
    private Image getRotatedPipeImage(int type, PathComponent component) {
        int colorLine = component.getColor().ordinal();
        Image pipeImage = AssetsUtil.extractPipeImage(colorLine, type);

        pipeImage = rotatePipeImage(pipeImage);

        return pipeImage;
    }
    
    /**
     * Rotates an image based on the number of clockwise rotations.
     *
     * @param image The image to be rotated.
     * @return The rotated image.
     */
    private Image rotatePipeImage(Image image) {
        return AssetsUtil.rotateImage(image, rotations * 90);
    }
    
    /**
     * Handles an invalid pipe type by throwing an exception.
     * This method is used for error handling when an unsupported pipe type is encountered.
     */
    private void handleInvalidPipeType() {
        try {
            throw new Exception("Le type de tuyau n'existe pas : " + type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a deep copy of the Pipe instance.
     *
     * @return A deep copy of the Pipe.
     */
    public Pipe deepCopy() {
        return new Pipe(this);
    }
}
