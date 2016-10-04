package nvTrees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class stores the permutation of the 
 * tree leaves
 * @author Romwell
 *
 */
public class TreePermutation {

	
	
	/**
	 * Stores the permutation of the leaves 
	 * as a map between the nodes. The nodes
	 * are identified by their Super Paths
	 */
	public HashMap<SuperPath, SuperPath> permutationMap;

	/**
	 * The associated left tree
	 */
	public NvTree left_tree;
	
	/**
	 * The associated right tree
	 */
	public NvTree right_tree;
	
	
	
	/**
	 * Returns the permutation of tree leaves stored in permutationMap as an array
	 * @return array containg the permutation of leaves
	 */
	public int[] toArray() throws TreeNodeException
	{
		try
		{
			int[] ans = new int[this.permutationMap.size()];
			ArrayList<SuperPath>left_leaves = left_tree.detailedDFS();
			ArrayList<SuperPath>right_leaves = right_tree.detailedDFS();
			for (int i=0;i<ans.length;i++)
			{
				SuperPath left_leaf = left_leaves.get(i);
				SuperPath right_leaf=permutationMap.get(left_leaf);
				int index = right_leaves.indexOf(right_leaf);
				if (index < 0)
				{
					throw new TreeNodeException("Permutation error: the leaf \n"+left_leaf+" has no match");
				}
				ans[index]=i+1;
			}
			return ans;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
				throw new TreeNodeException("Error trying to compute permutation as array. Message: \n"+e.getMessage());
		}
	}

	
	/**
	 * Reads an array of integeres from a string
	 * @param S String of the form a_1 a_2 ... a_n
	 * @return [a_1 a_2  ... a_n]
	 */
	public int[] arrayFromString(String S) throws TreeNodeException
	{
		try
		{		
		StringTokenizer T = new StringTokenizer(S);
		int[] ans = new int[T.countTokens()];
		int count = 0;			
		while (T.hasMoreTokens())
		{			
			ans[count]=Integer.valueOf(T.nextToken());
			count++;
		}
		return ans;
		}
		catch (NumberFormatException e)
		{
			throw new TreeNodeException("Invalid permutation string: invalid integer" +
					" format in string "+S+"\n + error message: "+e.getMessage());
		}
	}
	
	/**
	 * Generates an array containing a random permutation of number 1,2,...,n
	 * @param n 
	 * @return an array containing a random permutation of number 1,2,...,n
	 */
	public static int[] generateRandomArray(int n)
	{
		Random R = new Random();
		ArrayList<Integer>numbers = new ArrayList<Integer>();
		for (int i=1;i<=n;i++)
		{
			numbers.add(i);
		}
		int[] ans = new int[n];
		for (int i=0;i<n;i++)
		{
			int index = R.nextInt(numbers.size());
			ans[i]=numbers.get(index);
			numbers.remove(index);
		}
		return ans;
	}
	
	
	/**
	 * Generates an array containing the identity permutation of number 1,2,...,n
	 * @param n 
	 * @return an array containing numbers 1,2,...,n
	 */
	public static int[] generateIdArray(int n)
	{
		int[] ans = new int[n];
		for (int i=0;i<n;i++)
		{
			ans[i]=i+1;
		}
		return ans;
	}
	
	
	
	/**
	 * Constructs a permutation as a map between the nodes 
	 * @param perm_string A list of numbers 1,2,...,n in a (possibly) different order
	 * @param pair the associated tree pair
	 */
	public TreePermutation(String perm_string, TreePair pair) throws TreeNodeException
	{		
		this.left_tree = pair.left_tree;
		this.right_tree= pair.right_tree;
		
		permutationMap = new HashMap<SuperPath, SuperPath>();
		int n=pair.left_tree.getNumLeaves();
		int nR=pair.right_tree.getNumLeaves();
		if (n!=nR)
		{
			throw new TreeNodeException("The trees you're trying to put in a pair have different number of leaves: "+n+" and "+nR);
		}
		int[] permutation;
		if (perm_string.equals("random"))
		{			
			permutation = generateRandomArray(n);
		}
		else 
		if (perm_string.equals("id"))
		{			
			permutation = generateIdArray(n);
		}
		else {permutation= arrayFromString(perm_string);}
		 
		if (permutation.length != n)
		{
			throw new TreeNodeException("Wrong amount of numbers in permutation! Must be "+n+", you provided "+permutation.length);
		}
		ArrayList<SuperPath>left_leaves = pair.left_tree.detailedDFS();
		ArrayList<SuperPath>right_leaves = pair.right_tree.detailedDFS();
		for (int i=0;i<n;i++)
		{
			//IMPORTANT: NOTE THE ORDER !
			int index2 = i;
			int index1 = permutation[i]-1;
			try //put the leaf pair into permutation map
			{
				SuperPath leftnode = left_leaves.get(index1);
				SuperPath rightnode = right_leaves.get(index2);
				permutationMap.put(leftnode, rightnode);	
			}
			catch (IndexOutOfBoundsException e)
			{
				throw new TreeNodeException("Permutation indices out of bounds (must be in 1,2,..,n)!");
			}
			
			if ((index2>right_leaves.size()-1)||(index1>left_leaves.size()-1))
			{
				throw new TreeNodeException("Weird numbers in permutation! Must be 1, 2, ... ,n");
			}
		}		
	}

