package clustering;

import java.util.*;
import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.logging.SimpleFormatter;

//import graph.MapGraph;
import graph.Node;
import graph.UndirectedGraph;

/**
 * Implements the Greedy Modularity method proposed by Clauset, Newman and Moore
 * Reference: http://ece-research.unm.edu/ifis/papers/community-moore.pdf
 * https://networkx.github.io/documentation/stable/reference/algorithms/generated/networkx.algorithms.community.modularity_max.greedy_modularity_communities.html
 *
 */
public class OptimizedGreedyModularity {
	/**
	 * String to describe each merge
	 */
	private static String merge_format = "%d->%d(+%.2f)";
	private static final Logger log = Logger.getLogger(GreedyModularity.class.getName());
	
	/**
	 * Initialize the logger
	 */
	private static void initLogger() {
		log.setLevel(Level.FINE);
		try {
			// Output in a text file
//			FileHandler fh = new FileHandler("log.txt");
//			fh.setFormatter(new SimpleFormatter());
//			log.addHandler(fh);
			// No console
			log.setUseParentHandlers(false);
		}catch(Exception io) {
			System.out.println("Error in the Logger: "+io);
		}
		
	}
	/**
	 * Divides a Graph in clusters of nodes using the method proposed by Clauset, Newman and Moore
	 * @param g the graph to be analyzed
	 * @return the list of clusters found
	 */
	public static <N extends Node> List<Set<N>> extract(UndirectedGraph<N> g) {
		initLogger();
		log.info("start: GREEDY MODULARITY on "+g);
		
		double m = g.edgesN();
		double q0 = 1.0/(2.0*m);
		
		// Maps every node in an integer(just to simplify the use of nodes)
//		TreeMap<Integer, N> labelToNode = new TreeMap<Integer, N>(); // se necessario esiste gia' una mappa nome -> item all'interno di MapGrapBuilder.build()
//		HashMap<N, Integer> nodeToLabel = new HashMap<N, Integer>();
//		int index = 0;
//		for (N n: g.nodes()) {
//			labelToNode.put(index, n);
//			nodeToLabel.put(n, index);
//			index++;
//		}
		
		// Degree of each node
//		double[] k = IntStream.range(0, N).mapToDouble((i)->g.degree(labelToNode.get(i))).toArray();
		HashMap<N, Double> k = new HashMap<N, Double>();
		g.nodesStream().forEach((n)->k.put(n, g.degree(n)));
		
		// Initialize the communities and the Story of all the merges
		// At the start each node by itself is a community
//		TreeMap<Integer, HashSet<Integer>> communities = new TreeMap<Integer, HashSet<Integer>>();
//		labelToNode.keySet().stream().forEach((i)->{
//			communities.put(i, new HashSet<Integer>());
//			communities.get(i).add(i);
//		});
		HashMap<N, HashSet<N>> communities = new HashMap<N, HashSet<N>>();
		g.nodesStream().forEach((n)->{
			communities.put(n, new HashSet<N>());
			communities.get(n).add(n);
		});
		ArrayList<String> merges = new ArrayList<String>();
		
//		double[] a = IntStream.range(0, k.length).mapToDouble((i)->q0*k[i]).toArray();
		HashMap<N, Double> a = new HashMap<N, Double>();
		g.nodesStream().forEach((n)->a.put(n, q0*k.get(n)));
		
		// Contains the variations of Q related to the merge of 2 communities
		HashMap<N, HashMap<N, Double>> dq = new HashMap<N, HashMap<N, Double>>();
		g.nodesStream().forEach((n)->{
			dq.put(n, new HashMap<N, Double>());
			g.neighborsStream(n).forEach((n1)->{
				dq.get(n).put(n1, 2*q0 - 2*k.get(n)*k.get(n1)*q0*q0);
			});
		});
		
		// FIXME questo ciclo non termina con il problema eil33-2.mps, tuttavia termina con test.mps
//		for (int i = 0; i < N; i++) {
//			dq.put(i, new TreeMap<>());
//			final int finalI = i;
//			g.neighborsStream(labelToNode.get(i))
//					.mapToInt((n) -> nodeToLabel.get(n))
//					.forEach((j) -> dq.get(finalI).put(j, 2 * q0 - 2 * k[finalI] * k[j] * q0 * q0));
//		}

		// Contains, for each row of dQ, the max element. Log time to access the max of each row
//		HashMap<Integer, PriorityQueue<MatrixEntry>> dq_heap = new HashMap<Integer, PriorityQueue<MatrixEntry>>();
//		for (int idx = 0; idx < N; idx++) {
//			final int i1 = idx;
//			dq_heap.put(i1, new PriorityQueue<>((t1, t2) -> -t1.compareTo(t2)));
//			dq.get(i1).entrySet().stream()
//					.forEach((e) -> dq_heap.get(i1).add(new MatrixEntry(e.getValue(), i1, e.getKey())));
//		}
		HashMap<N, PriorityQueue<OptimizedMatrixEntry<N>>> dq_heap = new HashMap<N, PriorityQueue<OptimizedMatrixEntry<N>>>();
		g.nodesStream().forEach((n)->{
			dq_heap.put(n, new PriorityQueue<>((t1, t2) -> -t1.compareTo(t2)));
			dq.get(n).entrySet().stream()
					.forEach((e) -> dq_heap.get(n).add(new OptimizedMatrixEntry<N>(e.getValue(), n, e.getKey())));
		});
		
		// Contains all the row's maximum. Log time to access the max
		PriorityQueue<OptimizedMatrixEntry<N>> H = new PriorityQueue<OptimizedMatrixEntry<N>>((t1,t2)->-t1.compareTo(t2));
//		IntStream.range(0, N)
//					.filter((i)->dq_heap.get(i).size() > 0)
//					.forEach((i)->H.add(dq_heap.get(i).peek()));
		g.nodesStream()
			.filter((n)->dq_heap.get(n).size() > 0)
			.forEach((n)->H.add(dq_heap.get(n).peek()));
		
		// Initial modularity
		double Q = Modularity.nodesAsCommunities(a.values());
		
		log.info("Initial Q: "+Q+", H: "+H);
		// Till H is not empty
		while(H.size()>1) {
			// Heap update
			OptimizedMatrixEntry<N> best_t = H.poll();
			double dq_ij = best_t.value();
			N i = best_t.row();
			N j = best_t.col();
			
			dq_heap.get(i).poll();
			if (dq_heap.get(i).size()>0)
				H.add(dq_heap.get(i).peek());
			
			OptimizedMatrixEntry<N> symmetric_t = new OptimizedMatrixEntry<N>(dq_ij,j,i);
			if (dq_heap.get(j).peek().equals(symmetric_t)) {
				H.remove(symmetric_t);
				dq_heap.get(j).remove(symmetric_t);
				if (dq_heap.get(j).size()>0)
					H.add(dq_heap.get(j).peek());
			} else
				dq_heap.get(j).remove(symmetric_t);
			
			// Exit when the delta is not satisfactory
			if (dq_ij<=0)
				break;
			
			// Communities merge
			communities.get(j).addAll(communities.get(i));
			communities.remove(i);
			merges.add(String.format(merge_format,i,j,dq_ij));

			// Update of the modularity
			Q+=dq_ij;

			log.info("Key sets for i="+i+": "+dq.get(i).keySet()+" and for j="+j+": "+dq.get(j).keySet());
			
			// Update of dQ post merge of i to j
			Set<N> i_set = new HashSet<N>();
			i_set.addAll(dq.get(i).keySet());
			Set<N> j_set = new HashSet<N>();
			j_set.addAll(dq.get(j).keySet());
			Set<N> all_set = new HashSet<N>();
			all_set.addAll(i_set);
			all_set.addAll(j_set);
			all_set.remove(i);
			all_set.remove(j);
			Set<N> both_set = new HashSet<N>();
			both_set.addAll(i_set);
			both_set.retainAll(j_set);
			log.info("Set of keys: Union: "+all_set+", Intersection: "+both_set);
			
			all_set.stream().forEach((key)->{
				final double dq_jk;
				if(both_set.contains(key))
					dq_jk = dq.get(j).get(key) + dq.get(i).get(key);
				else if (dq.get(j).keySet().contains(key))
					dq_jk = dq.get(j).get(key) - 2.0*a.get(i)*a.get(key);
				else
					dq_jk = dq.get(i).get(key) - 2.0*a.get(j)*a.get(key);
				
				//update_rows(j, key, j_set, dq, dq_heap, H, dq_jk);
				log.info("Update index ("+j+", "+key+")");
				List<OptimizedEntry<N>> toUpdate = new ArrayList<OptimizedEntry<N>>();
				toUpdate.add(new OptimizedEntry<N>(j, key));
				toUpdate.add(new OptimizedEntry<N>(key, j));
				toUpdate.stream().forEach((e)->{
					final OptimizedMatrixEntry<N> d_old;
					if(j_set.contains(key))
						d_old = new OptimizedMatrixEntry<N>(dq.get(e.row()).get(e.col()), 
													e.row(),
													e.col());
					else
						d_old = null;
					dq.get(e.row()).put(e.col(), dq_jk);
					
					final OptimizedMatrixEntry<N> d_oldmax;
					if(dq_heap.get(e.row()).size()>0)
						d_oldmax = dq_heap.get(e.row()).peek();
					else
						d_oldmax = null;
					//update_heaps
					OptimizedMatrixEntry<N> d = new OptimizedMatrixEntry<N>(dq_jk, e.row(), e.col());
					log.info("New entry: "+d);
					if(d_old==null)
						dq_heap.get(e.row()).add(d);
					else {
						dq_heap.get(e.row()).remove(d_old);
						dq_heap.get(e.row()).add(d);
					}
					
					if(d_oldmax==null)
						H.add(d);
					else if(!dq_heap.get(e.row()).peek().equals(d_oldmax)) {
						H.remove(d_oldmax);
						H.add(dq_heap.get(e.row()).peek());
					}
				});
				log.info(dq.toString());
			});
			
			//Remove row/col i from matrix
			//remove_i(i, j, dq, dq_heap, H);
			log.info("Removal of row "+i);
			Set<N> i_neighbors = dq.get(i).keySet();
			i_neighbors.stream()
						.forEach((key)->{
							double dq_old = dq.get(key).get(i);
							dq.get(key).remove(i);
							if(key!=j) {
								log.info("Correction of Heap["+key+"]");
								List<OptimizedEntry<N>> toRemove = new ArrayList<OptimizedEntry<N>>();
								toRemove.add(new OptimizedEntry<N>(key, i));
								toRemove.add(new OptimizedEntry<N>(i, key));
								toRemove.stream()
											.forEach((e)->{
												OptimizedMatrixEntry<N> d_old = new OptimizedMatrixEntry<N>(dq_old, 
																					e.row(),
																					e.col());
												if(dq_heap.get(e.row()).peek().equals(d_old)) {
													dq_heap.get(e.row()).remove(d_old);
													H.remove(d_old);
													if(dq_heap.get(e.row()).size()>0)
														H.add(dq_heap.get(e.row()).peek());
												} else
													dq_heap.get(e.row()).remove(d_old);
											});
							}
						});
			dq.remove(i);
			dq_heap.put(i, new PriorityQueue<OptimizedMatrixEntry<N>>((t1,t2)->-t1.compareTo(t2)));
			a.put(j, a.get(j) + a.get(i));
			a.put(i, 0.0);
			log.info("Iteration end(Q:"+Q+"): merges : "+merges+", communities: "+communities+", H: "+H);
		}
		return communities.values()
							.stream()
							.collect(Collectors.toList());
	}
}
