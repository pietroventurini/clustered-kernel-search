package kernelSearch.bucketBuilder.clusteringBasedStrategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kernelSearch.Bucket;
import kernelSearch.Item;
import kernelSearch.bucketBuilder.ClusteredBucketBuilder;
import kernelSearch.itemSorter.ItemSorterByValueAndAbsoluteRC;

/**
 * Classe che implementa la strategia di creazione dei bucket basata su clustering degli item presenti nel problema.
 * 
 * In particolare, questa classe costruisce un bucket per ogni cluster secondo l'algoritmo di clustering 
 * di Clauset-Newmann-Moore, che non prevede la sovrapposizione dei cluster.
 * In aggiunta, inserisce in ciascun bucket un prefissato numero di item, selezionando i più promettenti
 * secondo il criterio di ordinamento basato sui coefficienti di costo ridotto. Questo inserimento aggiuntivo
 * è effettuato solamente se il bucket non presenta già gli elementi più promettenti, al fine di non appesantire 
 * con elementi poco interessanti i bucket già promettenti.
 * 
 * In pratica, l'algoritmo si assicura che in ogni bucket siano presenti i primi N elementi ordinati secondo
 * il loro coefficiente di costo ridotto.
 * 
 * NOTA: Attualmente l'algoritmo di clustering non esclude gli item appartenenti al Kernel. [FIXME]
 *
 */
public class SimpleClustersWithPrivilegedItemsBucketBuilder extends ClusteredBucketBuilder {
	
	private final double privilegedItemsPercentage;
	
	public SimpleClustersWithPrivilegedItemsBucketBuilder(double privilegedItemsPercentage) {
		this.privilegedItemsPercentage = privilegedItemsPercentage;
	}
	@Override
	public List<Bucket> composeBuckets(List<Set<Item>> clusters, int itemsN) {
		List<Bucket> buckets = new ArrayList<Bucket>();
		// Per ciascun cluster ottenuto dall'algoritmo
		clusters.forEach(cluster->{
			Bucket currentBucket = new Bucket();
			// Inserisco nel bucket tutti gli Item del cluster
			cluster.forEach(item->currentBucket.addItem(item));
			// Aggiungo il bucket alla lista
			buckets.add(currentBucket);
		});
		
		List<Item> notKernel_ordered = new ArrayList<Item>();
		buckets.stream().forEach(bucket->notKernel_ordered.addAll(bucket.getItems()));
		new ItemSorterByValueAndAbsoluteRC().sort(notKernel_ordered);
		notKernel_ordered.stream()
							.limit((int) (notKernel_ordered.size() * this.privilegedItemsPercentage))
							.forEach(item->buckets.stream().filter(bucket->!bucket.contains(item) && bucket.size() > 1).forEach(b->b.addItem(item)));
		notKernel_ordered.clear();
		return buckets;
	}
}
