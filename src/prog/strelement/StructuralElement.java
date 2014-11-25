package prog.strelement;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.Serializable;

import prog.exceptions.PolygonException;

public abstract class StructuralElement implements Comparable<StructuralElement>, Serializable{


	private static final long serialVersionUID = 1L;
	private Polygon pol;
	private static int numberOfElelements = 0;
	private String svgLine;
	private int id = -1;
	private int hashCode = 0, connectivityDegree = 0;
	private double area = 0, perimeter = 0, probabilityArea = 0, probabilityDist = 0, dsVsArea, length = 0;
	private Point2D centerOfMass = null;
	private boolean exterior = false;
	//private boolean[][] image;
	private double[][] extrema, boundingBox;
	private String gtType = null;

	// default constructor
	public StructuralElement()
	{
		++numberOfElelements;
		id = numberOfElelements;
	}

	// constructor 2
	public StructuralElement(int id)
	{
		++numberOfElelements;
		this.id = id;
	}

	// constructor 3
	public StructuralElement(int id, String svgLine)
	{
		++numberOfElelements;
		this.id = id;
		this.setSvgLine(svgLine);
	}

	// constructor 4
	public StructuralElement(int id, double length)
	{
		++numberOfElelements;
		this.id = id;
		this.length = length;
	}

	// constructor 5
	public StructuralElement(int id,String svgLine, Polygon p)
	{
		pol = p;
		++numberOfElelements;
		this.id = id;
		this.setSvgLine(svgLine);
		this.setArea(calculateTheArea());
		this.setPerimeter(calculateThePerimeter());
		try {
			this.setCenterOfMass(calculateTheCenterOfMass());
		} catch (PolygonException e) {
			e.printStackTrace();
		}
	}

	// constructor 5
	public StructuralElement(int id, double area, double[] centroid, double perimeter, double[][] extrema, double length, double[][] boundingBox)
	{
		++numberOfElelements;
		this.id = id;
		this.setArea(area);
		this.setPerimeter(perimeter);
		this.setCenterOfMass(new Point((int) centroid[0], (int) centroid[1]));
		this.extrema = extrema;
		this.length = length;
		this.setBoundingBox(boundingBox);
		//this.image = image;
	}

	// getters and setters
	public void setPol(Polygon pol){this.pol = pol;}
	public int getSamplingIndex()
	{
		if(this instanceof Wall)
			return 0;
		else if (this instanceof Door)
			return 1;
		else if (this instanceof Window)
			return 4;
		else if (this instanceof Separation)
			return 2;
		else if (this instanceof Parking)
			return 5;
		else if (this instanceof Nothing)
			return 3;
		else return -1;
	}
	public double getDSVSArea(){return this.dsVsArea;}
	public void setDSVSArea(double w){this.dsVsArea = w;}
	public Polygon getPol(){return pol;}
	public abstract String getType();
	public abstract Color getColor();
	public int getId(){return id;}
	public void setId(int id){this.id = id;}
	public void setSvgLine(String svgLine){this.svgLine = svgLine;}
	public String getSvgLine(){return svgLine;}
	public void setArea(double area) {this.area = area;}
	public double getArea() {
		if (area != 0)
			return Math.abs(area);
		else return Math.abs(calculateTheArea());
	}
	public void setPerimeter(double perimeter) {this.perimeter = perimeter;}
	public double getPerimeter() {
		if (perimeter >0)
			return perimeter;
		else return calculateThePerimeter();
	}
	public void setCenterOfMass(Point2D centerOfMass) {this.centerOfMass = centerOfMass;}
	public Point2D getCenterOfMass() throws PolygonException {
		if (centerOfMass != null)
			return centerOfMass;
		else return calculateTheCenterOfMass();
	}
	public double[] getCenterOfMass2(){
		double[] r = new double[2];
		if (centerOfMass == null)
		{
			r[0] = 0;
			r[1] = 0;
		}
		else
		{
			r[0] = centerOfMass.getX();
			r[1] = centerOfMass.getY();
		}
		return r;
	}
	public void setExterior(boolean ext){exterior = ext;}
	public boolean isExterior(){return exterior;}
	//public void setImage(boolean[][] image){this.image = image;}
	//public boolean[][] getImage(){return this.image;}
	public void setExtrema(double[][] extrema){this.extrema = extrema;}
	public double[][] getExtrema(){return this.extrema;}
	public double getProbabilityArea(){return this.probabilityArea;}
	public void setProbabilityArea(double prob){probabilityArea=prob;}
	public double getProbabilityDist(){return this.probabilityDist;}
	public void setProbabilityDist(double prob){probabilityDist=prob;}
	public abstract double getClassOut();
	public abstract boolean getAlreadyChanged();
	public abstract void setClassOut(double classOut);
	public abstract void setAlreadyChanged();
	public double getConnectivityDegree(){return this.connectivityDegree;}
	public void setConnectivityDegree(int cD){connectivityDegree=cD;}
	public void setLength(int cD){length=cD;}
	public void setGTType(String gtType){this.gtType = gtType;}
	public String getGTType(){return this.gtType;}

