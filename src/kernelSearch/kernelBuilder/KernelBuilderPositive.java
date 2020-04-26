package kernelSearch.kernelBuilder;
import java.util.ArrayList;
import java.util.List;

import kernelSearch.Item;

public class KernelBuilderPositive implements KernelBuilder
{
	@Override
	public List<Item> build(List<Item> items, double kernelSize){
		List<Item> kernel = new ArrayList<Item>();
		
		for(Item it : items){
			if(it.getXr()> 0){
				kernel.add(it);
			}
		}
		return kernel;
	}
}
