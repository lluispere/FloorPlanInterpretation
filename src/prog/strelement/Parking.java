package prog.strelement;

import java.awt.Color;
import java.awt.Polygon;

public class Parking extends StructuralElement{

	private static final long serialVersionUID = 1L;
	private double classOut = 0;
	// default constructor
	public Parking(int id, String svgLine, Polygon p)
	{
		super(id, svgLine, p);	
	}

	// get and set
	public String getType(){return "Parking";}
	public Color getColor(){return Color.YELLOW;}
	public double getClassOut(){return this.classOut;}
	public double getLength(){return super.getLength();}
	public void setClassOut(double classOut){this.classOut=classOut;}
	public boolean getAlreadyChanged(){return false;}
	public void setAlreadyChanged(){}
}
