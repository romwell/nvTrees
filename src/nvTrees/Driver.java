package nvTrees;
import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 * A Driver class for this program
 * (also known as Main class)
 * @author Romwell
 *
 */
public class Driver extends JFrame{

	final static long serialVersionUID = 1;
	
	/**
	 * Initializes the driver class
	 *
	 */
	public Driver()
	{
		super("nvTrees: Thompson Group Calculator");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		TreeApplet T = new TreeApplet();
		this.setLayout(new BorderLayout());
		this.add(T, BorderLayout.CENTER);
		T.init();
	}

	public static void main(String[] args)
	{
		Driver D = new Driver();
		D.pack();
		D.setExtendedState(MAXIMIZED_BOTH);
		D.setVisible(true);
		
	}
	
}
