package nvTrees;
/**
 * This class holds a path to a leaf in a colored tree.
 * In a binary tree a path from the root to a leaf can 
 * be stored as a binary sequence 0110101101.. where 0 means
 * going to the left child, and 1 means going to the right.
 * However, this provides no information about the colors.
 * <br>
 * Here, path in each color is stored separately. For instance,
 * if your path is 011010
 * with colors     121223
 * then the super path is stored as
 * 0 = "" //0 is not a color
 * 1 = 01
 * 2 = 101
 * 3 = 0 
 * <br>
 * The idea is that if two different trees correspond to the same pattern,
 * then the leaves that correspond to the same block in the pattern
 * will have the same Super Path.
 * @author Romwell
 *
 */
public class SuperPath  {

	
	private String[] colorpaths = new String[NvTree.MAXCOL];
	
	/**
	 * Constructs a new instance of a super path 
	 * by copying the contents of the array
	 * @param colorpaths array of color paths
	 */
	public SuperPath(String[] colorpaths)
	{
		for (int i=0;i<colorpaths.length;i++)
		{
			if (colorpaths[i]!=null)
			{
				this.colorpaths[i] = colorpaths[i];
			}
			else
			{
				this.colorpaths[i] = "";
			}
		}
	}
	
	
	
	
	/**
	 * Constructs a new empty Super Path
	 */
	public SuperPath()
	{
		this.colorpaths = new String[NvTree.MAXCOL];
		for (int i=0;i<colorpaths.length;i++)
		{
			colorpaths[i]="";
		}
	}
	

	/**
	 * Constructs a copy of another SuperPath
	 */
	public SuperPath(SuperPath P)
	{
		this(P.colorpaths);
	}
	
