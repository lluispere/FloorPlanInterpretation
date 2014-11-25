package prog.strelement;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;

import prog.exceptions.FloorPlanGTException;
import prog.exceptions.GraphCreationException;
import prog.exceptions.RelationException;
import prog.matlab.AdjacencyMatrix;
import prog.matlab.AlphaBetaData;
import prog.matlab.StructuralElementTriplets;
import prog.mygraph.MyDirectedGraph;
import prog.mygraph.MyGraph;
import prog.relations.Relation;
import prog.statistics.Statistics;
import prog.utils.PowerSet;

/**
 * Class for the floor plan image.
 * Is the main class of the program. It has the functionality to create the ANDgraph of the Floor Plan.
 * Checks whether the graph is consistent with the grammar.
 * Calculates the statistics of the floor plan.
 * @author Lluís-Pere de las Heras
 * @date 07/11/13
 */
public class FloorPlan implements Serializable, Cloneable{

	private static final long serialVersionUID = 1L;
	// private variables
	private String fileName;
	private int width, height;
	private Set<StructuralElement> structuralElements;
	private List<StructuralElement> outerPerimeter;
	private Set<Relation> relations;
	private MyDirectedGraph andGraph;
	private MyGraph neighbourGraph, accessGraph, incidentGraph, nothingGraph;
	private int[][] regionImage, adjacencyMatrix;

	// default constructor
	public FloorPlan(String fN)
	{
		// inicialize variables
		fileName = fN;
		structuralElements = new HashSet<StructuralElement>();
	}

	// getters and setters
	public void setFileName(String fileName){this.fileName = fileName;}
	public String getFileName(){return this.fileName;}
	public void setWidth(int w){this.width = w;};
	public int getWidth(){return this.width;}
	public void setHeight(int height){this.height = height;}
	public int getHeight(){return height;}
	public void setStructuralElements(Set<StructuralElement> structuralElements){
		this.structuralElements = structuralElements;}
	public Set<StructuralElement> getStructuralElements(){
		return structuralElements;}
	public void addStructuralElement(StructuralElement st){structuralElements.add(st);}
	public void setRelations(Set<Relation> relation){this.relations = relation;}
	public Set<Relation> getRelations(){return relations;}
	public void setOuterPerimeter(List<StructuralElement> op) {this.outerPerimeter = op;}
	public List<StructuralElement> getOuterPerimeter() {return outerPerimeter;}
	public MyDirectedGraph getAndGraph() {return andGraph;}
	public MyGraph getNeighbourGraph() {return neighbourGraph;}
	public MyGraph getNothingGraph() {return nothingGraph;}
	public MyGraph getIncidentGraph() {return incidentGraph;}
	public MyGraph getAccessGraph() {return accessGraph;}
	public int getNumberOfRooms() {return andGraph.getVertexesOfType("Room").size();}
	public int getNumberOfWalls() {return andGraph.getVertexesOfType("Wall").size();}
	public int getNumberOfDoors() {return andGraph.getVertexesOfType("Door").size();}
	public int getNumberOfWindows() {return andGraph.getVertexesOfType("Window").size();}
	public int getNumberOfParkings() {return andGraph.getVertexesOfType("Parking").size();}
	public int getNumberOfSeparations() {return andGraph.getVertexesOfType("Separation").size();}
	public boolean isConnected(String graphType)
	{
		if (graphType.equals("access"))
			return accessGraph.isConnected();
		else if (graphType.equals("neighbour"))
			return neighbourGraph.isConnected();
		else if (graphType.equals("and"))
			return andGraph.isConnected();
		else return false;
	}
	public double getArea()
	{
		double area = 0;
		for (StructuralElement st:andGraph.getVertexesOfType("Room"))
			area = area+st.getArea();
		return area;
	}
	public double getPerimeter() 
	{
		double perimeter = 0;
		for (StructuralElement st:andGraph.getVertexesOfType("Room"))
			perimeter = perimeter+st.getArea();
		return perimeter;
	}
	public double[] getRoomAreas() 
	{
		double[] areas = new double[andGraph.getVertexesOfType("Room").size()];
		//double areaPlanell = this.height*width;
		int i = 0;
		for(StructuralElement r : andGraph.getVertexesOfType("Room"))
		{
			//areas[i] = r.getArea()/areaPlanell;
			areas[i] = r.getArea()/this.getArea();
			//areas[i] = r.getArea();
			i++;
		}
		return areas;
	}
	public double getRoomArea(StructuralElement r)
	{
		return r.getArea()/this.getArea();
	}
	public double getLength(StructuralElement st)
	{
		return st.getLength()/Math.sqrt(this.getArea());
	}
	public double[] getRoomPerimeters() 
	{
		double[] perimeters = new double[andGraph.getVertexesOfType("Room").size()];
		int i = 0;
		for(StructuralElement r : andGraph.getVertexesOfType("Room"))
		{
			perimeters[i] = r.getPerimeter()/this.getArea();
			i++;
		}
		return perimeters;
	}
	public int getNumberOfExteriorDoors()
	{
		int num = 0;
		for (StructuralElement st:andGraph.getVertexesOfType("Door"))
		{
			if (st.isExterior()) num++;
		}
		return num;
	}
	public int getNumberOfExteriorWalls()
	{
		int num = 0;
		for (StructuralElement st:andGraph.getVertexesOfType("Wall"))
		{
			if (st.isExterior()) num++;
		}
		return num;
	}
	public int getNumberOfExteriorWindows()
	{
		int num = 0;
		for (StructuralElement st:andGraph.getVertexesOfType("Window"))
		{
			if (st.isExterior()) num++;
		}
		return num;
	}
	public int getNumberOfExteriorSeparations()
	{
		int num = 0;
		for (StructuralElement st:andGraph.getVertexesOfType("Separation"))
		{
			if (st.isExterior()) num++;
		}
		return num;
	}
	public int[][] getSamplingConfigurations() 
	{
		int[][] samplingConfiguration = new int[5][5];
		for (int i = 0; i<samplingConfiguration.length;i++)
		{
			for (int j = 0; j<samplingConfiguration[i].length;j++)
				samplingConfiguration[i][j] = 0;
		}

		for (DefaultEdge e:incidentGraph.getEdges())
		{
			int i = -1;
			int j = -1;

			if(andGraph.getSource(e) instanceof Wall)
			{
				i = 0;
			}
			if(andGraph.getSource(e) instanceof Door)
			{
				i = 1;
			}
			if(andGraph.getSource(e) instanceof Window)
			{
				i = 2;
			}
			if(andGraph.getSource(e) instanceof Separation)
			{
				i = 3;
			}
			if(andGraph.getSource(e) instanceof Parking)
			{
				i = 4;
			}
			if(andGraph.getTarget(e) instanceof Wall)
			{
				j = 0;
			}
			if(andGraph.getTarget(e) instanceof Door)
			{
				j = 1;
			}
			if(andGraph.getTarget(e) instanceof Window)
			{
				j = 2;
			}
			if(andGraph.getTarget(e) instanceof Separation)
			{
				j = 3;
			}
			if(andGraph.getTarget(e) instanceof Parking)
			{
				j = 4;
			}
			if(i != -1 && j != -1)
			{
				samplingConfiguration[i][j]++;
			}
		}

		// add the sum of the transposed
		for (int i = 0; i<samplingConfiguration.length;i++)
		{
			for (int j = 0; j<samplingConfiguration[i].length;j++)
			{
				int suma = samplingConfiguration[i][j]+samplingConfiguration[j][i];
				samplingConfiguration[i][j] = suma;
				samplingConfiguration[j][i] = suma;
			}
		}			

		return samplingConfiguration;
	}

	/**
	 * Function that gets the sampling configuration from the GT
	 * @return a matrix 5x5 with the incident modules among elements
	 */
	public int[][] getCCSamplingConfigurations()
	{
		int[][] samplingConfiguration = new int[5][5];
		for (int i = 0; i<samplingConfiguration.length;i++)
		{
			for (int j = 0; j<samplingConfiguration[i].length;j++)
				samplingConfiguration[i][j] = 0;
		}

		for (DefaultEdge e:incidentGraph.getEdges())
		{
			int i = -1;
			int j = -1;

			if(incidentGraph.getSource(e) instanceof Wall)
			{
				i = 0;
			}
			if(incidentGraph.getSource(e) instanceof Door)
			{
				i = 1;
			}
			if(incidentGraph.getSource(e) instanceof Window)
			{
				i = 2;
			}
			if(incidentGraph.getSource(e) instanceof Separation)
			{
				i = 3;
			}
			if(incidentGraph.getSource(e) instanceof Parking)
			{
				i = 4;
			}
			if(incidentGraph.getTarget(e) instanceof Wall)
			{
				j = 0;
			}
			if(incidentGraph.getTarget(e) instanceof Door)
			{
				j = 1;
			}
			if(incidentGraph.getTarget(e) instanceof Window)
			{
				j = 2;
			}
			if(incidentGraph.getTarget(e) instanceof Separation)
			{
				j = 3;
			}
			if(incidentGraph.getTarget(e) instanceof Parking)
			{
				j = 4;
			}
			if(i != -1 && j != -1)
			{
				if(i!=j)
				{
					samplingConfiguration[i][j]++;
				}
			}
		}

		// add the sum of the transposed
		for (int i = 0; i<samplingConfiguration.length;i++)
		{
			for (int j = 0; j<samplingConfiguration[i].length;j++)
			{
				int suma = samplingConfiguration[i][j]+samplingConfiguration[j][i];
				samplingConfiguration[i][j] = suma;
				samplingConfiguration[j][i] = suma;
			}
		}
		return samplingConfiguration;
	}

