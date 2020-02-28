package kernelSearch.bucketBuilder;

import gurobi.GRBModel;

import kernelSearch.Bucket;
import kernelSearch.Configuration;
import kernelSearch.Item;

import java.util.List;

public interface BucketBuilder
{
	public List<Bucket> build(List<Item> items, Configuration config);
}