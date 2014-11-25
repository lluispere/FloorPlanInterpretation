package prog.exceptions;

public class GraphCreationException extends Exception{

	private static final long serialVersionUID = 1L;

	// parameterless constructor
	public GraphCreationException(){}
	
	// constructor
	public GraphCreationException(String st)
	{
		super(st);
	}

}
