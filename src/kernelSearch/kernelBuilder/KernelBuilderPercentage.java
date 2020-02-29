package kernelSearch.kernelBuilder;
import java.util.List;

import kernelSearch.Configuration;
import kernelSearch.Item;
import kernelSearch.Kernel;

public class KernelBuilderPercentage implements KernelBuilder
{
	@Override
	public Kernel build(List<Item> items, Configuration config)
	{
		Kernel kernel = new Kernel();
		
		for(Item it : items)
		{
			if(it.getXr()> 0 && kernel.size() < Math.round(config.getKernelSize()*items.size()))
			{
				kernel.addItem(it);
			}
		}	
		return kernel;
	}
}