package kernelSearch.kernelBuilder;
import java.util.List;

import kernelSearch.Item;

public interface KernelBuilder
{
	public List<Item> build(List<Item> items, double kernelSize);
}