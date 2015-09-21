package nvTrees;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JOptionPane;

/**
 * This class parses expressions involving TreePairs
 * and vairous operations (a*b, a^b, !a, etc.)
 * NOTE: Any string can be an operator,
 * as long as the set of all operators does not allow containment, i.e
 * "x annihilate y" could be a valid expression  as long as no substring or
 * superstring of "annihilate" is an operator.
 * <br>
 * To add another operator, you need to modify/override the init() method 
 * and the applyUnaryOperator / applyBinaryOperator method(s), as applicable.
 * This is all you need to do; the rest is done by the parser.
 * <br>
 * TODO rewrite all code using regular expressions
 * @author Romwell
 *
 */
public class ExpressionParser {

	/**
	 * This counter is used to genereate new auxillary variable names and
	 * should be increased each time a new aux variable is stored.
	 */
	private static int varCounter = 0;
	
	/**
	 * Stores the list of unary operators, in the order of priority
	 */
	private static ArrayList<String> unary_opeators = new ArrayList<String>();
	
	/**
	 * Stores the set of binary operators, in the order of priority
	 */
	private static ArrayList<String> binary_opeators = new ArrayList<String>();
	
	
	/**
	 * Stores the set constant formats (regular expressions such that whenever 
	 * a string matches it, it is recognized as a constant value and not a 
	 * variable name).
	 */
	private static HashSet<String> constant_formats = new HashSet<String>();
	
	/**
	 * This string holds the format for tree pairs
	 */
	private static final String treePairFormat = "\\{(\\w|\\s|\\,)+\\}";
	
	
	/**
	 * If this is false, the init() method needs to be called
	 */
	private static boolean initialized = false;
	
	/**
	 * A list to store names of auxillary variables that are used to store intermidiate
	 * results while calculating long expressions. These variables should be 
	 * cleared from the bucket they are put into after the computation is over.
	 */
	