	/**
	 * Constructs a permutation from given two trees and a Map
	 * @param left_tree associated left tree
	 * @param right_tree associated left tree
	 * @param map the permutation map as a Map between SuperPaths
	 **/
	public TreePermutation(NvTree left_tree, NvTree right_tree, Map<SuperPath, SuperPath>map) 
	{		
		this.left_tree = left_tree;
		this.right_tree = right_tree;
		permutationMap = new HashMap<SuperPath, SuperPath>(map);
	}
	
	
	/**
	 * Resets the permutation to identity permutation
	 *
	 */
	public void reset() throws TreeNodeException
	{
		this.permutationMap.clear();
		ArrayList<SuperPath>L = left_tree.detailedDFS();
		ArrayList<SuperPath>R = right_tree.detailedDFS();
		if (L.size() != R.size())
		{
			throw new TreeNodeException("The trees have different number of nodes! \n Cannot construct default permutation.");
		}
		else
		{
			for (int i=0;i<L.size();i++)
			{
				permutationMap.put(L.get(i), R.get(i));
			}
		}
	}
	
	/**
	 * Inverts the permutation
	 */
	public void invert()
	{
		//Swap the trees
		NvTree temp = this.left_tree;
		this.left_tree = this.right_tree;
		this.right_tree = temp;
		//Fix the permutation
		HashMap<SuperPath, SuperPath>newMap = new HashMap<SuperPath, SuperPath>();
		for(SuperPath S:permutationMap.keySet())
		{
			SuperPath T = permutationMap.get(S);
			newMap.put(T, S);
		}
		this.permutationMap = newMap;
	}

	/**
	 * Tells whether the permutation map contains a key
	 * <br>(the keys in the map are paths to the nodes on the left tree)
	 * @param key a path in the left tree to look in keyset
	 * @return true if the permutation map contains the key
	 */
	public boolean containsKey(SuperPath key)
	{
		return permutationMap.containsKey(key);
	}
	
	/**
	 * Tells whether the permutation map contains a value
	 * <br>(the values in the map are paths to the nodes on the right tree)
	 * @param value a path in the right tree to look in keyset
	 * @return true if the permutation map contains the value
	 */
	public boolean containsValue(SuperPath value)
	{
		return permutationMap.containsValue(value);
	}
	
	/**
	 * Puts a key-value pair in the permutation
	 * @param key a path to a leaf in the left tree
	 * @param value a path to the leaf in the right tree that this key maps to
	 */
	public void put (SuperPath key, SuperPath value)
	{
		permutationMap.put(key, value);
	}
	
	
	/**
	 * Gets a value in the permutation
	 * @param key a path to a leaf in the left tree
	 * @return a path to the leaf in the right tree that this key maps to
	 */
	public SuperPath get(SuperPath key) 
	{
		return permutationMap.get(key);
	}
	

	/**
	 * Removes a key-value pair in the permutation
	 * @param key a path to a leaf in the left tree
	 */
	public void remove(SuperPath key)
	{
		permutationMap.remove(key);
	}
	
	
	/**
	 * Return the number of pairs stored in permutationMap
	 * @return number of items in the permutation
	 */
	public int size()
	{
		return permutationMap.size();
	}
	
	
	/**
	 * FIXME
	 * The following two methods are not safe, since the changes
	 * to the returned sets/collections are backed up by the map.
	 * Need to return copies of these collections.
	 */
	
	/**
	 * Returns the keys set of the permutation
	 * @return all the left node paths stored in the permutation 
	 */
	public Set<SuperPath>keySet()
	{
		return permutationMap.keySet();
	}
	
	/**
	 * Returns the value set of the permutation
	 * @return all the right node paths stored in the permutation 
	 */
	public Collection<SuperPath>valueSet()
	{
		return permutationMap.values();
	}
	
	
	
	/**
	 * Returns the composition of permutation A with another permutation B
	 * <br>
	 * The matching trees <B>MUST</B> correspond to the same pattern, or <B>HORROR</B> will happen. 
	 * @param A a permutation 
	 * @param B a permutation s.t. B.left_tree ~ A.right_tree (correspond to same pattern)
	 * @return A*B (right action)
	 * <br>
	 * Ex: if A has 1 -> 2, and B has 2->3, then the result will have 1 -> 3
	 */
	public static TreePermutation multiply(TreePermutation A, TreePermutation B) throws TreeNodeException
	{
		if (A==B)
		{
			throw new TreeNodeException("You are trying to combine a TreePermutation instance with itself. You must create a separate copy to do this.");
		}
		NvTree left = A.left_tree.duplicate();
		NvTree right = B.right_tree.duplicate();
		HashMap<SuperPath, SuperPath>map = new HashMap<SuperPath, SuperPath>();
		for (SuperPath p:A.keySet())
		{
			SuperPath q = A.get(p);
			SuperPath r = B.get(q);
			map.put(p, r);
		}
		TreePermutation result = new TreePermutation(left,right,map);
		return result;
	}
	
	public String toString()
	{
		try
		{
			return NvTree.arrayToString(this.toArray());
		}
		catch (TreeNodeException e)
		{			
			return "<broken permatuation>" + e.getError();
		}
	}
	
	
}
