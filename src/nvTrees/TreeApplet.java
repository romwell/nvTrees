package nvTrees;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JApplet;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * This applet is for playing with NvTrees
 * @author Romwell
 *
 */
public class TreeApplet extends JApplet implements  ChangeListener, KeyListener, ListSelectionListener{

//////////////////////////////////////////////////////////////////////////////////////////
//		CLASS FIELDS																	//	
//////////////////////////////////////////////////////////////////////////////////////////

	
	
	static final long serialVersionUID = 1;

	/**
	 * Various program options
	 */
	HashMap<String, String> program_options = new HashMap<String, String>();	
	
	/**
	 * The prefix of the default data files that will be loaded upon startup
	 */
	static final String defaultFname =  "~default";

	/**
	 * Used to retrieve commands from history when user hits up or down arrows
	 */
	private int history_cursor = 0;
	
	
	/**
	 * Sets the size of a tree node on display, in pixels
	 */
	private int nodesize=20;
	
	/**
	 * The rate at which the diameter of node decreases from the root to the leaves;
	 * this is used to make better tree drawings.
	 */
	private double dscale = 0.8;

	
	
	/**
	 * Implementation of the display console as a list
	 */
	JList display = new JList(new DefaultListModel());
	
	/**
	 * Objects to display
	 */
	Stack<Object> dispData = new Stack<Object>();
	
	/**
	 * List to store history data
	 */
	Stack<String> historyData = new Stack<String>();
	
	/**
	 * Panels to put controls on
	 */
	Panel rightPanel = new Panel(new BorderLayout());
	Panel leftPanel = new Panel(new BorderLayout());
	Panel bottomPanel= new Panel(new BorderLayout());

	
	/**
	 * Slider to change the log scale of tree drawing
	 */
	JSlider logSlider = new JSlider(1,30,100,(int)(getDscale()*100));
	
	/**
	 * Slider to change size of the nodes
	 */
	JSlider sizeSlider = new JSlider(1,2,50,getNodesize());

	
	/**
	 * Info label 
	 */
	Label infoLabel = new Label();
	
	/**
	 * Text input 
	 */
	TextField textField1 = new TextField("",30);
	
	
	/**
	 * Displays all the defined variables
	 */
	JList vars = new JList(new DefaultListModel());
	
	/**
	 * Displays the command history
	 */
	JList history = new JList(new DefaultListModel());
	
	
	/**
	 * A bucket that stores variable name-value assignments
	 */
	VarBucket bucket;
	
	
	/**
	 * 
	 */
	Font defaultFont = this.getFont();

//////////////////////////////////////////////////////////////////////////////////////////	
// 				PROPERTIES GETTERS, SETTERS AND DEFAULT VALUES							//	
//////////////////////////////////////////////////////////////////////////////////////////	
	/*
	 * For properties stored in program_options, default values are used to initialize proram_options, 
	 * and are also used for the case when property is read before program_options is populated.
	 */
	
	/**
	 * Default value of drawPatterns property
	 */
	static private final String defDrawPatterns = "true";
	

	/////////the following properties are not stored to program_options///////////////
	
	
	/**
	 * Reads the drawPatterns property from program_options
	 * @return value of drawPattern property, which determines whether
	 * the 2D patterns are displayed for each treepair.
	 * <br> The value of this property is toggled by \pattern command.
	 * <br>if the option is not present, default value "true" is returned.
	 */
	public boolean getDrawPatterns() 
	{		
		String ans;
		if (program_options.containsKey("drawPatterns"))
		{
			ans = program_options.get("drawPatterns");
		}
		else ans=defDrawPatterns;
		return Boolean.parseBoolean(ans);
	}


	/**
	 * Sets the drawPaterns property
	 * @param drawPatterns
	 */
	public void setDrawPatterns(boolean drawPatterns) 
	{
		program_options.put("drawPatterns", ""+drawPatterns);
	}

	
	 /** Returns the rate at which the node size decreases from level to level
	 * @return the dscale
	 */
	public double getDscale() 
	{
		return dscale;
	}


	/**
	 * Sets the rate at which the node size decreases from level to level
	 * @param dscale the dscale to set
	 */
	public void setDscale(double dscale) 
	{
		this.dscale = dscale;
		TreeDrawer.dscale = dscale;
		updateDisplay();
	}


