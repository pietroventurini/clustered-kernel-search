package graph;

import java.util.*;
import java.util.stream.Collectors;

public class MapGraph<N extends Node> implements UndirectedGraph<N> {

    private Map<String, N> nodes; //map from variable name to Item
    private Map<N, Set<N>> neighbors; //map from node (item) to a set of adjacent nodes
    private int edges;

    public MapGraph(Map<String, N> nodes, Map<N, Set<N>> neighbors) {
        this.nodes = nodes;
        this.neighbors = neighbors;
        this.edges = computeNumberOfEdges();
    }

    @Override
    public List<N> nodes() {
        return this.nodes.values().stream().collect(Collectors.toList());
    }

    @Override
    public double edgesN() {
        return this.edges;
    }

    @Override
    public double degree(N node) {
        return neighbors.get(node).size();
    }

    @Override
    public List<N> neighbors(N node) {
        return neighbors.get(node).stream().collect(Collectors.toList()); //valutare se convertire l'interfaccia e restituire un insieme
    }

    public void removeNode(N node) {
        if (neighbors.containsKey(node))
            removeNodeAndNeighbors(node);
        nodes.remove(node.getName());
    }

    /**
     * Removes all occurrences of 'node' from the set of adjacent nodes of the others nodes. Finally, it removes also the node itself.
     */
    private void removeNodeAndNeighbors(N node) {
        for (N neighbor : neighbors.get(node)) {
            neighbors.get(neighbor).remove(node);
        }
        this.edges -= neighbors.get(node).size();
        neighbors.remove(node);
    }

    /**
     * In an undirected graph G=(V,E), the sum of the vertex degrees is 2|E|
     */
    private int computeNumberOfEdges() {
        return neighbors.values().stream().map(set -> set.size()).reduce(0, (a,b) -> a + b) / 2;
    }
}
