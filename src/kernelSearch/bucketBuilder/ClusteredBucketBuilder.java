package kernelSearch.bucketBuilder;

import java.util.*;
import java.util.stream.*;

import clustering.GreedyModularity;
import graph.MapGraphBuilder;
import graph.UndirectedGraph;
import kernelSearch.Bucket;
import kernelSearch.Item;
import kernelSearch.ModelProperties;

/**
 * Define the behavior of Bucket Builders that build a list of Buckets
 * from clusters of these items.
 * Pattern: Template Method (circa)
 */
public abstract class ClusteredBucketBuilder implements BucketBuilder {
	
    @Override
    public List<Bucket> build(List<Item> items, List<Item> kernel, double bucketSize, ModelProperties config) { // NOTE: items does not contain kernel items
        //convert lists of items and kernel to maps (item_name -> item_object)
        Map<String, Item> itemsMap = items.stream().collect(Collectors.toMap(Item::getName, item -> item));
        Map<String, Item> kernelMap = kernel.stream().collect(Collectors.toMap(Item::getName, kernel_item -> kernel_item));

        System.out.println("COMPOSING THE GRAPH...");
        long tStart = System.nanoTime();
        UndirectedGraph<Item> g = MapGraphBuilder.build(itemsMap, kernelMap, config);
        System.out.println("GRAPH HAS BEEN CREATED in "+ (System.nanoTime() - tStart)/1000000  +"ms");

        //call clustered Kernel Search to identify the clusters
        List<Set<Item>> clusters = GreedyModularity.extract(g);
        // convert clusters back into buckets
        List<Bucket> buckets = composeBuckets(clusters, items.size() + kernel.size());
        return buckets;
    }
    


    /**
     * Compose a list of Buckets using the clusters of items found
     * @param clusters the list of items clusters
     * @param itemsN the total number of items: |kernel| + sum(|cluster|)
     * @return the list of Buckets
     */
    public abstract List<Bucket> composeBuckets(List<Set<Item>> clusters, int itemsN);
}
