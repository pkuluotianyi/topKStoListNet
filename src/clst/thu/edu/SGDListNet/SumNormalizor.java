package cslt.thu.edu.SGDListNet;

import java.util.Arrays;

import cslt.thu.edu.SGDListNet.DataPoint;
import cslt.thu.edu.SGDListNet.RankList;

/**
 * @author vdang
 */
public class SumNormalizor implements Normalizer {

	@Override
	public void normalize(RankList rl, int[] fids) {
		float[] norm = new float[fids.length];
		Arrays.fill(norm, 0);
		for(int i=0;i<rl.size();i++)
		{
			DataPoint dp = rl.get(i);
			for(int j=0;j<fids.length;j++)
				norm[j] += Math.abs(dp.getFeatureValue(fids[j]));
		}
		for(int i=0;i<rl.size();i++)
		{
			DataPoint dp = rl.get(i);
			dp.normalize(fids, norm);
		}
	}
	public String name()
	{
		return "sum";
	}
}
