package kernelSearch.bucketBuilder;

import java.util.*;
import java.util.stream.*;

import clustering.GreedyModularity;
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
        // Map<String, Set<String>> associations = fromConstraintsToAssociations(items, config);
        UndirectedGraph<Item> g = fromConstraintsToGraph(items, kernel, config);
        //UndirectedGraph<Item> g = fromConstraintsToGraph(items, GRBModel);
        //visualizeAssociations(associations);
        
        //TODO call clustered Kernel Search to identify the clusters
        //TODO convert clusters back into buckets
        List<Set<Item>> clusters = GreedyModularity.extract(g);
        
        List<Bucket> buckets = composeBuckets(clusters, items.size() + kernel.size());
        return buckets;
    }
    
    /**
     * Retrieve constraints from the model and convert them into an "adjacency map between variables"
     * @param kernel_items a list of items that are not in the kernel (out-of-base variables)
     * @param config problem's configuration
     * @return 
     */
    private UndirectedGraph<Item> fromConstraintsToGraph(List<Item> items, List<Item> kernel_items, ModelProperties config){
    	GRBModel model = retrieveGurobiModel(config);
        List<PriorityQueue<Item>> constraints = extractConstraints(items, kernel_items, model);
    	return composeGraph(constraints);
    }
    
    private List<PriorityQueue<Item>> extractConstraints(List<Item> items, List<Item> kernel_items, GRBModel model){
        List<PriorityQueue<Item>> constraints = new ArrayList<PriorityQueue<Item>>(); // list of lists of strings
        
        // iterate over all the constraints in the model
        for (GRBConstr c : model.getConstrs()) {
            try {
                GRBLinExpr e = model.getRow(c); //retrieve Linear Expression e corresponding to constraint c
                PriorityQueue<Item> constraint = new PriorityQueue<Item>();
                for (int i = 0; i < e.size(); i++) {
                    GRBVar var = e.getVar(i); // retrieve i-th variable of the LinExpr e
                    Item var_item = new Item(var.get(GRB.StringAttr.VarName), var.get(DoubleAttr.X), var.get(DoubleAttr.RC));
                    if(items.contains(var_item))
                    	constraint.add(items.stream().filter(item->item.equals(var_item)).findAny().get());
                }
                if(constraint.size() > 0)
                	constraints.add(constraint);
            } catch (GRBException ex) {
                ex.printStackTrace();
            }
        }
    	return constraints;
    }
    
    private UndirectedGraph<Item> composeGraph(List<PriorityQueue<Item>> constraints){
    	SimpleUndirectedGraph<Item> g = new SimpleUndirectedGraph<Item>();
    	IntStream.range(0, constraints.size())
    				.forEach(i->{
    					while(constraints.get(i).size()>1) {
    						Item current = constraints.get(i).poll();
    						g.add_node(current);
    						constraints.get(i).stream()
    											.forEach(item->{
    												g.add_node(item);
    												g.add_edge(current, item);
    											});
    					}
    				});
    	return g;
    }
    /**
     * Retrieve constraints from the model and convert them into an "adjacency map between variables"
     * @param kernel_items a list of items that are not in the kernel (out-of-base variables)
     * @param config problem's configuration
     * @return a map of the form <K: variable, V: set of variables that join a constraint with variable K>
     */
    private Map<String, Set<String>> fromConstraintsToAssociations(List<Item> kernel_items, ModelProperties config) {
        GRBModel model = retrieveGurobiModel(config);
        List<ArrayList<String>> constraints = readAllConstraints(model);
        removeKernelVarsFromConstraints(constraints, kernel_items);
        Map<String, Set<String>> associations = findAssociationsBetweenVars(constraints);
        return associations;
    }

    private GRBModel retrieveGurobiModel(ModelProperties config) {
        Model model = new Model(config, 100000, true);
        model.buildModel();
        return model.getGRBModel();
    }
    
    /**
     * Analyzes the Gurobi's model converting its constraints into lists of variables' names
     * @param model Gurobi's model
     * @return a list of constraints, where each constraint is represented by a list of variables, and each variables
     * is identified by its name
     */
    private List<ArrayList<String>> readAllConstraints(GRBModel model) {
        List<ArrayList<String>> constraints = new ArrayList<>(); // list of lists of strings

        // iterate over all the constraints in the model
        for (GRBConstr c : model.getConstrs()) {
            try {
                GRBLinExpr e = model.getRow(c); //retrieve Linear Expression e corresponding to constraint c
                ArrayList<String> constraint = new ArrayList<>();
                for (int i = 0; i < e.size(); i++) {
                    GRBVar var = e.getVar(i); // retrieve i-th variable of the LinExpr e
                    String var_name = var.get(GRB.StringAttr.VarName); // retrieve name of variable var
                    constraint.add(var_name);
                }
                constraints.add(constraint);
            } catch (GRBException ex) {
                ex.printStackTrace();
            }
        }
        return constraints;
    }

    /**
     * For each constraint, filter its variables by keeping only the ones that are contained into items (not the kernel ones)
     * @param constraints
     * @param items list of the items outside the kernel (out-of-base variables)
     */
    private void removeKernelVarsFromConstraints(List<ArrayList<String>> constraints, List<Item> items) {
        constraints.forEach(c -> c.retainAll(items.stream()
                                                    .map(Item::getName)
                                                    .collect(Collectors.toList())
                                            )
                            );
    }

    /**
     * Analyzes a list of constraints and returns a map <K: variable, V: set of variables that join a constraint with K>
     * @param constraints a list of constraints. Each constraint is represented by a list of strings,
     *                    where each string is the name of a variable involved in that constraint
     * @return a map of the form <K: variable, V: set of variables that join a constraint with variable K>
     */
    private Map<String, Set<String>> findAssociationsBetweenVars(List<ArrayList<String>> constraints) {
        Map<String, Set<String>> associations = new HashMap<>();

        // implementazione poco elegante e costosa
        for (ArrayList<String> constraint : constraints) {
            for (String v1 : constraint) {
                associations.putIfAbsent(v1, new HashSet<>());
                for (String v2 : constraint)
                    if (!v1.equals(v2))
                        associations.get(v1).add(v2);
            }
        }

        return associations;
    }

    private void visualizeAssociations(Map<String, Set<String>> associations) {
        System.out.println("VISUALIZING ASSOCIATIONS BETWEEN VARIABLES");
        associations.entrySet().forEach(v ->{
            System.out.println(v.getKey() + " is related with " + v.getValue());
        });
    }
    
    /**
     * Compose a list of Buckets using the clusters of items found
     * @param clusters the list of items clusters
     * @param itemsN the total number of items: |kernel| + sum(|cluster|)
     * @return the list of Buckets
     */
    public abstract List<Bucket> composeBuckets(List<Set<Item>> clusters, int itemsN);
}
