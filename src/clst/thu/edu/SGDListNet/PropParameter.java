package cslt.thu.edu.SGDListNet;

public class PropParameter {
	//RankNet
	public int current = -1;//index of current data point in the ranked list
	public int[][] pairMap = null;
	public PropParameter(int current, int[][] pairMap)
	{
		this.current = current;
		this.pairMap = pairMap;
	}
	//LambdaRank: RankNet + the following
	public float[][] pairWeight = null;
	public float[][] targetValue = null;
	public PropParameter(int current, int[][] pairMap, float[][] pairWeight, float[][] targetValue)
	{
		this.current = current;
		this.pairMap = pairMap;
		this.pairWeight = pairWeight;
		this.targetValue = targetValue;
	}
	//ListNet
	public float[] labels = null;//relevance label
	public PropParameter(float[] labels)
	{
		this.labels = labels;
	}
}
