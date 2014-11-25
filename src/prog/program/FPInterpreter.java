package prog.program;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import prog.exceptions.FloorPlanGTException;
import prog.exceptions.GraphCreationException;
import prog.exceptions.PolygonException;
import prog.exceptions.RelationException;
import prog.io.FloorPlanIO;
import prog.io.SVGfile;
import prog.matlab.AdjacencyMatrix;
import prog.matlab.DisconnectedGraphs;
import prog.matlab.ReadStatistics;
import prog.mygraph.MyGraph;
import prog.statistics.Statistics;
import prog.strelement.FloorPlan;
import prog.strelement.Room;
import prog.strelement.StructuralElement;


public class FPInterpreter {

	/**
	 * @param args is the file name
	 * @throws IOException 
	 * @throws GraphCreationException 
	 * @throws PolygonException 
	 * @throws RelationException 
	 * @throws FloorPlanGTException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, GraphCreationException, PolygonException, RelationException, FloorPlanGTException, ClassNotFoundException {

		System.out.println("\nThe program starts...\n");
		List<FloorPlan> fpList = new ArrayList<FloorPlan>();
		
		String forceDirectory = "E:\\Doctorat\\FinalFloorPlanInterpretation\\2014\\Force";
		ReadStatistics rs = new ReadStatistics(args[0], forceDirectory);
		Statistics statistics = rs.readFPStatistics();

		// list the files in the folder of the GT
		//File folder = new File(args[0]);
		// for each file do the stuff
		/*for (File file : folder.listFiles())
		{

			// create the SVGfile object which parses the ground truth
			SVGfile svgFile = new SVGfile(file.getCanonicalPath());

			// create the FloorPlan object from the GT
			FloorPlan fp;
			fp = svgFile.read();
			fp.createGraphsGT();
			
			/* check the FloorPlan object correctness
			// fp.checkFloorPlanCorrectness();

			// visualize the floor plan
			// Visualize v = new Visualize(fp);

			// write some statistics of the plan
			System.out.println(" >Graph. " + fp.getAndGraph().toString());
			System.out.println(" >Vertexes: " + fp.getAndGraph().getVertexes().size());
			System.out.println(" >Edges: " + fp.getAndGraph().getEdges().size());
			int contR = 0, contE = 0, contA = 0, contEx = 0;
			for(StructuralElement st : fp.getAndGraph().getVertexes())
			{
				if (st.getType() == "Room")
				{
					contR++;
					Room room = (Room) st;
					if (room.isAccessible())
						contA++;
					if (room.isEntrance())
						contE++;
					if (room.isExterior())
						contEx++;
				}
			}
			System.out.println("\n >Número d'habitacions: " + contR + " accessibles: " + contA + " entrades " + contE + " exteriors " + contEx);
			StructuralElement st = fp.getAndGraph().getVertexOfType("Building").iterator().next();
			System.out.println(" >The area of the building is "+ st.getArea() +" and the perimeter is " + st.getPerimeter());



			//Render a = new Render(fp.getHeight(),fp.getWidth(),"GRAPH", fp);
			//Render r = new Render(fp.getHeight(),fp.getWidth(),"Rooms",fp.getStructuralElements());
			//MyGraph incident = fp.getGTGraph().getRelationGraph("incident"); 
			/*System.out.println("Graph: " + incident.toString());
		System.out.println("Vertexes: " + incident.getVertexes().size());
		System.out.println("Edges: " + incident.getEdges().size());
				PlaneGraph pg = new PlaneGraph(incident);
		ArrayList<Region> regions = new ArrayList<Region>(pg.calculatePGraphRegions(true));
		int cont = 0;
		for(Region reg : regions)
		{
			reg.drawRegionOnSVG("E:/Doctorat/FinalFloorPlanInterpretation/2013/" + cont + ".svg");
			cont++;
		}
			fpList.add(fp);

		}*/
		
		
		FloorPlanIO fpio = new FloorPlanIO();
		FloorPlan fp = fpio.loadFloorPlan("E:/Doctorat/FinalFloorPlanInterpretation/2014/floorplans/1.java");
		DisconnectedGraphs dg = new DisconnectedGraphs(fp.getAccessGraph());
		
		
		Statistics stat = fpio.loadStatistics("E:/Doctorat/FinalFloorPlanInterpretation/2014/stat");
		double[][] Extrema = new double[1][1];
		int[][] Image = new int[1][1];
		Extrema[0][0] = 1;//Extrema[0][1] = 1;
		Image[0][0] = 1;//Image[0][1] = 1;
		double[] Point = new double[2];
		Point[0] = 2; Point[1]=2;
		Room jRoom = new Room(2,10,Point,50.3,Extrema,null);
		Set<Integer> ids = new HashSet<Integer>();
		ids.add(2); ids.add(3);
		fp.mergeRooms(ids, jRoom, Image);
		fp.calculatePrimitivesRelationProb(stat);
		fp.getTheAdjacencyMatrixOnDemand("ddgt");

		System.out.println("\nThe program ends...");
		
		
	}




}
