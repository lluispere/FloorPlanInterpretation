package prog.planegraph;

import java.awt.geom.Point2D;

import prog.exceptions.PolygonException;
import prog.strelement.StructuralElement;

public class PNode {
	
	private Point2D v;
	private int idP;
	private int hashCode = 0;
	StructuralElement st;
	public static int numOfPNodes = 0;
		
	public PNode(StructuralElement st) throws PolygonException
	{
		this.st = st;
		this.v = st.getCenterOfMass();
		this.idP = ++numOfPNodes;
	}	
	
	public int getId()
	{
		return this.idP;
	}
	
	public Point2D getPNodeVertex()
	{
		return this.v;
	}
	
	public StructuralElement getStructuralElement()
	{
		return this.st;
	}
	
	public static int genericHash(int... fieldHashes) {
		int result = 17;
		for (int hash : fieldHashes) {
			result = 37 * result + hash;
		}
		return result;
	}
	
	@Override
	public String toString()
	{
		return Integer.toString(this.idP);
	}
	
	@Override
	public boolean equals(final Object object) {

		if (this == object) return true;
		if (!(object instanceof PNode)) return false;
		final PNode point = (PNode) object;
		return (this.v.equals(point.getPNodeVertex()));
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			//this.hashCode = genericHash((int) this.v.getX(),(int) this.v.getY());
			this.hashCode =(int) (this.v.getX()+this.v.getY()*17);
		}
		return this.hashCode;
	}

}