	// functionality
	/**
	 * Function to calculate the area of a polygon, according to the algorithm
	 * defined at http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/
	 * 
	 * @param polyPoints
	 *            array of points in the polygon
	 * @return area of the polygon defined by pgPoints
	 */
	public double calculateTheArea() {
		int i, j, n = pol.npoints;
		double area = 0;

		for (i = 0; i < n; i++) {
			j = (i + 1) % n;
			area += pol.xpoints[i] * pol.ypoints[j];
			area -= pol.xpoints[j] * pol.ypoints[i];
		}
		area /= 2.0;
		return area;
	}

	/**
	 * Function to calculate the perimeter of a polygon
	 * 
	 * @return perimeter of the polygon
	 */
	private double calculateThePerimeter() {
		double p = 0;

		for (int i = 0; i < pol.npoints; i++) {
			Point2D.Double p1 = new Point2D.Double();
			Point2D.Double p2 = new Point2D.Double();
			p1.setLocation(pol.xpoints[i],pol.ypoints[i]);
			p2.setLocation(pol.xpoints[(i+1)%pol.npoints],pol.ypoints[(i+1)%pol.npoints]);
			p += p1.distance(p2);
		}
		return p;

	}

	/**
	 * Function to calculate the center of mass for a given polygon, according
	 * to the algorithm defined at
	 * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/
	 * @return point that is the center of mass
	 * @throws PolygonException 
	 */
	public Point2D calculateTheCenterOfMass() throws PolygonException {
		double cx = 0, cy = 0;
		double area = this.area;

		if (area == 0)
			throw new PolygonException("L'area del polígon és zero");

		// could change this to Point2D.Float if you want to use less memory
		Point2D res = new Point2D.Double();
		int i, j, n = pol.npoints;

		double factor = 0;
		for (i = 0; i < n; i++) {
			j = (i + 1) % n;
			factor = (pol.xpoints[i] * pol.ypoints[j]
					- pol.xpoints[j] * pol.ypoints[i]);
			cx += (pol.xpoints[i] + pol.xpoints[j]) * factor;
			cy += (pol.ypoints[i] + pol.ypoints[j]) * factor;
		}
		area *= 6.0f;
		factor = 1 / area;
		cx *= factor;
		cy *= factor;
		res.setLocation(cx, cy);
		return res;
	}

	public double getLength()
	{
		if (length == 0)
		{
			Rectangle r = pol.getBounds();
			if (r.getHeight() > r.getWidth())
				return r.getHeight();
			else return r.getWidth();
		} else return length;
	}

	@Override
	// the string is the id
	public String toString() {
		return Integer.toString(id);
	}

	@Override
	// two StructuralElement are compared
	public int compareTo(StructuralElement o) {
		String ownid = Integer.toString(id);
		String oid = Integer.toString(o.getId());
		return (ownid.compareTo(oid));
	}

	@Override
	public boolean equals(final Object object) {
		if (this == object) return true;
		if (!(object instanceof StructuralElement)) return false;
		final StructuralElement st = (StructuralElement) object;
		return this.id == st.getId();
	}


	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			this.hashCode = id*17;
		}
		return this.hashCode;
	}

	public double[][] getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(double[][] boundingBox) {
		this.boundingBox = boundingBox;
	}
}
