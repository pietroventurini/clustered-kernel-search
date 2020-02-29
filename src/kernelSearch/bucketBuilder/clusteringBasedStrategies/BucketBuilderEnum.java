package kernelSearch.bucketBuilder.clusteringBasedStrategies;

/**
 * Enumerazione delle possibili strategie di creazione dei bucket a partire dal modello matematico del problema.
 * La prima rappresenta la strategia di default, che ordina gli item secondo i coefficienti di costo ridotto.
 * Le successive sono invece differenti varianti della strategia basata su clustering degli item, possibilità che si 
 * intende testare in questo progetto.
 * 
 * Nota: A livello implementativo, questa enumerazione è utilizzata per una gestione più comoda delle configurazioni.
 */
public enum BucketBuilderEnum {
	
	DEFAULT_WITH_REDUCED_COSTS(
			"Default with RC",
			"Default con coefficienti di costo ridotto"),
	SIMPLE_CLUSTERS(
			"Simple clusters",
			"Buckets semplici corrispondenti ai cluster"),
	MIXED_CLUSTERS(
			"Mixed clusters",
			"Buckets ottenuti attingendo dai vari cluster"),
	AGGREGATED_CLUSTERS_WITH_BALANCED_SIZES(
			"Aggregated clusters with balanced sizes",
			"Buckets di dimensioni simili creati aggregando "),
	SIMPLE_CLUSTERS_WITH_PRIVILEGED_ITEMS(
			"Simple clusters with privileged items",
			"Buckets corrispondenti ai cluster, con l'aggiunta di item con alti RC");
	
	private final String name;
	private final String description;
	
	/**
	 * Costruttore.
	 * 
	 * @param name Il nome assegnato alla metodologia di costruzione dei buckets.
	 * @param description Una breve descrizione associata al metodo.
	 */
	private BucketBuilderEnum(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	/**
	 * Restituisce il nome del metodo di costruzione dei bucket.
	 * 
	 * @return Il nome che identifica il valore dell'enum.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Restituisce una breve descrizione del metodo, delineandone sinteticamente le caratteristiche.
	 * 
	 * @return Una descrizione sintetica del valore dell'enum.
	 */
	public String getDescription() {
		return this.description;
	}
}
