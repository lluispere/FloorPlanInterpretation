package prog.mygraph;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jgrapht.ListenableGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import prog.strelement.StructuralElement;


public class MyDirectedGraph implements Serializable{

	private static final long serialVersionUID = 1L;
	private ListenableGraph<StructuralElement, DefaultEdge> graph;


	public MyDirectedGraph()
	{
		graph = new ListenableDirectedGraph<StructuralElement, DefaultEdge>(DefaultEdge.class);
	}


	// getters and setters
	public void setGraph(ListenableDirectedGraph<StructuralElement, DefaultEdge> graph) {
		this.graph = graph;}
	public ListenableGraph<StructuralElement, DefaultEdge> getGraph() {
		return this.graph;}
	public SortedSet<StructuralElement> getVertexes(){
		SortedSet<StructuralElement> vertexSet = new TreeSet<StructuralElement>(this.graph.vertexSet());
		return vertexSet;}
	public Set<DefaultEdge> getEdges(){
		return this.graph.edgeSet();
	}

	// functionality

	// add a vertex into the graph
	public boolean addVertex(StructuralElement v)
	{
		return graph.addVertex(v);
	}

	// adds an edge to the graph
	public DefaultEdge addEdge(StructuralElement v1, StructuralElement v2)
	{
		return graph.addEdge(v1, v2);
	}

	// removes a vertex from the graph 
	public boolean removeVertex(StructuralElement st)
	{
		return this.graph.removeVertex(st);
	}

	// removes an edge from the graph
	public boolean removeEdge(DefaultEdge e)
	{
		return this.graph.removeEdge(e);
	}

	// gets the source of an edge e
	public StructuralElement getSource(DefaultEdge e)
	{
		return graph.getEdgeSource(e);
	}

	// gets the target of an edge e
	public StructuralElement getTarget(DefaultEdge e)
	{
		return graph.getEdgeTarget(e);
	}

	// gets all the edges of a certain vertex
	public Set<DefaultEdge> edgesOf(StructuralElement st)
	{
		return graph.edgesOf(st);
	}

	// gets the other vertex involved in an edge
	public StructuralElement getTheOtherVertex(DefaultEdge e, StructuralElement st)
	{
		if (graph.getEdgeSource(e).equals(st))
			return graph.getEdgeTarget(e);
		else return graph.getEdgeSource(e);
	}

	/**
	 * Gets the parents of the vertex, given by the relation child.
	 * @param st
	 * @return
	 */
	public Set<StructuralElement> getItsParents(StructuralElement st)
	{
		Set<StructuralElement> stSet = new HashSet<StructuralElement>();
		// get the parents
		for (DefaultEdge me : graph.edgesOf(st))
		{
			if(graph.getEdgeTarget(me).equals(st))
				stSet.add(graph.getEdgeSource(me));
		}
		return stSet;
	}

	/**
	 * Gets the children of the vertex, given by the relation child.
	 * @param st
	 * @return
	 */
	public Set<StructuralElement> getItsChildren(StructuralElement st)
	{
		Set<StructuralElement> stSet = new HashSet<StructuralElement>();
		// get the parents
		for (DefaultEdge me : graph.edgesOf(st))
		{
			if(graph.getEdgeSource(me).equals(st))
				stSet.add(graph.getEdgeTarget(me));
		}
		return stSet;
	}

	/**
	 * function that creates a value copy of the gtGraph
	 * @return a MyGraph
	 */
	public MyDirectedGraph graphValueCopy()
	{
		MyDirectedGraph g = new MyDirectedGraph();
		for (StructuralElement st : graph.vertexSet())
		{
			g.addVertex(st);
		}
		for (DefaultEdge e : graph.edgeSet())
		{
			g.addEdge(this.getSource(e), this.getTarget(e));
		}
		return g;
	}
	
	public boolean isConnected()
	{
		// check the floor plan neighbour connectivity
		@SuppressWarnings("unchecked")
		ConnectivityInspector<StructuralElement, DefaultEdge> ci = new ConnectivityInspector<StructuralElement, DefaultEdge>((UndirectedGraph<StructuralElement, DefaultEdge>) this.getGraph());
		if(ci.isGraphConnected())
			return true;
		else return false;
	}

	/**
	 * This functions returns a Set of StructuralElement of the type @type
	 * @param type is a string assessing the type of the element we are intereste in
	 * @return a Set of StructuralElement
	 */
	public Set<StructuralElement> getVertexesOfType(String type)
	{
		Set<StructuralElement> stSet = new HashSet<StructuralElement>();

		for (StructuralElement st : this.getVertexes())
		{
			if (st.getType().equals(type))
				stSet.add(st);
		}
		return stSet;
	}



	@Override
	public String toString()
	{
		return this.graph.toString();
	}
}
