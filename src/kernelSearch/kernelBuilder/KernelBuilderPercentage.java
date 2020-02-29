package kernelSearch.kernelBuilder;
import java.util.ArrayList;
import java.util.List;

import kernelSearch.Item;

public class KernelBuilderPercentage implements KernelBuilder
{
	@Override
	public List<Item> build(List<Item> items, double kernelSize){
		List<Item> kernel = new ArrayList<>();
		
		for(Item it : items){
			if(it.getXr()> 0 && kernel.size() < Math.round(kernelSize*items.size())){
				kernel.add(it);
			}
		}	
		return kernel;
	}
}