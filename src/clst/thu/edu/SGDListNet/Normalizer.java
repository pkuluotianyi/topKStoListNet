package cslt.thu.edu.SGDListNet;

import cslt.thu.edu.SGDListNet.RankList;

public interface Normalizer {
	public void normalize(RankList rl, int[] fids);
	public String name();
}
