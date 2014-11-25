package prog.io;
 
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
 
/**
 * Class that takes unde control the buttons when showing the image
 * @author Lluís-Pere de las Heras
 * @date 27/11/13
 */
public class ButtonControl extends JPanel
                        implements ActionListener {

	private static final long serialVersionUID = 1L;

	protected JButton b1, b2;
	private DrawingPanel drawingPanel;
 
    public ButtonControl(DrawingPanel dp) {
    	
    	drawingPanel = dp;
 
        b1 = new JButton("DrawComplete");
        b1.setVerticalTextPosition(AbstractButton.CENTER);
        b1.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        b1.setMnemonic(KeyEvent.VK_D);
        b1.setActionCommand("complete");
        b1.setEnabled(false);
 
        b2 = new JButton("DrawPerimeter");
        //Use the default text position of CENTER, TRAILING (RIGHT).
        b2.setMnemonic(KeyEvent.VK_E);
        b2.setActionCommand("perimeter");
        b2.setEnabled(true);
 
        //Listen for actions on buttons 1 and 3.
        b1.addActionListener(this);
        b2.addActionListener(this);
 
        b1.setToolTipText("Click this button to enable the complete floor plan view.");
        b2.setToolTipText("Click this button to enable the perimeter view.");
 
        //Add Components to this container, using the default FlowLayout.
        add(b1);
        add(b2);
    }
 
    public void actionPerformed(ActionEvent e) {
        if ("complete".equals(e.getActionCommand())) {
            b2.setEnabled(true);
            b1.setEnabled(false);
            drawingPanel.global();
            //add(p);
        } else {
            b2.setEnabled(false);
            b1.setEnabled(true);
            drawingPanel.perimeter();
        }
    }
}
