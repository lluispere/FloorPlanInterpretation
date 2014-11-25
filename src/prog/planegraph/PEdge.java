package prog.planegraph;

import org.jgrapht.graph.DefaultEdge;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;


public class PEdge extends DefaultEdge{

	private static final long serialVersionUID = 6154651173114197996L;	
	// This variable is the angle that forms the edge with the horizontal
	// line passing through the source pnode
	private double angle;
	private Line2D pEdgeLine;

	/**
	 * PEdge constructor, this is a directed edge
	 * @param source is the PNode where the edge starts
	 * @param target is the PNode where the edge finishes
	 */
	public PEdge(PNode source, PNode target)
	{
		this.pEdgeLine = new Line2D.Double(source.getPNodeVertex(),target.getPNodeVertex());
		this.angle = calculatePEdgeAngle(source, target);
	}

	/**
	 * This function calculates the angle that forms the PEdge line with
	 * the horizontal line that cross vSource
	 * @return is the angle 
	 */
	private double calculatePEdgeAngle(PNode source, PNode target)  
	{
		double catet, angle;
		double hip = 0;
		hip = getPEdgeLength();
		
		// Calculate the third vertex necessary to calculate the angle. We form a triangle
		Point2D v_prima = new Point2D.Double(target.getPNodeVertex().getX(), source.getPNodeVertex().getY());
	
		// The catet will be the length between the new Vertex and the target vertex
		catet = target.getPNodeVertex().distance(v_prima);
		
		catet = catet/hip;
		// The angle is the arc sin  of the catet divided by the hipotenusa 
		angle = Math.asin(catet);
		
		
		// If target has an equal Y than the source and 
		// X vTarget is lower than the X of vSource
		// we have to add 180º to the angle
		if(source.getPNodeVertex().getY() >= target.getPNodeVertex().getY())
		{
			if(source.getPNodeVertex().getX() > target.getPNodeVertex().getX())
				angle = Math.PI*2 - angle;
		}
		
		if(source.getPNodeVertex().getY() < target.getPNodeVertex().getY())
		{
			if(source.getPNodeVertex().getX()>= target.getPNodeVertex().getX())
			{
				angle = angle + Math.PI*2;
			}
			else
			{
				angle = (Math.PI - angle) + Math.PI*3;
			}
		}
		
		return angle;
	}

	public double getPEdgeAngle()
	{
		return this.angle;
	}
	
	public double getPEdgeLength()
	{
		return new Point2D.Double(pEdgeLine.getX1(),pEdgeLine.getY1()).distance(new Point2D.Double(pEdgeLine.getX2(),pEdgeLine.getY2()));
	}
	
	public Line2D getPEdgeLine()
	{
		return this.pEdgeLine;
	}

	@Override
	// two StructuralElement are compared
	public String toString() {
		
		return this.getSource().toString() + "-" + this.getTarget().toString() + ": " + Double.toString(angle);
	}

}
