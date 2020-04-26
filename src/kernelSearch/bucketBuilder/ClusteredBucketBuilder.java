package kernelSearch.bucketBuilder;

import java.util.*;
import java.util.stream.*;

import clustering.GreedyModularity;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import graph.SimpleUndirectedGraph;
import graph.UndirectedGraph;
import gurobi.*;
import gurobi.GRB.DoubleAttr;
import kernelSearch.Bucket;
import kernelSearch.Item;
import kernelSearch.Model;
import kernelSearch.ModelProperties;

/**
 * Define the behavior of Bucket Builders that build a list of Buckets
 * from clusters of these items.
 * Pattern: Template Method (circa)
 */
public abstract class ClusteredBucketBuilder implements BucketBuilder {
	
    @Override
    public List<Bucket> build(List<Item> items, List<Item> kernel, double bucketSize, ModelProperties config) { // NOTE: items does not contain kernel items
        //convert lists of items and kernel to maps
        Map<String, Item> itemsMap = items.stream().collect(Collectors.toMap(Item::getName, item -> item));
        Map<String, Item> kernelMap = kernel.stream().collect(Collectors.toMap(Item::getName, kernel_item -> kernel_item));
        MutableValueGraph<Item, Double> g = fromConstraintsToGraph(itemsMap, kernelMap, config);

        //call clustered Kernel Search to identify the clusters
        List<Set<Item>> clusters = GreedyModularity.extract(g);
        // convert clusters back into buckets
        List<Bucket> buckets = composeBuckets(clusters, items.size() + kernel.size());
        return buckets;
    }
    
    /**
     * Retrieve constraints from the model and convert them into an "adjacency map between variables"
     * @param kernel_items a list of items that are not in the kernel (out-of-base variables)
     * @param config problem's configuration
     * @return 
     */
    private MutableValueGraph<Item, Double> fromConstraintsToGraph(Map<String, Item> items, Map<String, Item> kernel_items, ModelProperties config){
    	GRBModel model = retrieveGurobiModel(config);
        List<PriorityQueue<Item>> constraints = extractConstraints(items, kernel_items, model);
    	return composeGraph(constraints);
    }

    /**
     * Read the constraints from the GRBModel, and convert them into a List<PriorityQueue<Item>>
     * @param items a map of the items that are NOT inside the kernel
     * @param kernel_items a map of the items in the kernel
     * @param model a fictitious GRBModel (from which we retrieve the constraints)
     * @return
     */
    private List<PriorityQueue<Item>> extractConstraints(Map<String, Item> items, Map<String, Item> kernel_items, GRBModel model){
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
     * Build a MutableValueGraph (using GUAVA library by Google: https://github.com/google/guava/wiki/GraphsExplained)
     * @param constraints
     * @return
     */
    //FIXME non termina in tempi ragionevoli (tuttavia termina commentando la riga g.putEdgeValue)
    private MutableValueGraph<Item, Double> composeGraph(List<PriorityQueue<Item>> constraints){
        long tStart = System.nanoTime();
        System.out.println("COMPOSING THE GRAPH...");
        MutableValueGraph<Item, Double> g = ValueGraphBuilder.undirected().build();
        for (PriorityQueue<Item> constraint : constraints) {
            while (constraint.size() > 1) {
                Item current = constraint.poll();
                for (Item item : constraint) {
                    g.putEdgeValue(current, item, 1.0);
                }
            }
        }
        long tEnd = System.nanoTime();
        System.out.println("GRAPH HAS BEEN CREATED in "+ (tEnd-tStart)/1000000  +"ms");
        return g;
    }


    /**
     * Retrieve a fictitious GRBModel that will be used to extract the constaints
     * @param config a configuration file containing the path to the problem instance file
     */
    private GRBModel retrieveGurobiModel(ModelProperties config) {
        Model model = new Model(config, 100000, true);
        model.buildModel();
        return model.getGRBModel();
    }

    
    /**
     * Compose a list of Buckets using the clusters of items found
     * @param clusters the list of items clusters
     * @param itemsN the total number of items: |kernel| + sum(|cluster|)
     * @return the list of Buckets
     */
    public abstract List<Bucket> composeBuckets(List<Set<Item>> clusters, int itemsN);
}
