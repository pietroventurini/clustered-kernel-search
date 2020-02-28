package graph;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.HashSet;

public class SimpleUndirectedGraph implements Graph{
	private HashMap<SimpleNode, HashSet<Edge>> comp;
	private double edges;
	
	public SimpleUndirectedGraph() {
		init();
	}
	private void init() {
		this.comp = new HashMap<SimpleNode, HashSet<Edge>>();
		this.edges=0;
	}
	
	public boolean add_node(SimpleNode n) {
		if(!comp.containsKey(n)) {
			comp.put(n, new HashSet<Edge>());
			return true;
		}
		return false;
	}
	
	private boolean add_new_edge(SimpleNode n, SimpleNode m, double weight, String label) {
		if(!comp.containsKey(n) || !comp.containsKey(m))
			return false;
		SimpleNode n_act = comp.keySet().stream()
									.filter((k)->k.equals(n))
									.findFirst().get();
		SimpleNode m_act = comp.keySet().stream()
									.filter((k)->k.equals(m))
									.findFirst().get();
		Edge e = new Edge(n_act, m_act, weight, label);
		comp.get(n).add(e);
		comp.get(m).add(e);
		edges+=e.weight();
		return true;
	}
	public boolean add_edge(SimpleNode n, SimpleNode m, double weight) {
		return add_new_edge(n, m, weight, "");
	}
	public boolean add_edge(SimpleNode n, SimpleNode m) {
		return add_new_edge(n, m, 1.0, "");
	}
	
	public double edgesN() {
		return edges;
	}
	
	public double degree(SimpleNode n) {
		return comp.get(n).stream().mapToDouble((e)->e.weight()).sum();
	}
	
	public List<SimpleNode> nodes(){
		return comp.keySet().stream().collect(Collectors.toList());
	}
	
	public List<SimpleNode> neighbors(SimpleNode n){
		return comp.get(n).stream()
							.map((edge)->edge.nodes().stream()
												.filter((k)->!k.equals(n))
												.findFirst()
												.get())
							.distinct()
							.collect(Collectors.toList());
	}
	
	public String toString() {
		return comp.toString();
	}
	public boolean equals(Object obj) {
		if(obj==null || !SimpleUndirectedGraph.class.isAssignableFrom(obj.getClass()))
			return false;
		
		final SimpleUndirectedGraph g = (SimpleUndirectedGraph) obj;
		return comp.keySet().containsAll(g.comp.keySet())
				&& comp.keySet().stream()
						.allMatch((k)->comp.get(k).containsAll(g.comp.get(k)));
	}
	public int hashCode() {
		return Objects.hash(comp, edges);
	}
}