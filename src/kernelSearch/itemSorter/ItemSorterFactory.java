package kernelSearch.itemSorter;

public class ItemSorterFactory {
	public static ItemSorter get(String itemSorter) {
		if(ItemSorterByValueAndAbsoluteRC.class.getSimpleName().equals(itemSorter))
			return new ItemSorterByValueAndAbsoluteRC();
		return null;
	}
}
