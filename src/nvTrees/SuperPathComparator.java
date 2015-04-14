package nvTrees;
import java.util.Comparator;

/**
 * This Comparator compares two blocks (SuperPaths) so that 
 * when you sort a list of them, the blocks that are 
 * ajacent along the sigColor that you specify in the constructor 
 * would also be adjacent in a list sorted with this Comparator
 * @author Romwell
 *
 */
public class SuperPathComparator implements Comparator {

	/**
	 * The SuperPaths are compared by treating each colorpath as a "digit"
	 * and comparing the resulting strings.
	 * This field determines which colorpath is the "least siginificant",
	 */
	int sigColor=1;
	
	public SuperPathComparator(int sigColor) throws TreeNodeException
	{
		if ((sigColor > 0) && (sigColor < NvTree.MAXCOL))
		{
			this.sigColor = sigColor;
		}
		else throw new TreeNodeException("You have attempted to create a SuperPathComparator with an invalid color: "+sigColor);
	}
	
	/**
	 * Compares two superPaths
	 * <br>
	 * This way, blocks adjacent along sigColor (SuperPaths that are
	 * identical, except for the last digit in sigColor's colorpath)
	 * could be easily detected by compating the string representations
	 * (check if they only differ at last symbol). 
	 * @param arg0 first SuperPath to extract the string from
	 * @param arg1 another SuperPath to extract the string from
	 * @return a string representation of the superpath, with the "least significant"
	 * colorpath being the one specified by sigColor.
	 */			
	public int compare(Object arg0, Object arg1) 
	{
		SuperPath P1 = (SuperPath)arg0;
		SuperPath P2 = (SuperPath)arg1;
		for (int i=1;i<NvTree.MAXCOL;i++)  
		{		
			if (i!=sigColor)
			{
				String S1 = P1.getColPath(i);
				String S2 = P2.getColPath(i);
				int ans = S1.compareTo(S2);
				if (ans!=0)
				{
					return ans;
				}
			}
		}		
		String S1 = P1.getColPath(sigColor);
		String S2 = P2.getColPath(sigColor);		
		int ans = S1.compareTo(S2);
		return ans;
	}

}
