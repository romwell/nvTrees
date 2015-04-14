package nvTrees;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;




/**
 * This class represents an nV tree.
 * nV trees correspong to pattern diagrams, which
 * are diadic decompositions of a square. Each
 * non-leaf node represents cutting current block
 * of an n-dimensional cube in half along the dimension
 * that is stored in node color.
 * <p>
 * The underlying data structure thus is just a tree with 
 * colors on non-leaf nodes and labels on the leaf nodes. 
 * 
 * @author Romwell
 *
 */
public class NvTree {
	
	/**
	 * Maximum number of colors the tree can hold.
	 * Do not set higher than 10: for convinience, 
	 * one symbol = one color. Fix the corresponding 
	 * string parsing code if you intend to use
	 * more than 10 colors.
	 */
	static final int MAXCOL = 10;
	
	
	/**
	 * Holds the root node of the tree
	 */
	public TreeNode rootNode;
	
	
	/**
	 * Constructs an nV tree from a string representation
	 * <p>
	 * <li>The structure string is obtained by the depth-first search of a 
	 * tree, where 0 is written whenever a node
	 * is a leaf and node color (1..9) is written otherwise. 
	 * @param structure Structure string
	 */
	public NvTree(String structure) throws TreeNodeException
	{		
		rootNode = new TreeNode(null,0,true);
		if (structure.startsWith("random")) //if we want to create a random tree
		{
			String S = structure.replaceAll("random", "");
			StringTokenizer T = new StringTokenizer(S);
			if (T.countTokens()==2)
			{
				try
				{
					int numnodes = Integer.valueOf(T.nextToken());
					int maxcol = Integer.valueOf(T.nextToken());
					structure = NvTree.generateRandomTree(numnodes, maxcol);
				}
				catch (NumberFormatException e)
				{
					throw new TreeNodeException("Invalid arguments: must be 2 space-sepaated integers: "+S);
				}
			}
			else throw new TreeNodeException("Not enough arguments for random tree in this string: "+S);
		}
		
		
		String S = createTreeFromString(rootNode, structure);
		if (S.length() > 0)
		{
			throw new TreeNodeException("Leftover symbols left: "+S);
		}
	}	
	
	
	/**
	 * Creates a tree structure from the given root and string representation
	 * of a tree
	 * @param root the root node of the tree structure
	 * @param structure String containing the tree structure
	 * @return used internally to pass back the structure string as 
	 * it is being processed
	 */
	private static String createTreeFromString(TreeNode root, String structure) throws TreeNodeException
	{		
		if (structure.length() > 0)
		{
			String color = "" + structure.charAt(0);
			try
			{
				int col = Integer.valueOf(color).intValue();			
				String S = structure.substring(1);
				if (col == 0) //if the node is a leaf node...
				{
					root.color = 0;
					return S; //eat the first symbol and return
				}
				else	//if the node is not a leaf node...
				{
					root.color = col;
					TreeNode left = new TreeNode(root, col, true);
					TreeNode right = new TreeNode(root, col, false);				
					root.left = left;
					root.right = right;
					S = createTreeFromString(left,S);
					S = createTreeFromString(right,S);
					return S;
				}
			}
			catch(NumberFormatException e)
			{
				throw new TreeNodeException("You have entered an inavalid tree description: " +
						"\n the following character is not a digit \n"+e.getMessage());
			}
		}
		else //if the structure string is empty, then something went wrong
		{
			throw new TreeNodeException("Not enough symbols for a tree structure !");
		}
	}
	
	
	/**
	 * Counts the number of leaves in the tree
	 */
	public int getNumLeaves()
	{
		return rootNode.getNumLeaves();
	}
	
	
	/**
	 * Returns the string form of this Tree
	 */
	public String toString()
	{
		return getStringAt(rootNode);
	}
	
	/**
	 * Returns the string representation of a subtree
	 * @param root The root node of the subtree
	 * @return The string representation of the subtree
	 */
	public static String getStringAt(TreeNode root)
	{
		String S ="";
		S += root.color;
		if (!root.isLeaf()) 
		{
			S+=getStringAt(root.left);		
			S+=getStringAt(root.right);
		}
		return S;
	}

