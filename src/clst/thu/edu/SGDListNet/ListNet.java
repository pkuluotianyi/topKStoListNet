package cslt.thu.edu.SGDListNet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cslt.thu.edu.SGDListNet.DataPoint;
import cslt.thu.edu.SGDListNet.RankList;
import cslt.thu.edu.SGDListNet.Ranker;
import cslt.thu.edu.SGDListNet.SimpleMath;

public class ListNet extends RankNet {
	
	//Parameters
	public static int kNum = 2;
	public static int nIteration = 30;
	public static double learningRate = 1.0E-7;
	public static int sgdrate = 1;
	public static String samplingMethods = "Uniform";
	public static int numberOfsampling = 5;
	
	//public static int nHiddenLayer = 0;//FIXED, it doesn't work with hidden layer
	
	
	public ListNet()
	{		
	}
	public ListNet(List<RankList> samples, int [] features)
	{
		super(samples, features);
	}
	
	protected float[] feedForward(RankList rl)
	{
		float[] labels = new float[rl.size()];
		for(int i=0;i<rl.size();i++)
		{
			addInput(rl.get(i));
			propagate(i);
			labels[i] = rl.get(i).getLabel();
		}
		return labels;
	}
	protected void backPropagate(float[] labels)
	{
		//back-propagate
		PropParameter p = new PropParameter(labels);
		outputLayer.computeDelta(p);//starting at the output layer
		
		//weight update
		outputLayer.updateWeight(p);
	}
	protected void estimateLoss() 
	{
		error = 0.0;
		double sumLabelExp = 0;
		double sumScoreExp = 0;
		//for(int i=0;i<471;i++)
		for(int i=0;i<samples.size();i++)
		{
			RankList rl = samples.get(i);
			double[] scores = new double[rl.size()];
			double err = 0;
			for(int j=0;j<rl.size();j++)
			{
				scores[j] = eval(rl.get(j));
				sumLabelExp += Math.exp(rl.get(j).getLabel());
				sumScoreExp += Math.exp(scores[j]);				
			}
			for(int j=0;j<rl.size();j++)
			{
				double p1 = Math.exp(rl.get(j).getLabel())/sumLabelExp;
				double p2 = (Math.exp(scores[j])/sumScoreExp);
				err +=  - p1 * SimpleMath.logBase2(p2);
			}
			error += err/rl.size();
		}
		if(error > lastError && Neuron.learningRate > 1.0E-30) {		
			Neuron.learningRate *= 1.0E-1;
			PRINTLN("Learning rate: " +  Neuron.learningRate);
			
			
			//learningRate = Neuron.learningRate;
		}
		lastError = error;
	}
	
