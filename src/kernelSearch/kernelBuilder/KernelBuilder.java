package kernelSearch.kernelBuilder;
import java.util.List;

import kernelSearch.Configuration;
import kernelSearch.Item;
import kernelSearch.Kernel;

public interface KernelBuilder
{
	public Kernel build(List<Item> items, Configuration config);
}