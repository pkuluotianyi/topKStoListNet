package cslt.thu.edu.evaluation;

import java.util.Hashtable;

/**
 * @author vdang
 */
public class MetricScorerFactory {

	private static MetricScorer[] mFactory = new MetricScorer[]{new APScorer(), new NDCGScorer(), new PrecisionScorer()};
	private static Hashtable<String, MetricScorer> map = new Hashtable<String, MetricScorer>();
	
	public MetricScorerFactory()
	{
		map.put("MAP", new APScorer());
		map.put("NDCG", new NDCGScorer());
		map.put("P", new PrecisionScorer());
	}
	public MetricScorer createScorer(METRIC metric)
	{
		return mFactory[metric.ordinal() - METRIC.MAP.ordinal()].clone();
	}
	public MetricScorer createScorer(METRIC metric, int k)
	{
		MetricScorer s = mFactory[metric.ordinal() - METRIC.MAP.ordinal()].clone();
		s.setK(k);
		return s;
	}
	public MetricScorer createScorer(String metric)//e.g.: metric = "NDCG@5"
	{
		int k = -1;
		String m = "";
		MetricScorer s = null;
		if(metric.indexOf("@") != -1)
		{
			m = metric.substring(0, metric.indexOf("@"));
			k = Integer.parseInt(metric.substring(metric.indexOf("@")+1));
			s = map.get(m.toUpperCase()).clone();
			s.setK(k);
		}
		else
			s = map.get(metric.toUpperCase()).clone();
		return s;
	}
}
