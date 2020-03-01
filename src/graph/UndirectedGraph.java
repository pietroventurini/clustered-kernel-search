package graph;

import java.util.List;

/**
 * Defines the behavior of a generic undirected Graph.
 * An undirected graph is composed by a set of Nodes and of a set of Edges between the nodes.
 * @param <N> the type of the nodes that compose the graph. See @Node for more details
 */
public interface UndirectedGraph<N> {
	
	double DEFAULT_EDGE_WEIGHT = 1;
	double ABSENT_EDGE_WEIGHT = 0;
	
	/**
	 * @return The number of nodes in this graph
	 */
	public default int size() {
		return this.nodes().size();
	}
	
	/**
	 * @return The list of all the nodes of the graph
	 */
	public List<N> nodes();
	
	/**
	 * @return The number of ALL the edges of the graph
	 */
	public double edgesNumber();
	
	/**
	 * Returns the degree of a specific node, i.e. the sum of weight of edges connected to that node.
	 * If the node is not present into the graph, this returns 0.
	 * @param node A node of the graph
	 * @return The weighted degree of the node
	 */
	public double degreeOf(N node);
	
	/**
	 * Returns the number of connected nodes to a node.
	 * If the node is not present into the graph, this returns 0.
	 * @param node A node of the graph
	 * @return The degree of the node
	 */
	public int neighborsNumberOf(N node);
	
	/**
	 * Returns the neighbors of a specific node.
	 * If the node is not present into the graph, this returns null.
	 * @param node A node of the graph
	 * @return The list of its neighbors
	 */
	public List<N> neighborsOf(N node);
	
	/**
	 * Inserts a new node into the graph with the parameter payload, but only if
	 * the node is not already present. The comparaison is made with method "equals".
	 * 
	 * @param node The paylod to insert into a new node
	 * @return TRUE iff the node can be inserted, i.e. if it's not already present
	 */
	public boolean addNode(N node);
	
	/**
	 * Removes a node from the graph, only if it's present.
	 * If not, nothing is removed and the method returns FALSE.
	 * 
	 * @param node The node payload to remove.
	 * @return TRUE iff the node is found and removed correctly
	 */
	public boolean removeNode(N node);
	
	/**
	 * Links two nodes in the graph, with the default weight = 1.
	 * If one node is not present, the method returns FALSE.
	 * @param first The first node to connect
	 * @param second The second node to connect
	 * @return TRUE iff the nodes are in the graph
	 */
	public default boolean connect(N first, N second) {
		return this.connect(first, second, DEFAULT_EDGE_WEIGHT);
	}
	
	/**
	 * Links two nodes with the specified weight.
	 * If one node is not present, the method returns FALSE
	 * @param first The first node to connect
	 * @param second The second node to connect
	 * @param weight The weight of the link
	 * @return TRUE iff the nodes are in the graph
	 */
	public boolean connect(N first, N second, double weight);
	
	/**
	 * Removes the link -if present- between two nodes.
	 * If the nodes are not connected, this method returns FALSE.
	 * @param first The first node of the link to remove
	 * @param second The second node of the link to remove
	 * @return TRUE iff the link is removed
	 */
	public boolean disconnect(N first, N second);
	
	/**
	 * Checks if two nodes are connected.
	 * @param first The first node of the link to check
	 * @param second The second node of the link to check
	 * @return TRUE iff it exists a link between N1 and N2
	 */
	public boolean areConnected(N first, N second);
	
	/**
	 * Returns the weight of the link between the two nodes. If the
	 * link does not exist, this method returns 0.
	 * @param first The first node of the link 
	 * @param second The second node of the link
	 * @return The weight of the link
	 */
	public double getConnectionWeight(N first, N second);
	
}
