package greedyModularity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import graph.Graph;
import graph.Node;

/**
 * Implements the Greedy Modularity method proposed by Clauset, Newman and Moore
 * Reference: http://ece-research.unm.edu/ifis/papers/community-moore.pdf
 * https://networkx.github.io/documentation/stable/reference/algorithms/generated/networkx.algorithms.community.modularity_max.greedy_modularity_communities.html
 * @author Matteo
 *
 */
public class GreedyModularity {
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
	public static List<Set<Node>> extract(Graph g) {
		initLogger();
		log.info("start: GREEDY MODULARITY on "+g);
		
		int N = g.nodes().size();
		double m = g.edgesN();
		double q0 = 1.0/(2.0*m);
		
		// Maps every node in an integer(just to simplify the use of nodes)
		TreeMap<Integer,Node> labelToNode = new TreeMap<Integer,Node>();
		HashMap<Node,Integer> nodeToLabel = new HashMap<Node,Integer>();
		IntStream.range(0, N).forEach((i)->{
			labelToNode.put(i, g.nodes().get(i));
			nodeToLabel.put(g.nodes().get(i), i);
		});
		
		// Degree of each node
		double[] k = IntStream.range(0, N).mapToDouble((i)->g.degree(labelToNode.get(i))).toArray();
		
		// Initialize the communities and the Story of all the merges
		// At the start each node by itself is a community
		TreeMap<Integer, HashSet<Integer>> communities = new TreeMap<Integer, HashSet<Integer>>();
		labelToNode.keySet().stream().forEach((i)->{
			communities.put(i, new HashSet<Integer>());
			communities.get(i).add(i);
		});
		ArrayList<String> merges = new ArrayList<String>();
		
		double[] a = IntStream.range(0, k.length).mapToDouble((i)->q0*k[i]).toArray();

		// Contains the variations of Q related to the merge of 2 communities
		TreeMap<Integer, TreeMap<Integer,Double>> dq = new TreeMap<Integer, TreeMap<Integer,Double>>();
		IntStream.range(0, N)
			.forEach((i)->{
				dq.put(i, new TreeMap<Integer, Double>());
				g.neighbors(labelToNode.get(i))
					.stream()
					.filter((n)->!n.equals(labelToNode.get(i)))
					.mapToInt((n)->nodeToLabel.get(n))
					.forEach((j)->dq.get(i).put(j, 2*q0 - 2*k[i]*k[j]*q0*q0));
			});
		
		// Contains, for each row of dQ, the max element. Log time to access the max of each row
		TreeMap<Integer,PriorityQueue<MatrixEntry>> dq_heap = new TreeMap<Integer,PriorityQueue<MatrixEntry>>();
		IntStream.range(0, N)
					.forEach((i)->{
						dq_heap.put(i, new PriorityQueue<MatrixEntry>((t1,t2)->-t1.compareTo(t2)));
						dq.get(i).entrySet().stream()
									.forEach((e)->dq_heap.get(i).add(new MatrixEntry(e.getValue(), i, e.getKey())));
						});

		// Contains all the row's maximum. Log time to access the max
		PriorityQueue<MatrixEntry> H = new PriorityQueue<MatrixEntry>((t1,t2)->-t1.compareTo(t2));
		IntStream.range(0, N)
					.filter((i)->dq_heap.get(i).size() > 0)
					.forEach((i)->H.add(dq_heap.get(i).peek()));
		
		// Initial modularity
		double Q = Modularity.nodesAsCommunities(a);
		
		log.info("Initial Q: "+Q+", H: "+H);
		// Till H is not empty
		while(H.size()>1) {
			// Heap update
			MatrixEntry best_t = H.poll();
			double dq_ij = best_t.value();
			int i = best_t.row();
			int j = best_t.col();
			
			dq_heap.get(i).poll();
			if (dq_heap.get(i).size()>0)
				H.add(dq_heap.get(i).peek());
			
			MatrixEntry symmetric_t = new MatrixEntry(dq_ij,j,i);
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
			Set<Integer> i_set = new HashSet<Integer>();
			i_set.addAll(dq.get(i).keySet());
			Set<Integer> j_set = new HashSet<Integer>();
			j_set.addAll(dq.get(j).keySet());
			Set<Integer> all_set = new HashSet<Integer>();
			all_set.addAll(i_set);
			all_set.addAll(j_set);
			all_set.remove(i);
			all_set.remove(j);
			Set<Integer> both_set = new HashSet<Integer>();
			both_set.addAll(i_set);
			both_set.retainAll(j_set);
			log.info("Set of keys: Union: "+all_set+", Intersection: "+both_set);
			
			all_set.stream().forEach((key)->{
				final double dq_jk;
				if(both_set.contains(key))
					dq_jk = dq.get(j).get(key) + dq.get(i).get(key);
				else if (dq.get(j).keySet().contains(key))
					dq_jk = dq.get(j).get(key) - 2.0*a[i]*a[key];
				else
					dq_jk = dq.get(i).get(key) - 2.0*a[j]*a[key];
				
				//update_rows(j, key, j_set, dq, dq_heap, H, dq_jk);
				log.info("Update index ("+j+", "+key+")");
				List<Entry> toUpdate = new ArrayList<Entry>();
				toUpdate.add(new Entry(j, key));
				toUpdate.add(new Entry(key, j));
				toUpdate.stream().forEach((e)->{
					final MatrixEntry d_old;
					if(j_set.contains(key))
						d_old = new MatrixEntry(dq.get(e.row()).get(e.col()), 
													e.row(),
													e.col());
					else
						d_old = null;
					dq.get(e.row()).put(e.col(), dq_jk);
					
					final MatrixEntry d_oldmax;
					if(dq_heap.get(e.row()).size()>0)
						d_oldmax = dq_heap.get(e.row()).peek();
					else
						d_oldmax = null;
					//update_heaps
					MatrixEntry d = new MatrixEntry(dq_jk, e.row(), e.col());
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
			Set<Integer> i_neighbors = dq.get(i).keySet();
			i_neighbors.stream()
						.forEach((key)->{
							double dq_old = dq.get(key).get(i);
							dq.get(key).remove(i);
							if(key!=j) {
								log.info("Correction of Heap["+key+"]");
								List<Entry> toRemove = new ArrayList<Entry>();
								toRemove.add(new Entry(key, i));
								toRemove.add(new Entry(i, key));
								toRemove.stream()
											.forEach((e)->{
												MatrixEntry d_old = new MatrixEntry(dq_old, 
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
			dq_heap.put(i, new PriorityQueue<MatrixEntry>((t1,t2)->-t1.compareTo(t2)));
			a[j] += a [i];
			a[i] = 0.0;
			log.info("Iteration end(Q:"+Q+"): merges : "+merges+", communities: "+communities+", H: "+H);
		}
		return communities.values()
							.stream()
							.map((c)->c.stream()
										.map((label)->labelToNode.get(label))
										.collect(Collectors.toSet()))
							.collect(Collectors.toList());
	}
}
