package kernelSearch;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import gurobi.GRBCallback;

import kernelSearch.bucketBuilder.BucketBuilder;
import kernelSearch.bucketBuilder.BucketBuilderFactory;
import kernelSearch.itemSorter.ItemSorter;
import kernelSearch.itemSorter.ItemSorterFactory;
import kernelSearch.kernelBuilder.KernelBuilder;
import kernelSearch.kernelBuilder.KernelBuilderFactory;

public class KernelSearch{
	private final String instPath;
	private final String logPath;
	private List<Item> items;
	private final ItemSorter sorter;
	private BucketBuilder bucketBuilder;
	private KernelBuilder kernelBuilder;
	private final int tlim;
	private Solution bestSolution;
	private List<Bucket> buckets;
	private List<Item> kernel;
	private final int tlimKernel;
	private final int tlimBucket;
	private int numIterations;
	private GRBCallback callback;
	private final int timeThreshold = 5;
	private final ModelProperties modelProp;
	private final double kernelSize;
	private final double bucketSize;
	
	
	
	private Instant startTime;
	
	public KernelSearch(Properties p){
		this.instPath = p.getProperty(ConfigKey.INSTANCE.key());
		this.logPath = p.getProperty(ConfigKey.LOG.key());
		bestSolution = new Solution();
		sorter = ItemSorterFactory.get(p.getProperty(ConfigKey.SORTER.key()));
		tlim = Integer.parseInt(p.getProperty(ConfigKey.TIME_LIMIT.key()));
		tlimKernel = Integer.parseInt(p.getProperty(ConfigKey.KERNEL_TIME_LIMIT.key()));
		numIterations = Integer.parseInt(p.getProperty(ConfigKey.ITERATIONS.key()));
		tlimBucket = Integer.parseInt(p.getProperty(ConfigKey.BUCKET_TIME_LIMIT.key()));
		kernelSize = Double.parseDouble(p.getProperty(ConfigKey.KERNEL_SIZE.key()));
		bucketSize = Double.parseDouble(p.getProperty(ConfigKey.BUCKET_SIZE.key()));
		System.out.println(p.getProperty(ConfigKey.PRIVILEGED_ITEMS_PERCENTAGE.key()));

		bucketBuilder = BucketBuilderFactory.get(p.getProperty(ConfigKey.BUCKET_BUILDER.key()),
													bucketSize,
													Double.parseDouble(p.getProperty(ConfigKey.PRIVILEGED_ITEMS_PERCENTAGE.key())));

		kernelBuilder = KernelBuilderFactory.get(p.getProperty(ConfigKey.KERNEL_BUILDER.key()));
		modelProp = new ModelProperties(instPath,
										logPath,
										Integer.parseInt(p.getProperty(ConfigKey.THREADS.key())),
										Integer.parseInt(p.getProperty(ConfigKey.PRESOLVE.key())),
										Double.parseDouble(p.getProperty(ConfigKey.MIPGAP.key())));

	}
	
	public Solution start(){
		startTime = Instant.now();
		callback = new CustomCallback(logPath, startTime);
		items = buildItems();
		sorter.sort(items);	
		kernel = kernelBuilder.build(items, kernelSize);
		// possibile fornire al bucketBuilder direttamente il GRBmodel usato nella risoluzione del kernel?
		// alla fine ci servono solo le variabili(Items), della soluzione non ci importa
		buckets = bucketBuilder.build(items.stream().filter(it -> !kernel.contains(it)).collect(Collectors.toList()), 
										kernel, 
										bucketSize,
										modelProp);
		solveKernel();
		iterateBuckets();
		
		return bestSolution;
	}

	private List<Item> buildItems()
	{
		Model model = new Model(modelProp, tlim, true); // time limit equal to the global time limit
		model.buildModel();
		model.solve(); // solving the relaxation (lpRelaxation parameter = true)
		List<Item> items = new ArrayList<>();
		List<String> varNames = model.getVarNames();
		for(String v : varNames)
		{
			double value = model.getVarValue(v);
			double rc = model.getVarRC(v); // can be called only after solving the LP relaxation
			Item it = new Item(v, value, rc);
			items.add(it);
		}
		return items;
	}
	
	private void solveKernel()
	{
		Model model = new Model(modelProp, Math.min(tlimKernel, getRemainingTime()), false);	
		model.buildModel();
		if(!bestSolution.isEmpty())
			model.readSolution(bestSolution);
		
		List<Item> toDisable = items.stream().filter(it -> !kernel.contains(it)).collect(Collectors.toList());
		model.disableItems(toDisable);
		model.setCallback(callback);
		model.solve();
		if(model.hasSolution() && (model.getSolution().getObj() < bestSolution.getObj() || bestSolution.isEmpty())){
			bestSolution = model.getSolution();
			model.exportSolution();
		}
	}
	
	private void iterateBuckets()
	{
		for (int i = 0; i < numIterations; i++)
		{
			if(getRemainingTime() <= timeThreshold)
				return;
			solveBuckets();			
		}		
	}

	private void solveBuckets()
	{
		for(Bucket b : buckets)
		{
			List<Item> toDisable = items.stream().filter(it -> !kernel.contains(it) && !b.contains(it)).collect(Collectors.toList());

			Model model = new Model(modelProp, Math.min(tlimBucket, getRemainingTime()), false);	
			model.buildModel();
					
			model.disableItems(toDisable);
		//	model.addBucketConstraint(b.getItems()); // can we use this constraint regardless of the type of variables chosen as items?
			
			if(!bestSolution.isEmpty())
			{
				model.addObjConstraint(bestSolution.getObj());		
				model.readSolution(bestSolution);
			}
			
			model.setCallback(callback);
			model.solve();
			
			if(model.hasSolution() && (model.getSolution().getObj() < bestSolution.getObj()  || bestSolution.isEmpty()))
			{
				bestSolution = model.getSolution();
				List<Item> selected = model.getSelectedItems(b.getItems());
				selected.forEach(it -> kernel.add(it));
				selected.forEach(it -> b.removeItem(it));
				model.exportSolution();
			}
			
			if(getRemainingTime() <= timeThreshold)
				return;
		}	
	}

	private int getRemainingTime()
	{
		return (int) (tlim - Duration.between(startTime, Instant.now()).getSeconds());
	}
}