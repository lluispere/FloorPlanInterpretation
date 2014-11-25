package prog.matlab;

import java.io.Serializable;

public class AdjacencyMatrix implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public double[][] matrix;
	public int[] idrows, idcolumns;
	
	public AdjacencyMatrix(int rows, int columns)
	{
		this.matrix = new double[rows][columns];
		this.idrows = new int[rows];
		this.idcolumns = new int[columns];
		
		// initialize
		for (int i = 0; i < idrows.length; i++)
		{
			idrows[i] = 0;
			for (int j = 0; j < idcolumns.length; j++)
			{
				idcolumns[j] = 0;
				matrix[i][j] = 0;
			}
		}
	}
	
	/**
	 * This function prints the ID's of the indices of the matrix
	 */
	public void printIds()
	{
		for (int i:idrows)
			System.out.print(i +":");
		System.out.print("\n");
		for (int j:idcolumns)
			System.out.print(j +":");
	}
	
}
