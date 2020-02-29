package graph;

import java.util.ArrayList;
import java.util.Objects;

public class Edge<N> {
	private static final String startFormat = "(%s, w:%.2f";
	private static final String labelFormat = ", l:%s";
	private static final String endFormat = ")";
	
	private String label;
	private N nodeA;
	private N nodeB;
	private double weight;
	
	public Edge(N a, N b) {
		init(a, b, 1.0, "");
	}
	public Edge(N a, N b, String label) {
		init(a, b, 1.0, label);
	}
	public Edge(N a, N b, double weight, String label) {
		init(a, b, weight, label);
	}
	public Edge(N a, N b, double weight) {
		init(a, b, weight, "");
	}
	private void init(N n, N m, double weight, String label) {
		this.label = label;
		this.nodeA = n;
		this.nodeB = m;
		this.weight = weight;
	}
	
	public boolean equals(Object o) {
		if(o==null)
			return false;
		if(!Edge.class.isAssignableFrom(o.getClass()))
			return false;
		try {
			@SuppressWarnings("unchecked")
			final Edge<N> other = (Edge<N>) o;
			boolean node_match = nodes().containsAll(other.nodes());
			return node_match && weight==other.weight && label.equals(other.label);
		}catch(ClassCastException e) {
			return false;
		}
	}
	public int hashCode() {
		if(Objects.hash(nodeA, nodeB) < Objects.hash(nodeB, nodeA))
			return Objects.hash(nodeA, nodeB, weight, label);
		return Objects.hash(nodeB, nodeA, weight, label);
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(startFormat, nodes().toString(), weight));
		if(label!="")
			sb.append(String.format(labelFormat, label));
		sb.append(endFormat);
		return sb.toString();
	}
	
	public ArrayList<N> nodes() {
		ArrayList<N> tmp = new ArrayList<N>();
		tmp.add(nodeA);
		tmp.add(nodeB);
		return tmp;
	}
	public double weight() {return weight;}
	public String label() {return label;}
}
