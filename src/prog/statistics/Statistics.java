package prog.statistics;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;

//import org.apache.commons.math3.stat.StatUtils;









import prog.io.FloorPlanIO;
import prog.mygraph.MyGraph;
import prog.strelement.FloorPlan;
import prog.strelement.StructuralElement;

public class Statistics implements Serializable{

	private static final long serialVersionUID = 1L;
	// total floor plan collection numbers 
	public int numberOfPlans = 0;
	public int numberOfRooms = 0;
	public int numberOfExteriorRooms = 0;
	public int numberOfDoors = 0;
	public int numberOfCCDoors = 0;
	public int numberOfExteriorDoors = 0;
	public int numberOfWalls = 0;
	public int numberOfCCWalls = 0;
	public int numberOfExteriorWalls = 0;
	public int numberOfInteriorWalls = 0;
	public int numberOfWindows = 0;
	public int numberOfCCWindows = 0;
	public int numberOfExteriorWindows = 0;
	public int numberOfParkings = 0;
	public int numberOfCCParkings = 0;
	public int numberOfExteriorParkings = 0;
	public int numberOfSeparations = 0;
	public int numberOfCCSeparations = 0;
	public int numberOfExteriorSeparations = 0;
	public double area = 0;
	public double meanarea = 0;
	public double perimeter = 0;
	public double meanperimeter = 0;


	// average numbers with respect to the plan area
	public double avAreaPlan = 0;
	public double avPerimeterPlan = 0;
	public double avNumberOfRoomsAreaPlan = 0;
	public double avNumberOfRooms = 0;
	public double avNumberOfWallsAreaPlan = 0;
	public double avNumberOfDoorsAreaPlan = 0;
	public double avNumberOfWindowsAreaPlan = 0;
	public double avNumberOfParkingsAreaPlan = 0;
	public double avNumberOfSeparationsAreaPlan = 0;

	// average numbers with respect to rooms area
	public double avAreaRoom = 0;
	public double avPerimeterRoom = 0;
	public double avNumberOfWallsAreaRoom = 0;
	public double avNumberOfDoorsAreaRoom = 0;
	public double avNumberOfWindowsAreaRoom = 0;
	public double avNumberOfParkingsAreaRoom = 0;
	public double avNumberOfSeparationsAreaRoom = 0;

	public Double[] roomsAreaVSPlanArea;
	public int[][] samplingConfiguration;
	public double[][] samplingProb;
	public double neighborConnectivityP = 0;
	public double accessConnectivityP = 0;
	public double[] areas;
	public Integer[] numOfBuildings;
	public Double[] areasOfBuildingsPlan;
	public Double[] roomsAreas;
	public Integer[] numberOfRoomsPlan;
	public Integer[] numberOfWallsRoom;
	public Integer[] numberOfDoorsRoom;
	public Integer[] numberOfWindowsRoom;
	public Integer[] numberOfSeparationsRoom;
	public Integer[] numberOfNothingsRoom;
	public Integer[] numberOfParkingsRoom;
	public Double[][] likelyhoodX;
	public Double[] likelyhoodY;

