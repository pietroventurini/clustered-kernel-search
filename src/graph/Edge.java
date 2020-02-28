package graph;

import java.util.ArrayList;
import java.util.Objects;

public class Edge {
	private static final String startFormat = "(%s, w:%.2f";
	private static final String labelFormat = ", l:%s";
	private static final String endFormat = ")";
	
	private String label;
	private SimpleNode nodeA;
	private SimpleNode nodeB;
	private double weight;
	
	public Edge(SimpleNode a, SimpleNode b) {
		init(a, b, 1.0, "");
	}
	public Edge(SimpleNode a, SimpleNode b, String label) {
		init(a, b, 1.0, label);
	}
	public Edge(SimpleNode a, SimpleNode b, double weight, String label) {
		init(a, b, weight, label);
	}
	public Edge(SimpleNode a, SimpleNode b, double weight) {
		init(a, b, weight, "");
	}
	private void init(SimpleNode n, SimpleNode m, double weight, String label) {
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
		
		final Edge other = (Edge) o;
		boolean node_match = nodes().containsAll(other.nodes());
		return node_match && weight==other.weight && label.equals(other.label);
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
	
	public ArrayList<SimpleNode> nodes() {
		ArrayList<SimpleNode> tmp = new ArrayList<SimpleNode>();
		tmp.add(nodeA);
		tmp.add(nodeB);
		return tmp;
	}
	public double weight() {return weight;}
	public String label() {return label;}
}
