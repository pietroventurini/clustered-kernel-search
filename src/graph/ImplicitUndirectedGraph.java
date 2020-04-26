package graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Simple implementation of an undirected graph with implicit "node" structure.
 * The payloads are stored into "node" structures, collected into a set. Every "node" 
 * contains the informations about edges to other nodes.
 * For the graph is undirected (i.e. the adjacency matrix is simmetric), every edge from node A to node B is stored TWICE.
 *
 * @param <N> The generic type of the payload of every node
 */
public class ImplicitUndirectedGraph<N extends Comparable<N>> implements UndirectedGraph<N> {
	
	private Map<N, Node<N>> nodes;
	
	public ImplicitUndirectedGraph() {
		this.nodes = new HashMap<>();
	}

	@Override
	public List<N> nodes() {
		return this.nodes
				.entrySet()
				.stream()
				.map(e -> e.getValue().getPayload()) // Mapping every node structure to its payload
				.collect(Collectors.toList()); // Collecting every payload into a list
	}

	@Override
	public double edgesNumber() {
		return this.nodes
				.entrySet()
				.stream()
				.mapToInt(e -> e.getValue().getNeighborsNumber()) // Gets the edges number connecting the node
				.sum() / 2.0; // Sums all the edges and divides by 2, since every edge is counted twice
	}

	@Override
	public double degreeOf(N node) {
		if (!this.nodes.containsKey(node)) {
			return 0;
		}
		return this.nodes.get(node).getDegree();
	}
	
	@Override
	public int neighborsNumberOf(N node) {
		if (!this.nodes.containsKey(node)) {
			return 0;
		}
		return this.nodes.get(node).getNeighborsNumber();
	}

	@Override
	public List<N> neighborsOf(N node) {
		if (!this.nodes.containsKey(node)) {
			return null;
		}
		return this.nodes.get(node).getNeighbors();
	}

	@Override
	public boolean addNode(N payload) {
		if (this.nodes.containsKey(payload)) {
			return false;
		} else {
			this.nodes.put(payload, new Node<N>(payload));
			return true;
		}
	}

	@Override
	public boolean removeNode(N payload) {
		if (this.nodes.containsKey(payload)) {
			// Removing node from graph
			this.nodes.remove(payload);
			// Removing node from neighbors of other nodes
			for (Entry<N, Node<N>> n : this.nodes.entrySet()) {
				n.getValue().removeEdge(payload);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean connect(N first, N second, double weight) {
		if (!this.nodes.containsKey(first) || !this.nodes.containsKey(second)) {
			return false;
		}
		this.nodes.get(first).addEdge(second, weight);
		this.nodes.get(second).addEdge(first, weight);
		return true;
	}

	@Override
	public boolean disconnect(N first, N second) {
		if (!this.nodes.containsKey(first) || !this.nodes.containsKey(second)) {
			return false;
		}
		boolean flag1 = this.nodes.get(first).removeEdge(second);
		boolean flag2 = this.nodes.get(second).removeEdge(first);
		// Control
		// TODO in futuro, quando saremo sicuri che funzionerà tutto, si potrà evitare questo controllo
		if (flag1 != flag2) {
			throw new IllegalStateException("Found asymmetric node into graph!");
		}
		return flag1 && flag2; // TRUE if both TRUE, FALSE if both FALSE. If different, an exception is thrown
	}

	@Override
	public boolean areConnected(N first, N second) {
		if (!this.nodes.containsKey(first) || !this.nodes.containsKey(second)) {
			return false;
		}
		return this.nodes.get(first).hasEdgeTo(second);
	}

	@Override
	public double getConnectionWeight(N first, N second) {
		if (!this.nodes.containsKey(first) || !this.nodes.containsKey(second)) {
			throw new IllegalArgumentException("Nodes are not present into graph");
		}
		return this.nodes.get(first).getEdgeWeight(second);
	}
	
	/**
	 * Restituisce una descrizione testuale, tramite String, di tutti i nodi 
	 * contenuti all'interno del grafo.
	 */
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (Entry<N,Node<N>> entry : this.nodes.entrySet()) {
			s.append(entry.getValue().toString() + "\n");
		}
		return s.toString();
	}

}
