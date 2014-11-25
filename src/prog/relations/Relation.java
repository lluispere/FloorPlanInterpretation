package prog.relations;

import java.util.List;

import prog.exceptions.RelationException;

public class Relation{
	
	private static int numberOfRelations = 0;
	private String svgLine;
	private String type;
	private List<Integer> nameIds;

	// default constructor
	public Relation(String svgLine, String type, List<Integer> id)
	{
		++numberOfRelations;
		this.type = type;
		this.svgLine = svgLine;
		this.nameIds = id;
		checkRelationCorrectness();
	}

	public String getType(){return this.type;}
	public void setType(String type){this.type = type;}
	public void setSvgFile(String svgFile){this.svgLine = svgFile;}
	public String getSvgFile(){return svgLine;}
	public void setNameIds(List<Integer> nameIds){this.nameIds = nameIds;}
	public List<Integer> getNameIds(){return nameIds;}
	public int getNumberOfRelations(){return numberOfRelations;}
	
	/**
	 * Checks the correctness of the relations in terms of the number of elements they connect
	 */
	private void checkRelationCorrectness()
	{
		// there cannot be any relation with less than two elements
		if (nameIds.size() < 2)
		{
			try {
				throw new RelationException("The realtion:\n" + svgLine + "\nhas less than two elements");
			} catch (RelationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// the neighbour and incident relations can relate at most 2 elements
		else if( (type == "neighbour" || type == "incident") && nameIds.size() != 2)
		{
			try {
				throw new RelationException("The realtion:\n" + svgLine + "\nhas more than two elements");
			} catch (RelationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// the access relations relates 3 elements
		else if (type == "access"  && nameIds.size() != 3)
		{
			try {
				throw new RelationException("The realtion:\n" + svgLine + "\nhas not three elements");
			} catch (RelationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Override of equals
	 * Two relations are equal if they contain the same objects id's
	 */
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Relation))return false;
	    Relation otherMyClass = (Relation)other;
	    return nameIds.equals(otherMyClass);
	}
	
	@Override
	public String toString(){
		String s = "";
		for (int i : nameIds)
		{
			s = s + " " + Integer.toString(i);
		}
		return s + ": " + this.type;
	}
}
