package nvTrees;
/**
 * This is a class to hold an expression node. It is used to 
 * create an expression tree.
 * @author Romwell
 *
 */
public class ExpressionNode {

	/**
	 * Holds the operator stored at this node. Operators are defined in the ExpressionParser class
	 * in ExpressionParser.unary_operators and ExpressionParser.binary_operators
	 */	
	public String operator;
	
	/**
	 * Children of the current node. Can be an array of 0, 1 or 2 elements 
	 */
	public ExpressionNode[] children;
	
	/**
	 * Holds the value of the current node. Must be null, Integer or a TreePair. 
	 */
	public Object value=null;
	
	/**
	 * Creates a node corresponding to unary operator applied to an object
	 * @param unary Operator operator to be applied, as defined in ExpressionParser.unary_opeators
	 * @param child node whose value contains the operand
	 */
	public ExpressionNode(String unaryOperator, ExpressionNode child)
	{
		this.operator = unaryOperator;
		children = new ExpressionNode[1];
		children[0]=child;
	}
	
	/**
	 * Creates a node corresponding to unary operator applied to an object
	 * @param unary Operator operator to be applied, as defined in ExpressionParser.binary_opeators
	 * @param leftChild node whose value contains the left operand
	 * @param rightChild node whose value contains the right operand
	 */
	public ExpressionNode(String binaryOperator, ExpressionNode leftChild, ExpressionNode rightChild)
	{
		this.operator = binaryOperator;
		children = new ExpressionNode[2];
		children[0]=leftChild;
		children[1]=rightChild;
	}
	
	/**
	 * Creates a lead node which only holds a value
	 * @param value a TreePair or an Integer that this object holds
	 */
	public ExpressionNode(Object value)
	{
		this.value = value;		
	}
	
	
	
	
}
