package cs4620.ray1.shader;

import cs4620.ray1.shader.Texture;
import egl.math.Color;
import egl.math.Colord;
import egl.math.Vector2d;

/**
 * A Texture class that repeats the texture image as necessary for UV-coordinates
 * outside the [0.0, 1.0] range.
 * 
 * @author eschweic
 *
 */
public class RepeatTexture extends Texture {

	public Colord getTexColor(Vector2d texCoord) {
		if (image == null) {
			System.err.println("Warning: Texture uninitialized!");
			return new Colord();
		}
				
		// TODO#A2 Fill in this function.
		// 1) Convert the input texture coordinates to integer pixel coordinates. Adding 0.5
		//    before casting a double to an int gives better nearest-pixel rounding.
		// 2) If these coordinates are outside the image boundaries, modify them to read from
		//    the correct pixel on the image to give a repeating-tile effect.
		// 3) Create a Color object based on the pixel coordinate (use Color.fromIntRGB
		//    and the image object from the Texture class), convert it to a Colord, and return it.
		// NOTE: By convention, UV coordinates specify the lower-left corner of the image as the
		//    origin, but the ImageBuffer class specifies the upper-left corner as the origin.
			
		int x = (int) (texCoord.x * image.getWidth() + 0.5);
		int y = (int) ((1.0 - texCoord.y) * image.getHeight() + 0.5);
		
		x = x % image.getWidth();
		if (x < 0) x += image.getWidth();
		y = y % image.getHeight();
		if (y < 0) y += image.getHeight();
		
		Color c = Color.fromIntRGB(image.getRGB(x, y));
		return new Colord(c);
	}

}
