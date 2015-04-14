package nvTrees;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
/**
 * This class holds a map between variable names and corresponding 
 * TreePairs, and operations on them (storage, etc.)
 * @author Romwell
 *
 */
public class TreeBucket 
{

	/**
	 * The map between string variable names and values
	 */
	private HashMap<String, TreePair> bucket;
	
	/**
	 * Constructs a new Bucket to store variables
	 *
	 */
	public TreeBucket()
	{
		bucket = new HashMap<String, TreePair>();
	}
	
	/**
	 * Clears all variables from TreeBucket
	 */
	public void clear()
	{
		bucket.clear();
	}
	
	/**
	 * Stores a variable in the bucket
	 * @param name variable name
	 * @param value variable value
	 */
	public void add(String name, TreePair value) throws TreeNodeException
	{
		if (isGoodVarName(name)){ bucket.put(name, value);}
		else {throw new TreeNodeException("Invalid variable name : "+name);}
	}
	
	/**
	 * Retrieves a variable from the bucket
	 * @param name name a variable to retrieve
	 * @return a TreePair stored in this variable
	 */
	public TreePair get(String name) throws TreeNodeException
	{
		if (bucket.containsKey(name))
		{
			return bucket.get(name);
		}
		else
		{
			throw new TreeNodeException("Variable "+name+" is undefined");
		}
	}
	
	/**
	 * Tells whether a given string is a valid variable name. Normally,
	 * number won't be good variable names, so that x^100, for example,
	 * is unambiguous. Also, variables should not start with a number 
	 * (a tradition). All other words (alphanumeric sequences) qualify.
	 * @param S the string in question
	 * @return true if S is a valid variable name
	 */
	public static boolean isGoodVarName(String S)
	{
		if (S.matches("\\d+\\w+")) {return false;}
		return S.matches("\\w+");
	}
	
	/**
	 * Returns the set of defined variable names
	 * @return the key set of the (variable name - tree pair) map
	 */
	public Collection<String> variables()
	{
		return new ArrayList<String>(bucket.keySet());
	}
	
	/**
	 * Clears a variable (removes it from the list of defined variables)
	 * @param S the name of the variable to clear
	 */
	public void clear(String S)
	{
		if (bucket.containsKey(S))
		{
			bucket.remove(S);
		}
	}
		
}
