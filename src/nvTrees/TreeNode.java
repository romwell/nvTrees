package nvTrees;
/**
 * This class represents a tree node in an nV tree.
 * <br>
 * See nvTree documentation for description of nV trees.
 * @author Romwell
 *
 */
public class TreeNode {
	/**
	 * Reference to the left Child
	 */
	public TreeNode left=null;
	
	/**
	 * Reference to the right Child
	 */
	public TreeNode right=null;
	
	/**
	 * Holds the color of the node;
	 * the color denotes the dimension along which the 
	 * pattern corresponding to the tree is cut
	 * <p><b>Color should be 0 if node is a leaf or an integer from 1 to 9 otherwise 
	 */
	public int color;
	
	/**
	 * The SuperPath to the node. See the documentation for {@link SuperPath} for details.
	 */
	private SuperPath superpath;
	
	/**
	 * Reference to the parent node
	 */
	public TreeNode parent;
	
	/**
	 * A string of 0's and 1's that holds the path from the root node;
	 * <b>0 means take the left path
	 * <b>1 means take the right path
	 */ 
	public String path;
	
	
	/**
	 * Creates a new TreeNode
	 * @param parent reference to the  parent node 
	 * @param color the color of the node
	 * @param isLeftChild is this node a left child ? value not used if parent is null
	 */
	public TreeNode(TreeNode parent, int color, boolean isLeftChild) throws TreeNodeException
	{
		if ((color<0)||(color>=NvTree.MAXCOL))
		{
			throw new TreeNodeException("The Color you specified for this node, "+color+", is invalid. Must be 0 to "+NvTree.MAXCOL+".");
		}
		this.parent = parent;
		this.color = color;
		this.superpath = new SuperPath();
		if (parent != null)
		{		
			String S = parent.path;
			this.superpath = new SuperPath(parent.superpath);
			int col = parent.color;			
			
			if (isLeftChild)
			{
				superpath.appendDown(col, true);
				this.path = S + '0';				
			}
			else
			{
				superpath.appendDown(col, false);
				this.path = S + '1';
			}
		}
		else
		{
			this.path="";
		}		
	}
	
	/**
	 * Counts the number of leaves in the subtree at this node
	 * @return the number of leaves in the subtree; 1 if it is a leaf
	 */
	public int getNumLeaves()
	{
		int count = 0; 
		if (left != null)
		{
			count += left.getNumLeaves();
		}
		if (right != null)
		{
			count += right.getNumLeaves();
		}
		if ((left == null)&&(right == null))
		{
			return 1;
		}
		else
		{
			return count;
		}
	}
	
	/**
	 * Reports whether this node has two children
	 * @return false if both child are present, true otherwise
	 */
	public boolean isLeaf()
	{
		return ((this.left == null) && (this.right == null));
	}

	/**
	 * Gets the node's superpath 
	 * @return A copy of the node's superpath
	 */
	public SuperPath getSuperpath() 
	{
		return new SuperPath(superpath);
	}

	
	
}
