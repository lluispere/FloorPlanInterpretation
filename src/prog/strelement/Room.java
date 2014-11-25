package prog.strelement;

import java.awt.Color;
import java.awt.Polygon;

public class Room extends StructuralElement{
	
	private static final long serialVersionUID = 1L;
	private boolean buildingEntrance = false, accessible = false, entrance = false;
	private double classOut = 0;
	
	// default constructor
	public Room(int id, String svgLine, Polygon p)
	{
		super(id, svgLine, p);
	}
	
	public Room(int id, double area, double[] centroid, double perimeter, double[][] extrema, double[][] boundingBox)
	{
		super(id,area,centroid,perimeter,extrema,1,boundingBox);
	}
	
	public String getType(){return "Room";};
	public Color getColor(){return Color.BLUE;}
	public boolean getBuildingEntrence(){return this.buildingEntrance;}
	public void setBuildingEntrance(boolean bE){this.buildingEntrance = bE;}
	public boolean isAccessible() {return accessible;}
	public void setAccessible(boolean accessible) {	this.accessible = accessible;}
	public boolean isEntrance() {return entrance;}
	public void setEntrance(boolean entrance) {	this.entrance = entrance;}
	public double getClassOut(){return this.classOut;}
	public void setClassOut(double classOut){this.classOut=classOut;}
	public boolean getAlreadyChanged(){return false;}
	public void setAlreadyChanged(){}
}
