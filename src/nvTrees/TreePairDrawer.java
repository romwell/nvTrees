package nvTrees;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;


/**
 * A class to draw a TreePair. Construct
 * this class with a TreePair you want to
 * draw and pass to a DisplayPannel as 
 * the paintItem to be drawn.
 * @author Romwell
 *
 */
public class TreePairDrawer implements Drawable{
	
	/**
	 * A Tree Pair to be drawn
	 */
	public TreePair pair;
	
	/**
	 * TreeDrawer to draw the left tree;
	 */
	public TreeDrawer leftDrawer;
	
	/**
	 * TreeDrawer to draw the right tree;
	 */
	public TreeDrawer rightDrawer;
	
	/**
	 * X-offset
	 */
	public int X0;
	
	/**
	 * Y-offset
	 */
	public int Y0;
	
	/**
	 * Tree node diameter
	 */
	public static int diameter;
	
	/**
	 * Enablling or disabling pattern drawing
	 */
	public boolean drawPatterns=true;
	
	/**
	 * Creates an instance of the tree pair drawer, a class to draw tree pairs.
	 * Add an instance to the DisplayPanel to draw a tree pair
	 * @param pair the tree pair to draw 
	 * @param diameter root node diameter
	 * @param dscale rate at which the diameter of the node decreases when going one level down
	 * @param X0 x-offset of the drawing
	 * @param Y0 y-offset of the drawing
	 * @param drawPaterns enables or disables 2D pattern drawwing
	 */
	public TreePairDrawer(TreePair pair,int diameter, double dscale, int X0, int Y0, boolean drawPatterns) 
	{	
		this.pair = pair;		
		TreePairDrawer.diameter = diameter;
		this.X0 = X0;
		this.Y0 = Y0;
		this.drawPatterns = drawPatterns;
		int[] leftperm  = generateArray(pair.getPermutation().size());
		this.leftDrawer = new TreeDrawer(pair.left_tree,diameter,dscale,X0,Y0,leftperm);
		int W = leftDrawer.getWidth();
		//int H = leftDrawer.getHeight();
		try
		{
			this.rightDrawer = new TreeDrawer(pair.right_tree,diameter,dscale,X0+diameter+W,Y0,pair.getPermutation().toArray());
		}
		catch (TreeNodeException e)
		{
			JOptionPane.showMessageDialog(null, "Permutation contains an error;  probably not bijective. Message: \n"+e.errorString);
		}
	}

