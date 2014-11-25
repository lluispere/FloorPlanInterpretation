package prog.planegraph;

import java.awt.geom.Line2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;


/**
 * This is the class for planar-graph region. Each regions is composed by a group of wedges,
 * pnodes and pedges. The region describes a closed environment inside the planar-graph.
 * @author Lluís-Pere de las Heras
 * 08/06/10
 */


public class Region {

	private List<PWedge> wedgeList;		
	private Set<PNode> pnodes;
	private Set<PEdge> pedges;
	private List<PNode> orderedNodes;
	private double perimetre;

	/**
	 * Class constructor. Given a list of wedges, the constructor creates
	 * the region.
	 * @param list is the list of wedges that forms the region 
	 */
	public Region(List<PWedge> list)
	{
		this.wedgeList = new ArrayList<PWedge>();
		this.orderedNodes = new ArrayList<PNode>();
		this.wedgeList.addAll(list);
		this.pedges = new HashSet<PEdge>();
		this.pnodes = new HashSet<PNode>();

		for(PWedge w : wedgeList)
		{
			pnodes.add(w.getPNodeComponent(0));
			pnodes.add(w.getPNodeComponent(1));
			pnodes.add(w.getPNodeComponent(2));

			orderedNodes.add(w.getPNodeComponent(0));
			orderedNodes.add(w.getPNodeComponent(1));
			orderedNodes.add(w.getPNodeComponent(2));
		}

		ArrayList<PNode> pNodeList = new ArrayList<PNode>(pnodes);
		int sizeNodeList = pNodeList.size();

		for(int i = 0; i<sizeNodeList; i++)
		{
			pedges.add(new PEdge(pNodeList.get(i%sizeNodeList),pNodeList.get((i+1)%sizeNodeList)));
		}

		for(PEdge pe : pedges)
		{
			this.perimetre = perimetre + pe.getPEdgeLength();
		}
	}


	/**
	 * This function return the list of wedges
	 * @return
	 */
	public List<PWedge> getPWedgeList()
	{
		return this.wedgeList;
	}


	/**
	 * This function returns the list of nodes 
	 * @return
	 */
	public Set<PNode> getPNodes()
	{
		return this.pnodes;
	}

	/**
	 * This function returns the list of edges 
	 * @return
	 */
	public Set<PEdge> getPEdges()
	{
		return this.pedges;
	}

	/**
	 * This function returns the list of nodes 
	 * @return
	 */
	public List<PNode> getPorderedNodes()
	{
		return this.orderedNodes;
	}


	/**
	 * This function looks for lines in a region which only belong to the region in the main graph.
	 * In other words, the do not connect the region with the main graph. For this, the function gets
	 * all the lines of the regions and checks using the graph which is their connection degree order.
	 * Since regions are formed by connected elements, at list they have 2 connections in the graph for
	 * each node. Only those ones that have more than 2 are connections with the main graph
	 * @param graph is the main graph where the region has to be encountered
	 * @return a list of lines which only belong to this region
	 */
	public ArrayList<Line2D> getIsolatedRegionVertex(UndirectedGraph<Line2D, DefaultEdge> graph)
	{
		ArrayList<Line2D> linesOfTheRegions;
		ArrayList<Line2D> isolatedLines = new ArrayList<Line2D>();
		// get all the lines of the region
		linesOfTheRegions = this.getLinesOfTheRegion();
		// for each of the lines of the region
		for(Line2D regionLine : linesOfTheRegions)
		{
			// get the degree of connection of each
			int degreeOfConnection = graph.edgesOf(regionLine).size();
			// check the degree of the connection
			if(degreeOfConnection < 3)
			{
				// add if only has 2 or less connections
				isolatedLines.add(regionLine);
			}
		}
		return isolatedLines;
	}

	/**
	 * This function returns the type of lines in a region found in a graph
	 * @param g is the original graph
	 * @return an array of strings will all the lines
	 */
	public String[] getTypeOfLines(UndirectedGraph<Line2D, DefaultEdge> g)
	{
		ArrayList<Line2D> graphLines = this.getLinesOfTheRegionInAGraph(g);
		String[] typeOfLines = new String[graphLines.size()];
		// for every line in the region
		for(int i = 0; i< graphLines.size(); i++)
		{
			// get the type of the line
			typeOfLines[i] = graphLines.get(i).toString();
		}
		return typeOfLines;
	}