	/**
	 * Function that gets the sampling configuration from the FORCE
	 * @return a matrix 4x4 with the incident modules among elements
	 */
	public int[][] getForceSamplingConfigurations()
	{
		int[][] samplingConfiguration = new int[4][4];
		for (int i = 0; i<samplingConfiguration.length;i++)
		{
			for (int j = 0; j<samplingConfiguration[i].length;j++)
				samplingConfiguration[i][j] = 0;
		}

		for (DefaultEdge e:nothingGraph.getEdges())
		{
			int i = -1;
			int j = -1;

			if(nothingGraph.getSource(e).getGTType().equals("Wall"))
			{
				i = 0;
			}
			if(nothingGraph.getSource(e).getGTType().equals("Door"))
			{
				i = 1;
			}
			if(nothingGraph.getSource(e).getGTType().equals("Separation"))
			{
				i = 2;
			}
			if(nothingGraph.getSource(e).getGTType().equals("Nothing"))
			{
				i = 3;
			}
			if(nothingGraph.getTarget(e).getGTType().equals("Wall"))
			{
				j = 0;
			}
			if(nothingGraph.getTarget(e).getGTType().equals("Door"))
			{
				j = 1;
			}
			if(nothingGraph.getTarget(e).getGTType().equals("Separation"))
			{
				j = 2;
			}
			if(nothingGraph.getTarget(e).getGTType().equals("Nothing"))
			{
				j = 3;
			}
			if(i != -1 && j != -1)
			{
				if(i!=j)
				{
					samplingConfiguration[i][j]++;
				}
			}
		}

		// add the sum of the transposed
		for (int i = 0; i<samplingConfiguration.length;i++)
		{
			for (int j = 0; j<samplingConfiguration[i].length;j++)
			{
				int suma = samplingConfiguration[i][j]+samplingConfiguration[j][i];
				samplingConfiguration[i][j] = suma;
				samplingConfiguration[j][i] = suma;
			}
		}
		return samplingConfiguration;
	}

	public void setRegionImage(int[][] regions)
	{
		this.regionImage = new int[regions.length][regions[0].length];
		for(int i = 0; i<regions.length; i++)
		{
			for(int j = 0; j<regions[i].length;j++)
			{
				regionImage[i][j] = regions[i][j];
			}
		}
	}
	public int[][] getRegionImage(){return this.regionImage;}
	public void setAdjacencyMatrix(int[][] aM){this.adjacencyMatrix = aM;}
	public int[][] getAdjacencyMatrix(){return this.adjacencyMatrix;}

	/**
	 * Function for getting the Rooms of the floor plan
	 * @return is an Array of Rooms
	 */
	public StructuralElement[] getRooms(){
		StructuralElement[] rooms = new StructuralElement[andGraph.getVertexesOfType("Room").size()];
		int i = 0;
		for (StructuralElement st : andGraph.getVertexesOfType("Room"))
		{
			rooms[i] = st;
			i++;
		}
		return rooms;
	}

	/**
	 * Function for getting the Walls of the floor plan
	 * @return is an Array of Walls
	 */
	public StructuralElement[] getWalls(){
		StructuralElement[] walls = new StructuralElement[andGraph.getVertexesOfType("Wall").size()];
		int i = 0;
		for (StructuralElement st : andGraph.getVertexesOfType("Wall"))
		{
			walls[i] = st;
			i++;
		}
		return walls;
	}

	/**
	 * Function for getting the Doors of the floor plan
	 * @return is an Array of Doors
	 */
	public StructuralElement[] getDoors(){
		StructuralElement[] doors = new StructuralElement[andGraph.getVertexesOfType("Door").size()];
		int i = 0;
		for (StructuralElement st : andGraph.getVertexesOfType("Door"))
		{
			doors[i] = st;
			i++;
		}
		return doors;
	}

	/**
	 * Function for getting the Windows of the floor plan
	 * @return is an Array of Windows
	 */
	public StructuralElement[] getWindows(){
		StructuralElement[] windows = new StructuralElement[andGraph.getVertexesOfType("Window").size()];
		int i = 0;
		for (StructuralElement st : andGraph.getVertexesOfType("Window"))
		{
			windows[i] = st;
			i++;
		}
		return windows;
	}

	/**
	 * Function for getting the Separationcs of the floor plan
	 * @return is an Array of Separationcs
	 */
	public StructuralElement[] getSeparations(){
		StructuralElement[] separations = new StructuralElement[andGraph.getVertexesOfType("Separation").size()];
		int i = 0;
		for (StructuralElement st : andGraph.getVertexesOfType("Separation"))
		{
			separations[i] = st;
			i++;
		}
		return separations;
	}

	/**
	 * Function for getting the Nothings of the floor plan
	 * @return is an Array of Nothings
	 */
	public StructuralElement[] getNothings(){
		StructuralElement[] nothings = new StructuralElement[nothingGraph.getVertexesOfType("Nothing").size()];
		int i = 0;
		for (StructuralElement st : nothingGraph.getVertexesOfType("Nothing"))
		{
			nothings[i] = st;
			i++;
		}
		return nothings;
	}

	/**
	 * Function for getting the Parkings of the floor plan
	 * @return is an Array of Parkings
	 */
	public StructuralElement[] getParking(){
		StructuralElement[] parkings = new StructuralElement[andGraph.getVertexesOfType("Parking").size()];
		int i = 0;
		for (StructuralElement st : andGraph.getVertexesOfType("Parking"))
		{
			parkings[i] = st;
			i++;
		}
		return parkings;
	}

	/**
	 * Function for getting the primitives of the floor plan
	 * @return is an Array of primitives
	 */
	public StructuralElement[] getPrimitives(){
		StructuralElement[] pris = new StructuralElement[incidentGraph.getVertexes().size()];
		int i = 0;
		for (StructuralElement st : incidentGraph.getVertexes())
		{
			pris[i] = st;
			i++;
		}
		return pris;
	}
	
	/**
	 * Function for getting the primitives including nothings of the floor plan
	 * @return is an Array of primitives
	 */
	public StructuralElement[] getPrimitivesNothing(){
		StructuralElement[] pris = new StructuralElement[nothingGraph.getVertexes().size()];
		int i = 0;
		for (StructuralElement st : nothingGraph.getVertexes())
		{
			pris[i] = st;
			i++;
		}
		return pris;
	}
	
	

	/**
	 * This function returns the plane configuration for a given room. 
	 * This includes all their children and their incident edges
	 * @param r is the desired room
	 * @return a MyGraph with the plane configuration
	 */
	public MyGraph getRoomPlaneConfiguration(Room r)
	{
		Set<StructuralElement> stSet = andGraph.getItsChildren(r);
		MyGraph rGraph = new MyGraph();
		for (StructuralElement st: stSet)
		{
			rGraph.addVertex(st);
		}

		for(StructuralElement st : stSet)
		{
			for(DefaultEdge de : incidentGraph.getGraph().edgesOf(st))
			{
				if(stSet.contains(incidentGraph.getTheOtherVertex(de, st)))
				{
					rGraph.addEdge(st, incidentGraph.getTheOtherVertex(de, st));
				}
			}
		}
		return rGraph;
	}
	
	/**
	 * This function returns the plane configuration including nothings for a given room. 
	 * This includes all their children and their incident edges
	 * @param r is the desired room
	 * @return a MyGraph with the plane configuration
	 */
	public MyGraph getRoomNothingPlaneConfiguration(Room r)
	{
		Set<StructuralElement> stSet = andGraph.getItsChildren(r);
		MyGraph rGraph = new MyGraph();
		for (StructuralElement st: stSet)
		{
			rGraph.addVertex(st);
		}

		for(StructuralElement st : stSet)
		{
			for(DefaultEdge de : nothingGraph.getGraph().edgesOf(st))
			{
				if(stSet.contains(nothingGraph.getTheOtherVertex(de, st)))
				{
					rGraph.addEdge(st, nothingGraph.getTheOtherVertex(de, st));
				}
			}
		}
		return rGraph;
	}

	/**
	 * This function returns a list with the number of a specific element per room.
	 * @param elementName is a structural element (Wall, Door, Sep... )
	 * @return a list of integers.
	 */
	public List<Integer> getNumberOfElementPerRoom(String elementName)
	{
		List<Integer> nElements = new ArrayList<Integer>();
		for (StructuralElement room : this.getRooms())
		{
			nElements.add(getNumberOfElementsAtRoom(room, elementName));
		}
		return nElements;
	}

	/**
	 * This function returns the number of an specific elements at one specific room
	 * @param st is the room
	 * @param elementName is the type of element
	 * @return is an integer
	 */
	public int getNumberOfElementsAtRoom(StructuralElement st, String elementName)
	{
		if(!(st instanceof Room))
			return 0;
		else
		{
			MyGraph g = this.getRoomNothingPlaneConfiguration((Room) st);
			int nElements = g.getVertexesOfType(elementName).size();
			return nElements;
		}
	}

	/**
	 * This function returns the elements at one specific room
	 * @param st is the room
	 * @return is an array of elements
	 */
	public StructuralElement[] getElementsAtRoom(StructuralElement st)
	{

		MyGraph g = this.getRoomPlaneConfiguration((Room) st);
		StructuralElement[] elA = new StructuralElement[g.getVertexes().size()];
		int i = 0;
		for(StructuralElement el : g.getVertexes())
		{
			elA[i] = el;
			i++;
		}
		return elA;
	}

	/**
	 * This function returns the number of walls, doors, and separations contained 
	 * in each room in a plan.It is useful to extract the statistics of this 
	 * elements for each domain.
	 * @return an array of Integers specifying the number of walls, doors, and separations.
	 * Integer[0] are walls, Integer[1] are for doors, and Integer[2] for separations.
	 */
	public Integer[][] getNumberOfWallsDoorsSeparationsForAllRooms()
	{
		Integer[][] numWDS = new Integer[3][this.getRooms().length];

		int i = 0;
		for (StructuralElement room : this.getRooms())
		{
			numWDS[0][i] = getNumberOfElementsAtRoom(room, "Wall");
			numWDS[1][i] = getNumberOfElementsAtRoom(room, "Door");
			numWDS[2][i] = getNumberOfElementsAtRoom(room, "Separation");
			i++;
		}
		return numWDS;
	}