	/**
	 * Returns the tree root size
	 * @return the nodesize
	 */
	public int getNodesize() 
	{
		return nodesize;
	}


	/**
	 * Sets the tree root size
	 * @param nodesize the nodesize to set
	 */
	public void setNodesize(int nodesize)
	{
		this.nodesize = nodesize;
		TreeDrawer.diam=nodesize;
		TreePairDrawer.diameter = nodesize;
		updateDisplay();
	}

////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	/**
	 * Initializes and defines program options:
	 * <br>fills the program_options hashmap with various option names and default values.
	 */
	public void initProgramOptions()
	{
		/* Option names should not be the following strings:
		 * variable_assignment_%d
		 * history_data_%d
		 */
		
		/*
		 * drawPatterns option toggles drawing 2D patterns for treepairs on and off
		 */
		
		program_options.put("drawPatterns", defDrawPatterns);		
	}
	
	
	
	/**
	 * Populate the Applet with buttons and other GUI elements
	 */
	public void placeGUIElements()
	{
		//add GUI elements to their places on the applet form
		setLayout(new BorderLayout());  //this allows placing GUI elements on the borders/center		
        //add(dispPanel, "Center");		
		JScrollPane mainPane = new JScrollPane(display);
		//TODO: figure out how to make text selectable
		add(mainPane, "Center");		
        add(bottomPanel, "South");        
        add(rightPanel, "East");
        add(leftPanel, "West");
        infoLabel.setText("Enter an expression above and hit Enter. Tree Pair format example: {12000,20100,2 3 1}. Type \\help to get help.");
        bottomPanel.add(textField1,"North");        
        bottomPanel.add(infoLabel,"South");
        rightPanel.setLayout(new GridLayout(2,1));
        history.setPreferredSize(new Dimension(200,100));
        rightPanel.add(new JScrollPane(history),"1");
        rightPanel.add(new JScrollPane(vars),"2");        
        leftPanel.add(logSlider,"West");
        leftPanel.add(sizeSlider,"East");
        textField1.addKeyListener(this); 
        logSlider.addChangeListener(this);
        sizeSlider.addChangeListener(this);
                
        vars.addListSelectionListener(this);
        history.addListSelectionListener(this);
        final int SINGLE = ListSelectionModel.SINGLE_SELECTION;  
        vars.setSelectionMode(SINGLE);
        history.setSelectionMode(SINGLE);
        display.setSelectionMode(SINGLE);
        //display.setFont(new Font("Andale Mono",Font.BOLD,14));
        
        textField1.requestFocus();
	}	
	
	
   /**
    * Initializes the applet (initializes fileds)
    */
   public void init() 
   {
	  bucket = new VarBucket();
	  
      setBackground(Color.WHITE); 
      initProgramOptions();
      placeGUIElements();      
      startSession();
      updateDisplay();
   }


	/**
    * Loads last session or performs a sample computation
    *
    */
   public void startSession()
   {
	   /*TODO: can't load files in an applet anymore. This needs to be fixed in an export to a desktop app. 
	   
	   File F = new File(defaultFname+".nvt");
	   if (F.exists())
	   {
		  executeCommand("\\load "+defaultFname);   
	   }
	   else 
	   //*/	   
		   
	   {
		   executeCommand("A={11000,10100,1 2 3}");
		   executeCommand("B={1011000,1010100,1 2 3 4}");
		   executeCommand("Ainv=A^-1");
		   executeCommand("Binv=B^-1");
		   executeCommand("growth [A,B,3]");
		   //executeCommand("semi [A,B,4]");
	   }
   }

   /**
    * Processes a keypress event from textField1
    */
   public void keyPressed(KeyEvent e)
   {
	   
	   if (e.getKeyCode()==KeyEvent.VK_ENTER)
	   {
		   String S = textField1.getText();
		   executeCommand(S);
		   history_cursor = 0;
	   }
	   else	if (e.getKeyCode()==KeyEvent.VK_UP)
	   {
		   int n = historyData.size();
		   if (history_cursor<n){history_cursor++;}
		   n -= history_cursor;
		   String S = historyData.get(n);
		   textField1.setText(S);
	   }
	   else	if (e.getKeyCode()==KeyEvent.VK_DOWN)
	   {
		   int n = historyData.size();
		   if (history_cursor>0){history_cursor--;}
		   if (history_cursor==0){textField1.setText("");}
		   else
		   {
			   n -= history_cursor;
			   String S = historyData.get(n);
			   textField1.setText(S);
		   }
	   }
	   else if (e.getKeyCode()==KeyEvent.VK_ESCAPE)
	   {
		   textField1.setText("");
		   history_cursor = 0;
	   }

   }
   
