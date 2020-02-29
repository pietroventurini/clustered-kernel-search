package kernelSearch.kernelBuilder;
import java.util.List;

import kernelSearch.Configuration;
import kernelSearch.Item;
import kernelSearch.Kernel;

public interface KernelBuilder
{
	public List<Item> build(List<Item> items, Configuration config);
}