	public void init()
	{
		PRINT("Initializing... ");
		
		//Set up the network
		setInputOutput(features.length, 1, 1);
		wire();
		
		if(validationSamples != null)
			for(int i=0;i<layers.size();i++)
				bestModelOnValidation.add(new ArrayList<Double>());
		
		Neuron.learningRate = learningRate;
		PRINTLN("[Done]");
	}
	public void learn()
	{
		PRINTLN("-----------------------------------------");
		PRINTLN("Training starts...");
		PRINTLN("--------------------------------------------------");
		PRINTLN(new int[]{7, 14, 9, 9}, new String[]{"#epoch", "C.E. Loss", scorer.name()+"-T", scorer.name()+"-V"});
		PRINTLN("--------------------------------------------------");
		long startTime = System.currentTimeMillis();
		for(int i=1;i<=nIteration;i++)
		{
			if (kNum >= 2) {
				for(int j=0;j<samples.size();j++)
				{
					int maxNum = samples.size() - 1;
					Random random = new Random();
				    int s = random.nextInt(maxNum);
				    //System.out.println(s);		    					    
				    float[] labels = feedForward(samples.get(s));
				    backPropagate(labels);
				    clearNeuronOutputs();
					
//				    float[] labels = feedForward(samples.get(j));
//					backPropagate(labels);
//					clearNeuronOutputs();
				}
			}
			if (kNum == 1) {
				for(int j=0;j<samples.size();j++)
				//for(int j=0;j<171/sgdrate;j++)
				{	
//					//System.out.println(j);
//					int maxNum = samples.size() - 1;
//					Random random = new Random();
//				    int s = random.nextInt(maxNum);
//				    //System.out.println(s);
//				    float[] labels = feedForward(samples.get(s));
//				    backPropagate(labels);
//				    clearNeuronOutputs();
					
					float[] labels = feedForward(samples.get(j));
					backPropagate(labels);
					clearNeuronOutputs();
				}
			}
			if (kNum == 0) {
				for(int j=0;j<samples.size();j++)
				//for(int j=0;j<472;j++)
				{	
					float[] labels = feedForward(samples.get(j));
					backPropagate(labels);
					clearNeuronOutputs();
				}
			}
			
			long endTime = System.currentTimeMillis();
			System.out.println("Programming running time: " + (endTime - startTime)/1000.0f + "s");
			estimateLoss();
			PRINT(new int[]{7, 14}, new String[]{i+"", SimpleMath.round(error, 6)+""});
			if(i % 1 == 0)
			{
				scoreOnTrainingData = scorer.score(rank(samples));
				PRINT(new int[]{9}, new String[]{SimpleMath.round(scoreOnTrainingData, 4)+""});
				if(validationSamples != null)
				{
					double score = scorer.score(rank(validationSamples));
					if(score > bestScoreOnValidationData)
					{
						bestScoreOnValidationData = score;
						saveBestModelOnValidation();
					}
					PRINT(new int[]{9}, new String[]{SimpleMath.round(score, 4)+""});
				}
			}
			PRINTLN("");
		}
		
		//if validation data is specified ==> best model on this data has been saved
		//we now restore the current model to that best model
		if(validationSamples != null)
			restoreBestModelOnValidation();
		
		scoreOnTrainingData = SimpleMath.round(scorer.score(rank(samples)), 4);
		PRINTLN("--------------------------------------------------");
		PRINTLN("Finished sucessfully.");
		PRINTLN(scorer.name() + " on training data: " + scoreOnTrainingData);
		if(validationSamples != null)
		{
			bestScoreOnValidationData = scorer.score(rank(validationSamples));
			PRINTLN(scorer.name() + " on validation data: " + SimpleMath.round(bestScoreOnValidationData, 4));
		}
		PRINTLN("---------------------------------");
	}
	public double eval(DataPoint p)
	{
		return super.eval(p);
	}
	public Ranker clone()
	{
		return new ListNet();
	}
	public String toString()
	{
		return super.toString();
	}
	public String model()
	{
		String output = "## " + name() + "\n";
		output += "## Epochs = " + nIteration + "\n";
		output += "## No. of features = " + features.length + "\n";
		
		//print used features
		for(int i=0;i<features.length;i++)
			output += features[i] + ((i==features.length-1)?"":" ");
		output += "\n";
		//print network information
		output += "0\n";//[# hidden layers, *ALWAYS* 0 since we're using linear net]
		//print learned weights
		output += toString();
		return output;
	}
	public void load(String fn)
	{
		try {
			String content = "";
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(fn), "ASCII"));
			
			List<String> l = new ArrayList<String>();
			while((content = in.readLine()) != null)
			{
				content = content.trim();
				if(content.length() == 0)
					continue;
				if(content.indexOf("##")==0)
					continue;
				l.add(content);
			}
			in.close();
			//load the network
			//the first line contains features information
			String[] tmp = l.get(0).split(" ");
			features = new int[tmp.length];
			for(int i=0;i<tmp.length;i++)
				features[i] = Integer.parseInt(tmp[i]);
			//the 2nd line is a scalar indicating the number of hidden layers
			int nHiddenLayer = Integer.parseInt(l.get(1));
			int[] nn = new int[nHiddenLayer];
			//the next @nHiddenLayer lines contain the number of neurons in each layer
			int i=2;
			for(;i<2+nHiddenLayer;i++)
				nn[i-2] = Integer.parseInt(l.get(i));
			//create the network
			setInputOutput(features.length, 1);
			for(int j=0;j<nHiddenLayer;j++)
				addHiddenLayer(nn[j]);
			wire();
			//fill in weights
			for(;i<l.size();i++)//loop through all layers
			{
				String[] s = l.get(i).split(" ");
				int iLayer = Integer.parseInt(s[0]);//which layer?
				int iNeuron = Integer.parseInt(s[1]);//which neuron?
				Neuron n = layers.get(iLayer).get(iNeuron);
				for(int k=0;k<n.getOutLinks().size();k++)//loop through all out links (synapses) of the current neuron
					n.getOutLinks().get(k).setWeight(Double.parseDouble(s[k+2]));
			}
		}
		catch(Exception ex)
		{
			System.out.println("Error in ListNet::load(): " + ex.toString());
		}
	}
	public void printParameters()
	{
		PRINTLN("No. of epochs: " + nIteration);
		PRINTLN("Learning rate: " + learningRate);
	}
	public String name()
	{
		return "ListNet";
	}
}
