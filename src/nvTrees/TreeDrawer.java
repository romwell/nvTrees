package nvTrees;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * This class draws Trees. Construct this 
 * class with the tree that you want to draw and 
 * set DisplayPanel's paintItem to an instance
 * of this class for that tree to be drawn.
 * @author Romwell
 *
 */



public class TreeDrawer implements Drawable{


	/**
	 * The diameter of the node circle (when painted on canvas)
	 */
	public static int diam=20;
	

	/**
	 * The tree to draw
	 */
	public NvTree tree;
	
	/**
	 * X-offset
	 */
	public int X0=30;
	
	/**
	 * Y-offset
	 */	
	public int Y0=30;
	
	/**
	 * Holds labels for cuts in various dimensions to be placed on the tree nodes
	 */
	public String[] labels = {" ","-","|","3","4","5","6","7","8","9"};
	
	/**
	 * Holds the labels for the leaves;
	 */
	public int[] leaf_labels;
	
	/**
	 * Used for counting leaves while drawing 
	 */
	private int count=0;
	

	/**
	 * Rate at which the diameter size decreases when going one level down
	 */
	public static double dscale = 1.2; 

	
	
	/**
	 * Constructs a TreeDrawer to draw a tree
	 * @param tree the tree to draw
	 * @param circle_width diameter of the node circle
	 */
	public TreeDrawer(NvTree tree, int circle_width)
	{
		this.tree = tree;		
		diam = circle_width;
	}
	

	/**
	 * Constructs a TreeDrawer to draw a tree
	 * @param tree the tree to draw
	 * @param circle_width diameter of the node circle
	 * @param X0 X-offset of the drawing
	 * @param Y0 Y-offset of the drawing
	 */
	public TreeDrawer(NvTree tree, int circle_width, int X0, int Y0)
	{
		this.tree = tree;		
		diam = circle_width;
		this.X0 = X0;
		this.Y0 = Y0;
	}
	
	/**
	 * Constructs a TreeDrawer to draw a tree with labels on leaves
	 * @param tree the tree to draw
	 * @param circle_width diameter of the node circle
	 * @param dscale rate at which the diameter of the node decreases when going one level down 
	 * @param X0 X-offset of the drawing
	 * @param Y0 Y-offset of the drawing
	 * @param labels Array of string labels for the leaves
	 */
	public TreeDrawer(NvTree tree, int circle_width, double dscale, int X0, int Y0, int[] labels)
	{
		this.tree = tree;		
		diam = circle_width;
		this.X0 = X0;
		this.Y0 = Y0;
		this.leaf_labels = labels;
		TreeDrawer.dscale = dscale;
	}
	
	
	public void paint(Graphics G)
	{
		count = 0;
		int X = getWidthAt(tree.rootNode.left,diam,dscale); 
		int Y = 0;		
		paintAt(G, tree.rootNode, X, Y,diam);
	}
	