	private static ArrayList<String>auxVars = new ArrayList<String>();
	
	
	/**
	 * Initializes static variables of this class
	 *
	 */
	private static void init()
	{
		/*
		 * All the operators that are implemented in applyUnaryOperator
		 * and applyBinary operator should be added here to corresponding
		 * operator sets (otherwise, they would not be parsed correctly).
		 * 
		 * Avoid using - as an operator, because then problems arise when parsing
		 * powers like A^-5
		 */
		unary_opeators.add("!"); //inverse
		unary_opeators.add("$"); //reduce
		unary_opeators.add("@"); //(un)reduce to smallest grid on the left
		binary_opeators.add("^"); //conjugation and power
		binary_opeators.add("*"); //multiplication
		binary_opeators.add("#"); //commutator: A#B = AB!A!B
		//TODO: maybe delete this in future releases
		//used for demonstration purposes
		binary_opeators.add("<"); //A<B: refine right tree of A to the left tree of B
		binary_opeators.add(">"); //A>B: refine the left tree of B to right tree of A 
		/*
		 * These (redundant) operators are added to test multicharacter operators
		 */
		unary_opeators.add(" inverse "); //inverse
		binary_opeators.add(" times "); //multiplication
		/*
		 * Here we add formats for Integer and TreePair. All the formats here should
		 * correspond to the formats in the getConstantValue.  
		 */
		constant_formats.add(treePairFormat); //Tree Pair
		constant_formats.add("\\d+"); //positive integer
		constant_formats.add("-\\d+"); //negative integer
		
		initialized = true;
	}
	
	
	/**
	 * Parses an expression contained in a string, and stores the result in a bucket in
	 * the slot "ans" (even if it is also assigned to other variable in the expression)
	 * @param expression a string containig an expression like (a*b)^c
	 * @param bucket a map between variable names and corresponding tree pairs.
	 * @throws an exception is thrown if the string contains an invalid expression
	 */
	public static void parse(String expression, TreeBucket bucket) throws TreeNodeException
	{
		expression = expression.trim();		
		try
		{
			if (!initialized) {init();}
			String storeVar=null;
			if (expression.contains("=")) //if the expression is an assignment, i.e. of the form a = ...
			{
				String[] exp = expression.split("=");
				if (exp.length!=2) {throw new TreeNodeException("Invalid number of tokens in assignment "+expression);}
				if (!TreeBucket.isGoodVarName(exp[0])) {throw new TreeNodeException("Invalid variable name: "+exp[0]);}
				storeVar = exp[0];
				expression = exp[1];
			}
			String A = parseExpression(expression, bucket);
			Object P = getAtomValue(A, bucket);
			if (P instanceof TreePair)
			{ 
				if (storeVar!=null){bucket.add(storeVar,(TreePair) P);}
				bucket.add("ans",(TreePair) P); //always store the result of last computation in the "ans" variable!					
			}
			else throw new TreeNodeException("The result of this computation was not a TreePair: \n" + expression); 
		}
		catch(NullPointerException e)
		{
			throw new TreeNodeException("Null pointer error occurred when parsing this:\n"+expression+"\n");
		}
		finally
		{
			for (String S:auxVars)
			{
				bucket.clear(S);
			}
		}
	}

	
	/**
	 * Parses expressions containing variables, operators and parentheses;
	 * Ex.: a^(b*c)
	 * @param expression
	 * @param expression a string containig an expression like (a*b)^c
	 * @param bucket a map between variable names and corresponding tree pairs. 
	 * @throws an exception is thrown if the string contains an invalid expression
	 * @throws TreeNodeException
	 */
	private static String parseExpression(String expression, TreeBucket bucket) throws TreeNodeException
	{
		int leftPar = expression.lastIndexOf('(');
		if (leftPar > -1)
		{
			int rightPar = expression.indexOf(')',leftPar);
			if (rightPar < leftPar) //misblanced parentheses: )...(  
			{
				throw new TreeNodeException("Misbalanced parentheses in the following expression: \n"+expression);
			}
			else
			{
				//we break the string into leftExp(midExp)rightExp
				String leftExp = expression.substring(0, leftPar);
				String midExp = expression.substring(leftPar+1, rightPar);
				String rightExp = expression.substring(rightPar+1);
				//we now process (midExp) and replace (midExp) with the result
				String newExp = leftExp+parseExpression(midExp, bucket)+rightExp;
				return parseExpression(newExp, bucket);
			}				
		}
		else
		{
			return parseNoParenExpression(expression, bucket);
		}
	}
	

