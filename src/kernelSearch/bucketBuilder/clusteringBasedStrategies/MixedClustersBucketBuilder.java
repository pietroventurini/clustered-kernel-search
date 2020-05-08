package kernelSearch.bucketBuilder.clusteringBasedStrategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kernelSearch.Bucket;
import kernelSearch.bucketBuilder.ClusteredBucketBuilder;
import kernelSearch.itemSorter.ItemSorterByValueAndAbsoluteRC;
import kernelSearch.Item;

/**
 * Classe che implementa la strategia di creazione dei bucket basata su clustering degli item
 * presenti nel problema.
 * In particolare, questa classe costruisce un bucket estraendo gli item dai differenti cluster
 * in ordine di costo ridotto.
 * 
 * NOTA: Attualmente l'algoritmo non esclude gli item appartenenti al Kernel. [FIXME]
 *
 */
public class MixedClustersBucketBuilder extends ClusteredBucketBuilder {
	
	private final double bucketsRelativeSize;

	/**
	 * Costruttore.
	 * Richiede in ingresso la dimensione relativa dei bucket rispetto all'insieme di tutti gli items.
	 * 
	 * @param bucketsRelativeSize La dimensione relativa dei buckets rispetto agli insieme di tutti gli items.
	 */
	public MixedClustersBucketBuilder(double bucketsRelativeSize) {
		if (bucketsRelativeSize > 0) {
			this.bucketsRelativeSize = bucketsRelativeSize;
		} else {
			throw new IllegalArgumentException(
					String.format("Impossibile istanziare un oggetto MixedClusterBucketBuilder con dimensione dei bucket pari a %d", 
							bucketsRelativeSize));
		}
	}
	
//	@Override
//	public List<Bucket> build(KernelSearch ks) {
//		
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
//		 * Creazione dei bucket.
//		 */
//		int bucketsAbsoluteSize = this.computeBucketsSize(ks);
//		// Inizialmente, per ciascun cluster ottenuto dall'algoritmo
//		for (Cluster cluster : clusters) {
//			/* Gli item del cluster vengono ordinati
//			 * Si suppone che l'ordinamento venga effettuato posizionando in testa gli item
//			 * più promettenti, che saranno i primi ad essere estratti.
//			 */
//			(new ItemSorterByValueAndAbsoluteRC()).sort(cluster.getItems());
//		}
//
//		List<Bucket> buckets = new ArrayList<Bucket>();
//		// Quindi estraggo a rotazione un item da ciascun cluster
//		Bucket currentBucket = new Bucket();
//		int levelIndex = 0;
//		for (int clusterIndex = 0; clusters.size() > 0; ) {
//			
//			// Reset dell'indice dei cluster, per effettuare una rotazione ciclica
//			if (clusterIndex >= clusters.size()) {
//				clusterIndex = 0;
//				levelIndex++;
//			}
//			
//			// Riferimento al cluster corrente
//			Cluster currentCluster = clusters.get(clusterIndex);
//			
//			// Se il cluster corrente non ha più item al suo interno
//			if (levelIndex >= currentCluster.getItems().size()) {
//				// Viene rimosso dalla lista dei cluster, che non è più necessaria
//				clusters.remove(clusterIndex);
//				continue;
//			}
//			
//			// Altrimenti aggiungo l'item al livello desiderato al bucket corrente
//			currentBucket.addItem(currentCluster.getItems().get(levelIndex));
//			
//			// Se il bucket raggiunge la dimensione massima
//			if (currentBucket.size() >= bucketsAbsoluteSize) {
//				// Allora viene chiuso e aggiunto alla lista
//				buckets.add(currentBucket);
//				currentBucket = new Bucket();
//			}
//			
//			// Passo al cluster successivo
//			clusterIndex++;
//		}
//		
//		return buckets;
//	}
//
//	/**
//	 * Calcola la cardinalità effettiva dei buckets da costruire.
//	 * Il calcolo è effettuato moltiplicando la dimensione relativa per il totale degli items.
//	 * 
//	 * @param ks L'attuale esecuzione della KernelSearch, serve per recuperare il numero di items.
//	 * @return Il numero di items in ciascun bucket.
//	 */
//	private int computeBucketsSize(KernelSearch ks) {
//		int itemsCardinality = ks.getItemsList().size();
//		return ((int) Math.floor(itemsCardinality * this.bucketsRelativeSize));
//	}

	@Override
	public List<Bucket> composeBuckets(List<Set<Item>> clusters, int itemsN) {
		int bucketsAbsoluteSize = ((int) Math.floor(itemsN * this.bucketsRelativeSize));
		
		List<ArrayList<Item>> clustersAsList = new ArrayList<ArrayList<Item>>();
		// Inizialmente, per ciascun cluster ottenuto dall'algoritmo
		for (Set<Item> cluster : clusters) {
			/* Gli item del cluster vengono ordinati
			 * Si suppone che l'ordinamento venga effettuato posizionando in testa gli item
			 * più promettenti, che saranno i primi ad essere estratti.
			 */
			ArrayList<Item> clusterL = new ArrayList<Item>(cluster);
			(new ItemSorterByValueAndAbsoluteRC()).sort(clusterL);
			clustersAsList.add(clusterL);
		}
		
		List<Bucket> buckets = new ArrayList<Bucket>();
		// Quindi estraggo a rotazione un item da ciascun cluster
		Bucket currentBucket = new Bucket();
		int levelIndex = 0;
		for (int clusterIndex = 0; clustersAsList.size() > 0; ) {
			
			// Reset dell'indice dei cluster, per effettuare una rotazione ciclica
			if (clusterIndex >= clustersAsList.size()) {
				clusterIndex = 0;
				levelIndex++;
			}
			
			// Riferimento al cluster corrente
			ArrayList<Item> currentCluster = clustersAsList.get(clusterIndex);
			if (currentCluster == null) {
				System.out.printf("L'indice %3d ha cluster nullo\n", clusterIndex);
			} else {
				System.out.printf("L'indice %3d non ha cluster nullo\n", clusterIndex);
			}
			
			// Se il cluster corrente non ha più item al suo interno
			if (levelIndex >= currentCluster.size()) {
				// Viene rimosso dalla lista dei cluster, che non è più necessaria
				clustersAsList.remove(clusterIndex);
				continue;
			}
			
			// Altrimenti aggiungo l'item al livello desiderato al bucket corrente
			currentBucket.addItem(currentCluster.get(levelIndex));
			
			// Se il bucket raggiunge la dimensione massima
			if (currentBucket.size() >= bucketsAbsoluteSize) {
				// Allora viene chiuso e aggiunto alla lista
				buckets.add(currentBucket);
				currentBucket = new Bucket();
			}
			
			// Passo al cluster successivo
			clusterIndex++;
		}
		return buckets;
	}

}
