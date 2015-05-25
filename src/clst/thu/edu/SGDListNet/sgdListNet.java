package cslt.thu.edu.SGDListNet;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cslt.thu.edu.evaluation.MetricScorer;
import cslt.thu.edu.evaluation.MetricScorerFactory;
import cslt.thu.edu.SGDListNet.Sorter;
import cslt.thu.edu.SGDListNet.RankList;
import cslt.thu.edu.SGDListNet.RankerFactory;
import cslt.thu.edu.SGDListNet.Normalizer;
import cslt.thu.edu.SGDListNet.MyThreadPool;
import cslt.thu.edu.SGDListNet.SumNormalizor;
import cslt.thu.edu.SGDListNet.ZScoreNormalizor;
import cslt.thu.edu.SGDListNet.Ranker;
import cslt.thu.edu.SGDListNet.ListNet;
import cslt.thu.edu.SGDListNet.SimpleMath;
import cslt.thu.edu.SGDListNet.FeatureManager;
import cslt.thu.edu.SGDListNet.DataPoint;

public class sgdListNet {
	public static boolean normalize = false;
	public static boolean mustHaveRelDoc = false;
	public static boolean letor = false;
	public static Normalizer nml = new SumNormalizor();
	public static String modelFile = "";
 	public static String modelToLoad = "";
// 	public enum METRIC {
//		MAP, NDCG, DCG, Precision, Reciprocal, Best, ERR
//	}
 	
