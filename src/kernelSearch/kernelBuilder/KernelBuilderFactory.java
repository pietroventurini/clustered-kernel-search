package kernelSearch.kernelBuilder;

public class KernelBuilderFactory {
	public static KernelBuilder get(String builder) {
		if(builder.equals(KernelBuilderPercentage.class.getSimpleName()))
			return new KernelBuilderPercentage();
		return new KernelBuilderPositive();
	}
}
