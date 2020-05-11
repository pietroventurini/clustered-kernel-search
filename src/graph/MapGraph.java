package graph;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class representing a WEIGHTED & UNDIRECTED GRAPH.
 * The internal data structure is a Map from each node to every couple <NeighborNode, WeightOfTheLink>.
 * The generic element must implement Node interface & Comparable interface.
 *
 * @param <N> The type of the Node
 */
public class MapGraph<N extends Node & Comparable<? super N>> implements UndirectedGraph<N> {

    private Map<N, Map<N, Integer>> neighbors; //map from node (item) to a set of adjacent nodes
    private int edges; // number of edges
    private int m; // sum of edges' weights

    /**
     * Public constructor.
     * @param nodes Map: <Name of node, Node Object>
     * @param neighbors: The map of neighbors
     */
    public MapGraph(Map<N, Map<N, Integer>> adjacencyMap) {
        this.neighbors = adjacencyMap;
        this.edges = computeNumberOfEdges();
        this.m = computeM();
    }
    
    /**
     * Public empty constructor.
     * When called, it return an empty graph.
     */
    public MapGraph() {
    	this.neighbors = getEmptyMap();
    	this.edges = 0;
    	this.m = 0;
    }
    
    /**
     * Each time a Map is needed in the class, we call this method.
     * In this way, we can easily edit the specific class, if we want to test different possibilities
     * (HashMap, TreeMap, ConcurrentHashMap, WeakHashMap, etc...).
     * @param <N> Map's keys type
     * @param <M> Map's values type
     * @return A new empty map
     */
    private static <N,M> Map<N,M> getEmptyMap() {
    	return new HashMap<N,M>();
    }

    @Override
    public List<N> nodes() {
        return this.neighbors.keySet().stream().collect(Collectors.toList());
    }
    
    @Override
    public double edgesN() {
        return this.edges;
    }

    @Override
    public double degree(N node) {
        return this.neighbors.get(node).size();
    }

    @Override
    public Set<N> neighbors(N node) {
    	return this.neighbors.get(node).keySet();
    }

    @Override
    public Stream<N> neighborsStream(N node) {
        return this.neighbors.get(node).keySet().stream();
    }

    @Override
    public Stream<N> nodesStream() {
        return this.neighbors.keySet().stream();
    }

    @Override
    public int getM() {
        return this.m;
    }

    /**
     * Returns the weight of the link between two nodes.
     * The link is always stored from the "previous" node to the "next" one, considering the order
     * given by the method compareTo().
     */
    @Override
    public int weightOfEdge(N n1, N n2) {
    	Integer weight = this.neighbors.get(n1).get(n2);
		return  weight != null ?
			    weight :
				0;
    }

    /**
     * Checks if it exists a link between node n1 and n2.
     * Since the link are doubled, one search should be enough.
     */
    @Override
    public boolean areConnected(N n1, N n2) {
		return this.neighbors.get(n1).containsKey(n2);
    }
    
    /**
     * Insert the node into the graph, IFF it's not already present.
     * @param node
     */
    @Override
    public void insertNode(N nodeToBeInserted) {
    	if (!this.neighbors.containsKey(nodeToBeInserted)) {
    		this.neighbors.put(nodeToBeInserted, getEmptyMap());
    	}
    }
    
    /**
     * Creates an edge from node1 to node2, with the specified weight.
     * Preconditions:
     * • The weight MUST be non-zero.
     * • The nodes MUST be in the graph (for efficiency reasons, we do not check this)
     * 
     * If an old link is already present, it will be overwritten.
     * 
     * @param node1 First node to link
     * @param node2 Second node to link
     * @param weight The weight of the new edge
     */
    @Override
    public void connect(N node1, N node2, int weight) {
    	// Cannot link two nodes with null weight
    	if (weight == 0) {
    		return;
    	}
    	if (this.neighbors.get(node1).containsKey(node2)) {
        	this.m += weight - this.neighbors.get(node1).get(node2);
    	} else {
        	this.edges += 1;
        	this.m += weight;
    	}
    	this.neighbors.get(node1).put(node2, weight);
    	this.neighbors.get(node2).put(node1, weight);
    }

    /**
     * Remove node from the graph.
     * @param node The node to be removed
     */
    public void removeNode(N nodeToBeRemoved) {
        if (this.neighbors.containsKey(nodeToBeRemoved)) {
            for (N neighbor : this.neighbors.get(nodeToBeRemoved).keySet()) {
            	// Verifico di non cancellare link ad anello
            	if (!neighbor.equals(nodeToBeRemoved)) {
            		this.neighbors.get(neighbor).remove(nodeToBeRemoved);
            	}
            }
            this.edges -= this.neighbors.get(nodeToBeRemoved).size();
            this.m -= this.neighbors.get(nodeToBeRemoved).values().stream().mapToInt(n -> n.intValue()).sum();
            this.neighbors.remove(nodeToBeRemoved);
        }
    }

    /**
     * In an undirected graph G=(V,E), the sum of the vertex degrees is 2|E|
     */
    private int computeNumberOfEdges() {
        return this.neighbors.values().stream().mapToInt(set -> set.size()).sum() / 2;
    }

    /**
     * @return The sum of the weights of all the edges in the graph.
     */
    private int computeM() {
        return this.neighbors
        		.values() // Set of neighbors for each node
        		.stream() // Stream of neighbors map for each node
        		.mapToInt(neighborMap -> neighborMap // Mapping each map to a int (the sum of edges)
        				.values() 	// Set of links' weights
        				.stream()	// Stream of Integer links' weight
        				.mapToInt(n -> n.intValue()) // Cast from Integer to int
        				.sum() // Sum of edges weight for a single node
        				)
        		.sum() // Sum of ALL edges, counting double
        		/ 2; // Necessary division
    }

	@Override
	public void eraseAllEdgesUnder(int threshold) {
		for (Map<N, Integer> nodeNeighbors : this.neighbors.values()) {
			// Removing link if the weight is under the threshold
			nodeNeighbors.values().removeIf(weight -> weight < threshold);
		}
		// Removing empty map (nodes that are alone)
		// TODO decidere se rimuovere i nodi lasciati singoli. Se no, rimuovere la riga successiva.
		this.neighbors.values().removeIf(map -> map.isEmpty());
		// Updating edges total weight and edges count
		// FIXME Per il momento è un'implementazione pessima, poi sarà da tener conto di ogni singolo edge rimosso invece che calcolare le due quantità da capo.
		this.edges = this.computeNumberOfEdges();
		this.m = this.computeM();
	}
}