	/**
	 * Paints a subtree rooted at root on a graphics G 
	 * at coordinates (x,y)
	 * @param G graphics to be painted on 
	 * @param root the root of the subtree
	 * @param x X-coordinate of the root
	 * @param y Y-coordinated of the root
	 * @param diam diameter of the node circle
	 */
	public void paintAt(Graphics G, TreeNode root, int x, int y, int diam)
	{
		int w=diam;
		if (!root.isLeaf())
		{
			int nw = newDiam(w, dscale); //new width
			int Y,xL,xR;
			//int dh = Math.max(root.left., b)
			
			Y = y + w*2;
			
			//paint left subtree			
			xL = x-getWidthAt(root.left.right,newDiam(nw,dscale),dscale)-nw; 		
			G.drawLine(X0+x+w/2, Y0+y+w/2, X0+xL+nw/2, Y0+Y+nw/2);
			
			paintAt(G, root.left, xL, Y, nw);
			
			//paint right subtree
			xR = x+getWidthAt(root.right.left,newDiam(nw,dscale),dscale)+w;  		
			G.drawLine(X0+x+w/2, Y0+y+w/2, X0+xR+nw/2, Y0+Y+nw/2);
			paintAt(G, root.right, xR, Y,nw);

		}
		
		
		
		
		if (root.isLeaf())
		{
			G.setColor(Color.LIGHT_GRAY);				
		}
		else
		{
			G.setColor(Color.WHITE);				
		}
		
		//paint the node itself
			
		G.fillOval(X0+x, Y0+y, w, w);
		G.setColor(Color.BLACK);
		G.drawOval(X0+x, Y0+y, w, w);
		//special labels for cuts 1 and 2
		if (root.color==2)
		{
			G.drawLine(X0+x, Y0+y+w/2, X0+x+w, Y0+y+w/2);
			
		}
		else if (root.color==1)
		{
			
			G.drawLine(X0+x+w/2, Y0+y, X0+x+w/2, Y0+y+w);
		}
		else 
		{
			String label;
			if (root.isLeaf()&&(leaf_labels!=null)&&(count<leaf_labels.length))
			{
				label = ""+leaf_labels[count];//+"="+root.path;
				count++;
			}
			else
			{
				label = labels[root.color];
			}
			G.setFont(new Font("Courier",0,diam));
			FontMetrics m = G.getFontMetrics(); //get font metrics
			int fw = m.stringWidth(label); //get string width in this font
			int fh = m.getHeight();		   //get font height	
			int yoffset=diam+fh; //the Y-coordinate here is the baseline of string
			if (!root.isLeaf()) {yoffset=diam-(fh-diam)/2;} //we draw label above the node itself unless it's a leaf; below the node otherwise
			G.drawString(label, X0+x+(w-fw)/2, Y0+y+yoffset);  
		}
	}
	
	
	/**
	 * Gets the width of the diagram, in pixels
	 * @return the width of the diagram,
	 */
	public int getWidth()
	{
		return getWidth(tree, diam,dscale);
	}
	
	/**
	 * Gets the height of the diagram, in pixels
	 * @return the height of the diagram,
	 */
	public int getHeight()
	{
		int depth = tree.getDepth();
		double ans = 0;
		double nodesize = diam;
		for (int i=0;i<depth;i++)
		{
			ans += 3*nodesize;
			nodesize *= dscale;
		}
		return (int) ans;
	}

	

	/**
	 * Returns the diameter of the tree node on the next level
	 * as a function of the diameter on the current level and
	 * scale factor
	 * @param diam diamter of the node on a the current level
	 * @param dscale scale factor
	 * @return new diameter
	 */
	public static int newDiam(int diam, double dscale)
	{
		return (int)Math.ceil(diam*dscale); 
	}
	

	/**
	 * Returns the width of the subtree, in pixels, at the node
	 * (the width is used for drawing the tree)
	 * @param root the root of the subtree
	 * @param diam the diameter of the root node
	 * @param dscale the scale at which the diameter decreases from level to level
	 * @return width at the subtree based at the root
	 */
	public static int getWidthAt(TreeNode root, int diam, double dscale)
	{
		if (root==null)
		{
			return 0;
		}
		else
		if (!root.isLeaf())
		{
			int newdiam = newDiam(diam, dscale);
			return diam + getWidthAt(root.left,newdiam,dscale)+ getWidthAt(root.right,newdiam,dscale);
		}
		else			
		{
			return diam;
		}
	}

	/**
	 * Returns the width of the tree, in pixels
	 * (the width is used for drawing the tree)
	 * @param tree the tree to return the width of
	 * @param diam the diameter of the root node
	 * @param dscale the scale at which the diameter decreases from level to level 
	 * @return width at the subtree based at the root
	 */
	public static int getWidth(NvTree tree, int diam, double dscale)
	{
		return getWidthAt(tree.rootNode,diam,dscale);
	}

	

	
	
}
