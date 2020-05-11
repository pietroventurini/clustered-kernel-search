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
 * 
 */
public class LouvainModularity {
	/**
	 * String to describe each merge
	 */
	//private static String merge_format = "%s->%s(+%.2f)";
	private static final Logger log = Logger.getLogger(GreedyModularity.class.getName());
	
	/**
	 * Initialize the logger
	 */
	private static void initLogger() {
//		log.setLevel(Level.FINE);
		log.setLevel(Level.OFF);
		try {
			// Output in a text file
//			FileHandler fh = new FileHandler("log.txt");
//			fh.setFormatter(new SimpleFormatter());
//			log.addHandler(fh);
			// No console
			//log.setUseParentHandlers(false);
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
		log.info("start: LOUVAIN MODULARITY on "+g);
		
		return null;
	}
}