	public List<Integer> getNumberOfCCElementsPerRoom(String elementName)
	{
		List<Integer> nElements = new ArrayList<Integer>();
		for (StructuralElement room : this.getRooms())
		{
			nElements.add(getNumberOfCCElementsAtRoom(room, elementName));
		}
		return nElements;
	}

	public int getNumberOfCCElementsAtRoom(StructuralElement room, String elementName)
	{
		if(!(room instanceof Room))
			return 0;
		else
		{
			MyGraph g = this.getRoomPlaneConfiguration((Room) room);
			MyGraph mg = g.graphValueCopy();

			if (!elementName.equals("Wall"))
			{
				for (StructuralElement st:mg.getVertexesOfType("Wall"))
					mg.removeVertex(st);
			}
			if (!elementName.equals("Door"))
			{
				for (StructuralElement st:mg.getVertexesOfType("Door"))
					mg.removeVertex(st);
			}
			if (!elementName.equals("Window"))
			{
				for (StructuralElement st:mg.getVertexesOfType("Window"))
					mg.removeVertex(st);
			}
			if (!elementName.equals("Separation"))
			{
				for (StructuralElement st:mg.getVertexesOfType("Separation"))
					mg.removeVertex(st);
			}
			if (!elementName.equals("Parking"))
			{
				for (StructuralElement st:mg.getVertexesOfType("Parking"))
					mg.removeVertex(st);
			}
			@SuppressWarnings("unchecked")
			ConnectivityInspector<StructuralElement, DefaultEdge> ci = new ConnectivityInspector<StructuralElement, DefaultEdge>((UndirectedGraph<StructuralElement, DefaultEdge>) mg.getGraph());
			List<Set<StructuralElement>> cSets = ci.connectedSets();
			return cSets.size();			
		}

	}

	public int getNumberOfIncidentNeighborsOf(StructuralElement st)
	{
		return incidentGraph.getConnectedfVertexes(st).length;
	}

	// Ojo! que té trampa
	// Conté el calcul de PWW
	public double calculatePrimitivesRelationProb(Statistics stat)
	{
		double suave = 0.01,pW,pD,pS,pN,pWalls=1,pDoors=1,pSeps=1,pNoths=1;
		int[][] A = stat.samplingConfiguration;
		double[][] B = new double[A.length][A[0].length];

		int a = 0;
		for (int i=0;i<A.length;i++)
		{
			for (int j=0;j<A[0].length;j++)
			{
				a+=A[i][j];
			}
		}
		double sumB[] = new double[4]; double b=0;
		for (int i=0;i<A.length;i++)
		{
			for (int j=0;j<A[0].length;j++)
			{
				B[i][j] = (double)A[i][j]/a;

				B[i][j]+= suave;
				b+=B[i][j];
			}
		}
		for (int i=0;i<A.length;i++)
		{
			for (int j=0;j<A[0].length;j++)
			{
				B[i][j]/=b;
				if (j==0)
					sumB[0]+=B[i][j];
				else if(j==1)
					sumB[1]+=B[i][j];
				else if (j==2)
					sumB[2]+=B[i][j];
				else if (j==3)
					sumB[3]+=B[i][j];
			}
		}

		pW = sumB[0]/(sumB[0]+sumB[1]+sumB[2]+sumB[3]);
		pD = sumB[1]/(sumB[0]+sumB[1]+sumB[2]+sumB[3]);
		pS = sumB[2]/(sumB[0]+sumB[1]+sumB[2]+sumB[3]);
		pN = sumB[3]/(sumB[0]+sumB[1]+sumB[2]+sumB[3]);

		stat.samplingProb = B;

		MyGraph copyIncident = nothingGraph.graphValueCopy();

		double totalProb = 1;
		for (DefaultEdge e : nothingGraph.getEdges())
		{
			double prob = 1;

			StructuralElement st1 = nothingGraph.getSource(e);
			StructuralElement st2 = nothingGraph.getTarget(e);

			double p = stat.samplingProb[st1.getSamplingIndex()][st2.getSamplingIndex()];
			// la trampa
			//if(p>0.008)
			{
				//prob = prob*p;
				st1.setProbabilityDist(prob);
				st2.setProbabilityDist(prob);
				totalProb = totalProb*p;
			}
			//else copyIncident.removeEdge(e);

		}

		StructuralElement[] walls = this.getWalls();
		StructuralElement[] doors = this.getDoors();
		StructuralElement[] seps = this.getSeparations();
		StructuralElement[] noths = this.getNothings();

		for (int i=0;i<walls.length;i++)
		{
			StructuralElement wall = walls[i];
			int numOfNeigs = copyIncident.getConnectedfVertexes(wall).length;

			pWalls = pWalls*( Math.pow(pW, numOfNeigs-1)  );
		}

		for (int i=0;i<doors.length;i++)
		{
			StructuralElement door = doors[i];
			int numOfNeigs = copyIncident.getConnectedfVertexes(door).length;

			pDoors = pDoors*( Math.pow(pD, numOfNeigs-1)  );
		}

		for (int i=0;i<seps.length;i++)
		{
			StructuralElement sep = seps[i];
			int numOfNeigs = copyIncident.getConnectedfVertexes(sep).length;

			pSeps = pSeps*( Math.pow(pS, numOfNeigs-1)  );
		}

		for (int i=0;i<noths.length;i++)
		{
			StructuralElement noth = noths[i];
			int numOfNeigs = copyIncident.getConnectedfVertexes(noth).length;

			pNoths = pNoths*( Math.pow(pN, numOfNeigs-1)  );
		}

		double PWW = totalProb/pWalls/pDoors/pSeps/pNoths;
		//totalProb = totalProb/cont1;
		return PWW;
	}

	public void setDegreeOfConnectivity2Rooms()
	{
		for (StructuralElement r : accessGraph.getVertexes())
		{
			int degree = accessGraph.getGraph().edgesOf(r).size();
			r.setConnectivityDegree(degree);
		}
	}
	/**
	 * This function returns the separators between the couple of rooms R1 and R2
	 * @param r1 is a Room
	 * @param r2 is a Room
	 * @return an array of structural elements 
	 */
	public StructuralElement[] getBordersBetweenRooms(Room r1, Room r2)
	{
		// get the separation/s of the merging
		Set<StructuralElement> stSet1 = andGraph.getItsChildren(r1);
		Set<StructuralElement> sepSet = new HashSet<StructuralElement>();
		for(StructuralElement st : andGraph.getItsChildren(r2))
		{
			if(stSet1.contains(st)  && (st instanceof Separation || st instanceof Door))
				sepSet.add(st);
		}
		StructuralElement[] sepArray = new StructuralElement[sepSet.size()];

		int i = 0;
		for(StructuralElement st : sepSet)
		{
			sepArray[i] =  st;
			i++;
		}
		return sepArray;
	}

	/**
	 * This function returns the separators that are shared by several rooms
	 * @param setIds is a Set of room ids
	 * @return an array of structural elements 
	 */
	public StructuralElement[] getAllBordersBetweenRoomIds(Set<Integer> setIds)
	{
		// get the separation/s of the merging
		Set<StructuralElement> rooms = this.getRoomsById(setIds);
		Set<StructuralElement> stSet = new HashSet<StructuralElement>();
		Set<StructuralElement> stSetRep = new HashSet<StructuralElement>();

		for (StructuralElement st : rooms)
		{
			Set<StructuralElement> chSet = andGraph.getItsChildren(st);
			for(StructuralElement st2 : chSet)
			{
				if (!stSet.add(st2)  && (st2 instanceof Separation || st2 instanceof Door))
					stSetRep.add(st2);
			}
		}

		// convert it to array
		StructuralElement[] sepArray = new StructuralElement[stSetRep.size()];

		int i = 0;
		for(StructuralElement st : stSetRep)
		{
			sepArray[i] =  st;
			i++;
		}
		return sepArray;
	}

	/**
	 * This function returns the room elements given a set of Ids
	 * @param ids is a set of integers 
	 * @return a set of StructuralElements
	 */
	public Set<StructuralElement> getRoomsById(Set<Integer> ids)
	{
		Set<StructuralElement> rooms = new HashSet<StructuralElement>();
		for (StructuralElement st : this.getRooms())
		{
			if (ids.contains(st.getId()))
				rooms.add(st);
		}
		return rooms;
	}

	/**
	 * This function return the region image for a given {@link StructuralElement}
	 * @param st is a {@link StructuralElement}
	 * @return a binary image
	 */
	public boolean[][] getStructuralElementImage(StructuralElement st)
	{
		if ((st == null) || !(andGraph.getGraph().vertexSet().contains(st)))
			return null;
		else
		{
			boolean[][] image = new boolean[regionImage.length][regionImage[0].length];
			for(int i=0; i<regionImage.length;i++)
			{
				for(int j=0; j<regionImage[i].length;j++)
				{
					if(regionImage[i][j] == st.getId())
						image[i][j] = true;
					else image[i][j] = false;
				}
			}
			return image;
		}
	}

	/**
	 * This function return the region image for the given ID {@link StructuralElement}
	 * @param id is an id of an {@link StructuralElement}
	 * @return a binary image
	 */
	public boolean[][] getStructuralElementImageFromId(int id)
	{
		boolean[][] image = new boolean[regionImage.length][regionImage[0].length];
		for(int i=0; i<regionImage.length;i++)
		{
			for(int j=0; j<regionImage[i].length;j++)
			{
				if(regionImage[i][j] == id)
					image[i][j] = true;
				else image[i][j] = false;
			}
		}
		return image;
	}

