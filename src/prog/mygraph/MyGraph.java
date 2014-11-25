package prog.mygraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jgrapht.ListenableGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableUndirectedGraph;

import prog.strelement.StructuralElement;

/**
 * @author Lluís-Pere de las Heras
 * @date 07/11/13
 */
public class MyGraph implements Serializable{


	private static final long serialVersionUID = 1L;
	private ListenableUndirectedGraph<StructuralElement, DefaultEdge> graph;
	
	
	public MyGraph()
	{
		graph = new ListenableUndirectedGraph<StructuralElement, DefaultEdge>(DefaultEdge.class);
	}

	
	// getters and setters
	public void setGraph(ListenableUndirectedGraph<StructuralElement, DefaultEdge> graph) {
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
	 * function that creates a value copy of the gtGraph
	 * @return a MyGraph
	 */
	public MyGraph graphValueCopy()
	{
		MyGraph g = new MyGraph();
		for (StructuralElement st : graph.vertexSet())
		{
			g.addVertex(st);
		}
		for (DefaultEdge e : graph.edgeSet())
		{
			g.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e));
		}
		return g;
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
	
	/**
	 * Returns the connected vertexes to a given one
	 * @param st is the selected element
	 * @return an array with the connected elements
	 */
	public StructuralElement[] getConnectedfVertexes(StructuralElement st)
	{
		List<StructuralElement> stList = new ArrayList<StructuralElement>();
		for (DefaultEdge de : graph.edgesOf(st))
		{
			stList.add(this.getTheOtherVertex(de, st));
		}
		
		StructuralElement[] stArray = new StructuralElement[stList.size()];
		
		for(int i = 0; i<stArray.length; i++)
			stArray[i] = stList.get(i);
		
		return stArray;
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
	
	public List<Set<StructuralElement>> getConnectedElements()
	{
		@SuppressWarnings("unchecked")
		ConnectivityInspector<StructuralElement, DefaultEdge> ci = new ConnectivityInspector<StructuralElement, DefaultEdge>((UndirectedGraph<StructuralElement, DefaultEdge>) this.getGraph());
		return ci.connectedSets();
	}
	
	public Set<StructuralElement> getDisconnectedElements()
	{
		Set<StructuralElement> stSet = new HashSet<StructuralElement>();
		for (StructuralElement st : graph.vertexSet())
		{
			if (graph.edgesOf(st).size() == 0)
				stSet.add(st);
		}
		return stSet;
	}
	
	public void removeAllVertexes(Set<StructuralElement> vs)
	{
		for (StructuralElement st : vs)
			this.removeVertex(st);
	}
	
	public void removeTheRestOfTheVertexesById(Set<Integer> set)
	{
		Set<StructuralElement> stSet = new HashSet<StructuralElement>(graph.vertexSet());
		for (StructuralElement st : stSet)
		{
			if (!set.contains(st.getId()))
				this.removeVertex(st);
		}
	}
	
	public boolean areConnected(StructuralElement st1, StructuralElement st2)
	{
		Set<DefaultEdge> set1 = edgesOf(st1);
		Set<DefaultEdge> set2 = edgesOf(st2);
		for (DefaultEdge e1 : set1)
		{
			if (set2.contains(e1))
				return true;
		}
		return false;
	}

	
	@Override
	public String toString()
	{
		return this.graph.toString();
	}

}
