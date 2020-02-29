package kernelSearch;


public class ModelProperties {
	private String instancePath;
	private String logPath;
	private int threads;
	private int presolve;
	private double mipgap;
	
	public ModelProperties(String instancePath,
							String logPath,
							int threads,
							int presolve,
							double mipgap
			) {
		this.instancePath = instancePath;
		this.logPath = logPath;
		this.threads = threads;
		this.presolve = presolve;
		this.mipgap = mipgap;
	}
	public String instance() {return instancePath;}
	public String log() {return logPath;}
	public int threads() {return threads;}
	public int presolve() {return presolve;}
	public double mipgap() {return mipgap;}
}