	public void calculateDSVSArea()
	{
		for (StructuralElement st : andGraph.getVertexesOfType("Room"))
		{
			double totalRoom = 0;
			MyGraph rG = getRoomPlaneConfiguration((Room) st);
			for (DefaultEdge de : rG.getEdges())
			{
				if (rG.getSource(de) instanceof Separation)
				{
					Separation sep = (Separation) rG.getSource(de);
					totalRoom = totalRoom + sep.getLength();
				}
				else
				{
					if (rG.getTarget(de) instanceof Separation)
					{
						Separation sep = (Separation) rG.getTarget(de);
						totalRoom = totalRoom + sep.getLength();
					}
				}

			}
			totalRoom = totalRoom/st.getArea();
			st.setDSVSArea(totalRoom);
		}
	}

	public void setAndGraph(MyDirectedGraph g)
	{
		andGraph = g.graphValueCopy();
	}

	public void setNeighbourGraph(MyGraph g)
	{
		neighbourGraph = g.graphValueCopy();
	}

	public void setAccessGraph(MyGraph g)
	{
		accessGraph = g.graphValueCopy();
	}

	public void setIncidentGraph(MyGraph g)
	{
		incidentGraph = g.graphValueCopy();
	}

	public void setNothingGraph(MyGraph g)
	{
		nothingGraph = g.graphValueCopy();
	}



	// functionality

	/**
	 * This is the main function of creating the floor plan object after reading the GT.
	 * This function creates the AND graph and fills all the attributes of the graph.
	 * It first creates the and-graph and its relations. Then fills the entities attributes,
	 * such as perimeters, areas, exterioricity, etc.
	 * @throws GraphCreationException when no-file has been read.
	 * @throws RelationException when a Relation is not well defined.
	 */
	public void createGraphsGT() throws GraphCreationException, RelationException
	{
		// creation of the graphs
		andGraph = new MyDirectedGraph();
		incidentGraph = new MyGraph();
		accessGraph = new MyGraph();
		neighbourGraph = new MyGraph();


		// add all the elements read from the GT to the graph
		for (StructuralElement st : structuralElements)
		{
			andGraph.addVertex(st);
			if(st instanceof Room)
			{	
				accessGraph.addVertex(st);
				neighbourGraph.addVertex(st);
			}
			else incidentGraph.addVertex(st);
		}

		// add the building element
		StructuralElement building = new Building();
		andGraph.addVertex(building);

		// add the children (rooms) of the building
		for (StructuralElement st : andGraph.getVertexes())
		{
			if (st.getType() == "Room")
				andGraph.addEdge(building, st);
		}

		for (Relation r : relations)
		{
			// add the incident relations
			if (r.getType() == "incident")
			{
				// get the id's of the neighbor structural elements
				int id1 = r.getNameIds().get(0);
				int id2 = r.getNameIds().get(1);

				StructuralElement st1 = null, st2 = null;

				// get the structural elements
				for (StructuralElement st : structuralElements)
				{
					// add the vertex to the graph
					if (st.getId() == id1)
						st1 = st;
					else if (st.getId() == id2)
						st2 = st;	
				}
				if ( (st1 instanceof Wall || st1 instanceof Separation ||
						st1 instanceof Window || st1 instanceof Door || st1 instanceof Parking)
						&& (st2 instanceof Wall || st2 instanceof Separation ||
								st2 instanceof Window || st2 instanceof Door || st2 instanceof Parking) )
				{
					incidentGraph.addEdge(st1, st2);
				}
				else throw new RelationException("The " + r.getType() + " as part of the line:\n" +
						r.getSvgFile() + "\n connects at least one of the nonvalid elements");
			}
			// add the children (walls, doors, windows) of the rooms
			// and also the incident relations among them
			else if (r.getType() == "surround")
			{
				Room room = null;
				// first get the room of the relation
				for (int i : r.getNameIds())
				{
					for (StructuralElement st : structuralElements)
					{
						if (st.getId() == i && st instanceof Room )
							room = (Room) st;
					}
				}
				// add the child relations
				for (int i=0;i<r.getNameIds().size()-1;i++)
				{
					int id1 = r.getNameIds().get(i);;
					int id2 = r.getNameIds().get(i+1);;

					StructuralElement st1 = null, st2 = null;
					for (StructuralElement st : structuralElements)
					{
						if (st.getId() == id1 && !(st instanceof Room) )
							st1 = st;
						if (st.getId() == id2 && !(st instanceof Room) )
							st2 = st;
					}
					if (st1 != null && st2 != null)
					{
						incidentGraph.addEdge(st1, st2);

						andGraph.addEdge(room, st1);
						andGraph.addEdge(room, st2);
					}
					else if (st1 != null && st2 == null)
					{
						andGraph.addEdge(room, st1);
					}
					else if (st1 == null && st2 != null)
					{
						andGraph.addEdge(room, st2);
					}
				}
			}
			// add the neighbor and access relations
			if (r.getType() == "neighbour")
			{
				// get the id's of the neighbor structural elements
				int id1 = r.getNameIds().get(0);
				int id2 = r.getNameIds().get(1);

				StructuralElement st1 = null, st2 = null;

				// get the structural elements
				for (StructuralElement st : structuralElements)
				{ 	
					// add the vertex to the graph
					if (st.getId() == id1)
						st1 = st;
					else if (st.getId() == id2)
						st2 = st;										
				}
				// add the relation into the graph
				if(st1 instanceof Room && st2 instanceof Room)
				{	
					neighbourGraph.addEdge(st1, st2);
				}
				else throw new RelationException("The " + r.getType() + " as part of the line:\n" +
						r.getSvgFile() + "\n connects at least one of the nonvalid elements");
			}
			// add the access relation into the graph
			if (r.getType() == "access")
			{
				// get the id's of the access structural elements
				int id1 = r.getNameIds().get(0);
				int id2 = r.getNameIds().get(1);
				int id3 = r.getNameIds().get(2);

				StructuralElement st1 = null, st2 = null, st3 = null;
				// get the structural elements
				for (StructuralElement st : structuralElements)
				{ 	
					// add the vertex to the graph
					if (st.getId() == id1)
						st1 = st;
					else if (st.getId() == id2)
						st2 = st;
					else if (st.getId() == id3)
						st3 = st;
				}
				// see which element is the door and add the access between the rooms
				if ( (st1 instanceof Door || st1 instanceof Separation)
						&& st2 instanceof Room && st3 instanceof Room)
				{
					Room r1 = (Room) st2;
					Room r2 = (Room) st3;
					r1.setAccessible(true);
					r2.setAccessible(true);
					accessGraph.addEdge(st2, st3);
				}
				else if ( (st2 instanceof Door || st2 instanceof Separation)
						&& st1 instanceof Room && st3 instanceof Room)
				{
					Room r1 = (Room) st1;
					Room r2 = (Room) st3;
					r1.setAccessible(true);
					r2.setAccessible(true);
					accessGraph.addEdge(st1, st3);
				}
				else if ( (st3 instanceof Door || st3 instanceof Separation) 
						&& st1 instanceof Room && st2 instanceof Room)
				{
					Room r1 = (Room) st2;
					Room r2 = (Room) st1;
					r1.setAccessible(true);
					r2.setAccessible(true);
					accessGraph.addEdge(st1, st2);
				}
				// if one of st2 or st3 is null means the the accessibility is to the building
				else if ( st2 == null || st3 == null)
				{
					Room room = (Room) st1;
					room.setEntrance(true);
				}
				else throw new RelationException("The " + r.getType() + " as part of the line:\n" +
						r.getSvgFile() + "\n connects at least one of the nonvalid elements");
			}
		}

		// get the outer-perimeter of the
		// this is labelled in the groundtruth as outerP
		outerPerimeter = new ArrayList<StructuralElement>();
		for (Relation r : relations)
		{
			// get the relation
			if (r.getType() == "outerP")
			{
				List<Integer> ids = r.getNameIds();
				// get the elements
				for (StructuralElement st : structuralElements)
				{
					if (ids.contains(st.getId()))
					{
						if (st instanceof Wall || st instanceof Window || st instanceof Door || 
								st instanceof Separation || st instanceof Parking)
						{
							st.setExterior(true);
							outerPerimeter.add(st);
						}
					}
				}
			}
		}
		// set the rooms whether they are exterior or not
		// it will depend on their children if they are exterior or not
		for (StructuralElement st : andGraph.getVertexes())
		{
			// only take into account the rooms
			if (st instanceof Room && !st.isExterior())
			{
				Set<StructuralElement> stSet = andGraph.getItsChildren(st);
				// see if they have at least one child which is exterior
				for (StructuralElement kid : stSet)
				{
					// if the child is exterior, we set the room exterior
					if(kid.isExterior())
					{
						st.setExterior(true);
						break;
					}
				}
			}
		}

		// get the building area and perimeter
		// the area is the summatory of the areas of the room
		// the perimeter is the length of the outer perimeter elements of the building
		double area = 0, perimeter = 0;

		Set<StructuralElement> rooms = andGraph.getVertexesOfType("Room");
		for (StructuralElement st : rooms)
		{
			area = area + st.getArea();
		}
		building.setArea(area);

		// get the perimeter
		for (StructuralElement st : andGraph.getVertexes())
		{
			if ( !(st instanceof Room) && !(st instanceof Building) && st.isExterior() )
			{
				Polygon p = st.getPol();
				// BoundingBox of the st
				Rectangle r = p.getBounds();
				if (r.getHeight()>r.getWidth())
					perimeter = perimeter + r.getHeight();
				else perimeter = perimeter + r.getWidth();
			}
		}
		building.setPerimeter(perimeter);
	}

