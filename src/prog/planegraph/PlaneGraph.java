package prog.planegraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import prog.exceptions.PolygonException;
import prog.mygraph.MyGraph;

public class PlaneGraph {

	private DirectedGraph<PNode, PEdge> planeGraph;
	private List<PWedge> pWedges;


	//**********************************//
	//***** PLANAR-GRAPH FUNCTIONS *****//
	//**********************************//


	/**
	 * PlanarGraph constructor. Taking and AndOrGraph i creates a simple PlanarGraph
	 * @param graph
	 * @throws PolygonException 
	 */
	public PlaneGraph(MyGraph graph) throws PolygonException
	{
		planeGraph = new DefaultDirectedGraph<PNode, PEdge>(PEdge.class);
		createPlanGraph(graph);
		this.cleanUnconnectedPNodes();
		try {
			this.drawThePlanarGraphInSVG("E:/Doctorat/FinalFloorPlanInterpretation/2013/e2.svg");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<PEdge> orderedPEdges = new ArrayList<PEdge>();
		pWedges = new ArrayList<PWedge>();
		// get the ordered nodes (See function)
		orderedPEdges = orderPEdgeList();
		// get the wedges
		pWedges = calculateWedges(orderedPEdges);
	}

	/**
	 * Creation of the directed graph with vi -> vj and vj -> vi
	 * @param mg is a MyGraph with the StructuralElement
	 * @throws PolygonException
	 */
	private void createPlanGraph(MyGraph mg) throws PolygonException 
	{
		for(DefaultEdge e : mg.getEdges())
		{
			PNode p1aux = null;
			PNode p2aux = null;
			PNode pN1 = new PNode(mg.getSource(e));
			PNode pN2 = new PNode(mg.getTarget(e));
			
			if (!planeGraph.containsVertex(pN1))
			{
				p1aux = pN1;
			}
			else
			{
				for (PNode p : planeGraph.vertexSet())
				{
					if (p.equals(pN1))
						p1aux = p;
				}
			}
			
			if (!planeGraph.containsVertex(pN2))
			{
				p2aux = pN2;
			}
			else
			{
				for (PNode p : planeGraph.vertexSet())
				{
					if (p.equals(pN2))
						p2aux = p;
				}
			}
			
			planeGraph.addVertex(p1aux);
			planeGraph.addVertex(p2aux);
			this.add2Edge(p1aux, p2aux);
			this.add2Edge(p2aux, p1aux);
		}


	}

	private boolean add2Edge(PNode p1, PNode p2)
	{
		PEdge pe = new PEdge(p1, p2);
		return planeGraph.addEdge(p1, p2, pe);
	}


	public List<PWedge> getPWedges()
	{
		return this.pWedges;
	}


	//***************************//
	//***** PEDGE FUNCTIONS *****//
	//***************************//	


	/**
	 * This function orders the pEdges by the source PNode id as primary key
	 * and their angle to the horizontal as secondary key
	 */
	private List<PEdge> orderPEdgeList() 
	{

		List<PEdge> peList = new ArrayList<PEdge>();
		List<PEdge> idList = new ArrayList<PEdge>();
		List<PEdge> finalList = new ArrayList<PEdge>();
		int id = 0; int idBefore = -1;
		ArrayList<PEdge> pEdgesEnlarged = new ArrayList<PEdge>();
		pEdgesEnlarged.addAll(planeGraph.edgeSet());

		idList.addAll(orderPEdges(pEdgesEnlarged, 0, pEdgesEnlarged.size()-1, false));

		for(PEdge pe : idList)
		{
			id = planeGraph.getEdgeSource(pe).getId();

			if( (id != idBefore) && (idBefore != -1) )
			{
				finalList.addAll(orderPEdges(peList, 0, peList.size()-1, true));
				peList.clear();
			}

			idBefore = id;
			peList.add(pe);
		}

		finalList.addAll(orderPEdges(peList, 0, peList.size()-1, true));
		return finalList;
	}


	/**
	 * This function orders the PEdges in ascendent order given a certain parameter (type_order)
	 * If type_order equals false, the PEdges are ordered in terms of Id
	 * If type_order equals false, the PEdges are ordered in terms of Angle
	 * @param list is the list with all the PEdges to order
	 * @param first is the first index of the list
	 * @param last is the last index of the list
	 * @param type_order is the parameter of sorting
	 * @return the ordered PEdge list
	 */
	private List<PEdge> orderPEdges(List<PEdge> list, int first, int last, boolean type_order)
	{
		int i = first, j = last;
		double pivot_value;

		PEdge pivotPE = list.get((first + last) / 2);

		if(type_order == false)
			pivot_value = planeGraph.getEdgeSource(pivotPE).getId();		
		else
			pivot_value = pivotPE.getPEdgeAngle();	

		PEdge aux;

		do
		{
			double mini, minj;
			if(type_order == false)
			{
				mini = planeGraph.getEdgeSource(list.get(i)).getId();
				minj = planeGraph.getEdgeSource(list.get(j)).getId();
			}
			else
			{
				mini = list.get(i).getPEdgeAngle();
				minj = list.get(j).getPEdgeAngle();
			}


			while (mini < pivot_value)
			{
				i++;

				if(type_order == false)
					mini = planeGraph.getEdgeSource(list.get(i)).getId();
				else
					mini = list.get(i).getPEdgeAngle();
			}
			while (minj > pivot_value)
			{
				j--;
				if(type_order == false)
					minj = planeGraph.getEdgeSource(list.get(j)).getId();
				else
					minj = list.get(j).getPEdgeAngle();
			}

			if( i <= j)
			{
				aux = list.get(j);
				list.set(j, list.get(i));
				list.set(i, aux);
				i++;
				j--;
			}
		} while(i <= j);

		if (first < j) orderPEdges(list, first, j, type_order);
		if (last > i) orderPEdges(list, i, last, type_order);

		return list;


	}

	/**
	 * This functions cleans those nodes that has less than two edges connected
	 * This is done because since we're looking for region we're not interested
	 * in terminal or unconnected nodes
	 */
	private void cleanUnconnectedPNodes()
	{
		boolean same = false;
		while (!same)
		{
			Set<PNode> pset = new HashSet<PNode>();
			pset.addAll(planeGraph.vertexSet());
			for (PNode pn : pset)
			{
				if(planeGraph.outDegreeOf(pn)<2)
					planeGraph.removeVertex(pn);
			}
			same = planeGraph.vertexSet().containsAll(pset);
		}
	}


	//****************************//
	//***** PWEDGE FUNCTIONS *****//
	//****************************//	


	/**
	 * This function calculates the Wedges in a planar graph.
	 * @return the list containing the Wedges
	 */
	private List<PWedge> calculateWedges(List<PEdge> orderedPE) 
	{
		pWedges = new ArrayList<PWedge>();
		PEdge first, last;
		int id = 0, idBefore = -1;

		first = orderedPE.get(0);
		last = orderedPE.get(0);
		idBefore = planeGraph.getEdgeSource(first).getId();
		for(int i = 1; i < orderedPE.size(); i++)
		{
			PEdge pe1 = orderedPE.get(i-1);
			PEdge pe2 = orderedPE.get(i);

			id = planeGraph.getEdgeSource(pe2).getId();

			if (id == idBefore)
			{
				pWedges.add(new PWedge(planeGraph.getEdgeTarget(pe2),
						planeGraph.getEdgeSource(pe1), planeGraph.getEdgeTarget(pe1)));

			}
			else
			{
				if(first != last)
				{
					pWedges.add(new PWedge(planeGraph.getEdgeTarget(first),
							planeGraph.getEdgeSource(last), planeGraph.getEdgeTarget(last)));

					first = pe2;
				}
			}

			idBefore = id;
			last = pe2;
		}

		pWedges.add(new PWedge(planeGraph.getEdgeTarget(first),
				planeGraph.getEdgeSource(last), planeGraph.getEdgeTarget(last)));

		return pWedges;

	}


	/**
	 * This function finds all the regions belonging to the planar graph
	 * @return the list of regions
	 */
	public List<Region> calculatePGraphRegions(boolean useOfLinesInBothDrections)
	{
		List<PWedge> orderedPWedges;
		List<PWedge> wedgeList;
		List<Region> regions = new ArrayList<Region>();
		PWedge pw = null, pw2 = null;
		boolean found = false, endRegion = false;

		// if really exists a planar graph
		if(planeGraph.vertexSet().size()>0)
		{
			orderedPWedges = new ArrayList<PWedge>();
			orderedPWedges.addAll(orderPWedges());


			do
			{
				found = false;
				for(PWedge w : orderedPWedges)
				{
					if(w.getStatus() == false)
					{
						pw = w;
						found = true;
						pw.setStatus(true);
						break;
					}
				}

				if(found == true)
				{
					wedgeList = new ArrayList<PWedge>();
					wedgeList.add(pw);
					endRegion = false;

					while(endRegion == false)
					{
						for(PWedge w : orderedPWedges)
						{
							if(pw.contigous(w))
							{
								pw2 = w;
								wedgeList.add(pw2);
								break;
							}
						}
						if(pw2 != null)
						{
							if(pw2.contigous(wedgeList.get(0)))
							{

								Region r = new Region(wedgeList);
								regions.add(r);
								wedgeList.clear();
								endRegion = true;
							}
							else
							{
								pw = pw2;
							}
						}

					}
				}

			}while(found == true);

			List<Region> finalRegionsA = new ArrayList<Region>();
			List<Region> finalRegionsB = new ArrayList<Region>();

			finalRegionsA.addAll(deleteRepeatedRegions(regions));
			if(!useOfLinesInBothDrections)
				finalRegionsB.addAll(regionsWithCommonPathcCycles(finalRegionsA));
			else finalRegionsB = finalRegionsA;

			return finalRegionsB;
		}
		else return regions;
	}


	/**
	 * This functions looks for regions which has common wedges in different ways to conform regions
	 * It is useful while finding regions in unconnected graphs which has regions in the extremes
	 * It bases its functionality on the equality defined in wedges. W1 == W2 t. <vk,vi,vj> == <vj,vi,vk> 
	 * @param finalRegions is an ArrayList of regions
	 * @return an ArrayList of regions which has this characteristic
	 */
	private List<Region> regionsWithCommonPathcCycles(List<Region> finalRegions) {

		List<Region> goodRegions = new ArrayList<Region>();

		// for all the regions
		for(Region r : finalRegions)
		{
			// create the control set
			Set<PWedge> wSet = new HashSet<PWedge>();
			// add all wedges to the set, it will skip adding those ones repeated
			wSet.addAll(r.getPWedgeList());
			// if the size is not the same -> wedges repeated in different directions
			if(wSet.size()==r.getPWedgeList().size())
				// add the region to the list
				goodRegions.add(r);
		}
		return goodRegions;
	}

	/**
	 * Given a list of regions this function deletes the repeated ones
	 * @param regions is the list of regions to sort
	 * @return the list with unique regions
	 */
	private List<Region> deleteRepeatedRegions(List<Region> regions) 
	{
		List<Region> elimR = new ArrayList<Region>();
		int iter = 0;
		Region r2;
		for(Region r1 : regions)
		{
			iter++;
			if(elimR.contains(r1))
				continue;

			for(int i = iter; i < regions.size(); i++)
			{
				r2 = regions.get(i);

				if(elimR.contains(r2))
					continue;

				if(r1.equals(r2))
				{
					elimR.add(r2);
				}
			}
		}

		regions.removeAll(elimR);

		return regions;
	}

	/**
	 * This functions orders the wedges of the planar graph. Vk is the first key of sorting and
	 * Vi the second.
	 * @return the list with the sorted wedges
	 */
	public List<PWedge> orderPWedges()
	{
		int id = 0, idBefore = -1;
		List<PWedge> idList = new ArrayList<PWedge>();
		List<PWedge> finalList = new ArrayList<PWedge>();
		List<PWedge> peList = new ArrayList<PWedge>();

		idList.addAll(orderPWedges(pWedges, 0, pWedges.size()-1, false));

		for(PWedge pe : idList)
		{
			id = pe.getPNodeComponent(0).getId();

			if( (id != idBefore) && (idBefore != -1) )
			{
				finalList.addAll(orderPWedges(peList, 0, peList.size()-1, true));
				peList.clear();
			}

			idBefore = id;
			peList.add(pe);
		}

		finalList.addAll(orderPWedges(peList, 0, peList.size()-1, true));

		//		for(PWedge j : finalList)
		//		{
		//			j.printPWedgeConsole();
		//
		//		}

		return finalList;
	}

	/**
	 * This functions orders the Wedges by a given parameter type_order
	 * If type_order is false, the wedges are ordered by Vk. Else, the
	 * wedges are sorted by Vi.
	 * @param list is the list of Wedges
	 * @param first	is the first element to sort
	 * @param last is the last element to sort
	 * @param type_order is the parameter that controls which sort is wanted
	 * @return the list of ordered wedges
	 */
	private List<PWedge> orderPWedges(List<PWedge> list, int first, int last, boolean type_order)
	{
		int i = first, j = last;
		int pivot_value;

		PWedge pivotPE = list.get((first + last) / 2);

		if(type_order == false)
			pivot_value = pivotPE.getPNodeComponent(0).getId();		
		else
			pivot_value = pivotPE.getPNodeComponent(1).getId();	

		PWedge aux;

		do
		{
			double mini, minj;
			if(type_order == false)
			{
				mini = list.get(i).getPNodeComponent(0).getId();
				minj = list.get(j).getPNodeComponent(0).getId();
			}
			else
			{
				mini = list.get(i).getPNodeComponent(1).getId();
				minj = list.get(j).getPNodeComponent(1).getId();
			}


			while (mini < pivot_value)
			{
				i++;

				if(type_order == false)
					mini = list.get(i).getPNodeComponent(0).getId();
				else
					mini = list.get(i).getPNodeComponent(1).getId();
			}

			while (minj > pivot_value)
			{
				j--;
				if(type_order == false)
					minj = list.get(j).getPNodeComponent(0).getId();
				else
					minj = list.get(j).getPNodeComponent(1).getId();
			}

			if( i <= j)
			{
				aux = list.get(j);
				list.set(j, list.get(i));
				list.set(i, aux);
				i++;
				j--;
			}
		} while(i <= j);

		if (first < j) orderPWedges(list, first, j, type_order);
		if (last > i) orderPWedges(list, i, last, type_order);

		return list;


	}

	//*************************//
	//***** I/O FUNCTIONS *****//
	//*************************//

	public void drawThePlanarGraphInSVG(String directory) throws IOException
	{
		FileWriter foutput = new FileWriter(directory); 
		BufferedWriter svg = new BufferedWriter(foutput);


		svg.write("<?xml version=" + '"' + "1.0" + '"' + " standalone=" + '"' + "no" + '"' + "?>\n");
		svg.write("<!DOCTYPE svg PUBLIC " + '"' + "-//W3C//DTD SVG 1.1//EN" + '"' + " " + '"' + "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd" + '"'+ " >\n");
		svg.write("<svg width=" + '"' + "2480" + '"' + " height=" + '"' + "3508" + '"' + " viewBox=" + '"' + "0 0 5480 5508" + '"' + " xmlns=" + '"' + "http://www.w3.org/2000/svg" + '"' + " xmlns:xlink=" + '"' + "http://www.w3.org/1999/xlink" + '"' + ">\n");

		for(PNode pn : planeGraph.vertexSet())
		{
			svg.write("<text x =" + '"' + pn.getPNodeVertex().getX() + '"' 
					+ " y=" + '"' + pn.getPNodeVertex().getY() + '"' + ">"
					+ pn.getId() + "</text>\n");
		}

		for(PEdge pe : planeGraph.edgeSet())
		{
			svg.write("<line x1=" + '"' + planeGraph.getEdgeSource(pe).getPNodeVertex().getX() + '"' 
					+ " y1=" + '"' + planeGraph.getEdgeSource(pe).getPNodeVertex().getY() + '"'
					+ " x2=" + '"' + planeGraph.getEdgeTarget(pe).getPNodeVertex().getX() + '"'
					+ " y2=" + '"' + planeGraph.getEdgeTarget(pe).getPNodeVertex().getY() + '"'
					+ " stroke=" + '"' + "black" + '"' + "/>\n");
			/*svg.write("<text x =" + '"' + pe.getPEdgeLine().getMediumPoint().getX() + '"' 
					+ " y=" + '"' + pe.getPEdgeLine().getMediumPoint().getY() + '"' + ">"
					+ pe.getPEdgeId() + "</text>\n");*/
		}

		svg.write("</svg>");
		svg.close();


	}

	public void writeGraphPEdgesOnTXT(String filename) throws IOException 
	{

		FileWriter foutput = new FileWriter(filename);
		BufferedWriter txt = new BufferedWriter(foutput);

		for(PEdge e : planeGraph.edgeSet())
		{
			txt.write(e.getPEdgeAngle() + ": \n");

			txt.write(" Source  Id: " +  planeGraph.getEdgeSource(e).getId() + "\n"
					+ " Target Id: " + planeGraph.getEdgeTarget(e).getId() + "\n"); 

		}

		txt.close();

	}

	public void writeGraphPNodesOnTXT(String filename) throws IOException 
	{

		FileWriter foutput = new FileWriter(filename);
		BufferedWriter txt = new BufferedWriter(foutput);

		for(PNode n : planeGraph.vertexSet())
		{
			txt.write("Node Id: " + n.getId() + "\n");

			for(PEdge e : planeGraph.edgeSet())
			{
				txt.write(" Edge: " + e.getPEdgeAngle() + "; X: " 
						+ planeGraph.getEdgeSource(e).getPNodeVertex().getX() + "; Y: "
						+ planeGraph.getEdgeSource(e).getPNodeVertex().getY() + " :::: X: "
						+ planeGraph.getEdgeTarget(e).getPNodeVertex().getX() + "; Y: "
						+ planeGraph.getEdgeTarget(e).getPNodeVertex().getY()
						+ "; Source : " + planeGraph.getEdgeSource(e).getId()
						+ "; Target : " + planeGraph.getEdgeTarget(e).getId() + "\n");

			}
		}

		txt.close();

	}

	public void printPEdgeConsole(List<PEdge> orderedPE)
	{
		for(PEdge pe : orderedPE)
		{
			System.out.println("( " + planeGraph.getEdgeSource(pe).getId() + " : " + planeGraph.getEdgeTarget(pe).getId() + " : " + pe.getPEdgeAngle() + " )\n");
		}
	}
}