   public void keyReleased(KeyEvent e)
   {
	   
   }
   
   public void keyTyped(KeyEvent e)
   {
	   
   }
      
   
   /**
    * Distingueshes system commands, i.e. commands having to do with GUI or file I/O.
    * System commands have the following format: "\command args"
    * @param S a command
    * @return true if S has a system command format "\command args"
    */
   public boolean isSystemCommand(String S)
   {
	   return ((!(S==null))&&(S.charAt(0)=='\\'));
   }
   
   
   /**
    * This method executes the command stored in string S
    * and updates the display. The string may contain multiple
    * commands separated by semicolon.
    * If the string has a wrong format, the exception is 
    * caught and displayed.
    * @param S  a string containing a command
    */
   public void executeCommand(String S)
   {	   
	   if ((S!=null)&&(S.length()>0))
	   {
		     
		 try{
		   if (isSystemCommand(S))
		   {
			   parseSystemCommand(S);
			   textField1.setText(""); //clear the command field
			   updateDisplay();
		   }
		   else
		   {			   
			   		historyData.add(S);
			   /*
				    * Multiple commands may be entered at a time, separated with a semicolon
				    */
				   String[] commands = S.split(";"); 
				   for (String command:commands)
				   {
					   ExpressionParser.parse(command, bucket);
					   displayResult(">"+command);
				   }
				   textField1.setText(""); //clear the command field
			   }
			   updateDisplay();
		   }
		   catch(TreeNodeException ex)
		   {
			   JOptionPane.showMessageDialog(this, ex.errorString);
		   }
		
	   }
	
   }
	
   public void stateChanged(ChangeEvent e)   
   {
	   Object source = e.getSource();
	   if (source == logSlider)
	   {
		   int val = ((JSlider)source).getValue();
		   double dval = val / 100.0;
		   setDscale(dval);
	   }	   
	   else if (e.getSource() == sizeSlider)
	   {
		   int val = ((JSlider)source).getValue();
		   setNodesize(val);
	   }	   		  
   }
   
   /**
    * Re-draws all relavant displayable items
    *
    */
   private void updateDisplay()
   {
	   displayBucket();	  
	   DisplayPanel dPanel = new DisplayPanel();
	   display.setCellRenderer(dPanel);
	   display.setListData(dispData.toArray());
	   if (!dispData.empty()) {display.setSelectedValue(dispData.peek(), true);}
	   history.setListData(historyData.toArray());
   }

   /**
    * After a computation has been performed, adds the result of the computation to display.
    * More specifically, if the result is a TreePair, a TreePair drawer is added; otherwise,
    * a string is added.
    * @param description description of the result; typically, the command that 
    * caused the said result. 
    */
   public void displayResult(String description) throws TreeNodeException
   {
	   //String command = historyData.get(historyData.size()-1);
	   //command = ">"+command;
	   dispData.add(description);	   
	   Object result = bucket.get("ans");	   
	   if (result instanceof TreePair) 
	   {
		   TreePair pair = (TreePair) result;
		   TreePairDrawer D = new TreePairDrawer(pair,getNodesize(),getDscale(),30,30,getDrawPatterns());
		   dispData.add(D);
	   }
	   else
	   {
		   dispData.add(result);
	   }
   }
   
     
   
   /**
    * Lists all trees stored in the bucket in a textArea
    *
    */
   private void displayBucket()
   {
	   vars.setListData(bucket.variables().toArray());
   }
   
   /**
    * Processes events from the display lists
    */
   public void valueChanged(ListSelectionEvent e) 
   {	 
	   String S = null;
	   if ((e.getSource().equals(vars))||(e.getSource().equals(history)))
	   {
		   S = (String) ((JList) e.getSource()).getSelectedValue();
	   }
	   else if (e.getSource().equals(display))
	   {
		   display.setSelectedIndices(new int[0]);
	   }
	
	
	   if (S!=null)
	   {
		   textField1.setText(S);		   
	   }
	   textField1.requestFocus();
   }
   