	/**
	 * Parses an expression contained in a string without parentheses. 
	 * The unary operators are applied first, in the order of appearence in unary_operators list.
	 * Then the binary operators are processed, in the order of appearance in binary_operators list.
	 * @param expression a string containig an expression like a*b^c
	 * @param bucket a map between variable names and corresponding tree pairs. The bucket
	 * is also used to store intermidiate results in temporary variables; temp. variable names
	 * are generated by nextVarName() function. This is used instea of building an expression tree.
	 * TODO replace storage of intermidiate results in the bucket with an expession tree.
	 * @throws an exception is thrown if the string contains an invalid expression
	 */
	private static String parseNoParenExpression(String expression, TreeBucket bucket) throws TreeNodeException
	{
		String S = expression;			
		
		/*
		 * Apply all unary opeators first
		 */
		int iU =indexOfLastUnaryOperator(S); 
		while(iU>-1)
		{
			//show(S);
			String operator = operatorAt(S, iU);
			int cut = (iU-1)+operator.length(); //that's the index of the last character of the operator
			
			String var = rightVar(S, cut);
			Object O = getAtomValue(var, bucket);			
			TreePair newO = applyUnaryOperator(O, operator);
			String newVar = nextVarName();
			bucket.add(newVar, newO);
			auxVars.add(newVar);
			
			cut = cut + var.length(); //now cut is the index of the last letter in the variable
									  //iU is the index of the first letter in operator
									  //Now we cut out chars between iU and cut and replace with the result of applying the operator
									  //ex: (blah)!baker(blah) -> (blah)tmpvar1(blah)
			S = S.substring(0,iU)+newVar+S.substring(cut+1,S.length());
			
			iU=indexOfLastUnaryOperator(S);
		}		
		
		/*
		 * Now apply all binary operators
		 */
		int iB=indexOfLastBinaryOperator(S); 
		while(iB>-1)
		{
			//show(S);
			String operator = operatorAt(S, iB);
			int cut = (iB-1)+operator.length(); //that's the index of the last character of the operator
			
			String Lvar = leftVar(S, iB);
			String Rvar = rightVar(S, cut);
			Object L = getAtomValue(Lvar, bucket);
			Object R = getAtomValue(Rvar, bucket);
			TreePair newO = applyBinaryOperator(L, R, operator);
			String newVar = nextVarName();
			bucket.add(newVar, newO);
			auxVars.add(newVar);
			S = S.substring(0,iB-Lvar.length())+newVar+S.substring(cut+1+Rvar.length(),S.length());
			
			iB=indexOfLastBinaryOperator(S);
		}			
		return S;
	}

	
	/**
	 * Applies an unary operator to a TreePair
	 * @param O the argument of the operator (must be a TreePair instance) 
	 * @param operator the operator to be applied
	 * @return the result of the operation
	 */
	private static TreePair applyUnaryOperator(Object O, String operator) throws TreeNodeException
	{
			TreePair ans=null;
			if ((operator=="!")||(operator==" inverse "))
			{
				if (O instanceof TreePair) {
					TreePair P = (TreePair) O;
					ans = TreePair.inverseOf(P);
					return ans;
				}
				else {throwOpException(operator, O);}					
			}
			else if (operator=="$")
			{
				if (O instanceof TreePair) {
					TreePair P = ((TreePair) O).duplicate();
					ans = P.reduce();
					return ans;
				}
				else {throwOpException(operator, O);}					
			}			
			else if (operator=="@")
			{
				if (O instanceof TreePair) {
					TreePair P = ((TreePair) O).duplicate();
					ans = P.reduce(false);
					return ans;
				}
				else {throwOpException(operator, O);}
			}
			else throw new TreeNodeException(operator+" is not a valid unary operator!");
			return ans;
	}

	
	/**
	 * Applies an binary operator to two treepairs or a treepair and a number (e.g. A^3) 
	 * @param L the left argument of the operator 
	 * @param R the right argument of the operator
	 * @param operator the operator to be applied
	 * @return the result of the operation
	 */
	private static TreePair applyBinaryOperator(Object L, Object R, String operator) throws TreeNodeException
	{
			TreePair ans=null;
			if ((operator=="*")||(operator==" times ")) //multiplication
			{
				if ((L instanceof TreePair)&&(L instanceof TreePair))
				{
					ans = TreePair.multiply((TreePair)L, (TreePair)R);
					return ans;
				}
				else {throwOpException(operator, L, R);}
			}
			else if (operator=="#") //commutator
			{
				if ((L instanceof TreePair)&&(L instanceof TreePair))
				{
					TreePair A = (TreePair)L;
					TreePair B = (TreePair)R;					
					ans = TreePair.commutator(A, B);
					return ans;
				}
				else {throwOpException(operator, L, R);}
			}
			else if (operator=="^") //conjugation and exponentiation
			{
				ans = null;
				if (L instanceof TreePair) {
					TreePair P = (TreePair) L;
					if (R instanceof Integer) {
						Integer exp = (Integer) R;
						ans = TreePair.power(P, exp.intValue());
						return ans;
					}
					else
					if (R instanceof TreePair) {
						TreePair Q = (TreePair) R;
						ans = TreePair.conjugate(P, Q);
						return ans;
					}											
				}
				throwOpException(operator, L, R);
			}
			else if ((operator=="<")||(operator==">")) //refine left pair
			{				
				if ((L instanceof TreePair)&&(L instanceof TreePair))
				{
					TreePair A = (TreePair)L;
					TreePair B = (TreePair)R;					
					if (operator==">")
					{
						ans = TreePair.refineLeftTreeTo(B, A.right_tree);
					}
					else
					{
						ans = TreePair.inverseOf(TreePair.refineLeftTreeTo(TreePair.inverseOf(A), B.left_tree));
					}
					return ans;
				}
				else {throwOpException(operator, L, R);}
								
			}
			else throw new TreeNodeException(operator+" is not a valid unary operator!");
			return ans;
	}

	
	/**
	 * Throws an instance of TreeNode exception for binary operator failure
	 * @param op operator
	 * @param L
	 * @param R
	 * @throws TreeNodeException
	 */
	private static void throwOpException(String op, Object L, Object R) throws TreeNodeException
	{
		throw new TreeNodeException("Operator "+op+" cannot be applied to types "+L.getClass().getName()+ " and "+R.getClass().getName());
	}
	
