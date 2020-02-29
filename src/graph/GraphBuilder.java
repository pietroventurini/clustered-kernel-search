package graph;

import java.util.HashMap;
import java.util.stream.IntStream;

/**
 * Class to build a graph from an adjacency matrix
 *
 */
public class GraphBuilder {
	public static SimpleUndirectedGraph<SimpleNode> build(int[][] adj) throws IllegalArgumentException{
		if(IntStream.range(0, adj.length).anyMatch((i)->adj[i].length!=adj.length))
			throw new IllegalArgumentException("Matrix is not square");
		
		HashMap<Integer, SimpleNode> intToNode = new HashMap<Integer, SimpleNode>();
		IntStream.range(0, adj.length).forEach((i)->intToNode.put(i, new SimpleNode(""+i)));
		
		SimpleUndirectedGraph<SimpleNode> g = new SimpleUndirectedGraph<SimpleNode>();
		intToNode.forEach((i,n)->g.add_node(n));
		IntStream.range(0, adj.length)
					.forEach((i)->IntStream.range(0, adj.length)
											.filter((j)->j>i)
											.filter((j)->adj[i][j]!=0)
											.forEach((j)->g.add_edge(intToNode.get(i), intToNode.get(j))));
		return g;
	}
}