	/**
	 * This function checks the correctness of the floor plan in terms of the rooms
	 * accessibility and window exterioribility. It is important to check whether
	 * no relation is missed in the GT. All the rooms should be accessible from another
	 * room and it must exist at least one entrance to the building. Likewise, all the
	 * windows in the floor plan must be in the outer perimeter. 
	 * @return true whether the plan is correct.
	 * @throws FloorPlanGTException
	 */
	public boolean checkFloorPlanCorrectness() throws FloorPlanGTException
	{
		// check the AndGraph
		if (andGraph == null)
			throw new FloorPlanGTException("The AndGraph is null");
		else if (andGraph.getVertexes().size() == 0)
			throw new FloorPlanGTException("The AndGraph doesn't have any vertex");
		else if (andGraph.getEdges().size() == 0)
			throw new FloorPlanGTException("The AndGraph doesn't have any edge");

		// check the accessibility
		int numberEntrance = 0;
		for (StructuralElement st : andGraph.getVertexes())
		{
			// check whether is a room
			if (st.getType() == "Room")
			{
				Room r = (Room) st;
				if (!r.isAccessible() && !r.isEntrance())
					throw new FloorPlanGTException("The room id " + r.getId() + " is not" +
							" accessible from any other room");
				if (r.isEntrance()) numberEntrance++;
			}
			if (st.getType() == "Window" && !st.isExterior())
				throw new FloorPlanGTException("The window id " + st.getId() + " is not" +
						" exterior");

		}
		// all the rooms must be accessible from another room
		if (numberEntrance < 1)
			throw new FloorPlanGTException("There is not any entrance room");
		return true;
	}

	/**
	 * This function creates the AND-Graph from the structural elements and the adjacency matrices.
	 * It is difference from the createAND-Graph, since there are not relations; all of them are
	 * induced from the adjacency matrix.
	 */
	public void createGraphs() throws GraphCreationException, RelationException
	{
		// creation of the AndGraph
		andGraph = new MyDirectedGraph();
		incidentGraph = new MyGraph();
		accessGraph = new MyGraph();
		neighbourGraph = new MyGraph();
		nothingGraph = new MyGraph();

		// add all the elements
		for (StructuralElement st : structuralElements)
		{
			andGraph.addVertex(st);
			if (st instanceof Room)
			{
				accessGraph.addVertex(st);
				neighbourGraph.addVertex(st);
			}
			else incidentGraph.addVertex(st);
		}

		// add the building element
		StructuralElement building = new Building(0);
		andGraph.addVertex(building);

		// add the children (rooms) of the building
		for (StructuralElement st : andGraph.getVertexes())
		{
			if (st.getType() == "Room")
				andGraph.addEdge(building, st);
		}

		// add the relations in the adjacency matrix
		for (int i=0; i<adjacencyMatrix.length; i++)
		{
			int id1 = adjacencyMatrix[i][0];
			int id2 = adjacencyMatrix[i][1];
			StructuralElement st1 = null;
			StructuralElement st2 = null;

			for(StructuralElement st:andGraph.getVertexes())
			{
				if (st.getId() == id1)
					st1 = st;

				else if (st.getId() == id2)
					st2 = st;
			}

			if (st1 instanceof Room)
			{
				if(st2 instanceof Room)
					neighbourGraph.addEdge(st1, st2);
				else
				{
					//System.out.println(st1 + ":" + st2);
					andGraph.addEdge(st1, st2);
				}

			}
			else if (st2 instanceof Room)
			{
				if(st1 instanceof Room)
					neighbourGraph.addEdge(st1, st2);
				else andGraph.addEdge(st1, st2);
			}
			else
			{
				incidentGraph.addEdge(st1, st2);
			}
			// calculate the access graph
			calculateAccessibilityGraph();

		}
		nothingGraph = incidentGraph.graphValueCopy();
	}

	/**
	 * This function creates the accessibility graph. For each couple of rooms
	 * checks if they contain a door or a separation as children.
	 */
	private void calculateAccessibilityGraph()
	{
		for (StructuralElement st1:andGraph.getVertexesOfType("Room"))
		{
			for (StructuralElement st2:andGraph.getVertexesOfType("Room"))
			{
				if (st1.equals(st2))
					break;
				else
				{
					for(StructuralElement st1Fill : andGraph.getItsChildren(st1))
					{
						for(StructuralElement st2Fill : andGraph.getItsChildren(st2))
						{
							if((st1Fill instanceof Door || st1Fill instanceof Separation) && st1Fill.equals(st2Fill))
								accessGraph.addEdge(st1, st2);
						}
					}
				}
			}
		}
	}

	/**
	 * This function cleans from the floor plan those rooms that have a
	 * a very small area under a given integer threshold called area
	 */
	public void cleanVerySmallRooms(int area)
	{
		List<Integer> listInt = new ArrayList<Integer>();
		// for each st in the graph
		for (StructuralElement st : andGraph.getVertexes())
		{
			// check if it is a room and if it has a zero area
			if( (st instanceof Room) && (st.getArea() < area) )
			{
				// delete the node from the graph
				listInt.add(st.getId());
				andGraph.removeVertex(st);
				neighbourGraph.removeVertex(st);
				accessGraph.removeVertex(st);
			}
		}
		// in the region image put to 0 those id's equal to the zero images
		for (int i=0; i<regionImage.length; i++)
		{
			for (int j=0; j<regionImage[i].length; j++)
			{
				if(listInt.contains(regionImage[i][j]))
					regionImage[i][j] = 0;
			}
		}
	}

	/**
	 * This function deletes those rooms that are isolated, this is they do not have any
	 * edge connecting their children in the andGraph
	 */
	public void cleanIsolatedRooms()
	{
		List<Integer> listInt = new ArrayList<Integer>();
		for (StructuralElement st : andGraph.getVertexesOfType("Room"))
		{
			MyGraph rG = getRoomPlaneConfiguration((Room) st);
			if (rG.getEdges().size()==0)
			{
				// delete the node from the graph
				listInt.add(st.getId());
				andGraph.removeVertex(st);
				neighbourGraph.removeVertex(st);
				accessGraph.removeVertex(st);
			}
		}
		// in the region image put to 0 those id's equal to the zero images
		for (int i=0; i<regionImage.length; i++)
		{
			for (int j=0; j<regionImage[i].length; j++)
			{
				if(listInt.contains(regionImage[i][j]))
					regionImage[i][j] = 0;
			}
		}
	}

	/**
	 * puts those image pixels that are equal to id to 0
	 * @param ide is an int with the id of the element
	 */
	public void cleanImageById(int ide)
	{
		// in the region image put to 0 those id's equal id
		for (int i=0; i<regionImage.length; i++)
		{
			for (int j=0; j<regionImage[i].length; j++)
			{
				if(regionImage[i][j]==ide)
					regionImage[i][j] = 0;
			}
		}
	}


	/**
	 * This function merges 2 rooms. It is critical step that implies the upload of the floorplan structure
	 * The 4 graphs of the floor plan are updeted. In the andgraph, the children of the new room is composed
	 * by the union of the children of the 2 predecesors. In the neighbor and acces graph, while the 2 rooms are 
	 * erased, the new one inherits their whole connections. Finally, in the incident graph the separations between
	 * the two old room are removed. The region Image of the plan is also updated.
	 * @param r1 is an old Room	
	 * @param r2 is an old Room
	 * @param newRoom is the new Room
	 * @param imagePlan is the new imageRegion for the plan
	 */
	public void mergeTwoRooms(Room r1, Room r2, Room newRoom, int[][] imagePlan)
	{
		// AND GRAPH
		// let's start by getting the separation elements that have to be eliminated
		StructuralElement[] sepElements = getBordersBetweenRooms(r1, r2);
		// get the building element
		StructuralElement building = null;
		for (StructuralElement st : andGraph.getItsParents(r1))
			building = st;
		// get the children
		Set<StructuralElement> stChildren = new HashSet<StructuralElement>();
		for (StructuralElement st : andGraph.getItsChildren(r1))
			stChildren.add(st);
		for (StructuralElement st : andGraph.getItsChildren(r2))
			stChildren.add(st);
		// remove from them the separations that will be eliminated
		stChildren.removeAll(Arrays.asList(sepElements));
		// remove the old rooms
		andGraph.removeVertex(r1);
		andGraph.removeVertex(r2);
		// remove the separations
		for (int i = 0; i < sepElements.length; i++)
			andGraph.removeVertex(sepElements[i]);
		// add the new room
		andGraph.addVertex(newRoom);
		// add the realtion with the building
		andGraph.addEdge(building, newRoom);
		// add the children
		for (StructuralElement st : stChildren)
			andGraph.addEdge(newRoom, st);

		// NEIGHBOUR GRAPH
		// get all the neighbour nodes of the two old rooms except their own
		Set<StructuralElement> connectedV = new HashSet<StructuralElement>(); 
		connectedV.addAll(Arrays.asList(neighbourGraph.getConnectedfVertexes(r1)));
		connectedV.addAll(Arrays.asList(neighbourGraph.getConnectedfVertexes(r2)));
		connectedV.remove(r1);
		connectedV.remove(r2);
		// remove the old rooms from the graph
		neighbourGraph.removeVertex(r1);
		neighbourGraph.removeVertex(r2);
		// add the new room and connect it to its neighbours
		neighbourGraph.addVertex(newRoom);
		for (StructuralElement st : connectedV)
			neighbourGraph.addEdge(newRoom, st);

		// AND GRAPH
		// get all the neighbour nodes of the two old rooms except their own
		connectedV.clear(); 
		connectedV.addAll(Arrays.asList(accessGraph.getConnectedfVertexes(r1)));
		connectedV.addAll(Arrays.asList(accessGraph.getConnectedfVertexes(r2)));
		connectedV.remove(r1);
		connectedV.remove(r2);
		// remove the old rooms from the graph
		accessGraph.removeVertex(r1);
		accessGraph.removeVertex(r2);
		// add the new room and connect it to its neighbours
		accessGraph.addVertex(newRoom);
		for (StructuralElement st : connectedV)
			accessGraph.addEdge(newRoom, st);


		// INCIDENT GRAPH
		// here only is needed to delete the separations between the two rooms
		for (int i = 0; i < sepElements.length; i++)
			incidentGraph.removeVertex(sepElements[i]);

		// REGION IMAGE
		this.setRegionImage(imagePlan);
	}

