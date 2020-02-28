package greedyModularity;
import java.util.stream.*;

public class Modularity {
	public static double nodesAsCommunities(double[] a) {
		return IntStream.range(0, a.length)
				.mapToDouble((i)->-Math.pow(a[i], 2))
				.sum();
	}
}
