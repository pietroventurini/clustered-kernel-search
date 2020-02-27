import gurobi.*;

import java.util.*;
import java.util.stream.Collectors;

public class ClusteredBucketBuilder implements BucketBuilder {

    @Override
    public List<Bucket> build(List<Item> items, Configuration config) { // NOTE: items does not contain kernel items


        Map<String, Set<String>> associations = fromConstraintsToAssociations(items, config);
        visualizeAssociations(associations);

        //TODO call clustered Kernel Search to identify the clusters

        //TODO convert clusters back into buckets
        List<Bucket> buckets = new ArrayList<>();

        return buckets;
    }


    private GRBModel retrieveGurobiModel(Configuration config) {
        Model model = new Model(config.getInstPath(), config.getLogPath(), config.getTimeLimit(), config, true); // time limit equal to the global time limit
        model.buildModel();
        return model.getGRBModel();
    }

    /**
     * Retrieve constraints from the model and convert them into an "adjacency map between variables"
     * @param items a list of items that are not in the kernel (out-of-base variables)
     * @param config problem's configuration
     * @return a map of the form <K: variable, V: set of variables that join a constraint with variable K>
     */
    private Map<String, Set<String>> fromConstraintsToAssociations(List<Item> items, Configuration config) {
        GRBModel model = retrieveGurobiModel(config);
        List<ArrayList<String>> constraints = readAllConstraints(model);
        removeKernelVarsFromConstraints(constraints, items);
        Map<String, Set<String>> associations = findAssociationsBetweenVars(constraints);
        return associations;
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
}