	/**
	 * Throws an instance of TreeNode exception for unary operator failure
	 * @param op operator
	 * @param L
	 * @throws TreeNodeException
	 */
	private static void throwOpException(String op, Object L) throws TreeNodeException
	{
		throw new TreeNodeException("Operator "+op+" cannot be applied to type "+L.getClass().getName());
	}
	
	/**
	 * Returns the last index of the last character in operator at index in a string S
	 * @param S a string cotaining the expressions 
	 * @param index index of an operator
	 * @return index of the last character of the operator at index
	 * @throws TreeNodeException if there is no operator at index
	 */
	private static int lastOpCharIndex(String S, int index) throws TreeNodeException
	{
		String op = operatorAt(S, index);
		if (op!=null)
		{
			return (index - 1)+op.length();
		}
		else
		{
			throw new TreeNodeException("There is no operator in string "+S+" at index "+index);
		}
	}
	
	/**
	 * Returns the variable in S
	 * to which an operator at index is being applied. (It is the longest string to the right
	 * of index that does not contain any operators)
	 * @param S a parentheses-free string containing expressions
	 * @param index index of the operator (or index of last symbol, if operator is not a single character)
	 * @return an expression to the right of index that does not contain any operators
	 * @throws an exception is thrown if the string to be returned is empty 
	 * <br>
	 * S = a*b^!d*f
	 * index = 4 -> ans = d;
	 * index = 6  -> ans = f;
	 * index = 3 -> exception is thrown
	 */
	private static String rightVar(String S, int index) throws TreeNodeException
	{
		if ((index < 0)||(index >= S.length()-1)) //operator should be at least one character before the end of the string
		{
			throw new TreeNodeException("Invalid index of an operator: "+index+" at string "+S);
		}
		if (hasOperatorAt(S, index+1)) //if the next operator immediately successes this one
		{
			throw new TreeNodeException("Operator at "+index+" in string "+S+" is not applied to a variable. \n" +
					"Possibly, the string has two consecutive operators, like a**b. ");
		}		
		int j = index+1;
		while ((j<S.length())&&(!hasOperatorAt(S, j)))
		{				
			j++;
		}
		String var = S.substring(index+1,j);
		return var;
	}

	
	
