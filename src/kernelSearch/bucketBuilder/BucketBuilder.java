package kernelSearch.bucketBuilder;

import java.util.List;

import kernelSearch.Bucket;
import kernelSearch.Item;
import kernelSearch.ModelProperties;

public interface BucketBuilder
{
	/**
	 * Builds a list of Buckets starting from a set of items
	 * @param toDispose the set of items to dispose in buckets
	 * @param kernel the kernel
	 * @param bucketSize relative size of the buckets
	 * @param config the configuration for the construction
	 * @return the list of Buckets
	 */
	public List<Bucket> build(List<Item> toDispose, List<Item> kernel, double bucketSize, ModelProperties config);
}