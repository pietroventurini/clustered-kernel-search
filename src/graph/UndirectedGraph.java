package graph;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Defines the behavior of a generic undirected Graph.
 * An undirected graph is composed by a set of Nodes and of a set of Edges between the nodes.
 * @param <N> the type of the nodes that compose the graph. See @Node for more details
 */
public interface UndirectedGraph<N extends Node> {
	
	/**
	 * @return the list of all the nodes of the graph
	 */
	public List<N> nodes();
	
	/**
	 * @return the number of ALL the edges of the graph
	 */
	public double edgesN();
	
	/**
	 * Returns the degree of a specific node
	 * @param node a node of the graph
	 * @return the degree of the node
	 */
	public double degree(N node);
	
	/**
	 * Returns the neighbors of a specific node
	 * @param node a node of the graph
	 * @return the list of its neighbors
	 */
	public Set<N> neighbors(N node);
	
	/**
	 * Returns a stream containing the neighbors of a specific node
	 * @param node a node of the graph
	 * @return the list of its neighbors
	 */
	public Stream<N> neighborsStream(N node);
	
	/**
	 * @return a stream of all the nodes of the graph
	 */
	public Stream<N> nodesStream();

	/**
	 * @return the sum of the weights of all the links in the network
	 */
	public int getM();

	/**
	 * @return the weight of the edge between n1 and n2
	 */
	public int weightOfEdge(N n1, N n2);

	/**
	 * @return true if n1 and n2 are connected, false otherwise.
	 */
	public boolean areConnected(N n1, N n2);

	/**
	 * Creates an edge from node1 to node2, with the specified weight.
	 * If an old link is already present, it will be overwritten.
	 * Preconditions:
	 * • The weight MUST be non-zero.
	 * • The nodes MUST be in the graph (for efficiency reasons, we do not check this)
	 * @param node1 First node to link
	 * @param node2 Second node to link
	 * @param weight The weight of the new edge
	 */
	void connect(N node1, N node2, int weight);

	/**
	 * Insert the node into the graph, IFF it's not already present.
	 * @param node The node to be added
	 */
	void insertNode(N node);
}
