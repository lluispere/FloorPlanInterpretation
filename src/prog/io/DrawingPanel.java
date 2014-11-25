package prog.io;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import prog.strelement.FloorPlan;
import prog.strelement.StructuralElement;

public class DrawingPanel extends JPanel{
	
	private static final long serialVersionUID = -3850643711590729901L;
	
	private Set<StructuralElement> stSet;
	private FloorPlan fp;
	
	public DrawingPanel(FloorPlan fp)
	{
		this.fp = fp;
		stSet = new HashSet<StructuralElement>();
		setBackground(Color.WHITE);
		global();
	}
	
	/**
	 * Painting function
	 */
	public void paint(Graphics g) {
        super.paintComponent(g);

        for (StructuralElement st : stSet)
        {
        	if (st.getType() != "Building")
        	{
        		g.setColor(st.getColor());
        		g.fillPolygon(st.getPol());
        	}
        		
        }
    }
	
	/**
	 * Draws the exterior elements of the floor plan.
	 */
	public void perimeter()
	{
		stSet.clear();
		for (StructuralElement st : fp.getAndGraph().getVertexes())
		{
			if(st.isExterior())
				stSet.add(st);
		}
		repaint();
	}
	
	/**
	 * Draws all the elements in the floor plan.
	 */
	public void global()
	{
		Set<StructuralElement> stSetAux = new HashSet<StructuralElement>();
		for (StructuralElement st : fp.getAndGraph().getVertexes())
		{
			stSetAux.add(st);
		}
		stSet = stSetAux;
		repaint();
	}

}