	/**
	 * This function merges 2 rooms. It is critical step that implies the upload of the floorplan structure
	 * The 4 graphs of the floor plan are updeted. In the andgraph, the children of the new room is composed
	 * by the union of the children of the 2 predecesors. In the neighbor and acces graph, while the 2 rooms are 
	 * erased, the new one inherits their whole connections. Finally, in the incident graph the separations between
	 * the two old room are removed. The region Image of the plan is also updated.
	 * @param r1 is an old Room	
	 * @param r2 is an old Room
	 * @param newRoom is the new Room
	 * @param imagePlan is the new imageRegion for the plan
	 */
	public void mergeRooms(Set<Integer> roomIds, Room newRoom, int[][] imagePlan)
	{
		// SetOfRooms
		Set<StructuralElement> rooms = getRoomsById(roomIds);

		// let's start by getting the separation elements that have to be eliminated
		StructuralElement[] sepElements = getAllBordersBetweenRoomIds(roomIds);

		// create the nothing elements
		StructuralElement[] nothElements = new StructuralElement[sepElements.length];
		for (int i = 0; i < sepElements.length;i++)
		{
			StructuralElement nothing = new Nothing(sepElements[i].getId(),sepElements[i].getLength());
			nothElements[i] = nothing;
		}




		// AND GRAPH

		// get the building element
		StructuralElement building = null;
		StructuralElement r1 = rooms.iterator().next();
		for (StructuralElement st : andGraph.getItsParents(r1))
			building = st;
		// get the children
		Set<StructuralElement> stChildren = new HashSet<StructuralElement>();
		for (StructuralElement room : rooms)
		{
			for (StructuralElement st : andGraph.getItsChildren(room))
				stChildren.add(st);
		}
		// remove from them the separations that will be eliminated
		stChildren.removeAll(Arrays.asList(sepElements));
		// remove the old rooms
		for(StructuralElement room : rooms)
			andGraph.removeVertex(room);

		// remove the separations
		for (int i = 0; i < sepElements.length; i++)
			andGraph.removeVertex(sepElements[i]);
		// add the new room
		andGraph.addVertex(newRoom);
		// add the relation with the building
		andGraph.addEdge(building, newRoom);
		// add the children
		for (StructuralElement st : stChildren)
			andGraph.addEdge(newRoom, st);
		// add the nothings
		for (StructuralElement st : nothElements)
		{
			andGraph.addVertex(st);
			andGraph.addEdge(newRoom, st);
		}

		// NEIGHBOUR GRAPH
		// get all the neighbour nodes of the two old rooms except their own
		Set<StructuralElement> connectedV = new HashSet<StructuralElement>(); 
		for (StructuralElement room : rooms)
			connectedV.addAll(Arrays.asList(neighbourGraph.getConnectedfVertexes(room)));
		for (StructuralElement room : rooms)
			connectedV.remove(room);

		// remove the old rooms from the graph
		for (StructuralElement room : rooms)
			neighbourGraph.removeVertex(room);

		// add the new room and connect it to its neighbours
		neighbourGraph.addVertex(newRoom);
		for (StructuralElement st : connectedV)
			neighbourGraph.addEdge(newRoom, st);

		// AND GRAPH
		// get all the neighbour nodes of the two old rooms except their own
		connectedV.clear(); 
		for (StructuralElement room : rooms)
			connectedV.addAll(Arrays.asList(accessGraph.getConnectedfVertexes(room)));

		for (StructuralElement room : rooms)
			connectedV.remove(room);

		// remove the old rooms from the graph
		for (StructuralElement room : rooms)
			accessGraph.removeVertex(room);

		// add the new room and connect it to its neighbours
		accessGraph.addVertex(newRoom);
		for (StructuralElement st : connectedV)
			accessGraph.addEdge(newRoom, st);


		// INCIDENT GRAPH
		// here only is needed to delete the separations between the two rooms
		for (int i = 0; i < sepElements.length; i++)
			incidentGraph.removeVertex(sepElements[i]);


		// NOTHING GRAPH
		Set<StructuralElement> connectedV1 = new HashSet<StructuralElement>(); 
		for (int i = 0; i < sepElements.length;i++)
		{
			connectedV1.addAll(Arrays.asList(nothingGraph.getConnectedfVertexes(sepElements[i])));
			//StructuralElement nothing = new Nothing(sepElements[i].getId(), sepElements[i].getSvgLine(), sepElements[i].getPol());
			StructuralElement nothing = new Nothing(sepElements[i].getId(),sepElements[i].getLength());
			nothingGraph.removeVertex(sepElements[i]);
			nothingGraph.addVertex(nothing);
			for (StructuralElement st1 : connectedV1)
				nothingGraph.addEdge(nothing,st1);
		}



		// REGION IMAGE
		this.setRegionImage(imagePlan);
	}

	/**
	 * This function changes the class of primitives. It is critical step that implies the upload of the floorplan structure
	 * The 4 graphs of the floor plan are updeted. In the andgraph, the children of the new room is composed
	 * by the union of the children of the 2 predecesors. In the neighbor and acces graph, while the 2 rooms are 
	 * erased, the new one inherits their whole connections. Finally, in the incident graph the separations between
	 * the two old room are removed. The region Image of the plan is also updated.
	 * @param r1 is an old Room	
	 * @param r2 is an old Room
	 * @param newRoom is the new Room
	 * @param imagePlan is the new imageRegion for the plan
	 */
	public boolean changePrimitiveClass(Set<Integer> roomIds)
	{
		// SetOfRooms
		Set<StructuralElement> rooms = getRoomsById(roomIds);

		// AND GRAPH
		// let's start by getting the separation elements that have to be eliminated
		StructuralElement[] sepElements = getAllBordersBetweenRoomIds(roomIds);

		// get the children
		Set<StructuralElement> stChildren = new HashSet<StructuralElement>();
		for (StructuralElement room : rooms)
		{
			for (StructuralElement st : andGraph.getItsChildren(room))
				stChildren.add(st);
		}
		// remove from them the separations that will be eliminated
		stChildren.removeAll(Arrays.asList(sepElements));

		StructuralElement firstElement = stChildren.iterator().next();

		// if the elements have been alredy changed return false
		if(firstElement.getAlreadyChanged())
			return false;

		StructuralElement[] newSet = new StructuralElement[sepElements.length];

		int cont = 0;
		// create new elements
		for (StructuralElement st : sepElements)
		{
			st.setAlreadyChanged();
			StructuralElement newSt;
			if(st.getType().equals("Door"))
				newSt = new Separation(st.getId(),st.getArea(),st.getCenterOfMass2(),st.getPerimeter(),st.getExtrema(),st.getLength(),st.getBoundingBox());
			else newSt = new Door(st.getId(),st.getArea(),st.getCenterOfMass2(),st.getPerimeter(),st.getExtrema(),st.getLength(),st.getBoundingBox());
			newSet[cont] = newSt;
			cont++;
		}

		// remove the new primitives
		for (int i = 0; i < sepElements.length; i++)
			andGraph.removeVertex(sepElements[i]);
		// add the new elements into the andgraph
		for (StructuralElement st : newSet)
			andGraph.addVertex(st);
		// add the new link between rooms and the new primitives
		for (StructuralElement r : rooms)
		{
			for (StructuralElement st : newSet)
			{
				andGraph.addEdge(r, st);
			}
		}


		// INCIDENT GRAPH
		// here we need to relocate the new instances
		for (int i = 0; i < sepElements.length; i++)
		{
			// look for the edges of the old elements
			StructuralElement st1 = sepElements[i];
			StructuralElement st2 = newSet[i];
			Set<StructuralElement> connectedSt = new HashSet<StructuralElement>();
			for (StructuralElement st3 : incidentGraph.getConnectedfVertexes(st1))
				connectedSt.add(st3);
			// delete the old elements
			incidentGraph.removeVertex(st1);
			incidentGraph.addVertex(st2);
			// connect the vertexes
			for (StructuralElement st : connectedSt)
				incidentGraph.addEdge(st, st2);

		}

		// Nothing GRAPH
		// here we need to relocate the new instances
		for (int i = 0; i < sepElements.length; i++)
		{
			// look for the edges of the old elements
			StructuralElement st1 = sepElements[i];
			StructuralElement st2 = newSet[i];
			Set<StructuralElement> connectedSt = new HashSet<StructuralElement>();
			for (StructuralElement st3 : nothingGraph.getConnectedfVertexes(st1))
				connectedSt.add(st3);
			// delete the old elements
			nothingGraph.removeVertex(st1);
			nothingGraph.addVertex(st2);
			// connect the vertexes
			for (StructuralElement st : connectedSt)
				nothingGraph.addEdge(st, st2);

		}

		return true;
	}

	/**
	 * This function deletes from the floor plan a set of rooms passed as parameter.
	 * It deletes the rooms from the graphs where they are involved. Deletes their
	 * separation children and updates the floor plan image
	 * @param setOfRooms is a set of rooms to be eliminated
	 */
	public void deleteASetOfRooms(Set<Room> setOfRooms)
	{
		for (Room r : setOfRooms)
		{
			// delete the rooms from the graphs
			List<Integer> idList = new ArrayList<Integer>();
			idList.add(r.getId());
			Set<StructuralElement> children = andGraph.getItsChildren(r);
			andGraph.removeVertex(r);
			neighbourGraph.removeVertex(r);
			accessGraph.removeVertex(r);

			for (StructuralElement st: children)
			{
				if (st instanceof Separation)
					idList.add(st.getId());
			}


			// update the region image
			for (int i=0; i<regionImage.length; i++)
			{
				for (int j=0; j<regionImage[i].length; j++)
				{
					if (idList.contains(regionImage[i][j]))
						regionImage[i][j] = 0;
				}
			}
		}
	}

