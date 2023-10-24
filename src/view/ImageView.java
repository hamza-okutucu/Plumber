package view;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import model.BoardElement;

/**
 * A custom component for displaying images associated with a game board element.
 *
 * @author hamza-okutucu
 */
public class ImageView extends Component {
	
	private static final long serialVersionUID = 1L;
	private Image image;
    private BoardElement element;
    private boolean moveable;

    /**
     * Constructs an empty ImageView.
     * The image, board element, and moveable status are set to their default values.
     */
    public ImageView() {
    	image = null;
    	element = null;
    	moveable = true;
    }
    
    /**
     * Constructs an ImageView with the specified image, board element, and moveable status.
     *
     * @param image    The image to display in the view.
     * @param element  The associated game board element.
     * @param moveable Indicates whether the view is moveable.
     */
    public ImageView(Image image, BoardElement element, boolean moveable) {
        this.image = image;
        this.element = element;
        this.moveable = moveable;
    }

    /**
     * Paints the image on the component.
     *
     * @param g The graphics context for painting.
     */
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }
    
    /**
     * Gets the image displayed by this ImageView.
     *
     * @return The image associated with the view.
     */
    public Image getImage() {
    	return image;
    }

    /**
     * Gets the board element associated with this ImageView.
     *
     * @return The game board element linked to the view.
     */
    public BoardElement getElement() {
        return element;
    }
    
    /**
     * Checks if the ImageView is moveable.
     *
     * @return true if the view is moveable, false otherwise.
     */
    public boolean isMoveable() {
    	return moveable;
    } 

    /**
     * Sets the image to be displayed by this ImageView.
     *
     * @param image The new image to display.
     */
    public void setImage(Image image) {
    	this.image = image;
    }
    
    /**
     * Sets the board element associated with this ImageView.
     *
     * @param element The new game board element to link with the view.
     */
    public void setElement(BoardElement element) {
    	this.element = element;
    }
    
    /**
     * Sets the moveable status of the ImageView.
     *
     * @param moveable true to make the view moveable, false to make it non-moveable.
     */
    public void setMoveable(boolean moveable) {
    	this.moveable = moveable;
    }
}