	/**
	 * Returns the left variable in S
	 * to which an operator at index is being applied. (It is the longest string to the left
	 * of index that does not contain any operators)
	 * @param S a parentheses-free string containing expressions
	 * @param index index of the operator (index of fisrt symbol, if operator is multi-character)
	 * @return a maximal expression to the left of index that does not contain any operators
	 * @throws an exception is thrown if the string to be returned is empty 
	 * <br>
	 * S = a*b^!d*f
	 * index = 4 -> exception is thrown
	 * index = 6  -> ans = d;
	 * index = 3 ->  ans = b;
	 */
	private static String leftVar(String S, int index) throws TreeNodeException
	{
		if ((index < 1)||(index >= S.length()-1)) //operator should be at least one character after the beginning 
		{
			throw new TreeNodeException("Invalid index of an operator: "+index+" at string "+S);
		}
		int j = index-1;
		while ((j>=0)&&(!hasOperatorAt(S, j)))
		{				
			j--;
		}
		if ((hasOperatorAt(S, j))&&(index==lastOpCharIndex(S, j)+1))
		{
			throw new TreeNodeException("Operator at "+index+" in string "+S+" is not applied to a variable on the left. \n" +
					"Possibly, the string has two consecutive operators, like a**b. ");
		}
		String var = S.substring(j+1,index);
		//show("left var: "+var);
		return var;
	}
	

	/**
	 * Returns the index of the last unary operator in a sring S or -1 if none found
	 * @param S the string containing expressions
	 * @return the index of the FIRST character of an unary operator in S, or -1 if S contains none
	 */
	private static int indexOfLastUnaryOperator(String S) throws TreeNodeException
	{
		int ans=-1;
		for (int i=0; i<S.length();i++)
		{
			if (hasUnaryOperatorAt(S,i)) 
			{
				ans = i;			
			}
		}
		return ans;		
	}
	
	/**
	 * Returns the index of the last binary operator in a sring S or -1 if none found
	 * @param S the string containing expressions
	 * @return the index of the FIRST character of an binary operator in S, or -1 if S contains none
	 */
	private static int indexOfLastBinaryOperator(String S) throws TreeNodeException
	{
		int ans=-1;
		String op="";
		for (int i=0; i<S.length();i++)
		{
			if (hasBinaryOperatorAt(S,i)) 
			{
				String cur_op=operatorAt(S, i);
				if ((ans<0)||(binary_opeators.indexOf(cur_op)<=binary_opeators.indexOf(op)))
				{
					/*
					 * We only move on to next operator if it has same or higher priority
					 */
					op = cur_op;
					ans = i;
				}
			}
		}
		return ans;		
	}
	
	

	
	
	/**
	 * Tells whether a String S begins with an unary operator
	 * @param S string that might start with an operator, ex.: "*a^b"
	 * @return true if the string starts with an operator
	 */
	private static boolean startsWithUnaryOperator(String S)
	{
		for (String U:unary_opeators)
		{
			if (S.startsWith(U)) {return true;}
		}
		return false;
	}
	
