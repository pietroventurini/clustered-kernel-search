package kernelSearch;

public enum ConfigKey {
	PRESOLVE(""),
	TIME_LIMIT("Time limit"),
	MIPGAP(""),
	THREADS("Number of threads"),
	SORTER("Item sorter"),
	KERNEL_BUILDER("Builder for the Kernel"),
	KERNEL_SIZE("Kernel's size"),
	BUCKET_SIZE("Buckets' size"),
	BUCKET_BUILDER("Builder for the buckets"),
	KERNEL_TIME_LIMIT("Time limit to compose the kernel"),
	ITERATIONS("Max number of iterations"),
	BUCKET_TIME_LIMIT("Time limit to compose a bucket"),
	INSTANCE("Path to the mpl file"),
	LOG("Path to the log file"),
	PRIVILEGED_ITEMS_PERCENTAGE("The percentage of priviledged items");
	
	private String description;
	private ConfigKey(String description) {
		this.description = description;
	}
	public String key() {
		return this.name().toLowerCase();
	}
	public String description() {return description;}
}
