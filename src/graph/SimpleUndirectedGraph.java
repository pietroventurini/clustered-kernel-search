package graph;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.HashSet;

public class SimpleUndirectedGraph<N> implements UndirectedGraph<N> {
	
	private HashMap<N, HashSet<Edge<N>>> comp;
	private double edges;
	
	/**
	 * Constructor.
	 * Inits the map of nodes and edges.
	 */
	public SimpleUndirectedGraph() {
		this.comp = new HashMap<N, HashSet<Edge<N>>>();
		this.edges=0;
	}
	
	@Override
	public double edgesNumber() {
		return edges;
	}
	
	@Override
	public double degreeOf(N node) {
		return comp.get(node).stream().mapToDouble((e)->e.weight()).sum();
	}
	
	@Override
	public int neighborsNumberOf(N node) {
		if (!this.comp.containsKey(node)) {
			return 0;
		}
		return comp.get(node).size();
	}
	
	@Override
	public List<N> nodes() {
		return comp.keySet().stream().collect(Collectors.toList());
	}
	
	public boolean addNode(N n) {
		if(!comp.containsKey(n)) {
			comp.put(n, new HashSet<Edge<N>>());
			return true;
		}
		return false;
	}
	
	private boolean connect(N n, N m, double weight, String label) {
		if (!comp.containsKey(n) || !comp.containsKey(m))
			return false;

		N n_act = comp.keySet().stream()
									.filter((k)->k.equals(n))
									.findFirst().get();
		N m_act = comp.keySet().stream()
									.filter((k)->k.equals(m))
									.findFirst().get();
		Edge<N> e = new Edge<N>(n_act, m_act, weight, label);
		if(neighborsOf(n).contains(m)) {
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
	
	public boolean connect(N n, N m, double weight) {
		return connect(n, m, weight, "");
	}
	
	public List<N> neighborsOf(N n){
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
	
	@Override
	public boolean removeNode(N node) {
		this.comp.remove(node);
		// TODO FIXME Sistemare questo metodo
		return false;
	}
	
	@Override
	public boolean disconnect(N first, N second) {
		// TODO FIXME Sistemare questo metodo
		return false;
	}
	@Override
	public boolean areConnected(N first, N second) {
		// TODO FIXME Sistemare questo metodo
		return false;
	}
	@Override
	public double getConnectionWeight(N first, N second) {
		// TODO FIXME Sistemare questo metodo
		return 0;
	}
}