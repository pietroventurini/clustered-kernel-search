package kernelSearch;


public class ModelProperties {
	private String instancePath;
	private String logPath;
	private int threads;
	private int presolve;
	private double mipgap;
	private int maxGraphEdges;
	
	public ModelProperties(String instancePath,
							String logPath,
							int threads,
							int presolve,
							double mipgap,
						    int maxGraphEdges
			) {
		this.instancePath = instancePath;
		this.logPath = logPath;
		this.threads = threads;
		this.presolve = presolve;
		this.mipgap = mipgap;
		this.maxGraphEdges = maxGraphEdges;
	}
	public String instancePath() {return instancePath;}
	public String logPath() {return logPath;}
	public int threads() {return threads;}
	public int presolve() {return presolve;}
	public double mipgap() {return mipgap;}
	public int maxGraphSize() {return maxGraphEdges;}
}