	/**
	 * Tells whether this is an empty Super Path
	 * @return true if all color paths are empty
	 */
	public boolean isEmpty()
	{
		for (String S:colorpaths)
		{
			if ((S!=null)&&(S.length()>0))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Tells whether this path is the same as some other path
	 * @param o the other path (must be of type SuperPath)
	 * @return true if the paths are the same
	 */
	public boolean equals(Object o)
	{
		
		for (int i=1;i<NvTree.MAXCOL;i++)
		{
			SuperPath p = (SuperPath) o;
			String S = this.colorpaths[i]; if (S==null) {S="";}
			String T = p.colorpaths[i]; if (T==null) {T="";}
			if (!S.equals(T))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns a hashCode for this superpath.
	 * Overriding this is required for hashtable performance.
	 */
	public int hashCode()
	{
		return this.toString().hashCode();
	}
	
	/**
	 * Returns a human-readable string representation of a Superpath 
	 */ 
	public String toString()
	{
		String ans ="[ ";
		for (int i=1; i<colorpaths.length;i++)
		{
			if ((colorpaths[i]!=null)&&(colorpaths[i].length()>0))
				{
					ans += i +":"+colorpaths[i] +"; ";
				}
		}
		ans+="]";
		return ans;
	}
	
	
	
	/**
	 * @return the log base (-2) of the volume of the block represented 
	 * by this SuperPath (as a fraction of a unit hypercube).
	 * <br>
	 * Ex.: this = 1:10,2:0101,3:0; volume = 1/128; result = 7
	 */
	public int sizeLog()
	{
		String len="";
		for (int i=1;i<NvTree.MAXCOL;i++)
		{
			len+=this.getColPath(i);
		}
		return len.length();
	}

	

	/**
	 * Appends 0 or 1 to  the color path of the corresponding color
	 * <br>Ex.: superpath = 
	 * 1: 0101
	 * 2: 001
	 * appendDown(1,0) yields
	 * 1: 01010
	 * 2: 001
	 * <br>
	 * @param i the color to go down in
	 * @param goLeft set to true if you're going left from the parent.
	 * This results in appending 0 to the colorpath; otherwise, 1 is appended
	 */
	
	public void appendDown(int i, boolean goLeft) throws TreeNodeException
	{
		if ((i<1)||(i>=NvTree.MAXCOL))
		{
			throw new TreeNodeException("Cannot go up in color "+i+ ": invalid color.");
		}
		else
		{
			String S = this.colorpaths[i];
			if (goLeft)
			{
				S+='0';
			}
			else
			{
				S+='1';
			}
			this.colorpaths[i]=S;
		}		
	}
	
	/**
	 * Goes up in one color, i.e. removes the last symbol in
	 * the color path of the corresponding color
	 * <br>Ex.: superpath = 
	 * 1: 0101
	 * 2: 001
	 * goUp(1) yields
	 * 1: 010
	 * 2: 001
	 * <br>
	 * Note that this does not necesserily produce the SuperPath of
	 * the node parent, since the parent might have a color different 
	 * from i.
	 * @param i the color to go up in
	 */
	public void goUp(int i) throws TreeNodeException
	{
		if ((i<1)||(i>=NvTree.MAXCOL)||(colorpaths[i]==null)||(colorpaths[i].length()==0))
		{
			throw new TreeNodeException("Cannot go up in color "+i+ "! The color path is that color is empty.");
		}
		else
		{
			String S = colorpaths[i];
			S = S.substring(0, S.length()-1);
			this.colorpaths[i]=S;
		}
	}
	

	/**
	 * Eats down in one color, i.e. removes the FIRST symbol in
	 * the color path of the corresponding color
	 * <br>Ex.: superpath = 
	 * 1: 0101
	 * 2: 001
	 * goDown(1) yields
	 * 1: 101
	 * 2: 001
	 * <br>
	 * This function can be used for tree traversal:
	 * eat down on the root's color to get the superpath
	 * of the node relative to the root's cooresponding 
	 * left/right child.
	 * @param i the color to go up in
	 */
	public void eatDown(int i) throws TreeNodeException
	{
		if ((i<1)||(i>=NvTree.MAXCOL)||(colorpaths[i]==null)||(colorpaths[i].length()==0))
		{
			throw new TreeNodeException("Cannot go up in color "+i+ "! The color path is that color is empty.");
		}
		else
		{
			String S = colorpaths[i];
			S = S.substring(1);
			this.colorpaths[i]=S;
		}
	}
	
	
	
	/**
	 * Returns the color path of the corresponding color
	 * @param i the color whose path you want to get
	 * @return the path at color i
	 */
	public String getColPath(int i)
	{
		if ((i<1)||(i>=NvTree.MAXCOL))
		{
			return "";
		}
		else
		{
			return colorpaths[i];
		}
	}
	
	
	/**
	 * Gets the width of the block represented by this superpaths in the correspoinding dimension,
	 * as a fraction of the unit segment. Should only be used for drawing purposes (due to floating-point
	 * imprecision)
	 * @param i the dimension in which to get the width
	 * @return the width of the pattern in that dimension
	 */
	public double getWidth(int i)
	{
		String S = getColPath(i);
		double ans = 1.0;
		return ans / Math.pow(2, S.length());
	}
	
	
	/**
	 * Gets the i'th coordinate of the beginning of the block, 
	 * as a fraction of the unit segment. Should only be used for drawing purposes (due to floating-point
	 * imprecision)
	 * @param i the dimension in which to get the offset
	 * @return the i'th coordinate of this block
	 */
	public double getOffset(int i)
	{
		String S = getColPath(i);
		double power = 1.0;
		double ans = 0;
		for (int j=0;j<S.length();j++)
		{
			power = power / 2.0;
			int digit = Integer.valueOf(""+S.charAt(j));
			ans += power*digit;
			
		}
		return ans;
	}
	
	
	/**
	 * Tests if the block represented by the superpath is adjacent
	 * to the block represented by another superparth in a 
	 * pattern.
	 * <br>
	 * This is true if the colorpaths are the same except for one 
	 * color in which they differ in the last symbol.  
	 * @param P SuperPath to check the ajacency to
	 * @return -1 if the paths are not ajacent;
	 * 			the index of the color along which they're ajacent otherwise
	 */
	public int isAdjacentTo(SuperPath P)
	{
		int ans = -1;
		for (int i=0;i<NvTree.MAXCOL;i++)
		{			
			String S1 = this.getColPath(i);
			String S2 = P.getColPath(i);
			if (!S1.equals(S2))
			{
				if ((ans>-1)|| (S1.length() != S2.length()))  //if P differes in more than one colorpath,									
				{										   //or differs too much in any, return false
					return -1;
				}
				else
				{
					S1 = S1.substring(0, S1.length()-1);
					S2 = S2.substring(0, S2.length()-1);
					if (S2.equals(S1))
					{
						ans = i;			//ans is set only if the first unequal strings are of same length
					}						//and differ in the last character
					else
					{
						return -1;
					}
				}
			}
		}
		return ans;
	}
	
}