	public Statistics(List<FloorPlan> fpList, File[] forceList) throws IOException, ClassNotFoundException
	{
		numberOfPlans = fpList.size();
		List<Double> rAreaVSPArea = new ArrayList<Double>();
		List<Double> areasRooms = new ArrayList<Double>();
		List<Double> longWalls = new ArrayList<Double>();
		List<Double> longDoors = new ArrayList<Double>();
		List<Double> longSep = new ArrayList<Double>();
		List<Integer> numRooms = new ArrayList<Integer>();
		List<Integer> numWalls = new ArrayList<Integer>();
		List<Integer> numDoors = new ArrayList<Integer>();
		List<Integer> numWindows = new ArrayList<Integer>();
		List<Integer> numSeparations = new ArrayList<Integer>();
		List<Integer> numParkings = new ArrayList<Integer>();
		samplingConfiguration = new int[4][4];
		areas = new double[fpList.size()];

		for (int i=0; i<samplingConfiguration.length; i++)
		{
			for (int j=0; j<samplingConfiguration[i].length; j++)
				samplingConfiguration[i][j]=0;
		}
		
		List<Double> arees = new ArrayList<Double>();
		List<Integer> nB = new ArrayList<Integer>();

		int n = 0;
		for (FloorPlan fp : fpList)
		{
			System.out.println(fp.getFileName());
			area = area+fp.getArea();
			areas[n] = fp.getArea();
			perimeter = perimeter+fp.getPerimeter();
			numberOfRooms = numberOfRooms+fp.getNumberOfRooms();
			numberOfExteriorRooms = numberOfExteriorRooms+fp.getNumberOfRooms();
			numberOfDoors = numberOfDoors+fp.getNumberOfDoors();
			numberOfCCDoors = numberOfCCDoors+calculateNumberCCElement(fp,"Door");
			numberOfExteriorDoors = numberOfExteriorDoors+fp.getNumberOfExteriorDoors();
			numberOfWalls = numberOfWalls+fp.getNumberOfWalls();
			numberOfCCWalls = numberOfCCWalls+calculateNumberCCElement(fp,"Wall");
			numberOfExteriorWalls = numberOfExteriorWalls+fp.getNumberOfExteriorWalls();
			numberOfWindows = numberOfWindows+fp.getNumberOfWindows();
			numberOfCCWindows = numberOfCCWindows+calculateNumberCCElement(fp,"Window");
			numberOfExteriorWindows = numberOfExteriorWindows+fp.getNumberOfExteriorWindows();
			numberOfSeparations = numberOfSeparations+fp.getNumberOfSeparations();
			numberOfCCSeparations = numberOfCCSeparations+calculateNumberCCElement(fp,"Separation");
			numberOfParkings = numberOfParkings+fp.getNumberOfParkings();
			numberOfCCParkings = numberOfCCParkings+calculateNumberCCElement(fp,"Parking");
			numberOfExteriorSeparations = numberOfExteriorSeparations+fp.getNumberOfExteriorSeparations();

			numRooms.add(fp.getNumberOfRooms());
			numWalls.addAll(fp.getNumberOfCCElementsPerRoom("Wall"));
			numDoors.addAll(fp.getNumberOfCCElementsPerRoom("Door"));
			numWindows.addAll(fp.getNumberOfCCElementsPerRoom("Window"));
			numSeparations.addAll(fp.getNumberOfCCElementsPerRoom("Separation"));
			numParkings.addAll(fp.getNumberOfCCElementsPerRoom("Parking"));


			longWalls.addAll(calculateLongitud(fp,"Wall"));
			longDoors.addAll(calculateLongitud(fp,"Door"));
			longSep.addAll(calculateLongitud(fp,"Separation"));
			
			List<Set<StructuralElement>> graphs = fp.getConnectedGraphs("access");
			nB.add(graphs.size());
			for (Set<StructuralElement> con : graphs)
			{
				area = 0;
				for (StructuralElement r:con)
					area = area+r.getArea();
				arees.add(area);
			}
			
			
			for (double d:fp.getRoomAreas())
			{
				rAreaVSPArea.add(d/fp.getArea());
				areasRooms.add(d);
			}
			roomsAreaVSPlanArea = rAreaVSPArea.toArray(new Double[rAreaVSPArea.size()]);

			avNumberOfRooms = avNumberOfRooms+fp.getNumberOfRooms();
			
			// check the floor plan neighbour connectivity
			MyGraph nmg = fp.getNeighbourGraph();
			if(nmg.isConnected())
				neighborConnectivityP++;

			// check the floor plan access connectivity
			MyGraph amg = fp.getAccessGraph();
			if(amg.isConnected())
				accessConnectivityP++;

			n++;
		}

		avNumberOfRooms = avNumberOfRooms/fpList.size();
		meanarea = area/fpList.size();
		perimeter = perimeter/fpList.size();

		neighborConnectivityP = neighborConnectivityP/fpList.size();
		accessConnectivityP = accessConnectivityP/fpList.size();

		roomsAreas = areasRooms.toArray(new Double[areasRooms.size()]);
		
		numberOfRoomsPlan = numRooms.toArray(new Integer[numRooms.size()]);
		numberOfWallsRoom = numWalls.toArray(new Integer[numWalls.size()]);
		numberOfDoorsRoom = numDoors.toArray(new Integer[numDoors.size()]);
		numberOfWindowsRoom = numWindows.toArray(new Integer[numWindows.size()]);
		numberOfSeparationsRoom = numSeparations.toArray(new Integer[numSeparations.size()]);
		numberOfParkingsRoom = numParkings.toArray(new Integer[numParkings.size()]);
		
		// declare the lists for all the plans
		List<Double> xListLength = new ArrayList<Double>();
		List<Double> xList1 = new ArrayList<Double>();
		List<Double> xList2 = new ArrayList<Double>();
		List<Double> yList = new ArrayList<Double>();
		
		List<Integer> numOfNothings = new ArrayList<Integer>();
		
		areasOfBuildingsPlan = arees.toArray(new Double[arees.size()]);
		numOfBuildings = nB.toArray(new Integer[nB.size()]);
		
		// for each of the force executions
		for(File file : forceList)
		{
			System.out.println(file);
			// read the floor plan
			FloorPlanIO fpio = new FloorPlanIO();
			FloorPlan fp = fpio.loadFloorPlan(file.getCanonicalPath());
			
			StructuralElement[] primitives = fp.getPrimitives();
			List<StructuralElement> walls = new ArrayList<StructuralElement>();
			List<StructuralElement> doors = new ArrayList<StructuralElement>();
			List<StructuralElement> separations = new ArrayList<StructuralElement>();
			List<StructuralElement> nothings = new ArrayList<StructuralElement>();
			
			// assign the primitive to its GT label
			for (StructuralElement st : primitives)
			{
				if (st.getGTType().equals("Wall"))
					walls.add(st);
				else if (st.getGTType().equals("Door"))
					doors.add(st);
				else if (st.getGTType().equals("Separation"))
					separations.add(st);
				else nothings.add(st);
			}
			
			// create the corresponding matrices
			for (int i=0;i<walls.size();i++)
			{
				xListLength.add(walls.get(i).getLength());
				xList1.add(walls.get(i).getClassOut());
				xList2.add(0.0);
				yList.add(1.0);
			}
			for (int i=0;i<doors.size();i++)
			{
				xListLength.add(doors.get(i).getLength());
				xList1.add(0.0);
				xList2.add(doors.get(i).getClassOut());
				yList.add(2.0);
			}
			for (int i=0;i<separations.size();i++)
			{
				xListLength.add(separations.get(i).getLength());
				xList1.add(0.0);
				xList2.add(0.0);
				yList.add(3.0);
			}
			for (int i=0;i<nothings.size();i++)
			{
				xListLength.add(nothings.get(i).getLength());
				xList1.add(0.0);
				xList2.add(0.0);
				yList.add(4.0);
			}
			
			//create the sampling configuration matrix
			int[][] fpFSC = fp.getForceSamplingConfigurations();

			for (int i=0; i<samplingConfiguration.length; i++)
			{
				for (int j=0; j<samplingConfiguration[i].length; j++)
					samplingConfiguration[i][j]=samplingConfiguration[i][j]+fpFSC[i][j];
			}
			
			// check the number of Nothing per room
			for (StructuralElement room : fp.getRooms())
			{
				int cont = 0;
				StructuralElement[] pris = fp.getElementsAtRoom(room);
				for (int i = 0; i<pris.length; i++)
				{
					if (pris[i].getGTType().equals("Nothing"))
						cont++;
				}
				numOfNothings.add(cont);
			}
			numberOfNothingsRoom = numOfNothings.toArray(new Integer[numOfNothings.size()]);
		}
		
		// ojo, com que el programa no detecta cap separation pero si que n'hi ha, afegim una desena part de separations
		if (samplingConfiguration[0][2] < samplingConfiguration[0][1]/10)
		{
			samplingConfiguration[0][2] = samplingConfiguration[0][1]/10;
			samplingConfiguration[2][0] = samplingConfiguration[0][1]/10;
		}
		// segona trampa, per tal de no excedir el nombre de nothings, igualar-ho al nombre de portes.
		samplingConfiguration[0][3] = (int) 1.5*samplingConfiguration[0][1];		
		samplingConfiguration[3][0] = (int) 1.5*samplingConfiguration[0][1];
		
		
		// store into the arrays suitable for MATLAB
		likelyhoodX = new Double[xList1.size()][3];
		likelyhoodY = new Double[yList.size()];
		for (int i = 0; i < yList.size(); i++)
		{
			likelyhoodX[i][0] = xListLength.get(i);
			likelyhoodX[i][1] = xList1.get(i);
			likelyhoodX[i][2] = xList2.get(i);
			likelyhoodY[i] = yList.get(i);
		}
	}
	
	private int calculateNumberCCElement(FloorPlan fp, String elementName)
	{
		List<Set<StructuralElement>> disconnectedSets = calculateDisconnectedSetsOfElement(fp, elementName);
		
		return disconnectedSets.size();		
	}

	private List<Double> calculateLongitud(FloorPlan fp, String elementName) 
	{
		List<Double> longitudTotal = new ArrayList<Double>();

		
		List<Set<StructuralElement>> cSets = calculateDisconnectedSetsOfElement(fp, elementName);

		for (Set<StructuralElement> set : cSets)
		{
			double longitud = 0;
			for (StructuralElement st:set)
			{
				longitud = longitud + (st.getLength()/Math.sqrt(fp.getArea()));
			}
			longitudTotal.add(longitud);
		}

		return longitudTotal;
	}
	
	private List<Set<StructuralElement>> calculateDisconnectedSetsOfElement(FloorPlan fp, String elementName)
	{
		MyGraph mg = fp.getIncidentGraph().graphValueCopy();

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
		return ci.connectedSets();
	}







}