	/**
	 * Get the set of rooms isolated in a given graph. This rooms are isolated,
	 * they do not have any edge with any other room.
	 * @param graphType is the type of graph "Access" or "Neighbour"
	 * @return the set of rooms. In the case that all the rooms are connected,
	 * it returns an empty set.
	 */
	public Set<StructuralElement> getRoomNotConnected(String graphType)
	{
		MyGraph graph;
		if (graphType.equals("Access"))
			graph = accessGraph;
		else graph = neighbourGraph;

		return graph.getDisconnectedElements();
	}

	public int[][] drawImage()
	{
		int[][] image = new int[regionImage.length][regionImage[0].length];
		Set<Integer> roomSet = new HashSet<Integer>();
		Set<Integer> wallSet = new HashSet<Integer>();
		Set<Integer> doorSet = new HashSet<Integer>();
		Set<Integer> sepSet = new HashSet<Integer>();
		int maxRoomId = 0;                                            
		for(StructuralElement st : andGraph.getVertexes())
		{
			if (st instanceof Room)
			{
				roomSet.add(st.getId());
				if(st.getId()>maxRoomId)
					maxRoomId = st.getId();
			}
			else
			{
				if (st instanceof Wall)
				{
					wallSet.add(st.getId());
				}
				else
				{
					if (st instanceof Door)
						doorSet.add(st.getId());
					else sepSet.add(st.getId());
				}
			}
		}
		for (int i=0;i<regionImage.length;i++)
		{
			for(int j=0;j<regionImage[i].length;j++)
			{
				if (regionImage[i][j] == 0)
					image[i][j] = 0;
				else
				{
					if(wallSet.contains(regionImage[i][j]))
						image[i][j] = maxRoomId+1;
					else
					{
						if(doorSet.contains(regionImage[i][j]))
							image[i][j] = maxRoomId+10;
						else
						{
							if(sepSet.contains(regionImage[i][j]))
								image[i][j] = maxRoomId+30;
							else
							{
								image[i][j] = regionImage[i][j];
							}
						}
					}
				}
			}
		}
		return image;
	}

	/**
	 * This function calculates all valid room mergings for the actual rooms of the plan.
	 * @return a Set of all possible sets containing the combinations
	 */
	public List<Set<Set<Integer>>> calculateAllValidRoomMergings()
	{
		Set<Integer> roomsIds = new HashSet<Integer>();

		for(StructuralElement st : this.getRooms())
			roomsIds.add(st.getId());

		// get all possible combinations involving 2 or more rooms
		Set<Set<Integer>> powerSet = PowerSet.powerSet(2,roomsIds);

		// get only those merging that are valid agreeing with the graph
		List<Set<Integer>> validList = new ArrayList<Set<Integer>>();
		for (Set<Integer> set : powerSet)
		{
			if (set.size() > 1)
			{
				MyGraph g = accessGraph.graphValueCopy();
				g.removeTheRestOfTheVertexesById(set);
				if (g.isConnected())
					validList.add(set);
			}
		}
		// create all the possible combination of merging
		Set<Set<Integer>> validSet = new HashSet<Set<Integer>>(validList);
		Set<Set<Set<Integer>>> finalSet = new HashSet<Set<Set<Integer>>>();
		finalSet.addAll(PowerSet.powerSetSets(validSet));
		List<Set<Set<Integer>>> finalList = new ArrayList<Set<Set<Integer>>>(finalSet);
		// remove the empty set
		finalList.remove(0);
		return finalList;

	}

	/**
	 * This function calculates all valid room mergings of a given degree 
	 * for the actual rooms of the plan.
	 * @param degree is an integer specifying the degree of merging.
	 * @return a Set of all possible sets containing the combinations of the input degree
	 */
	public List<Set<Integer>> calculateAllValidRoomMergingsOfDegree(int degree)
	{
		Set<Integer> roomsIds = new HashSet<Integer>();

		for(StructuralElement st : this.getRooms())
			roomsIds.add(st.getId());

		Set<Set<Integer>> powerSet = new HashSet<Set<Integer>>();
		if (degree==2)
		{
			powerSet = new HashSet<Set<Integer>>();
			for(int ri1 : roomsIds)
			{
				for (int ri2 : roomsIds)
				{
					if (ri1 != ri2)
					{
						Set<Integer> intSet = new HashSet<Integer>();
						intSet.add(ri1);
						intSet.add(ri2);
						powerSet.add(intSet);
					}
				}
			}
		}
		else
		{
			//	get all possible combinations involving 2 or more rooms
			powerSet = PowerSet.powerSetEq(2,roomsIds);
		}
		// get only those merging that are valid agreeing with the graph
		List<Set<Integer>> validList = new ArrayList<Set<Integer>>();
		for (Set<Integer> set : powerSet)
		{
			if (set.size() > 1)
			{
				MyGraph g = accessGraph.graphValueCopy();
				g.removeTheRestOfTheVertexesById(set);
				if (g.isConnected())
					validList.add(set);
			}
		}

		return validList;

	}

	/**
	 * This function returns the room ids of the floor plan that are not
	 * included in the input set IDS
	 * @param ids is a SET of INTEGERS
	 * @return a SET of INTEGERS
	 */
	public Set<Integer> getRestOfRoomsIds(Set<Integer> ids)
	{
		Set<Integer> restOfIds = new HashSet<Integer>();

		for(StructuralElement st : this.getRooms())
		{
			if (!ids.contains(st.getId()))
			{
				restOfIds.add(st.getId());
			}
		}
		return restOfIds;
	}

	/**
	 * This function returns the room ids of the floor plan.
	 * @param ids is an array of INTEGERS
	 * @return a SET of INTEGERS
	 */
	public Integer[] getRoomsIds()
	{
		Integer[] ids = new Integer[this.getRooms().length];

		int i = 0;
		for(StructuralElement st : this.getRooms())
		{
			ids[i] = st.getId();
			i++;
		}
		return ids;
	}

	/**
	 * This function returns the associated adjacency matrix to the input string.
	 * @param type is a string specifying the matrix to be returned:
	 * When TYPE =
	 * INCIDENCE the returned matrix is the adjacency between walls, doors, and windows. 
	 * @return is an adjacency matrix.
	 */
	public AdjacencyMatrix getTheAdjacencyMatrixOnDemand(String type)
	{
		// the adjacency matrix to be returned
		AdjacencyMatrix am = null;

		// construct a HashMap with the elements wanted
		HashMap<Integer, StructuralElement> map = new HashMap<Integer, StructuralElement>();

		// look which is the adjacency matrix demanded
		if (type.equals("ww"))
		{
			int numOfCells = this.incidentGraph.getVertexes().size();
			// create the matrix
			am = new AdjacencyMatrix(numOfCells, numOfCells);

			//construct the matrix
			int i = 0;
			// set the idrows and idcolumns
			for (StructuralElement st : incidentGraph.getVertexes())
			{
				// store into a mapset the element and its key
				map.put(st.getId(), st);
				am.idrows[i] = st.getId();
				am.idcolumns[i] = st.getId();
				i++;
			}

			// time to calculate the adjacency matrix
			for (int n=0; n < am.idrows.length; n++)
			{
				for (int m=0; m < am.idcolumns.length; m++)
				{
					// when they are connected the adjacency matrix is 1
					if (incidentGraph.getGraph().containsEdge(map.get(am.idrows[n]), map.get(am.idcolumns[m])))
					{
						am.matrix[n][m] = 1;
						am.matrix[m][n] = 1;
					}
				}
			}
		}
		// look which is the adjacency matrix demanded
		if (type.equals("dw"))
		{
			int numOfRows = andGraph.getVertexesOfType("Room").size();
			int numOfColumns = incidentGraph.getVertexes().size();

			// create the matrix
			am = new AdjacencyMatrix(numOfRows, numOfColumns);

			// construct the matrix //
			int i = 0;
			// set the idrows and idcolumns
			for (StructuralElement st : andGraph.getVertexesOfType("Room"))
			{
				// store into a mapset the element and its key
				map.put(st.getId(), st);
				am.idrows[i] = st.getId();
				i++;
			}
			i=0;
			for (StructuralElement st : incidentGraph.getVertexes())
			{
				// store into a mapset the element and its key
				map.put(st.getId(), st);
				am.idcolumns[i] = st.getId();
				i++;
			}

			// time to calculate the adjacency matrix
			for (int n=0; n < am.idrows.length; n++)
			{
				for (int m=0; m < am.idcolumns.length; m++)
				{
					// when they are connected the adjacency matrix is 1
					if (andGraph.getGraph().containsEdge(map.get(am.idrows[n]), map.get(am.idcolumns[m])))
					{
						am.matrix[n][m] = 1;
					}
				}
			}
		}
		// look which is the adjacency matrix demanded
		if (type.equals("dd"))
		{
			int numOfCells = andGraph.getVertexesOfType("Room").size();
			// create the matrix
			am = new AdjacencyMatrix(numOfCells, numOfCells);

			// construct the matrix //
			int i = 0;
			// set the idrows and idcolumns
			for (StructuralElement st : andGraph.getVertexesOfType("Room"))
			{
				// store into a mapset the element and its key
				map.put(st.getId(), st);
				am.idrows[i] = st.getId();
				am.idcolumns[i] = st.getId();
				i++;
			}

			// time to calculate the adjacency matrix
			for (int n=0; n < am.idrows.length; n++)
			{
				for (int m=0; m < am.idcolumns.length; m++)
				{
					// when they are connected the adjacency matrix is 1
					if (accessGraph.getGraph().containsEdge(map.get(am.idrows[n]), map.get(am.idcolumns[m])))
					{
						am.matrix[n][m] = 1;
						am.matrix[m][n] = 1;
					}
				}
			}
		}
		// look which is the adjacency matrix demanded
		if (type.equals("ddgt"))
		{
			int numOfCells = andGraph.getVertexesOfType("Room").size();
			// create the matrix
			am = new AdjacencyMatrix(numOfCells, numOfCells);

			// construct the matrix //
			int i = 0;
			// set the idrows and idcolumns
			for (StructuralElement st : andGraph.getVertexesOfType("Room"))
			{
				// store into a mapset the element and its key
				map.put(st.getId(), st);
				am.idrows[i] = st.getId();
				am.idcolumns[i] = st.getId();
				i++;
			}

			// time to calculate the adjacency matrix
			for (int n=0; n < am.idrows.length; n++)
			{
				for (int m=0; m < am.idcolumns.length; m++)
				{
					StructuralElement s1 = map.get(am.idrows[n]);
					StructuralElement s2 = map.get(am.idrows[m]);
					Set<StructuralElement> st1 = andGraph.getItsChildren(map.get(am.idrows[n]));
					Set<StructuralElement> st2 = andGraph.getItsChildren(map.get(am.idrows[m]));

					st2.retainAll(st1);

					// when they are connected the adjacency matrix is 1
					for (StructuralElement st : st2)
					{
						if(!s1.equals(s2) && (st.getGTType().equals("Door") || st.getGTType().equals("Separation") || st.getGTType().equals("Nothing")))
						{
							am.matrix[n][m] = 1;
							am.matrix[m][n] = 1;
						}
					}
				}
			}
		}
		return am;
	}

