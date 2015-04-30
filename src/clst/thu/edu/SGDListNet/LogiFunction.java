package cslt.thu.edu.SGDListNet;

public class LogiFunction{
	
	public double compute(double x)
	{
		return (double) (1.0 / (1.0 + Math.exp(-x)));
	}
	
	public double computeDerivative(double x)
	{
		double output = compute(x);
		return (double) (output * (1.0 - output));
	}
}
