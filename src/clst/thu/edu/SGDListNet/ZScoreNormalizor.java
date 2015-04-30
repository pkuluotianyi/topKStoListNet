package cslt.thu.edu.SGDListNet;

import java.util.Arrays;

import cslt.thu.edu.SGDListNet.DataPoint;
import cslt.thu.edu.SGDListNet.RankList;

/**
 * @author vdang
 */
public class ZScoreNormalizor implements Normalizer {

	@Override
	public void normalize(RankList rl, int[] fids) {
		
		float[] mean = new float[fids.length];
		float[] std = new float[fids.length];
		Arrays.fill(mean, 0);
		Arrays.fill(std, 0);
		for(int i=0;i<rl.size();i++)
		{
			//System.out.println(rl.size());
			DataPoint dp = rl.get(i);
			for(int j=0;j<fids.length;j++)
				mean[j] += dp.getFeatureValue(fids[j]);
		}
		
		for(int j=0;j<fids.length;j++)
		{
			mean[j] = mean[j] / rl.size();
			for(int i=0;i<rl.size();i++)
			{
				DataPoint p = rl.get(i);
				float x = p.getFeatureValue(fids[j]) - mean[j];
				std[j] += x*x;
			}
			std[j] = (float) Math.sqrt(std[j] / (rl.size()-1));
			//normalize
			if(std[j] > 0.0)
			{
				for(int i=0;i<rl.size();i++)
				{
					DataPoint p = rl.get(i);
					float x = (p.getFeatureValue(fids[j]) - mean[j])/std[j];//x ~ standard normal (0, 1)
					p.setFeatureValue(fids[j], x);
				}
			}
		}
	}
	public String name()
	{
		return "zscore";
	}
}