	/**
	 * Gets color depths for each color 1..9
	 * <p>
	 * <li>result[0] = 1, always
	 * <li>result[i] = maximum depth of color i, i.e. the largest amount
	 * of times color i occurred on the path from the root to a leaf 
	 * @return the array containing color depths for each color 
	 */
	public int[] getColorDepths()
	{
		return getColorDepthsAt(rootNode);
	}
	/**
	 * Gets the color depths in the subtree at the root node in the parameter
	 * @param root the root node of the subtree to check
	 * @return the array containing color depths; see getColorDepths() for more details
	 */
	public static int [] getColorDepthsAt(TreeNode root)
	{		
		int[] depths = new int[MAXCOL];
		depths[root.color]=1;
		if (!root.isLeaf())
		{
			int[] left_depths = getColorDepthsAt(root.left);
			int[] right_depths = getColorDepthsAt(root.right);
			for (int i=0;i<depths.length;i++)
			{
				depths[i] +=Math.max(left_depths[i], right_depths[i]);
			}
		}
		return depths;
	}
	
	/**
	 * Returns the largest color depth in this tree 
	 * @return the largest amount of times a color occurred on a path
	 * from the root to a leaf in the tree
	 */
	public int maxColorDepth()
	{
		int[] depths = getColorDepths();
		int max = 1;
		for (int i=0;i<depths.length;i++)
		{
			if (depths[i]>max)
			{
				max = depths[i];
			}
		}
		return max;
	}
	
	/**
	 * Returns the largest color label used 
	 * @return the largest color value that appears in this tree 
	 */	
	public int maxColorValue()
	{
		return maxColorValueAt(this.rootNode);
	}
	
	/**
	 * Returns the largest color label in a subtree
	 * @param node the root of the subtree 
	 * @return the largest color value that appears in this tree 
	 */	
	private int maxColorValueAt(TreeNode node)
	{
		if (!node.isLeaf())
		{
			int L = maxColorValueAt(node.left);
			int R = maxColorValueAt(node.right);
			int C = node.color;
			return Math.max(Math.max(L,R), C);
		}
		return 0;
	}
	
	/**
	 * Returns the depth of the tree
	 * @return the depth of the tree
	 */
	public int getDepth()
	{
		return getDepthAt(rootNode);
	}
	
	/**
	 * Returns the depth of the subtree at the node
	 * @param root the root of the subtree
	 * @return depth at the subtree based at the root
	 */
	public static int getDepthAt(TreeNode root)
	{
		if (!root.isLeaf())
		{
			return 1+Math.max(getDepthAt(root.left), getDepthAt(root.right));
		}
		else
		{
			return 1;
		}
	}
	



	
	/**
	 * Returns the array list that contains unique node labels
	 * (stored in node.path field) of the LEAVES as they are being
	 * traversed Depth-First
	 * @return DFS-ordered list of node path strings
	 */
	public ArrayList<String> traverseDFS()
	{				
		ArrayList<String> ans = new ArrayList<String>();
		traverseDFSat(this.rootNode, ans);
		return ans;
	}
	
	/**
	 * Traverses the subtreet rooted at root depth-first and puts the 
	 * leaf node labels into the list as they are discovered
	 * @param root the root of the subtree to be searched
	 * @param list the list to add node labels into
	 */
	public static void traverseDFSat (TreeNode root, ArrayList<String> list)
	{
		if (root.isLeaf())
		{
			list.add(root.path);
		}
		else
		{
			traverseDFSat(root.left, list);
			traverseDFSat(root.right, list);
		}
	}
	
	/**
	 * Returns a node by its path value; throws an exception if the tree does not contain such node.
	 * @param path path to the node (also store in the node's path field; see {@link TreeNode} documentation
	 * for details
	 * @return the node whose path was specified
	 */
	public TreeNode nodeByPath(String path) throws TreeNodeException
	{
		return nodeByPathAt(this.rootNode, path);
	}
	
	
	/**
	 * Returns a node by relative path from another node. throws an exception if the tree does not contain such node.
	 * The path of the returned node is the path of the root concatenated with path in the request
	 * @param start_node start node
	 * @param path path to the node from the start node
	 * @return the node whose path was specified
	 */
	public TreeNode nodeByPathAt(TreeNode start_node, String path) throws TreeNodeException
	{
		if (path.length() > 0)
		{
			if (start_node.isLeaf())
			{
				throw new TreeNodeException("No leaf with such path !");
			}
			else
			{
				char c = path.charAt(0);
				String S = path.substring(1);
				if (c=='0')
				{
					return nodeByPathAt(start_node.left,S);
				}
				else
				{
					return nodeByPathAt(start_node.right,S);
				}
			}
		}
		else
		{
			return start_node;
		}
	}
		
	/**
	 * Returns an int array as a string
	 * @param A integer array
	 * @return its string form
	 */
	public static String arrayToString(int[] A)
	{
		String S="";
		for (int i=0;i<A.length;i++)
		{
			S+=A[i]+" ";
		}
		return S;
	}
	
	
	
