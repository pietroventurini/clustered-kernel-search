package kernelSearch.kernelBuilder;
import java.util.ArrayList;
import java.util.List;

import kernelSearch.Configuration;
import kernelSearch.Item;
import kernelSearch.Kernel;

public class KernelBuilderPositive implements KernelBuilder
{
	@Override
	public List<Item> build(List<Item> items, Configuration config){
		List<Item> kernel = new ArrayList<Item>();
		
		for(Item it : items){
			if(it.getXr()> 0){
				kernel.add(it);
			}
		}
		return kernel;
	}
}
