package prog.strelement;

import java.awt.Color;
import java.awt.Polygon;

public class Window extends StructuralElement{
	
	private static final long serialVersionUID = 1L;
	private double classOut = 0;
	// default constructor
	public Window(int id, String svgLine, Polygon p)
	{
		super(id, svgLine, p);
	}
	
	public String getType(){return "Window";}
	public Color getColor(){return Color.GREEN;}
	public double getClassOut(){return this.classOut;}
	public double getLength(){return super.getLength();}
	public void setClassOut(double classOut){this.classOut=classOut;}
	public boolean getAlreadyChanged(){return false;}
	public void setAlreadyChanged(){}
}
