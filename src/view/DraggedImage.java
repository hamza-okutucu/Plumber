package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JFrame;

/**
 * A custom JFrame class for displaying a draggable image with transparency.
 *
 * @author hamza-okutucu
 */
public class DraggedImage extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private Image image;
    private Point originLocation;
    
    /**
     * Constructs a new DraggedImage object.
     *
     * @param image           The image to be displayed in the frame.
     * @param originLocation  The initial location of the image.
     */
    public DraggedImage(Image image, Point originLocation) {
        this.image = image;
        this.originLocation = originLocation;
        
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setSize(image.getWidth(null), image.getHeight(null));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Overrides the paint method to draw the image in the JFrame.
     *
     * @param g The graphics context in which to paint the image.
     */
    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }
    
    /**
     * Translates the image back to its original location by calculating the path
     * between the current location and the origin location.
     */
	public void translateToOriginLocation() {
		double sourcePosX = originLocation.getX();
		double sourcePosY = originLocation.getY();
		double targetPosX = getX();
		double targetPosY = getY();
		double slope = (targetPosY - sourcePosY) / (targetPosX - sourcePosX);
		double b = targetPosY - (slope * targetPosX);
		double x = targetPosX;
		double y = (slope * x) + b;
		boolean decrement = x > sourcePosX;
		
		while(x != sourcePosX && y != sourcePosY) {
			translate(new Point((int)x, (int)y));
			if(decrement) x--;
			else x++;
			y = (slope * x) + b;
		}
		
		dispose();
	}
	
	/**
	 * Translates the position of the frame to a new location specified by the given Point.
	 *
	 * @param location The new location, expressed as a Point object, where the frame will be moved.
	 */
	private void translate(Point location) {
		setLocation((int)location.getX(), (int)location.getY());
		revalidate();
		repaint();
	}
}