	/**
	 * This function returns the lines in a specifyed graph of this region
	 * @param g is the input graph
	 * @return an array list of the lines
	 */
	public ArrayList<Line2D> getLinesOfTheRegionInAGraph(UndirectedGraph<Line2D, DefaultEdge> g)
	{
		// get lines of the region
		ArrayList<Line2D> linesOfTheRegion = this.getLinesOfTheRegion();
		// get lines of the graph
		ArrayList<Line2D> graphLines = new ArrayList<Line2D>(g.vertexSet());
		// array where the lines will be stored
		ArrayList<Line2D> linesInTheGraph = new ArrayList<Line2D>();
		// for each region line
		try {
			drawRegionOnSVG("D:\\Doctorat\\MatlabDoctorat\\Vectoritzacio\\FinalVectorizationBlack\\r.svg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i< linesOfTheRegion.size(); i++)
		{
			Line2D l = linesOfTheRegion.get(i);
			// get the index of the line in the graph
			int index = graphLines.indexOf(l);
			// in the case is found in the oposite
			if(index != -1)
			{
				l = new Line2D.Double(l.getP2(),l.getP1());
				index = graphLines.indexOf(l);
				// get the line in the graph
				linesInTheGraph.add(graphLines.get(index));
			}

		}
		return linesInTheGraph;
	}

	/**
	 * This function gets all the lines formed by the ordered vertexes of the region
	 * Ordered vertexes are based on the wedges of the planar graph, so each three pnodes
	 * they are consecutively forming a line
	 * @return an ArrayList of lines that form the region
	 */
	public ArrayList<Line2D> getLinesOfTheRegion(){

		ArrayList<Line2D> linesOfTheRegion = new ArrayList<Line2D>();

		linesOfTheRegion.add(new Line2D.Double(wedgeList.get(0).getPNodeComponent(0).getPNodeVertex(),wedgeList.get(0).getPNodeComponent(1).getPNodeVertex()));

		for(int i = 1; i<wedgeList.size(); i++)
		{
			PWedge pw = wedgeList.get(i);
			Line2D l = new Line2D.Double(pw.getPNodeComponent(0).getPNodeVertex(),pw.getPNodeComponent(1).getPNodeVertex());
			linesOfTheRegion.add(l);
		}

		return linesOfTheRegion;
		
//		for(PWedge pw : wedgeList)
//		{
//			Line l = new Line(pw.getPNodeComponent(0).getPNodeVertex(),pw.getPNodeComponent(1).getPNodeVertex());
//			Line l2 = new Line(pw.getPNodeComponent(1).getPNodeVertex(),pw.getPNodeComponent(2).getPNodeVertex());
//			if(!linesOfTheRegion.contains(l))
//				linesOfTheRegion.add(l);
//			if(!linesOfTheRegion.contains(l2))
//				linesOfTheRegion.add(l2);
//		}
//
//		return linesOfTheRegion;
	}


	/**
	 * Given another region this function compares with this.
	 * Here is considered that two regions are the same if they have
	 * the same number of wedges and pnodes and also the pnodes are
	 * the same.
	 * @param r is the other region.
	 * @return a boolean weather the rigions are the same or not.
	 */
	public boolean equals(Region r)
	{
		Set<PNode> nodes = new HashSet<PNode>();
		nodes.addAll(pnodes);

		if( (wedgeList.size() == r.getPWedgeList().size()) && (nodes.size() == r.getPNodes().size()) )
		{
			for(PNode n : r.getPNodes())
			{
				if(nodes.add(n))
				{
					return false;
				}
			}

			return true;
		}
		else
		{
			return false;
		}
	}

	public double getPerimetre()
	{
		try {
			drawRegionOnSVG("D:\\Doctorat\\MatlabDoctorat\\Vectoritzacio\\FinalVectorizationBlack\\r.svg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.perimetre;
	}


	/**
	 * This function draws the region in a SVG type file. 
	 * @param directory is the complete path where the file will be save at.
	 * @throws IOException if there is an IO problem.
	 */
	public void drawRegionOnSVG(String directory) throws IOException
	{
		FileWriter foutput = new FileWriter(directory); 
		BufferedWriter svg = new BufferedWriter(foutput);


		svg.write("<?xml version=" + '"' + "1.0" + '"' + " standalone=" + '"' + "no" + '"' + "?>\n");
		svg.write("<!DOCTYPE svg PUBLIC " + '"' + "-//W3C//DTD SVG 1.1//EN" + '"' + " " + '"' + "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd" + '"'+ " >\n");
		svg.write("<svg width=" + '"' + "2480" + '"' + " height=" + '"' + "3508" + '"' + " viewBox=" + '"' + "0 0 5480 5508" + '"' + " xmlns=" + '"' + "http://www.w3.org/2000/svg" + '"' + " xmlns:xlink=" + '"' + "http://www.w3.org/1999/xlink" + '"' + ">\n");

		for(PWedge w : wedgeList)
		{
			svg.write("<line x1=" + '"' + w.getPNodeComponent(0).getPNodeVertex().getX() + '"' 
					+ " y1=" + '"' + w.getPNodeComponent(0).getPNodeVertex().getY() + '"'
					+ " x2=" + '"' + w.getPNodeComponent(1).getPNodeVertex().getX() + '"'
					+ " y2=" + '"' +w.getPNodeComponent(1).getPNodeVertex().getY() + '"'
					+ " stroke=" + '"' + "black" + '"' + "/>\n");
			svg.write("<line x1=" + '"' + w.getPNodeComponent(1).getPNodeVertex().getX() + '"' 
					+ " y1=" + '"' + w.getPNodeComponent(1).getPNodeVertex().getY() + '"'
					+ " x2=" + '"' + w.getPNodeComponent(2).getPNodeVertex().getX() + '"'
					+ " y2=" + '"' +w.getPNodeComponent(2).getPNodeVertex().getY() + '"'
					+ " stroke=" + '"' + "black" + '"' + "/>\n");
		}

		svg.write("</svg>");
		svg.close();
	}

	/**
	 * This function returns the shortest edge within the region
	 * @return the line that the shortest edge creates
	 */
	public Line2D getShortestEdge() {

		// variable declaration
		Line2D shortestL = null;
		double lengthBefore = 10000;
		// for each edge in the region
		for(PEdge pe : pedges)
		{
			// get its length
			double length = pe.getPEdgeLength();
			// if the length is the shortest till the moment
			if(length < lengthBefore)
			{
				// store the new shortest length and the shortest line
				lengthBefore = length;
				shortestL = pe.getPEdgeLine();
			}
		}
		// return the shortest line
		return shortestL;
	}

	/**
	 * Get the unsigned Area of the region
	 * @return a double with the signed area
	 */
	public double getArea()
	{

		try {
			this.drawRegionOnSVG("D:\\Doctorat\\MatlabDoctorat\\Vectoritzacio\\r");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double auxArea = 0, areaTriangle;
		double x1, x2, y1, y2;

		for(int i = 0; i < this.getLinesOfTheRegion().size(); i++)
		{
			Line2D line = this.getLinesOfTheRegion().get(i);
			x1 = line.getP1().getX();
			y1 = line.getP1().getY();
			x2 = line.getP2().getX();
			y2 = line.getP2().getY();
			areaTriangle = (x2 - x1)*(y2 - y1)/2;
			auxArea += ( (x2 - x1)*(Math.min(y2,y1)) - areaTriangle );  
		}
		return Math.abs(auxArea);
	}


	/*public boolean hasSmallAngle(int smallAngle)
	{
		ArrayList<Line2D> lines = this.getLinesOfTheRegion();
		boolean hasSmallAngle = false;

		for(int i = 0; i<lines.size(); i++)
		{
			Line2D l1 = lines.get(i);
			Line2D l2 = lines.get((i+1)%lines.size());

			double angle = l1.calculteAngle(l2);
			if(angle < smallAngle)
				hasSmallAngle = true;
		}

		return hasSmallAngle;
	}*/


}
