package util;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Utility class for handling game assets, including image extraction, manipulation, and file operations.
 *
 * @author hamza-okutucu
 */
public class AssetsUtil {
    
    /**
     * The path to the assets directory where game resources are stored.
     */
    public static final String ASSETS_DIRECTORY_PATH = "src/assets";
    
    /**
     * The path to the pipes image file within the assets directory.
     */
    public static final String PIPES_FILE_PATH = ASSETS_DIRECTORY_PATH + "/pipes.gif";
    
    /**
     * The size (width and height) of a pipe image.
     */
    public static final int PIPE_SIZE = 120;
    
    /**
     * The padding between pipe images
     */
    public static final int PIPE_PADDING = 20;

    /**
     * Retrieves an array of files that represent game levels from the assets directory.
     *
     * @return An array of files representing game levels (with a ".p" extension).
     */
    public static File[] getLevelFiles() {
        File levelDirectory = new File(ASSETS_DIRECTORY_PATH);
        
        if (levelDirectory.exists() && levelDirectory.isDirectory()) {
            File[] levelFiles = levelDirectory.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".p");
                }
            });

            return levelFiles;
        } else {
            return new File[0];
        }
    }

    /**
     * Extracts a pipe image from the "pipes.gif" image file.
     *
     * @param line   The line (row) of the desired pipe image.
     * @param column The column of the desired pipe image.
     * @return The extracted pipe image.
     */
    public static Image extractPipeImage(int line, int column) {
        File pipesFile = new File(PIPES_FILE_PATH);
        BufferedImage pipesImage = null;
        BufferedImage extractedPipe = null;

        try {
            pipesImage = ImageIO.read(pipesFile);

            int x = PIPE_SIZE * column + PIPE_PADDING * column;
            int y = PIPE_SIZE * line + PIPE_PADDING * line;

            extractedPipe = pipesImage.getSubimage(x, y, PIPE_SIZE, PIPE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return extractedPipe;
    }
    
    /**
     * Combines two images into one.
     *
     * @param first  The first image to combine.
     * @param second The second image to combine.
     * @return The resulting combined image.
     */
    public static Image combineImages(Image first, Image second) {
        if (first == null) return second;
        if (second == null) return first;

        int width = first.getWidth(null);
        int height = first.getHeight(null);

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();

        graphics.drawImage(first, 0, 0, null);
        graphics.drawImage(second, 0, 0, null);
        graphics.dispose();

        return result;
    }

    /**
     * Rotates an image by a specified number of degrees.
     *
     * @param image  The image to rotate.
     * @param degree The number of degrees to rotate the image.
     * @return The rotated image.
     */
    public static Image rotateImage(Image image, int degree) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        BufferedImage initialImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D initialImageGraphics = initialImage.createGraphics();

        initialImageGraphics.drawImage(image, 0, 0, null);
        initialImageGraphics.dispose();

        BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D rotatedImageGraphics = rotatedImage.createGraphics();

        rotatedImageGraphics.rotate(Math.toRadians(degree), width / 2, height / 2);
        rotatedImageGraphics.drawImage(initialImage, null, 0, 0);
        rotatedImageGraphics.dispose();

        return rotatedImage;
    }

    /**
     * Writes a number onto an image at a specified position.
     *
     * @param image  The image to write the number on.
     * @param number The number to write.
     * @param x      The X-coordinate for the number.
     * @param y      The Y-coordinate for the number.
     * @return The image with the number written on it.
     */
    public static Image writeNumberOnImage(Image image, int number, int x, int y) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();

        graphics.drawImage(image, 0, 0, null);
        graphics.setFont(new Font("Arial Black", Font.PLAIN, 20));
        graphics.drawString(Integer.toString(number), x, y);
        graphics.dispose();

        return result;
    }
}