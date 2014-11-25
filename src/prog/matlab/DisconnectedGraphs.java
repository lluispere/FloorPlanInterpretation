package prog.matlab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;

import prog.mygraph.MyGraph;
import prog.strelement.StructuralElement;

public class DisconnectedGraphs {

	private ArrayList<MyGraph> listOfDisconnectedGraphhs;

	/**
	 * Constructor
	 * @param graph is any type of MyGraph
	 */
	public DisconnectedGraphs(MyGraph graph)
	{
		setListOfDisconnectedGraphhs(new ArrayList<MyGraph>(calculateDisconnectedGraphs(graph)));
	}

	/**
	 * This function calculates the different disconnected subgraphs of a graph using the BreathFirstIterator
	 * implemented in jgrapht library
	 * @param graph is the original graph
	 * @return a List of disconnected subgra
	 */
	private List<MyGraph> calculateDisconnectedGraphs(
			MyGraph graph) {

		// variable creation
		ArrayList<MyGraph> disconnectedVertexes = new ArrayList<MyGraph>();
		Set<StructuralElement> setOfVisitedLines = new HashSet<StructuralElement>();
		// for each vertex in the graph
		for(StructuralElement vertex : graph.getVertexes())
		{
			// if it has not been visited yet
			if (!setOfVisitedLines.contains(vertex))
			{
				// add to the visited vertex list
				setOfVisitedLines.add(vertex);
				// create the new arraylist for the connected vertexes
				MyGraph connectedV = new MyGraph();
				// get the iterator of the BFS
				BreadthFirstIterator<StructuralElement, DefaultEdge> bfi = new BreadthFirstIterator<StructuralElement, DefaultEdge>(graph.getGraph(), vertex);
				// while there is a connected vertex
				while(bfi.hasNext())
				{
					StructuralElement l = bfi.next();
					// add it to the connected vertexes
					connectedV.addVertex(l);
					if (!connectedV.areConnected(vertex, l) && !vertex.equals(l))
						connectedV.addEdge(vertex, l);
					// add the new vertex to visited
					setOfVisitedLines.add(l);
				}
				disconnectedVertexes.add(connectedV);
			}
		}
		return disconnectedVertexes;

	}

	public ArrayList<MyGraph> getListOfDisconnectedGraphhs() {
		return listOfDisconnectedGraphhs;
	}

	public void setListOfDisconnectedGraphhs(
			ArrayList<MyGraph> listOfDisconnectedGraphhs) {
		this.listOfDisconnectedGraphhs = listOfDisconnectedGraphhs;
	}

}