   /**
    * Processes a system command: a command like reset, clear screen, save, load, etc.
    * @param S a string of the form \command
    * <br>May be one of the following:
    * <ul>
    * <li>\clear <br>Clears screen</li>
    * <li>\reset <br>Resets the progam</li>
    * <li>\save filename <br>Saves session to filename.nvt as a text file</li>
    * <li>\load filename <br>Loads session from a text file filename.nvt and re-runs the history</li>
    * <li>\qload filename <br>Quick-load session from a text file filename.nvt: load variable values into memory without re-runnning</li>
    * <li>\help<br>Displays some help</li>
    * <li>\about<br>Displays about box</li>
    * </ul> 
    */
   public void parseSystemCommand(String command) throws TreeNodeException
   {
	 dispData.add(">"+command);
	 String S = command.substring(1);
	 if (S.equals("clear"))
	 {
		 dispData.clear();
	 }
	 else if (S.equals("reset"))
	 {
		 sessionReset();
	 }
	 else if (S.equals("about"))
	 {
		 dispData.add("nvTrees - a Thompson Groups calculator");
		 dispData.add("Written by Roman Kogan for Cornell 2008 Math REU");
		 dispData.add("romwell@gmail.com");
	 }
	 else if (S.equals("pattern"))
	 {
		 setDrawPatterns(!getDrawPatterns());
		 dispData.add("Drawing patterns is set to: "+getDrawPatterns());
	 }
	 else if (S.equals("help"))
	 {
		 
		 Scanner sc = loadJARText("/nvTrees/help.html");
		 String H = "";
		 if (sc!=null)
		 {
			 while (sc.hasNextLine()) {H+=sc.nextLine()+"\n";}
			 dispData.add(H);
		 }
		 else
		 {
			 dispData.add("Help file not found.");
		 }
	 }
	 else if ((S.equals("save"))||S.startsWith("save ")) //save data to file
	 {
		 //get the filename, which starts after "save "
		 if (S.length()>=5){S = S.substring(5);} else {S="";}
		 saveData(S);
	 }
	 else if ((S.equals("load"))||S.startsWith("load ")) //slow load: reads the command history and re-runs it
	 {
		 //get the filename, which starts after "load "
		 if (S.length()>=5){S = S.substring(5);} else {S="";}
		 loadData(S, false);
	 }
	 else if ((S.equals("qload"))||S.startsWith("qload "))  //quick load: reads history AND the values of variables from the file
	 {
		 //get the filename, which starts after "qload "
		 if (S.length()>=6){S = S.substring(6);} else {S="";}
		 loadData(S, true);
	 }
	 else if (S.startsWith("growth")){
		 String params =S.substring(6);
		 StringTokenizer ST = new StringTokenizer(params, ",");
		 if (ST.countTokens() != 2){
			 dispData.add("Invalid parameters for growth. The syntax is:  \\growth g_1 g_2 ... g_k, n");
		 }
		 else{
			 String genSet = ST.nextToken().trim();
			 String nStr = ST.nextToken().trim();
			 int n = Integer.parseInt(nStr);
			 int[] growthData = growth(genSet, n);
			 String gdisp = "Growth: ";
			 for (int i=0; i<growthData.length-1; i++){
				 gdisp += growthData[i] +", ";
			 }
			 gdisp += growthData[growthData.length-1];
			 dispData.add(gdisp);
		 }
	 }
	 else
	 {
		 dispData.add("System command unknonwn: "+command);
	 }
	  
   }
   
   /**
    * Computes the growth function of the semigroup generated by a list of group elements
    * @param genSet a string containing variable names, separated by spaces, which form the generating set
    * @return the values of the growth function. Refer to TreePair.growth() for more info.
    */
   public int[] growth(String genSet, int n) throws TreeNodeException{
	   StringTokenizer ST = new StringTokenizer(genSet," ");
	   ArrayList<TreePair> S = new ArrayList<TreePair>();
	   while (ST.hasMoreTokens()){
		   String v = ST.nextToken();
		   Object V = bucket.get(v);
		   if (V instanceof TreePair)
		   {
			   S.add((TreePair) V);
		   }
		   else
		   {
			   throw new TreeNodeException("Variable " + v + " is not a tree pair.");
		   }
	   }
	   return TreePair.growth(S, n);
   }
   
   
   /**
    * Formats a string to be a proper filename for saving/loading data:
    * replaces empty string with default filename, and attached .nvt
    * extension, if needed.
    */
   private String formatFname(String fname)
   {
		  if (fname.equals("")){fname = defaultFname;}
   		  if (!fname.endsWith(".nvt")){fname+=".nvt";}
   		  return fname;
   }
   
