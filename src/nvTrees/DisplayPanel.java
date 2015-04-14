package nvTrees;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * This class is used to display 
 * nV Trees, Tree Pairs and other related structures
 * @author Romwell
 *
 */
public class DisplayPanel extends JPanel implements ListCellRenderer {
	
	static final long serialVersionUID = 1;
	
	/**
	 * The item to be displayed on this panel
	 */
	private Drawable paintItem=null;	

	
	/**
	 * Contructs an instances of a Display Panel 
	 */
	public DisplayPanel(Drawable D)
	{
		super();
	}
	

	/**
	 * Contructs an instance of a Display Panel 
	 */
	public DisplayPanel()
	{
		super();
	}
	
	
	public void paint(Graphics G)
	{
		super.paint(G);
		G.clearRect(0, 0, getWidth(), getHeight());
		if (paintItem !=null)
		{
			paintItem.paint(G);
		}
	}
	
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		if (value instanceof Drawable) 
		{
			Drawable D = (Drawable) value;
			this.paintItem = D;
			return this;
		}
		else 
		{	
			JLabel val = new JLabel(value.toString()); 
			val.setFont(list.getFont());
			return val;
		}		
	}
	
	public Dimension getPreferredSize()
	{	
		if (paintItem==null)
		{
			return new Dimension(0,0);
		}
		else
		{
			return new Dimension(paintItem.getWidth(),paintItem.getHeight());
		}
	}
	
}
