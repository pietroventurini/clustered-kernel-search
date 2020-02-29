package kernelSearch;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Classe del dominio che rappresenta concettualmente un bucket all'interno della KernelSearch.
 * Al suo interno è implementata con un TreeSet, per favorire operazioni di ricerca ed eliminazione
 * di duplicati.
 * 
 */
public class Bucket {
	
	private Set<Item> items;
	
	/**
	 * Costruttore.
	 */
	public Bucket() {
		items = new TreeSet<Item>((it1,it2)->it1.compareTo(it2));
	}
	
	/**
	 * Aggiunge alla lista l'item passato come parametro.
	 * Se l'item NON era presente, l'aggiunta va a buon fine e il metodo restituisce TRUE.
	 * Se l'item è già presente, il metodo non aggiunge nulla e restituisce FALSE.
	 * 
	 * @param it Il nuovo item da aggiungere.
	 * @return TRUE se l'aggiunta viene effettuta. FALSE altrimenti.
	 */
	public boolean addItem(Item it) {
		return items.add(it);
	}
	
	/**
	 * Restituisce il numero di elementi all'interno del bucket.
	 * 
	 * @return La dimensione del bucket.
	 */
	public int size() {
		return items.size();
	}
	
	/**
	 * Restituisce la lista degli elementi del bucket.
	 * Questo metodo è computazionalmente dispendioso poiché la lista deve essere
	 * costruita a partire da un set.
	 * 
	 * @return La lista di elementi all'interno del bucket.
	 */
	public List<Item> getItems() {
		return new ArrayList<>(this.items);
	}
	
	/**
	 * Restituisce TRUE se il bucket contiene un item con lo stesso nome di quello
	 * passato come parametro; FALSE altrimenti.
	 * 
	 * @param it L'item da cercare
	 * @return Un valore booleano che indica la presenza dell'item nel bucket
	 */
	public boolean contains(Item it) {
		return this.items.contains(it);
	}
	
	/**
	 * Rimuove l'Item passato come parametro dal bucket.
	 * Il confronto è sempre effettuato tramite il nome.
	 * 
	 * @param it L'item da eliminare.
	 * @return TRUE se la rimozione viene effettuta. FALSE altrimenti.
	 */
	public boolean removeItem(Item it)	{
		return this.items.remove(it);
	}
}