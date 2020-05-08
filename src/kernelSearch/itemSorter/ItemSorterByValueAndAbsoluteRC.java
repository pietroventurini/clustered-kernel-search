package kernelSearch.itemSorter;
import java.util.Comparator;
import java.util.List;

import kernelSearch.Item;

/**
	Sort items according to their Value: from highest to lowest.
	If two variables have the same value, then order according to reduced cost: from lowest to highest.
 */
public class ItemSorterByValueAndAbsoluteRC implements ItemSorter
{
	@Override
	public void sort(List<Item> items)
	{
		items.sort(Comparator.comparing(Item::getXr).reversed()
				.thenComparing(Item::getRc));
	}

}
