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
    public List<Bucket> build(List<Item> items, List<Item> kernel, double bucketSize, ModelProperties config) { // NOTE: items does NOT contain kernel items
        //convert lists of items and kernel to maps (item_name -> item_object)
        Map<String, Item> itemsMap = items.stream().collect(Collectors.toMap(Item::getName, item -> item));

        System.out.println("COMPOSING THE GRAPH...");
        long tStart = System.nanoTime();
        UndirectedGraph<Item> g = MapGraphBuilder.build(itemsMap, config);
        System.out.println("GRAPH HAS BEEN CREATED in "+ (System.nanoTime() - tStart)/1000000  +"ms");

        // call clustered Kernel Search to identify the clusters
        System.out.println("CLUSTERING...");
        tStart = System.nanoTime();
        List<Set<Item>> clusters = GreedyModularity.extract(g);
        System.out.println("CLUSTERING COMPLETED in "+ (System.nanoTime() - tStart)/1000000  +"ms");
        
        // Logging to StdOut
        System.out.println("\nCLUSTERING INFO:");
        System.out.printf("\tNumber of kernel items: %d\n", kernel.size());
        System.out.printf("\tNumber of out-of-kernel items: %d\n", items.size());
        System.out.printf("\tNumber of generated clusters: %d\n", clusters.size());
        double averageClusterSize = clusters.stream().mapToInt(cluster -> cluster.size()).sum() / (double)clusters.size(); 
        System.out.printf("\tAverage cluster size: %.3f\n\n", averageClusterSize);

        // convert clusters back into buckets
        List<Bucket> buckets = composeBuckets(clusters, items.size() + kernel.size());
        System.out.println("BUCKET BUILDING INFO:");
        System.out.printf("\tNumber of generated buckets: %d\n", buckets.size());      
        double averageBucketSize = clusters.stream().mapToInt(bucket -> bucket.size()).sum() / (double)buckets.size(); 
        System.out.printf("\tAverage bucket size: %.3f\n", averageBucketSize); 
        System.out.printf("\tExpected relative bucket size: %f\n", bucketSize);
        System.out.printf("\tExpected absolute bucket size: %f\n\n", bucketSize * kernel.size());

        handle1SizedBuckets(buckets);
        System.out.println("HANDLING 1 SIZED BUCKETS:");
        System.out.printf("\tNumber of total buckets: %d\n", buckets.size()); 
        return buckets;
    }
    

    /**
     * Compose a list of Buckets using the clusters of items found
     * @param clusters the list of items clusters
     * @param itemsN the total number of items: |kernel| + sum(|cluster|)
     * @return the list of Buckets
     */
    public abstract List<Bucket> composeBuckets(List<Set<Item>> clusters, int itemsN);
    
    /**
     * Incorporates eventual 1 sized buckets into the other buckets.
     * Distribute the 1 sized buckets' items as fairly as possible.
     * The first buckets will receive the most profitable items.
     * @param buckets the list of buckets
     * @return the buckets with an acceptable size
     */
    private void handle1SizedBuckets(List<Bucket> buckets){
    	PriorityQueue<Item> outsiders = new PriorityQueue<Item>(
                Comparator.comparingDouble(Item::getRc)
    			);
    	buckets.stream()
    			.filter((b)->b.size() == 1)
    			.forEach((b)->outsiders.add(b.getItems().get(0)));
    	buckets.removeIf((bucket)->bucket.size() == 1);
    	int i = 0;
    	while(!outsiders.isEmpty()) {
    		buckets.get(i%buckets.size()).addItem(outsiders.poll());
    		i++;
    	}
    	return;
    }
}
