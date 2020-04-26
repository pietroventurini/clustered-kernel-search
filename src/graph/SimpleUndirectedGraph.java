package graph;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleUndirectedGraph<N extends Node> implements UndirectedGraph<N>{
	private Map<String, N> nodes;
	private HashMap<N, HashSet<Edge<N>>> comp;
	private double edges;
	
	public SimpleUndirectedGraph() {
		init();
	}
	private void init() {
		this.nodes = new HashMap<>();
		this.comp = new HashMap<N, HashSet<Edge<N>>>();
		this.edges=0;
	}
	
	public boolean add_node(N n) {
		if(!nodes.containsKey(n.getName())) {
			nodes.put(n.getName(), n);
			comp.put(n, new HashSet<Edge<N>>());
			return true;
		}
		return false;
	}

	private boolean add_new_edge(N n, N m, double weight, String label) {
		if(!nodes.containsKey(n.getName()) || !nodes.containsKey(m.getName()))
			return false;

		N n_act = nodes.get(n.getName());
		N m_act = nodes.get(m.getName());
		Edge<N> e = new Edge<N>(n_act, m_act, weight, label);

		if(neighbors(n).contains(m)) {
			Edge<N> e_new = new Edge<N>(n_act,
					m_act,
					weight + comp.get(n).stream()
										.filter(edge->edge.equals(e))
										.findAny().get().weight(),
					label);
			comp.get(n).remove(e);
			comp.get(m).remove(e);
			comp.get(n).add(e_new);
			comp.get(m).add(e_new);
		} else {
			comp.get(n).add(e);
			comp.get(m).add(e);
		}

		edges += weight;
		return true;
	}
	/*
	La complessità di add_new_edge dovrebbe essere lineare:
	containsKey			O(1)
	filter.findFirst	O(n)
	neighbors			O(n)
	contains			O(n)
	filter.findAny		O(n)
	get/remove/add		O(1)

	private boolean add_new_edge(N n, N m, double weight, String label) {
		if(!comp.containsKey(n) || !comp.containsKey(m))
			return false;

		N n_act = comp.keySet().stream()
									.filter((k)->k.equals(n))
									.findFirst().get();
		N m_act = comp.keySet().stream()
									.filter((k)->k.equals(m))
									.findFirst().get();
		Edge<N> e = new Edge<N>(n_act, m_act, weight, label);

		if(neighbors(n).contains(m)) {
			Edge<N> e_new = new Edge<N>(n_act, 
										m_act, 
										weight + comp.get(n).stream()
														.filter(edge->edge.equals(e))
														.findAny().get().weight(), 
										label);
			comp.get(n).remove(e);
			comp.get(m).remove(e);
			comp.get(n).add(e_new);
			comp.get(m).add(e_new);
		}else {
			comp.get(n).add(e);
			comp.get(m).add(e);
		}
		edges+=weight;
		return true;
	}
	 */

	public boolean add_edge(N n, N m, double weight) {
		return add_new_edge(n, m, weight, "");
	}
	public boolean add_edge(N n, N m) {
		return add_new_edge(n, m, 1.0, "");
	}
	
	public double edgesN() {
		return edges;
	}
	
	public double degree(N n) {
		return comp.get(n).stream().mapToDouble((e)->e.weight()).sum();
	}
	
	public List<N> nodes(){
		return comp.keySet().stream().collect(Collectors.toList());
	}


	public List<N> neighbors(N n) {
		List<N> nodes = new ArrayList<>();
		for (Edge<N> e : comp.get(n))
			for (N node : e.nodes())
				if (!node.equals(n))
					nodes.add(node);
		return nodes.stream().distinct().collect(Collectors.toList());
	}

	/*
	public List<N> neighbors(N n){
		return comp.get(n).stream()
							.map((edge)->edge.nodes().stream()
												.filter((k)->!k.equals(n))
												.findFirst()
												.get())
							.distinct()
							.collect(Collectors.toList());
	}*/
	
	public String toString() {
		return comp.toString();
	}
	public boolean equals(Object obj) {
		if(obj==null || !SimpleUndirectedGraph.class.isAssignableFrom(obj.getClass()))
			return false;
		try {
			@SuppressWarnings("unchecked")
			final SimpleUndirectedGraph<N> g = (SimpleUndirectedGraph<N>) obj;
			return comp.keySet().containsAll(g.comp.keySet())
					&& comp.keySet().stream()
							.allMatch((k)->comp.get(k).containsAll(g.comp.get(k)));
		}catch(ClassCastException e) {
			return false;
		}
	}
	public int hashCode() {
		return Objects.hash(comp, edges);
	}
}