package prog.mygraph;

import org.jgrapht.graph.DefaultEdge;

/**
 * 
 * @author Lluís-Pere de las Heras
 * @date 07/11/13
 */
public class MyEdge extends DefaultEdge {

	private static final long serialVersionUID = 1L;

	private String type;
	private String lineText;
	
	// default constructor
	public MyEdge(String label, String lineText) {
        this.setType(label);
    }

	// getters and setters
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setLineText(String lineText) {
		this.lineText = lineText;
	}

	public String getLineText() {
		return lineText;
	}
	
	@Override
	// two StructuralElement are compared
	public String toString() {
		return type;
	}

}
