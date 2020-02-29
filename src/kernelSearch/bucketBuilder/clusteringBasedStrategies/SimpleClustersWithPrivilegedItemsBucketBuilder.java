package kernelSearch.bucketBuilder.clusteringBasedStrategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kernelSearch.Bucket;
import kernelSearch.Item;
import kernelSearch.bucketBuilder.ClusteredBucketBuilder;

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
	private final List<Item> notKernel_ordered;
	
	public SimpleClustersWithPrivilegedItemsBucketBuilder(double privilegedItemsPercentage, List<Item> notKernel_ordered) {
		this.privilegedItemsPercentage = privilegedItemsPercentage;
		this.notKernel_ordered = notKernel_ordered;
	}
//	@Override
//	public List<Bucket> build(KernelSearch ks) {
//		/* FASE 1
//		 * Creazione del grafo degli item.
//		 */
//		UndirectedGraph<Item> correlationGraph = null;
////		correlationGraph = new UndirectedGraph<T>() {}; // TODO TODO TODO 
//		
//		/* FASE 2
//		 * Clustering su grafo.
//		 */
//		ClausetNewmanMooreAlgorithm clusteringAlgorithm = new ClausetNewmanMooreAlgorithm();
//		List<Cluster> clusters = clusteringAlgorithm.findCommunities(correlationGraph);
//		
//		/* FASE 3
//		 * Creazione dei bucket
//		 */
//		List<Bucket> buckets = new ArrayList<Bucket>();
//		// Per ciascun cluster ottenuto dall'algoritmo
//		for (Cluster cluster : clusters) {
//			// Creo un nuovo bucket
//			Bucket currentBucket = new Bucket();
//			// Inserisco nel bucket tutti gli Item del cluster
//			for (Item item : cluster.getItems()) {
//				currentBucket.addItem(item);
//			}
//			// Aggiungo il bucket alla lista
//			buckets.add(currentBucket);
//		}
//		
//		// Itero su tutti gli item
//		ks.getItemsList().stream()
//				// Filtro solo quelli non appartenenti al kernel
//				.filter((item) -> (!item.isInKernel()))
//				// Prendo solamente i primi N
//				.limit((int) (ks.getItemsList().size() * this.privilegedItemsPercentage))
//				// Aggiungo ciascuno di essi a tutti i bucket
//				.forEach((item) -> {
//					for (Bucket bucket : buckets) {
//						bucket.addItem(item);
//					}
//				});
//		
//		return buckets;
//	}

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
		
		notKernel_ordered.stream()
							.limit((int) (notKernel_ordered.size() * this.privilegedItemsPercentage))
							.forEach(item->buckets.forEach(b->b.addItem(item)));
		return buckets;
	}

}
