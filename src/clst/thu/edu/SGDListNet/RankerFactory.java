package cslt.thu.edu.SGDListNet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

import cslt.thu.edu.SGDListNet.ListNet;

/**
 * @author vdang
 * 
 * This class implements the Ranker factory. All ranking algorithms implemented have to be recognized in this class. 
 */
public class RankerFactory {
	public enum RANKER_TYPE {
		MART, RANKBOOST, RANKNET, ADARANK, COOR_ASCENT, LAMBDARANK, LAMBDAMART, LISTNET, RANDOM_FOREST
	}
	protected Ranker[] rFactory = new Ranker[]{new ListNet()};
	protected static Hashtable<String, RANKER_TYPE> map = new Hashtable<String, RANKER_TYPE>();
	
	public RankerFactory()
	{
		map.put(createRanker(RANKER_TYPE.LISTNET).name().toUpperCase(), RANKER_TYPE.LISTNET);
	}
	
	public Ranker createRanker(RANKER_TYPE type)
	{
		Ranker r = rFactory[0].clone();
		return r;
	}
	public Ranker createRanker(List<RankList> samples, int[] features)
	{
		RANKER_TYPE type = RANKER_TYPE.LISTNET;
		Ranker r = createRanker(type);
		r.set(samples, features);
		return r;
	}
	public Ranker loadRanker(String modelFile)
	{
		Ranker r = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(modelFile), "ASCII"));
			String content = in.readLine();//read the first line to get the name of the ranking algorithm
			in.close();
			content = content.replace("## ", "").trim();
			System.out.println("Model:\t\t" + content);
			r = createRanker(map.get(content.toUpperCase()));
			r.load(modelFile);
		}
		catch(Exception ex)
		{
			System.out.println("Error in RankerFactory.load(): " + ex.toString());
		}
		return r;
	}
}
