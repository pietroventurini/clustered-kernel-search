package clustering;

import java.util.Collection;
import java.util.stream.*;

public class Modularity {
	public static double nodesAsCommunities(double[] a) {
		return IntStream.range(0, a.length)
				.mapToDouble((i)->-Math.pow(a[i], 2))
				.sum();
	}
	
	public static double nodesAsCommunities(Collection<Double> a) {
		return a.stream()
				.mapToDouble((v)->-Math.pow(v, 2))
				.sum();
	}
}
