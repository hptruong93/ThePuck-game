package utilities;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import features.Log;

/**
 * Provide interface to load a sprite sheet
 * This is a static class. No instance of this class should be created.
 * @author VDa
 *
 */
public class SpriteSheetReader {
	
	/**
	 * Private constructor so that no instance is created
	 */
	private SpriteSheetReader() {
		throw new IllegalStateException("Cannot create an instance of static class SpriteSheetReader");
	}
	
	/**
	 * Read sprite sheet from a file and convert it to an ArrayList of images
	 * @param dir directory path to the spritesheet. This includes the spritesheet name
	 * @param instances number of instances present on the sheet
	 * @param columnNumber the number of column of the sprite sheet
	 * @param scaleWidth width of the desired output image
	 * @param scaleHeight height of the desired output image
	 * @param initialRotation angle in radian representing the bias of the image (standard image faces 0 to start with)
	 * @return ArrayList of image of size [scaleWidth x scaleHeight] loaded from the sprite sheet
	 */
	public static ArrayList<Image> readImage(String dir, int instances, int columnNumber, int scaleWidth, int scaleHeight, double initialRotation) {
		ArrayList<Image> output = new ArrayList<Image>();
		
		try {
            BufferedImage img = ImageIO.read(new File(dir));
            int width = img.getWidth() / columnNumber;
            int numberOfRow = instances % columnNumber == 0 ? instances / columnNumber : instances / columnNumber + 1;
            int height = img.getHeight() / numberOfRow;
            
            int column = 0;
            int row = 0;
            
            while (true) {
            	BufferedImage newly = rotateImage(img.getSubimage(column * width, row * height, width, height), initialRotation);
            	output.add(newly.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH));
            	
            	column++;
            	if (column == columnNumber) {
            		column = 0;
            		row++;
            	}
            	if (row * columnNumber + column >= instances) {
            		break;
            	}
            }
            return output;
        } catch (IOException e) {
        	Log.writeLog(e);
        	return null;
        }
	}
	
	/**
	 * Return the rotation of the input image by an angle
	 * @param input the original image
	 * @param angle the angle by which the original image will be rotated in radian
	 * @return the original image rotated by angle
	 */
	private static BufferedImage rotateImage(BufferedImage input, double angle) {
		AffineTransform tx = AffineTransform.getRotateInstance(angle, input.getWidth()/2, input.getHeight()/2);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		BufferedImage output = op.filter(input, null);
		
		return output;
	}
}