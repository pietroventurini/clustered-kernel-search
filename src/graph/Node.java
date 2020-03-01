package graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class used to define a node in the ImplicitUndirectedGraph class.
 * This allows to split the complexity and the responsibilities of the graph class in multiple instances of nodes.
 * The type parameter <i>N</i> is used to contain a generic payload, on which all the comparaisons are made.
 *
 * @param <N> The type of the payload
 */
class Node<N extends Comparable<N>> implements Comparable<Node<N>> {
	
	private N payload;
	private Map<N, Double> edges;
	
	/**
	 * Constructor.
	 * @param payload The effective object contained into the node.
	 */
	public Node(N payload) {
		this.payload = payload;
		this.edges = new HashMap<>();
	}
	
	/**
	 * @return The payload contained into this node
	 */
	public N getPayload() {
		return this.payload;
	}
	
	/**
	 * @return The sum of the weights of the edges connecting this node.
	 */
	public double getDegree() {
		// TODO Valutare la possibilità di utilizzare un attributo di istanza per memorizzare la somma dei pesi degli archi. In tal caso l'attributo andrebbe costantemente aggiornato, ma permetterebbe di evitare i calcoli ad ogni chiamata
		double sum = 0;
		for (N neighbor : this.edges.keySet()) {
			sum += this.edges.get(neighbor);
		}
		return sum;
	}
	
	/**
	 * @return The number of neighbors connected to this node with a non-zero edge
	 */
	public int getNeighborsNumber() {
		return this.edges.size();
	}
	
	/**
	 * @return The list of the neighbors connected to this node with a non-zero edge
	 */
	public List<N> getNeighbors() {
		return this.edges.keySet().stream().collect(Collectors.toList());
	}
	
	/**
	 * Checks if exists an edge between this and the parameter node.
	 * @param node The other node to check if it's connected
	 * @return TRUE iff it exists an edge connecting the two
	 */
	public boolean hasEdgeTo(N node) {
		return this.edges.containsKey(node);
	}
	
	/**
	 * Returns the weight of the edge connecting this node and the node passed as parameter.
	 * If there are no links, this method returns 0.
	 * @param neighbor The neighbor node connected to this
	 * @return The weight of the link between this and the neighbor node
	 */
	public double getEdgeWeight(N neighbor) {
		if (this.edges.containsKey(neighbor)) {
			return this.edges.get(neighbor);
		} else {
			return 0;
		}
	}
	
	/**
	 * Connect this node to another node, with the specified weight.
	 * If the node is already connected, the old weight is overwritten.
	 * If the new weight is 0, the link is removed.
	 * @param neighbor The node to connect
	 * @param weight The weight of the new edge
	 */
	public void addEdge(N neighbor, double weight) {
		if (weight != 0) {
			this.edges.put(neighbor, weight);
		} else {
			this.edges.remove(neighbor);
		}
	}
	
	/**
	 * Removes the edge between this node and the neighbor node, if present.
	 * If not, it returns FALSE.
	 * @param neighbor The node to disconnect
	 * @return TRUE iff the edge was present and it's been removed
	 */
	public boolean removeEdge(N neighbor) {
		if (this.edges.containsKey(neighbor)) {
			this.edges.remove(neighbor);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The comparaison method wraps the comparaison method of the payload.
	 * The payload type <i>N</i> has to implement the "Comparable" interface.
	 */
	@Override
	public int compareTo(Node<N> o) {
		return this.getPayload().compareTo(o.getPayload());
	}
	
	/**
	 * Restituisce una descrizione testuale del nodo.
	 */
	@Override
	public String toString() {
		return String.format("%s {%s}", this.payload.toString(), this.edges.toString());
	}

}
