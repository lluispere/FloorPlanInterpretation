package prog.strelement;

import java.awt.Color;
import java.awt.Polygon;

public class Nothing extends StructuralElement{
	private static final long serialVersionUID = 1L;
	private double classOut = 0;
	// default constructor
	public Nothing(int id, String svgLine, Polygon p)
	{
		super(id, svgLine, p);
	}
	public Nothing(int id, double length)
	{
		super(id,length);

	}
	
	public Nothing(int id, double area, double[] centroid, double perimeter, double[][] extrema, int length, double[][] boundingBox)
	{
		super(id,area,centroid,perimeter,extrema,length, boundingBox);
		
	}
	public String getType(){return "Nothing";}
	public Color getColor(){return Color.BLACK;}
	public double getClassOut(){return this.classOut;}
	public double getLength(){return super.getLength();}
	public void setClassOut(double classOut){this.classOut=classOut;}
	public boolean getAlreadyChanged(){return false;}
	public void setAlreadyChanged(){}
}