	/**
	 * Get the domains that are adjacent and the primitive that have in common.
	 * @return a double with triplets, where [0] and [1] are for domains and [2] for
	 * the primitive that relates them in terms of neighborhood.
	 */
	public double[][] getRelationBetweenDomainsAndPrimitives()
	{

		// construct a HashMap with the elements wanted
		HashMap<Integer, StructuralElement> map = new HashMap<Integer, StructuralElement>();
		HashMap<Integer, StructuralElement> map2 = new HashMap<Integer, StructuralElement>();

		// this will be the final list of triplets
		ArrayList<StructuralElementTriplets> stTriplets = new ArrayList<StructuralElementTriplets>();

		// set the idrows with the rooms
		int i = 0;
		int[] dArray = new int[andGraph.getVertexesOfType("Room").size()];
		for (StructuralElement st : andGraph.getVertexesOfType("Room"))
		{
			// store into a mapset the element and its key
			map.put(st.getId(), st);
			dArray[i] = st.getId();
			i++;
		}

		// set the W in the same order as always
		i = 0;
		int[] wArray = new int[incidentGraph.getVertexes().size()];
		for (StructuralElement st : incidentGraph.getVertexes())
		{
			// store into a mapset the element and its key
			map2.put(st.getId(), st);
			wArray[i] = st.getId();
			i++;
		}

		// start the search
		// for each room
		for (int n=0; n < dArray.length; n++)
		{
			for (int m=n+1; m < dArray.length; m++)
			{
				if(neighbourGraph.areConnected(map.get(dArray[n]), map.get(dArray[m])))
				{
					// for each W
					for (int j=0; j<wArray.length; j++)
					{
						// check if both domains have the same 
						StructuralElement w = map2.get(wArray[j]);
						Set<StructuralElement> st1Set = andGraph.getItsChildren(map.get(dArray[n]));
						Set<StructuralElement> st2Set = andGraph.getItsChildren(map.get(dArray[m]));
						if(st1Set.contains(w) && st2Set.contains(w))
						{	
							// create the new triplet
							StructuralElementTriplets triplet = new StructuralElementTriplets(n, m, j);
							stTriplets.add(triplet);
						}
					}
				}
			}
		}
		// print it into a file
		double[][] triplets = new double[stTriplets.size()][3];
		int j = 0;
		for (StructuralElementTriplets s : stTriplets)
		{
			triplets[j][0] = s.getSt1()+1;
			triplets[j][1] = s.getSt2()+1;
			triplets[j][2] = s.getSt3()+1;
			j++;
		}

		return triplets; 

	}

	/**
	 * This function returns the ids traveling from the incident graph
	 * @return is an array with the ids of the primitives
	 */
	public Integer[][] getIDWMap()
	{
		int i=0,cont=1;
		Integer[][] ids = new Integer[incidentGraph.getVertexes().size()][2];
		// get the ids
		for (StructuralElement st : incidentGraph.getVertexes())
		{
			ids[i][0] = st.getId();
			ids[i][1] = cont;
			i++;
			cont++;
		}
		return ids;
	}

	/**
	 * This function returns the ids traveling from the incident graph
	 * @return is an array with the ids of the primitives
	 */
	public Integer[][] getIDDMap()
	{
		int i=0,cont=1;
		Integer[][] ids = new Integer[andGraph.getVertexesOfType("Room").size()][2];
		// get the ids
		for (StructuralElement st : andGraph.getVertexesOfType("Room"))
		{
			ids[i][0] = st.getId();
			ids[i][1] = cont;
			i++;
			cont++;
		}
		return ids;
	}

	public AlphaBetaData calculateAlphaBeta()
	{
		// how many W exist?
		int numW = incidentGraph.getVertexes().size();

		// this will be the final list of triplets
		Set<Set<Integer>> alphaSet = new HashSet<Set<Integer>>();
		Set<Set<Integer>> betaSet = new HashSet<Set<Integer>>();

		//
		// start the search for ALPHAS and Betas
		// for each domain
		for (StructuralElement st1 : andGraph.getVertexesOfType("Room"))
		{
			for (StructuralElement st2 : andGraph.getVertexesOfType("Room"))
			{
				if(accessGraph.areConnected(st1, st2))
				{
					Set<StructuralElement> set1 = andGraph.getItsChildren(st1);
					Set<StructuralElement> set2 = andGraph.getItsChildren(st2);
					set1.retainAll(set2);

					// if 2 D have more that one W in common, they are added to the betas
					if (set1.size() > 1)
					{
						Set<Integer> duplet1 = new HashSet<Integer>();
						duplet1.add(st1.getId());
						duplet1.add(st2.getId());
						betaSet.add(duplet1);
					}

					// every triplet of 2 D and 1 W is added to the alphas
					for (StructuralElement st : set1)
					{
						// add the triplets to alphas 
						Set<Integer> triplet = new HashSet<Integer>();
						triplet.add(st1.getId());
						triplet.add(st2.getId());
						triplet.add(st.getId()+numW);
						alphaSet.add(triplet);

						// add the duplets DW to betas
						Set<Integer> duplet1 = new HashSet<Integer>();
						duplet1.add(st1.getId());
						duplet1.add(st.getId()+numW);
						betaSet.add(duplet1);
						duplet1.clear();
						duplet1.add(st2.getId());
						duplet1.add(st.getId()+numW);
						betaSet.add(duplet1);
					}
				}
			}
		}

		// every W duplet is part of alphas and betas
		for (StructuralElement st : incidentGraph.getVertexes())
		{
			Set<Integer> duplet = new HashSet<Integer>();
			Set<Integer> unique = new HashSet<Integer>();
			duplet.add(st.getId());
			duplet.add(st.getId()+numW);
			unique.add(st.getId()+numW);
			alphaSet.add(duplet);
			betaSet.add(unique);
		}


		// construct the struct to return
		AlphaBetaData alphaBetaData = new AlphaBetaData();
		alphaBetaData.setAlphaSet(alphaSet);
		alphaBetaData.setBetaSet(betaSet);
		alphaBetaData.calculateMatriu();

		return alphaBetaData;

	}

	/**
	 * This fucntion removes the rooms that are not in the input graph
	 * @param g is a graph of type MyGraph
	 */
	public void eleminateRestOfUnconnectedGraphs(MyGraph g)
	{
		// create two sets of the nodes of both graphs
		Set<StructuralElement> stSet = g.getVertexes();
		// remove that nodes that are not in the accessgraph
		// updete the graphs
		for(StructuralElement st : stSet)
		{
			andGraph.removeVertex(st);
			accessGraph.removeVertex(st);
			neighbourGraph.removeVertex(st);
			cleanImageById(st.getId());
		}
	}
	
	/**
	 * This function looks for the connected elements in a graph
	 * @param typeGraph is the graph to do the search
	 * @return a List of the elements in each connected graph
	 */
	public List<Set<StructuralElement>> getConnectedGraphs(String typeGraph)
	{
		if (typeGraph.equals("access"))
		{
			return accessGraph.getConnectedElements();
		}
		return null;
	}
	
	public Double[] getConnectedAreas(List<Set<StructuralElement>> se)
	{
		Double[] d = new Double[se.size()];
		int i = 0;
		for (Set<StructuralElement> st : se)
		{
			double area = 0;
			for(StructuralElement r : st)
				area += r.getArea();
			d[i] = area;
			i++;
		}
		return d;		
	}
	
	/**
	 * This function clones the plan in terms of values.
	 * Avoiding any reference to the original instance.
	 */
	@Override public FloorPlan clone()
	{
		FloorPlan fp = new FloorPlan(fileName);
		fp.setHeight(height);
		fp.setWidth(width);
		fp.setRegionImage(regionImage);
		fp.setAndGraph(andGraph);
		fp.setAccessGraph(accessGraph);
		fp.setNeighbourGraph(neighbourGraph);
		fp.setIncidentGraph(incidentGraph);
		fp.setNothingGraph(nothingGraph);
		return fp;

	}





}	