	/**
	 * Tells whether a String S begins with an binary operator
	 * @param S string that might start with an operator, ex.: "*a^b"
	 * @return true if the string starts with an operator
	 */
	private static boolean startsWithBinaryOperator(String S)
	{
		for (String B:binary_opeators)
		{
			if (S.startsWith(B)) {return true;}
		}
		return false;
	}
	
	
	/**
	 * Return whether S has an unary operator starting at index (inclusive)
	 * @param S string containing an expression 
	 * @param index the index of the first character of the operator
	 * @return true if S has an unary operator starting at index
	 */
	private static boolean hasUnaryOperatorAt(String S, int index) throws TreeNodeException
	{
		try
		{
			String T = S.substring(index);
			return startsWithUnaryOperator(T);
		}
		catch(IndexOutOfBoundsException e)
		{
			return false;
		}
	}
	
	
	
	
	/**
	 * Return whether S has a binary operator starting at index (inclusive)
	 * @param S string containing an expression 
	 * @param index the index of the first character of the operator
	 * @return true if S has a binary  operator starting at index
	 */
	private static boolean hasBinaryOperatorAt(String S, int index) throws TreeNodeException
	{
		try
		{
			String T = S.substring(index);
			return startsWithBinaryOperator(T);
		}
		catch(IndexOutOfBoundsException e)
		{
			return false;
		}
	}
	
	
	/**
	 * Return whether S has an unary operator starting at index (inclusive)
	 * @param S string containing an expression 
	 * @param index the index of the first character of the operator
	 * @return true if S has an unary operator starting at index
	 */
	private static boolean hasOperatorAt(String S, int index) throws TreeNodeException
	{
		try
		{
			String T = S.substring(index);
			return (startsWithUnaryOperator(T)||startsWithBinaryOperator(T));
		}
		catch(IndexOutOfBoundsException e)
		{
			return false;
		}
	}
	
	
	/**
	 * Returns an operator starting at index i (inclusive) if there is one, or null if there is not operator at index
	 * @param S string containing an expression 
	 * @param index the index of the first character of the operator
	 * @return if S has an operator starting at index, returns the operator; returns null otherwise 
	 * @throws an exception is thrown if index exceeds the largest index in S
	 */
	private static String operatorAt(String S, int index) throws TreeNodeException
	{
		try
		{
			S = S.substring(index);
			for (String U:unary_opeators)
			{
				if (S.startsWith(U)) {return U;}
			}
			for (String B:binary_opeators)
			{
				if (S.startsWith(B)) {return B;}
			}
			return null;

		}
		catch(IndexOutOfBoundsException e)
		{
			throw new TreeNodeException("Index out of bounds at "+index+" while parsing substring "+S+" : \n"+e.getMessage());
		}
	}
	
	
	/**
	 * Returns the name for the next auxillay variable
	 * @return name for the next auxillary variable
	 */
	private static String nextVarName()
	{
		varCounter++;
		return "auxVar"+varCounter;		
	}
		
	
	/**
	 * Tells whether a string S holds a representation of a constant value (i.e., a number, a TreePair representation, etc.),
	 * as opposed to holding a variable.
	 * @param S  a string of symobls, like "1400", "100,200,1 2" or "x" 
	 * @return true if the string holds a constant value.
	 * <p>
	 * Ex:"1400"(Integer) or "100,200,1 2" (TreePair format)
	 */
	private static boolean holdsConstant(String S)
	{
		if (!initialized) {init();}
		for (String format:constant_formats)
		{
			if (S.matches(format)) {return true;}
		}
		return false;
		
	}
	
	/**
	 * Given a string that holds a constant value, returns that vaue
	 * A constant value is something that is not a variable or 
	 * an expression. These are numbers (Ex.: 10, -5) and strings
	 * that match TreePair format (as defined in treePairFormat regex
	 * in this class).  
	 * @param S string holding a constant  
	 * @return the constant that S holds, as an object 
	 * @throws TreeNodeException
	 */
	private static Object resolveConstantValue(String S) throws TreeNodeException
	{
		if(S.matches("\\d+")||S.matches("-\\d+"))
		{
			Integer I = Integer.valueOf(S);
			return I;
		}
		else
		if(S.matches(treePairFormat))
		{
			String T = S.substring(1, S.length()-1);
			return new TreePair(T);
		}
		else
		{
			throw new TreeNodeException("Error: the expression "+S+" is not a constant.");
		}
		
	}
	
	
	/**
	 * If a string S is an atom (expression with no operators or parentheses), retrieves
	 * its value (it must be either a variable defined in the bucket or a constant value)
	 * @param S a string containing an expression
	 * @param bucket the bucket with variables
	 * @return the object that corresponds to S
	 */
	private static Object getAtomValue(String S, TreeBucket bucket) throws TreeNodeException
	{		
		if (holdsConstant(S))
		{		
			//show("const: "+S);
			return resolveConstantValue(S);
		}
		else
		{
			//show("var: "+S);
			return bucket.get(S);
		}
	}
	
	/**
	 * Displays a message using JOPtionPane
	 * @param S message to display
	 */
	@SuppressWarnings("unused")
	private static void show(String S)
	{
		JOptionPane.showConfirmDialog(null, S);
	}
	
}