	/**
	 * Does a detailed DFS of the tree, returning an list of 
	 * SuperPaths of each node
	 * @return SuperPaths to nodes as they are discovered
	 */
	public ArrayList<SuperPath>detailedDFS()
	{
		 ArrayList<SuperPath> ans = new ArrayList<SuperPath>();
		 String[] cur_path = new String[MAXCOL];
		 try
		 {
			 detailedDFSAt(this.rootNode, ans, cur_path);
		 }
		 catch (TreeNodeException e)
		 {
			 JOptionPane.showMessageDialog(null, "Unknown error occurred during DFS: \n "+e.errorString);
		 }
		 return ans;
	}
	
	/**
	 * Does a detailed DFS of the subtree at the root, and fills the ans array with Superpaths
	 * @param root  the root of the subtree to search
	 * @param ans a list to be filled with SuperPaths to each node in the order of discovery
	 * @param cur_path superpath to root's parent (empty array if root is actually a tree root) - an array of size NvTree.MAXCOL 
	 * @throws TreeNodeException if you try to invoke this method with null-reference or small array
	 */
	public static void detailedDFSAt(TreeNode root, ArrayList<SuperPath> ans, String[] cur_path) throws TreeNodeException
	{  
		if ((cur_path==null)||(cur_path.length < MAXCOL))
		{			
			throw new TreeNodeException("You tried to call Detailed DFS with invalid cur_path argument: \n must be an array of size "+MAXCOL);
		}
		if (ans==null)
		{			
			throw new TreeNodeException("You tried to call Detailed DFS with un-initialized ans argument: \n must be an non-null array list");
		}
		
		int col = root.color;				
		String rollback = "";
		if (cur_path[col]!=null) {rollback= cur_path[col];}
		
		if (root.isLeaf())
		{
			SuperPath P = new SuperPath(cur_path);
			ans.add(P);
		}
		else
		{
			cur_path[col] = rollback+'0';
			detailedDFSAt(root.left, ans, cur_path);
			cur_path[col] = rollback+'1';
			detailedDFSAt(root.right, ans, cur_path);
			cur_path[col] = rollback;
		}
		
		cur_path[col] = rollback;
	}
	
	/**
	 * Given a node Super Path, returns the corresponding leaf 
	 * @param spath the Super Path to the node you want to get
	 * @return the node with this superpath
	 * @exception throws an exception if the node is not a lead
	 */
	public TreeNode nodeBySuperPath(SuperPath spath) throws TreeNodeException
	{
		return nodeBySuperPathAt(this.rootNode, spath);
	}
	
	/**
	 * Returns the leaf given its SuperPath relative to the given root 
	 * @param root the root of the subtree to search
	 * @param spath the Super Path to the node
	 * @return SuperPath to the node
	 */
	public static TreeNode nodeBySuperPathAt(TreeNode root, SuperPath spath) throws TreeNodeException 
	{
		int col = root.color;
		SuperPath cur_path = new SuperPath(spath);
		if (!spath.isEmpty())
		{
			if (root.isLeaf())
			{
				throw new TreeNodeException("No node with such path !");
			}
			else
			{
				
				char c = cur_path.getColPath(col).charAt(0);

				cur_path.eatDown(col);
				
				if (c=='0')
				{
					return nodeBySuperPathAt(root.left,cur_path);
				}
				else
				{
					return nodeBySuperPathAt(root.right,cur_path);
				}			
			}
		}
		else
		{
			return root;
		}
		
	}
	
	/**
	 * Given a node path, returns a copy of corresponding SuperPath.
	 * @param path the path to the node whose superpath you want to get
	 * @return Superpath of the corresponding node
	 * 
	 */
	public SuperPath superPathByPath(String path) throws TreeNodeException
	{
		return nodeByPath(path).getSuperpath();
	}
		
	
	/**
	 * Provides a duplicate of this tree
	 * @return a deep copy of this NvTree
	 */
	public NvTree duplicate() throws TreeNodeException 
	{		
		return new NvTree(this.toString());
	}
	
	/**
	 * Tells whether two trees A and B correspond to the same pattern
	 * @param A a tree
	 * @param B another tree
	 * @return true if A and B represent the same pattern
	 */
	public static boolean isEquiv(NvTree A, NvTree B) throws TreeNodeException
	{
		if ((A!=null)&&(B!=null))
		{
			ArrayList<SuperPath>P = A.detailedDFS();
			ArrayList<SuperPath>Q = B.detailedDFS();
			return (P.containsAll(Q)&&Q.containsAll(P));
		}
		else
		{
			throw new TreeNodeException("One of the trees you're comparing is null");
		}
	}
	
	
	