	public static void main(String[] args) {
		String trainFile = "";
		String validationFile = "";
		String testFile = "";
		String trainMetric = "P@1";
		String testMetric = "P@1";
		sgdListNet.normalize = false;
		String savedModelFile = "";
		String rankFile = "";
		String scoreFile = "";
		String k = "";
		String iterations = "";
		String learningRate = "";
		String sgdrate = "";
		String samplingMethods = "";
		String numberOfsampling = "";
		
		if(args.length < 2)
		{
			System.out.println("Usage: java -jar SGDListNet.jar <Params>");
			System.out.println("Params:");
			System.out.println("  [+] Training (+ tuning and evaluation)");
			System.out.println("\t-train <file>\t\tTraining data");
			System.out.println("\t[ -metric2t <metric> ]\tMetric to optimize on the training data. Supported: MAP, NDCG@k, DCG@k, P@k, RR@k, ERR@k (default=" + trainMetric + ")");
			System.out.println("\t[ -metric2T <metric> ]\tMetric to evaluate on the test data (default to the same as specified for -metric2t)");
				
			System.out.println("\t[ -test <file> ]\tSpecify if you want to evaluate the trained model on this data (default=unspecified)");
			System.out.println("\t[ -validate <file> ]\tSpecify if you want to tune your system on the validation data (default=unspecified)");
			System.out.println("\t\t\t\tIf specified, the final model will be the one that performs best on the validation data");
				
			System.out.println("\t[ -norm <method>]\tNormalize feature vectors (default=no-normalization). Method can be:");
			System.out.println("\t\t\t\tsum: normalize each feature by the sum of all its values");
			System.out.println("\t\t\t\tzscore: normalize each feature by its mean/standard deviation");
				
			System.out.println("\t[ -save <model> ]\tSave the learned model to the specified file (default=not-save)");				
			System.out.println("    [-] ListNet-specific parameters");
			System.out.println("\t[ -epoch <T> ]\t\tThe number of epochs to train (default=" + ListNet.nIteration + ")");
			System.out.println("\t[ -lr <rate> ]\t\tLearning rate (default=" + (new DecimalFormat("###.########")).format(ListNet.learningRate) + ")");

				
			System.out.println("");
			System.out.println("  [+] Testing previously saved models");
			System.out.println("\t-load <model>\t\tThe model to load");
			System.out.println("\t-test <file>\t\tTest data to evaluate the model (specify either this or -rank but not both)");
			System.out.println("\t[ -metric2T <metric> ]\tMetric to evaluate on the test data (default=" + trainMetric + ")");
			System.out.println("\t[ -score <file>]\tStore ranker's score for each object being ranked (has to be used with -rank)");
			System.out.println("\t[ -norm ]\t\tNormalize feature vectors (similar to -norm for training/tuning)");
			System.out.println("\t[ -samplingMethods ]\t\t Uniform, fixedProba and adaptiveProba)");

			System.out.println("");
			return;
		}
		MyThreadPool.init(Runtime.getRuntime().availableProcessors());
		//MyThreadPool.init(2);
		
		for(int i=0;i<args.length;i++)
		{			
			if(args[i].compareTo("-train")==0) {
				trainFile = args[++i];
			} else if(args[i].compareTo("-metric2t")==0) {
				trainMetric = args[++i];				
			} else if(args[i].compareTo("-metric2T")==0) {
				testMetric = args[++i];	
			} else if(args[i].compareTo("-k")==0) {
				k = args[++i];
				ListNet.kNum = Integer.parseInt(k);
			} else if(args[i].compareTo("-learningRate")==0) {
				learningRate = args[++i];
				ListNet.learningRate = Double.parseDouble(learningRate);
			} else if(args[i].compareTo("-samplingMethods")==0) {
					samplingMethods = args[++i];
					ListNet.samplingMethods = samplingMethods;
			} else if(args[i].compareTo("-iterations")==0) {
				iterations = args[++i];
				ListNet.nIteration = Integer.parseInt(iterations);
			} else if(args[i].compareTo("-sgdrate")==0) {
				sgdrate = args[++i];
				ListNet.sgdrate = Integer.parseInt(sgdrate);
			} else if(args[i].compareTo("-numberOfsampling")==0) {
				numberOfsampling = args[++i];
				ListNet.numberOfsampling = Integer.parseInt(numberOfsampling);
			} else if(args[i].compareTo("-validate")==0) {
				validationFile = args[++i];
			} else if(args[i].compareTo("-test")==0) {
				testFile = args[++i];
			}
			else if(args[i].compareTo("-norm")==0)
			{
				sgdListNet.normalize = true;
				String n = args[++i];
				if(n.compareTo("sum") == 0)
					sgdListNet.nml = new SumNormalizor();
				else if(n.compareTo("zscore") == 0)
					sgdListNet.nml = new ZScoreNormalizor();
				else
				{
					System.out.println("Unknown normalizor: " + n);
					System.out.println("System will now exit.");
					System.exit(1);
				}
			}
			else if(args[i].compareTo("-save")==0)
				sgdListNet.modelFile = args[++i];
			else if(args[i].compareTo("-load")==0)
			{
				savedModelFile = args[++i];
				modelToLoad = args[i];
			}
			else if(args[i].compareTo("-rank")==0)
				rankFile = args[++i];
			else if(args[i].compareTo("-score")==0)
				scoreFile = args[++i];			
			else
			{
				System.out.println("Unknown command-line parameter: " + args[i]);
				System.out.println("System will now exit.");
				System.exit(1);
			}
		}
		
		if(testMetric.compareTo("")==0) {
			testMetric = trainMetric;
		}
			
		MetricScorer trainScorer = null;
		MetricScorer testScorer = null;
		System.out.println("");
		//System.out.println((keepOrigFeatures)?"Keep orig. features":"Discard orig. features");
		System.out.println("[+] General Parameters:");
		System.out.println("LETOR 4.0 dataset: " + (letor?"Yes":"No"));
		sgdListNet e = new sgdListNet(trainMetric, testMetric);
		if(trainFile.compareTo("")!=0)
		{
			System.out.println("Training data:\t" + trainFile);
						
			if(testFile.compareTo("")!=0)
				System.out.println("Test data:\t" + testFile);
			if(validationFile.compareTo("")!=0)//the user has specified the validation set 
				System.out.println("Validation data:\t" + validationFile);

			System.out.println("Ranking method:\t" + "SGD ListNet");
			System.out.println("Train metric:\t" + trainMetric);
			System.out.println("Test metric:\t" + testMetric);
			System.out.println("Feature normalization: " + ((sgdListNet.normalize)?sgdListNet.nml.name():"No"));
			if(modelFile.compareTo("")!=0)
				System.out.println("Model file: " + modelFile);
			
			System.out.println("");
			System.out.println("[+] " + "SGD ListNet" + "'s Parameters:");
			System.out.println("SGD List's number of iterations is " + ListNet.nIteration);
			System.out.println("SGD List's learning rate is " + ListNet.learningRate);
			System.out.println("SGD List's k in the top k probabilities k(ListNet's rapid computing calculation algorithms)  is " + ListNet.kNum);
			
			System.out.println("");
			
			//starting to do some work
			e.evaluate(trainFile, validationFile, testFile);
			if(rankFile.compareTo("")!=0)
			{
				if(scoreFile.compareTo("") != 0)
					e.score(savedModelFile, rankFile, scoreFile);
			}
		}
		else //scenario: test a saved model
		{
			System.out.println("Model file:\t" + savedModelFile);
			System.out.println("Feature normalization: " + ((sgdListNet.normalize)?sgdListNet.nml.name():"No"));
			if(rankFile.compareTo("")!=0)
			{
				if(scoreFile.compareTo("") != 0)
					e.score(savedModelFile, rankFile, scoreFile);
				else
					e.rank(savedModelFile, rankFile);
			}
			else
			{
				System.out.println("Test metric:\t" + testMetric);
				if(savedModelFile.compareTo("") != 0)
					e.test(savedModelFile, testFile);
			}
		}
		MyThreadPool.getInstance().shutdown();
	}
	
