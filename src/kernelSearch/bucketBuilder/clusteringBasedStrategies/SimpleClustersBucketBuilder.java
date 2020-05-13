package kernelSearch.bucketBuilder.clusteringBasedStrategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kernelSearch.Bucket;
import kernelSearch.bucketBuilder.ClusteredBucketBuilder;
import kernelSearch.Item;

/**
 * Classe che implementa la strategia di creazione dei bucket basata su clustering degli item
 * presenti nel problema.
 * In particolare, questa classe costruisce un bucket per ogni cluster secondo
 * l'algoritmo di clustering di Clauset-Newmann-Moore, che non prevede la sovrapposizione dei cluster.
 *
 */
public class SimpleClustersBucketBuilder extends ClusteredBucketBuilder {

	@Override
	public List<Bucket> composeBuckets(List<Set<Item>> clusters, int itemsN) {
		List<Bucket> buckets = new ArrayList<Bucket>();
		// Per ciascun cluster ottenuto dall'algoritmo
		for (Set<Item> cluster : clusters) {
			Bucket currentBucket = new Bucket();
			cluster.forEach(item->currentBucket.addItem(item));
			buckets.add(currentBucket);
		}
		return buckets;
	}
	
	/* TODO Implementazione futura
	 * Dato che l'algoritmo di clustering di CNM costruisce il dendrogramma del grafo,
	 * sarebbe interessante permettere di scegliere il numero di cluster (e quindi di bucket) 
	 * da restituire. Si tratterebbe di "tagliare" il dendrogramma all'altezza corretta in modo
	 * da avere una suddivisione del grafo in esattamente N cluster.
	 */

}