	/**
	 * Constructs an NvTree from a pattern (collection of superpaths,
	 * as returened by detailedDFS()).
	 * <br>
	 * The algorithms is as follows:
	 * <li>Create a list of SuperPaths for each color and sort it with a corresponding
	 * comparator
	 * <li>Search the first list for adjacent blocks and merge them. Update the other lists.
	 * Since the lists were sorted with a nice comparator, blocks adjacent along color i
	 * will be adjacent in list i.
	 * <li>Whenever no merges are made, move to the next color
	 * <li>Whenver a merge is made, reset (go to the beginning of the list for color 1)
	 * @param pattern a collection of superpaths that forms a tree
	 * @return a tree that represents this pattern with all leaf-root paths being
	 * alphabetically smallest ones.
	 */
	@SuppressWarnings("unchecked")
	public static NvTree fromPattern(Collection<SuperPath> pattern) throws TreeNodeException	
	{
		 ArrayList<SuperPath> blocks= new ArrayList<SuperPath>(pattern);
		/*
		 * We shall store the partial tree here
		 */
		HashMap<SuperPath, TreeNode>nodes = new HashMap<SuperPath, TreeNode>();
		for (SuperPath P:pattern)
		{
			TreeNode tn = new TreeNode(null,0,false);
			nodes.put(P, tn);
		}
		
		int i=0;
		int curcol = 1;		
		boolean found = false;
		Collections.sort(blocks, new SuperPathComparator(curcol));
		while (blocks.size()>1)	//the goal is to merge all the blocks int ONE square 
		{	 
			SuperPath L = blocks.get(i);			
			SuperPath R = blocks.get(i+1);
			int c = L.isAdjacentTo(R);		//if L is adjacent to R, c is the color along which they're adjacent
			if (c==curcol)
			{
				found = true;				
				TreeNode parent = new TreeNode(null,curcol,false);
				TreeNode left = nodes.get(L);
				TreeNode right = nodes.get(R);
				parent.left = left;
				parent.right = right;
				nodes.remove(L);
				nodes.remove(R);
				SuperPath LRMerged = new SuperPath(L); 
				LRMerged.goUp(c);
				nodes.put(LRMerged, parent);				
				blocks.remove(R);
				blocks.remove(L);
				blocks.add(LRMerged);							
				Collections.sort(blocks, new SuperPathComparator(curcol));
			}
			if (i<blocks.size()-2){i++;} else 
			{				
				if (!found)
				{
					curcol++;
					if (curcol>=MAXCOL){curcol=1;} 
					Collections.sort(blocks, new SuperPathComparator(curcol));
				}
				else {curcol = 1;}
				found = false;
				i=0;
			}
			
		}
			SuperPath top = blocks.get(0);		
			TreeNode root = nodes.get(top);
			return new NvTree(getStringAt(root));
			/**
			 * TODO FIXME
			 * There is no check or error handling of whether the list of blocks actually forms 
			 * a pattern. If it doesn't, the algorithm simply loops forever - no good.
			 * <br>
			 * The code is unoptimal. Instead of re-sorting one list,
			 * one could maintain several sorted lists.
			 */
	}
	
	
	/**
	 * Generates a random tree on a given number of nodes and using given number 
	 * of colors 
	 * @param numnodes number of leaves the tree will have
	 * @param maxcol maximum color used
	 * @return a string representation of a random tree with numnodes leaves and which has colors among
	 * (NLR DFS traversal) 
	 * 1,2,3,...maxcol
	 */
	public static String generateRandomTree(int numnodes, int maxcol) throws TreeNodeException
	{
		if ((maxcol>0)&&(maxcol<NvTree.MAXCOL)&&(numnodes>0))
		{
			/* this list will holds string representations of subtrees, which will
			 * be joined randomly until the list contains only one tree. 
			 * It is initialized with "trivial" subtrees (leaves).
			 */  			
			ArrayList<String>blocks = new ArrayList<String>();
			for (int i=0;i<numnodes;i++)
			{
				blocks.add("0");
			}
			Random R = new Random();
			while (blocks.size()>1)
			{
				int i1 = R.nextInt(blocks.size());
				int i2=i1; while (i2==i1) {i2=R.nextInt(blocks.size());}
				int c = R.nextInt(maxcol)+1;
				String S1  = blocks.get(i1);
				String S2 =  blocks.get(i2);
				blocks.remove(S1); blocks.remove(S2);
				String S = c+S1+S2;
				blocks.add(S);
			}
			String treeStr = blocks.get(0);
			return treeStr;
		}
		else
		{
			throw new TreeNodeException("Invalid maximum color or number of nodes: "+maxcol+"; "+numnodes);			
		}
	}
		
}