	protected RankerFactory rFact = new RankerFactory();
	protected MetricScorerFactory mFact = new MetricScorerFactory();
	
	protected MetricScorer trainScorer = null;
	protected MetricScorer testScorer = null;

	public sgdListNet(String trainMetric, String testMetric)
	{
		trainScorer = mFact.createScorer(trainMetric);
		testScorer = mFact.createScorer(testMetric);
	}
	public List<RankList> readInput(String inputFile)	
	{
		FeatureManager fm = new FeatureManager();
		List<RankList> samples = fm.read(inputFile, letor, mustHaveRelDoc);
		return samples;
	}
	public void normalize(List<RankList> samples, int[] fids)
	{
		for(int i=0;i<samples.size();i++)
			nml.normalize(samples.get(i), fids);
	}
	public void score(String modelFile, String testFile, String outputFile)
	{
		Ranker ranker = rFact.loadRanker(modelFile);
		int[] features = ranker.getFeatures();
		List<RankList> test = readInput(testFile);
		if(normalize)
			normalize(test, features);
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "ASCII"));
			for(int i=0;i<test.size();i++)
			{
				RankList l = test.get(i);
				for(int j=0;j<l.size();j++)
				{
					out.write(ranker.eval(l.get(j))+"");
					System.out.println(ranker.eval(l.get(j))+"");
					out.newLine();
				}
			}
			out.close();
		}
		catch(Exception ex)
		{
			System.out.println("Error in Evaluator::rank(): " + ex.toString());
		}
	}
	public int[] getFeatureFromSampleVector(List<RankList> samples)
	{
		DataPoint dp = samples.get(0).get(0);
		int fc = dp.getFeatureCount();
		int[] features = new int[fc];
		for(int i=0;i<fc;i++)
			features[i] = i+1;
		return features;
	}
	public void rank(String modelFile, String testFile)
	{
		Ranker ranker = rFact.loadRanker(modelFile);
		int[] features = ranker.getFeatures();
		List<RankList> test = readInput(testFile);
		if(normalize)
			normalize(test, features);
		
		for(int i=0;i<test.size();i++)
		{
			RankList l = test.get(i);
			double[] scores = new double[l.size()]; 
			for(int j=0;j<l.size();j++)
				scores[j] = ranker.eval(l.get(j));
			int[] idx = Sorter.sort(scores, false);
			List<Integer> ll = new ArrayList<Integer>();
			for(int j=0;j<idx.length;j++)
				ll.add(idx[j]);
			for(int j=0;j<l.size();j++)
			{
				int index = ll.indexOf(j) + 1;
				System.out.print(index + ((j==l.size()-1)?"":" "));
			}
			System.out.println("");
		}
	}
	public void test(String modelFile, String testFile)
	{
		Ranker ranker = rFact.loadRanker(modelFile);
		int[] features = ranker.getFeatures();
		List<RankList> test = readInput(testFile);
		if(normalize)
			normalize(test, features);
		
		double rankScore = 0.0;
		double score = 0.0;
		for(int i=0;i<test.size();i++)
		{
			RankList l = ranker.rank(test.get(i));
			score = testScorer.score(l);

			rankScore += score;
		}
		rankScore /= test.size();
		System.out.println(testScorer.name() + " on test data: " + SimpleMath.round(rankScore, 4));
	}
	public double evaluate(Ranker ranker, List<RankList> rl)
	{
		List<RankList> l = rl;
		if(ranker != null)
			l = ranker.rank(rl);
		return testScorer.score(l);
	}
	/**
	 * Evaluate the currently selected ranking algorithm using <training data, validation data, testing data and the defined features>.
	 * @param trainFile
	 * @param validationFile
	 * @param testFile
	 * @param featureDefFile
	 */
	public void evaluate(String trainFile, String validationFile, String testFile)
	{
		List<RankList> train = readInput(trainFile);//read input
		List<RankList> validation = null;
              if(validationFile.compareTo("")!=0)
			validation = readInput(validationFile);
		List<RankList> test = null;
		if(testFile.compareTo("")!=0)
			test = readInput(testFile);
		int[] features = getFeatureFromSampleVector(train);
		
		if(normalize)
		{
			normalize(train, features);
			if(validation != null)
				normalize(validation, features);
			if(test != null)
				normalize(test, features);
		}
		
		Ranker ranker = rFact.createRanker(train, features);
		ranker.set(trainScorer);
		ranker.setValidationSet(validation);
		ranker.init();
		ranker.learn();
		
		if(test != null)
		{
			double rankScore = evaluate(ranker, test);
			System.out.println(testScorer.name() + " on test data: " + SimpleMath.round(rankScore, 4));
		}
		if(modelFile.compareTo("")!=0)
		{
			System.out.println("");
			ranker.save(modelFile);
			System.out.println("Model saved to: " + modelFile);
		}
	}
}
