package cslt.thu.edu.SGDListNet;

import java.util.Random;

/**
 * @author vdang
 */
public class Synapse {
	static Random random = new Random();
	protected double weight = 0.0;
	protected double dW = 0.0;//last weight adjustment
	protected Neuron source = null;
	protected Neuron target = null;
	
	public Synapse(Neuron source, Neuron target)
	{
		this.source = source;
		this.target = target;
		this.source.getOutLinks().add(this);
		this.target.getInLinks().add(this);
		//weight = random.nextDouble()/5;
		weight = (random.nextInt(2)==0?1:-1)*random.nextFloat()/100000;
	}
	public Neuron getSource()
	{
		return source;
	}
	public Neuron getTarget()
	{
		return target;
	}
	public void setWeight(double w)
	{
		this.weight = w;
	}
	public double getWeight()
	{
		return weight;
	}
	public double getLastWeightAdjustment()
	{
		return dW;
	}
	public void setWeightAdjustment(double dW)
	{
		this.dW = dW;
	}
	public void updateWeight()
	{
		this.weight += dW;
	}
}
