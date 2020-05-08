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
	 * @param kernel_items a list of items that are not in the kernel (out-of-base variables)
	 * @param config problem's configuration
	 * @return
	 */
	public static UndirectedGraph<Item> build(Map<String, Item> items, Map<String, Item> kernel_items, ModelProperties config){
		GRBModel model = retrieveGurobiModel(config);
		List<PriorityQueue<Item>> constraints = extractConstraints(items, kernel_items, model);
		return composeGraph(constraints, items); // valutare se convertire in Map<Item, Set<Item>>
	}

	/**
	 * Read the constraints from the GRBModel, and convert them into a List<PriorityQueue<Item>>
	 * @param items a map of the items that are NOT inside the kernel
	 * @param kernel_items a map of the items in the kernel
	 * @param model a fictitious GRBModel (from which we retrieve the constraints)
	 * @return
	 */
	private static List<PriorityQueue<Item>> extractConstraints(Map<String, Item> items, Map<String, Item> kernel_items, GRBModel model){
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
	 * Build a graph that consists in a map (node -> [set of adjacent nodes])
	 * @param constraints
	 * @return
	 */
	private static MapGraph<Item> composeGraph(List<PriorityQueue<Item>> constraints, Map<String, Item> items) {
		Map<Item, Set<Item>> g = new HashMap<>();
		// Initialize HashSets
		for (Item item : items.values()) {
			g.put(item, new HashSet<>());
		}
		// Filling HashSets
		for (PriorityQueue<Item> constraint : constraints) {
			for (Item v1 : constraint) {
				for (Item v2 : constraint) {
					if (!v1.equals(v2)) {
						g.get(v1).add(v2);
					}
				}
			}
		}
		return new MapGraph<>(items, g);
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
}