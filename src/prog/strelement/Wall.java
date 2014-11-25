package prog.strelement;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Point2D;

public class Wall extends StructuralElement{
	
	private static final long serialVersionUID = 1L;
	private double thickness = 0, classOut = 0;
	
	// default constructor
	public Wall(int id, String svgLine, Polygon p)
	{
		super(id, svgLine, p);
		thickness = calculateThickness();
	}
	
	public Wall(int id, double area, double[] centroid, double perimeter, double[][] extrema, double thickness, double length, double[][] boundingBox)
	{
		super(id,area,centroid,perimeter,extrema,length,boundingBox);
		this.thickness = thickness;
	}
	
	public String getType(){return "Wall";}
	public Color getColor(){return Color.BLACK;}
	public void setThickness(double thickness){this.thickness = thickness;}
	public double getThickness(){return thickness;}
	public double getClassOut(){return this.classOut;}
	public void setClassOut(double classOut){this.classOut=classOut;}
	public double getLength(){return super.getLength();}
	public boolean getAlreadyChanged(){return false;}
	public void setAlreadyChanged(){}
	
	/**
	 * This function is only called when the this is an instance of Wall.
	 * Calculates its thickness. Since walls are mostly rectangular elements,
	 * their thickness usualy matches with the shortest segment of the polygon.
	 * @return a double with the thickness of the element.
	 */
	private double calculateThickness()
	{
		double minim = Double.MAX_VALUE;
		for (int i = 0; i < super.getPol().npoints; i++) {
			Point2D.Double p1 = new Point2D.Double();
			Point2D.Double p2 = new Point2D.Double();
			p1.setLocation(super.getPol().xpoints[i],super.getPol().ypoints[i]);
			p2.setLocation(super.getPol().xpoints[(i+1)%super.getPol().npoints],super.getPol().ypoints[(i+1)%super.getPol().npoints]);
			double distance = p1.distance(p2);
			if (distance < minim)
				minim = distance;
		}
		return minim;
	}
}
