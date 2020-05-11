package graph;

import gurobi.*;
import kernelSearch.Item;
import kernelSearch.Model;
import kernelSearch.ModelProperties;

import java.util.*;

/**
 * Class to build a graph from an adjacency matrix
 *
 */
public class MapGraphBuilder {
	/**
	 * Retrieve constraints from the model and convert them into an "adjacency map between variables"
	 * @param config problem's configuration
	 * @return
	 */
	public static UndirectedGraph<Item> build(Map<String, Item> items, ModelProperties config){
		GRBModel model = retrieveGurobiModel(config);
		List<PriorityQueue<Item>> constraints = extractConstraints(items, model);
		return composeGraph(constraints, items, config);
	}

	/**
	 * Read the constraints from the GRBModel, and convert them into a List<PriorityQueue<Item>>
	 * @param items a map of the items that are NOT inside the kernel
	 * @param model a fictitious GRBModel (from which we retrieve the constraints)
	 * @return
	 */
	private static List<PriorityQueue<Item>> extractConstraints(Map<String, Item> items, GRBModel model){
		List<PriorityQueue<Item>> constraints = new ArrayList<PriorityQueue<Item>>();

		// iterate over all the constraints in the model
		for (GRBConstr c : model.getConstrs()) {
			try {
				GRBLinExpr e = model.getRow(c); //retrieve Linear Expression e corresponding to constraint c
				PriorityQueue<Item> constraint = new PriorityQueue<Item>();
				for (int i = 0; i < e.size(); i++) {
					GRBVar var = e.getVar(i); // retrieve i-th variable of the LinExpr e
					String var_name = var.get(GRB.StringAttr.VarName);
					Item it = items.get(var_name);
					if (it != null)
						constraint.add(it);
				}
				if(constraint.size() > 0)
					constraints.add(constraint);
			} catch (GRBException ex) {
				ex.printStackTrace();
			}
		}
		return constraints;
	}

	/**
	 * Build a graph that consists in a map (node -> adjacent nodes)
	 * @param constraints the list of constraints
	 * @param items the map (item_name -> item_obj) of out-of-basis items
	 * @return a map (node -> [set of adjacent nodes]
	 */
	private static UndirectedGraph<Item> composeGraph(List<PriorityQueue<Item>> constraints, Map<String, Item> items, ModelProperties config) {
		UndirectedGraph<Item> graph = new MapGraph<>();
		// Initialize HashSets
		for (Item item : items.values())
			graph.insertNode(item);

		// Filling HashSets
		for (PriorityQueue<Item> constraint : constraints)
			for (Item v1 : constraint)
				for (Item v2 : constraint)
					if (v1.compareTo(v2) < 0) {
						int previousWeight = graph.weightOfEdge(v1, v2);
						graph.connect(v1, v2, previousWeight + 1);
					}
		
		System.out.printf("Number of nodes: %10d\n", graph.nodes().size());
		System.out.printf("Number of edges: %10.0f\n", graph.edgesN());
		
		// Cleaning graph from unnecessary links
		pruneGraph(graph, config.maxGraphSize());

		System.out.printf("Number of nodes after cleaning: %10d\n", graph.nodes().size());
		System.out.printf("Number of edges after cleaning: %10.0f\n", graph.edgesN());
		
		return graph;
	}

	/**
	 * Retrieve a fictitious GRBModel that will be used to extract the constaints
	 * @param config a configuration file containing the path to the problem instance file
	 */
	private static GRBModel retrieveGurobiModel(ModelProperties config) {
		Model model = new Model(config, 100000, true);
		model.buildModel();
		return model.getGRBModel();
	}

	/**
	 * Remove edges whose weight does not exceed a certain threshold, incrementing the threshold until the graph
	 * has less than maxGraphEdges edges.
	 * @param g the graph to prune
	 * @param maxGraphEdges the maximum number of edges it can have
	 */
	private static void pruneGraph(UndirectedGraph g, int maxGraphEdges) {
		int threshold = (int) Math.round(g.getM() / g.edgesN()); // average weight
		while (g.edgesN() > maxGraphEdges) {
			g.eraseAllEdgesUnder(threshold);
			threshold++;
		}
	}
}