	/**
	 * Generates an array with numbers 1,2,...,n
	 * @param n how many numbers should there be 
	 * @return {1,2,3,..,n}
	 */
	private static int[] generateArray(int n)
	{
		int [] A = new int[n];
		for (int i=0;i<n;i++)
		{
			A[i]=i+1;
		}
		return A;
	}
	
	
	public void paint(Graphics G)
	{
		leftDrawer.leaf_labels = generateArray(pair.getPermutation().size());;
		int W = leftDrawer.getWidth();
		leftDrawer.paint(G);
		try
		{			
			rightDrawer.X0 = this.X0 + diameter + W;
			rightDrawer.paint(G);
			rightDrawer.leaf_labels = this.pair.getPermutation().toArray();
		}
		catch (TreeNodeException e)
		{
			
			String S1= "Permutation error! \n Cannot draw right tree =(";
			String S2= "Message: \n"+e.errorString;
			G.drawString(S1, X0+diameter+W, Y0);
			G.drawString(S2, X0+diameter+W, Y0+20);
			
		}		
		catch (NullPointerException e)
		{
			String S1= "Unknown error - Cannot draw right tree =(";
			String S2= "Message: \n"+e.getMessage();
			G.drawString(S1, X0+diameter+W, Y0);
			G.drawString(S2, X0+diameter+W, Y0+20);
			
		}
		
		if (drawPatterns) {this.drawPatterns(G);}
	}
	
	
	/**
	 * Attempts to draw a collection of blocks as a 2D pattern (only color dimensions 1 and 2 are used)
	 * @param G Graphics to paint on 
	 * @param blocks the collection of blocks (SuperPaths) to paint
	 * @param labels a Map containing the labels for the blocks
	 * @param X0 X-Offset
	 * @param Y0 Y-Offset
	 * @param width the width of the pattern, in pixels
	 */
	public static void drawPatternAt(Graphics G, Collection<SuperPath> blocks, Map<SuperPath, String> labels, int X0, int Y0, int width)
	{
		Font f = G.getFont();
		for (SuperPath P:blocks)
		{
			String S = labels.get(P);
			int X = (int)(X0 + width * P.getOffset(1));
			int Y = (int)(Y0 + width * P.getOffset(2));
			int W = (int) (width * P.getWidth(1));
			int H = (int) (width * P.getWidth(2));
			G.drawRect(X, Y, W, H);
			int fontSize = (int) (Math.min(W, H)/ (S.length()*1.0) * 0.8);
			Font cur = new Font("Courier",0,fontSize);
			G.setFont(cur);
			FontMetrics fm =  G.getFontMetrics();
			int sW = fm.stringWidth(S);
			int sH = fm.getMaxAscent();
			G.drawString(S, X+(W-sW)/2, Y+(H+sH)/2);
		}
		G.setFont(f);
	}
	
	
	/**
	 * Draws the TreePair as a 2D pattern pair (i.e., ignoring all color dimensions except 1 and 2) 
	 * @param G graphics to draw on
	 * @param LX0 left pattern X offset
	 * @param LY0 left pattern Y offset
	 * @param RX0 right pattern X offset
	 * @param RY0 right pattern Y offset
	 * @param width pattern width
	 */
	public void drawPatternsAt(Graphics G, int LX0, int LY0, int RX0, int RY0, int width)
	{
		TreePermutation perm = this.pair.getPermutation();
		ArrayList<SuperPath> left = pair.left_tree.detailedDFS();
		ArrayList<SuperPath> right = pair.right_tree.detailedDFS();
		HashMap<SuperPath, String> leftLabels = new HashMap<SuperPath, String>();
		HashMap<SuperPath, String> rightLabels = new HashMap<SuperPath, String>();
		int cur_label = 1;
		for (SuperPath L:left)
		{
			SuperPath R = perm.get(L);
			leftLabels.put(L, ""+cur_label);
			rightLabels.put(R, ""+cur_label);
			cur_label++;
		}
		drawPatternAt(G, left, leftLabels, LX0, LY0, width);
		drawPatternAt(G, right, rightLabels, RX0, RY0, width);
	}
	
	public void drawRectDiagramAt(Graphics G, int X0, int Y0, int width, int height) 
	{
		int col = pair.left_tree.rootNode.color;
		TreePermutation perm = this.pair.getPermutation();		
		ArrayList<SuperPath> left = pair.left_tree.detailedDFS();
		
		for (SuperPath L:left)
		{
			SuperPath R = perm.get(L);
			int XL = (int)(X0 + width * L.getOffset(col));
			int XR = (int)(X0 + width * R.getOffset(col));
			int WL = (int) (width * L.getWidth(col));
			int WR = (int) (width * R.getWidth(col));
			int [] x = {XL, XL+WL, XR+WR, XR};
			int [] y = {Y0, Y0, Y0+height, Y0+height};
			G.drawPolygon(x, y, 4);
		}
	}
	
	
	/**
	 * Draws the TreePair as a 2D pattern pair (i.e., ignoring all color dimensions except 1 and 2)
	 * with some pre-set parameters 
	 * @param G graphics to draw on
	 */
	public void drawPatterns(Graphics G)
	{
		int width = patternWidth();
		int LX0 = X0;
		int RX0 = LX0 + diameter + width;
		int LY0 = Y0 + Math.max(leftDrawer.getHeight(), rightDrawer.getHeight());
		int RY0 = LY0;
		boolean drawRectDiagram = false;
		try
		{
			
			drawRectDiagram = (!TreePair.isMultiDimensional(pair)) && pair.getPermutation().preservesOrder();;
		}
		catch (TreeNodeException E) {/*TODO: what really should be done then? */}
		
		if (drawRectDiagram)
		{			
			drawRectDiagramAt(G, X0, RY0, width * 2, (int) width/4);
		}
		else
		{
			this.drawPatternsAt(G, LX0, LY0, RX0, RY0, width);
		}
	}
	
	public int getWidth()
	{
		return diameter+leftDrawer.getWidth()+rightDrawer.getWidth();
	}
	
	public int getHeight()
	{
		return Math.max(leftDrawer.getHeight(), rightDrawer.getHeight()) + diameter*3 + patternWidth();
	}
	
	/**
	 * Calculates pattern width based on treenode diameter 
	 * @return width of a pattern
	 */
	private static int patternWidth()
	{
		return diameter * 5;
	}
}
