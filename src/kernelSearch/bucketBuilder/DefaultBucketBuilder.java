package kernelSearch.bucketBuilder;

import java.util.ArrayList;
import java.util.List;

import kernelSearch.Bucket;
import kernelSearch.Configuration;
import kernelSearch.Item;

/**
 * Algoritmo di costruzione dei bucket.
 * Costruisce un set di bucket di dimensione predefinita; gli item vengono analizzati
 * in modo sequenziale, senza che siano effettuati ulteriori ordinamenti. Si suppone
 * pertanto che gli item siano già stati ordinati in precedenza, secondo i coefficienti
 * di costo ridotto o secondo altre logiche.
 */
public class DefaultBucketBuilder implements BucketBuilder
{
	@Override
	public List<Bucket> build(List<Item> toDispose, List<Item> kernel, Configuration config)
	{
		List<Bucket> buckets = new ArrayList<>();
		Bucket b = new Bucket();
		int size = (int) Math.floor(toDispose.size()*config.getBucketSize());
		for(Item it : toDispose){
			b.addItem(it);
			
			if(b.size() == size){
				buckets.add(b);
				b = new Bucket();
			}
		}
		if(b.size() < size && b.size() > 0){
			buckets.add(b);
		}
		return buckets;
	}
}