   /**
    * Saves session data to disk
    * @param fname Filename for the data to be saved to.
    * The data is saved to fname.nvt, unless fname ends in .nvt already.
    * <br> If fname is empty, the defaultFname is used instead.
    */
   public void saveData(String fname) throws TreeNodeException
   {
	   	  fname = formatFname(fname);
	   	  Properties P = new Properties();
	   	  //save program options (drawPatterns, ...)
   		  for (String S:program_options.keySet()) 
   		  {   			  
   			  P.put(S,program_options.get(S));
   		  }
   		  int counter = 0; //save command history
   		  for (String S:historyData) 
   		  {   			  
   			  P.put("history_data_"+counter,S);
   			  counter++;
   		  }
   		  counter = 0; //save values of variables
   		  for (String var:bucket.variables())
   		  {
   			  String treeString = (bucket.get(var)).toString();
   			  P.put("variable_assignment_"+counter, var+"={"+treeString+"}");
   			  counter++;
   		  }
   		  
   	    // Write properties file.
   	    try 
   	    {
   	        String comment = "nvTrees session save file";
   	    	P.store(new FileOutputStream(fname), comment);
   	        dispData.add("Session sucessfuly saved to "+fname);
   	    } 
   	    catch (IOException e) 
   	    {
   	    	throw new TreeNodeException("I/O error occurred: \n"+e.getMessage());
   	    }
   }
   
   /**
    * Loads session data from disk
    * @param fname Filename of, without extension, for the data to be loaded from
    * The data is loaded from fname.nvt
    * <br> If fname is empty, the defaultFname is used instead.
    * @param quick if set to true, the variable values are read from the file
    * and loaded into memory. 
    * <br>
    * if set to false, the variable values are not loaded. Instead, the commands in the history
    * are re-run to get to the final state.
    */
   public void loadData(String fname, boolean quick) throws TreeNodeException
   {
   		  fname=formatFname(fname);
   		  Properties P = new Properties();
   	    // Read properties file.
   	    try {
   	        P.load(new FileInputStream(fname));
   	    } catch (IOException e) 
   	    {
   	    	throw new TreeNodeException("I/O error occurred: \n"+e.getMessage());
   	    }
   	    
   	    //clear data before proceeding
   	    parseSystemCommand("\\reset");
   	    //load program options
   	    for (String S:program_options.keySet())
   	    {
   	    	if (P.containsKey(S))
   	    	{
   	    		program_options.put(S, (String)P.get(S));
   	    	}
   	    }
   	    int counter = 0; //load history data and execute it if quick=false
    	while (P.containsKey("history_data_"+counter))
    	{
    		String S = (String) P.get("history_data_"+counter);
    		if (quick) {historyData.add(S);}
    		else {executeCommand(S);}
    		counter++;
    	}
   	    if (quick) //if quick=true, load the values of variables (i.e. computation results)
   	    {
   	    	counter = 0;
   	    	while (P.containsKey("variable_assignment_"+counter))
   	    	{
   	    	String S = (String) P.get("variable_assignment_"+counter);
			ExpressionParser.parse(S, bucket);
			//displayResult(S);	
   	    	counter++;   	    	
   	    	}
   	    }
   	    dispData.add("Session loaded from "+fname);
   }

   /**
    * Resets the current session: clears variables and history,
    * loads default program options.
    */
   public void sessionReset()
   {
	   bucket.clear();
	   historyData.clear();
	   dispData.clear();
	   
   }
   
   /**
    * If the application is packed into a JAR,
    * this method fetches text files from the JAR.
    * @param fname filename of the textfile to fetch
    * @return an instance of a Scanner class reading the text file fname,
    * or null if such file could not be read
    */
   private Scanner loadJARText(String fname)
   {
	  Scanner sc = null; 
	  try
	  {		  
		  InputStream in = TreeApplet.class.getResourceAsStream(fname);
		  sc = new Scanner(in);
		  return sc;
	  }
	  catch(Exception e)
	  {
		  
	  }
	  return sc;
   }
}
