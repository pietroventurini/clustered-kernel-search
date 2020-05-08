package graph;

import java.util.List;
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
	public List<N> neighbors(N node);

	public Stream<N> neighborsStream(N node);

	public Stream<N> nodesStream();
}
