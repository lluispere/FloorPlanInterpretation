package prog.io;
import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;

import prog.strelement.FloorPlan;


public class Visualize{

	private static FloorPlan fp;
	
	/**
	 * Inicialize the visualizator
	 */
	public Visualize(FloorPlan fp)
	{
		this.setFp(fp);
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				createAndShowGUI();
			}
		});
	}
	
	/**
	 * Create the GUI and inicialize it
	 */
	private static void createAndShowGUI(){
		
		// create the frame
		JFrame frame = new JFrame("Visualization");
		// exit on close
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// create the JPanel to include the graphics
		frame.setBounds(30, 30, fp.getHeight(), fp.getWidth());
		Container container = frame.getContentPane();
		DrawingPanel drawingPanel = new DrawingPanel(fp);
		ButtonControl newContentPane = new ButtonControl(drawingPanel);

		// add the panel to the container, and the container to the jframe
		container.add(newContentPane,BorderLayout.PAGE_START);
		container.add(drawingPanel, BorderLayout.CENTER);
		frame.setContentPane(container);
		frame.validate();
		frame.repaint();
		
		
		// make the frame visible
		frame.setVisible(true);
	}

	public FloorPlan getFp() {
		return fp;
	}

	public void setFp(FloorPlan fp) {
		Visualize.fp = fp;
	}
}


