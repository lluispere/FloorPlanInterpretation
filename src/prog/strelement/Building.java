package prog.strelement;

import java.awt.Color;

public class Building extends StructuralElement {

	private static final long serialVersionUID = 1L;
	private double classOut = 0;
	// default constructor
	public Building()
	{
		super();		
	}
	
	public Building(int id)
	{
		super(id);
	}
	
	public String getType(){return "Building";}
	public Color getColor(){return Color.GRAY;}
	public double getClassOut(){return this.classOut;}
	public void setClassOut(double classOut){this.classOut=classOut;}
	public boolean getAlreadyChanged(){return false;}
	public void setAlreadyChanged(){}
}
