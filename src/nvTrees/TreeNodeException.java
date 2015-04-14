package nvTrees;
/**
 * A general exception for errors related to tree nodes.
 * @author Romwell
 *
 */
public class TreeNodeException extends Exception{
	
	static final long serialVersionUID = 1; 
	
	/**
	 * The error string
	 */
	String errorString;

	/**
	 * Standard constructor with a mystic error string
	 *
	 */
	public TreeNodeException() 
	{
		super();             // call superclass constructor
		errorString = "Tree Format Error for unknown reasons.";
	}
  
	/**
	 * Extended constructor that lets you specify the error message
	 * @param err the error string
	 */
	public TreeNodeException(String err)
	{
		super(err);     // call super class constructor
		errorString = err;  // save message
	}
  
	/**
	 * Retutns the error string
	 * @return the error string of this exception
	 */
	public String getError()
	{
		return errorString;
	}
}