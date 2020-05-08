package kernelSearch.kernelBuilder;
import java.util.ArrayList;
import java.util.List;

import kernelSearch.Item;

public class KernelBuilderPositive implements KernelBuilder
{
	@Override
	public List<Item> build(List<Item> items, double kernelSize){
		List<Item> kernel = new ArrayList<Item>();

		// Nota: essendo gli items già stati ordinati precedentemente, possiamo evitare di ricontrollarli tutti,
		// Dubbio: items con valore < 0 non li prendiamo?
		for(Item it : items){
			if(it.getXr()> 0){
				kernel.add(it);
			}
		}
		return kernel;
	}
}
