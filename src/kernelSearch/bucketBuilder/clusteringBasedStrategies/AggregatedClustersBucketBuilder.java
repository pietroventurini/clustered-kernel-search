package kernelSearch.bucketBuilder.clusteringBasedStrategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kernelSearch.Bucket;
import kernelSearch.Item;
import kernelSearch.bucketBuilder.ClusteredBucketBuilder;


/**
 * Implementazione dell'interfaccia {@link BucketBuilder}, che rappresenta una possibile metodologia per 
 * creare la lista di bucket a partire dal modello matematico.
 * 
 * In questa classe il focus è sulla dimensione dei bucket: dopo aver ottenuto da un algoritmo apposito i cluster 
 * degli item correlati fra loro, tali cluster vengono aggregati in modo da raggiungere all'incirca tutti la stessa dimensione.
 * Al momento dell'istanziazione è possibile fissare il numero di buckets richiesti al termine della costruzione.
 *
 */
public class AggregatedClustersBucketBuilder extends ClusteredBucketBuilder {
	
	private final double bucketsRelativeSize;
	
	/**
	 * Costruttore.
	 * Richiede la dimensione indicativa (relativa) di ciascun bucket. Tale dimensione 
	 * verrà rispettata il più possibile. Si noti che una dimensione troppo stretta potrebbe
	 * portare ad effetti indesiderati, come la creazione di un numero eccessivo di buckets, ciascuno con 
	 * gli items di un cluster.
	 * Nel caso di valore di bucketsRelativeSize prossimi allo zero, questo algoritmo fornisce risultati simili
	 * a {@link SimpleClustersBucketBuilder}, poiché non aggrega nessun cluster per formare i buckets.
	 * Per cluster molto piccoli e buckets molto capienti, invece, questo algoritmo tende ad aggregare i cluster fino
	 * a riempire il più possibile i buckets.
	 * 
	 * @param bucketsRelativeSize La dimensione relativa dei buckets rispetto AL KERNEL.
	 */
	public AggregatedClustersBucketBuilder(double bucketsRelativeSize, double kernelSize) {
		if (bucketsRelativeSize <= 0 || bucketsRelativeSize >= 1) {
			throw new IllegalArgumentException(
					String.format("Impossibile istanziare un oggetto AggregatedClustersBucketBuilder con dimensione relativa dei buckets pari a %f", bucketsRelativeSize));
		}
		this.bucketsRelativeSize = bucketsRelativeSize;
	}
	
	/**
	 * @see kernelSearch.bucketBuilder.ClusteredBucketBuilder#composeBuckets(List)
	 */
	@Override
	public List<Bucket> composeBuckets(List<Set<Item>> clusters, int itemsNumber) {
		int effectiveBucketsSize = (int) Math.ceil(itemsNumber * this.bucketsRelativeSize);
		
		// Istanzio il numero corretto di buckets
		List<Bucket> buckets = new ArrayList<Bucket>();
		
		/* In pratica, cerco il primo bucket che può ancora contenere tutti gli elementi del cluster,
		 * e li inserisco tutti. Se nessun bucket può accettare elementi, ne creo uno nuovo in coda.
		 * Itero per tutti i cluster.
		 */
		
		// Per ciascun cluster ottenuto dall'algoritmo
		for (Set<Item> cluster : clusters) {
			boolean insertionFlag = false;
			// Verifico per ciascun bucket
			for (Bucket currentBucket : buckets) {
				// Se tutti gli item ci stanno
				if (cluster.size() + currentBucket.size() <= effectiveBucketsSize) {
					// Riverso gli item del cluster nel bucket
					cluster.stream().forEach(it->currentBucket.addItem(it));
					// Segnalo che ho aggiunto gli item
					insertionFlag = true;
					// Non ho bisogno di verificare i prossimi bucket
					break;
				}
				// Altrimenti, passo al bucket successivo
			}
			
			// Verifico se NON sono riuscito ad aggiungere il cluster
			if (!insertionFlag) {
				// In tal caso, creo un bucket apposito
				Bucket newBucket = new Bucket();
				cluster.stream().forEach(it->newBucket.addItem(it));
				buckets.add(newBucket);
				
				// Verifico che la dimensione non ecceda quella prevista
				if (newBucket.size() > effectiveBucketsSize) {
					// In tal caso, segnalo l'errore
					System.err.println("La dimensione del bucket eccede quella prevista. Il cluster con cui è stato generato ha una cardinalità eccessiva. Si provi ad ampliare la dimensione relativa dei buckets.");
				}
			}
		}
		return buckets;
	}
}
