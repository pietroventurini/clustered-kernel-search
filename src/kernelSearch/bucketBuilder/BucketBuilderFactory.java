package kernelSearch.bucketBuilder;

import kernelSearch.bucketBuilder.clusteringBasedStrategies.AggregatedClustersBucketBuilder;
import kernelSearch.bucketBuilder.clusteringBasedStrategies.MixedClustersBucketBuilder;
import kernelSearch.bucketBuilder.clusteringBasedStrategies.SimpleClustersBucketBuilder;
import kernelSearch.bucketBuilder.clusteringBasedStrategies.SimpleClustersWithPrivilegedItemsBucketBuilder;

public class BucketBuilderFactory {
	public static BucketBuilder get(String builder, double bucketRelativeSize, double privilegedItemsPercentage, double kernelRelativeSize) {
		if(builder.equals(AggregatedClustersBucketBuilder.class.getSimpleName()))
			return new AggregatedClustersBucketBuilder(bucketRelativeSize, kernelRelativeSize);
		
		if(builder.equals(MixedClustersBucketBuilder.class.getSimpleName()))
			return new MixedClustersBucketBuilder(bucketRelativeSize);
		
		if(builder.equals(SimpleClustersBucketBuilder.class.getSimpleName()))
			return new SimpleClustersBucketBuilder();
		
		if(builder.equals(SimpleClustersWithPrivilegedItemsBucketBuilder.class.getSimpleName()))
			return new SimpleClustersWithPrivilegedItemsBucketBuilder(privilegedItemsPercentage);
		
		return new DefaultBucketBuilder();
	}
}
