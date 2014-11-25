package prog.strelement;

import java.awt.Color;
import java.awt.Polygon;

public class Door extends StructuralElement{
	
	private static final long serialVersionUID = 1L;
	private double classOut = 0;
	private boolean alreadyChanged=false;
	// default constructor
	public Door(int id, String svgLine, Polygon p)
	{
		super(id, svgLine, p);	
	}
	
	public Door(int id, double area, double[] centroid, double perimeter, double[][] extrema, double length, double[][] boundingBox)
	{
		super(id,area,centroid,perimeter,extrema,length,boundingBox);
	}
	
	// get and set
	public String getType(){return "Door";}
	public Color getColor(){return Color.MAGENTA;}
	public double getLength(){return super.getLength();}
	public double getClassOut(){return this.classOut;}
	public void setClassOut(double classOut){this.classOut=classOut;}
	public void setAlreadyChanged(){alreadyChanged=true;}
	public boolean getAlreadyChanged(){return alreadyChanged;}
}
