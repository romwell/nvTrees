package nvTrees;
import java.awt.Graphics;

/**
 * This interface is for classes that 
 * have graphical content that can be drawn
 * on a panel. The DisplayPanel would call
 * the paint methhod of a Drawable class
 * to paint it on a graphics
 * @author Romwell
 *
 */
public interface Drawable {

	/**
	 * Paints the object on a graphics 
	 * @param G the graphics to be painted on
	 */
	public void paint(Graphics G);
	
	
	/**
	 * Returns the width of the drawable object
	 */
	public int getWidth();
	
	/**
	 * Returns the height of the drawable object
	 */
	public int  getHeight();
	
}
