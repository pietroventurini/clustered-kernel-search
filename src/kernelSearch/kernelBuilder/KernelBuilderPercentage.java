package kernelSearch.kernelBuilder;
import java.util.ArrayList;
import java.util.List;

import kernelSearch.Configuration;
import kernelSearch.Item;
import kernelSearch.Kernel;

public class KernelBuilderPercentage implements KernelBuilder
{
	@Override
	public List<Item> build(List<Item> items, Configuration config){
		List<Item> kernel = new ArrayList<>();
		
		for(Item it : items){
			if(it.getXr()> 0 && kernel.size() < Math.round(config.getKernelSize()*items.size())){
				kernel.add(it);
			}
		}	
		return kernel;
	}
}