import java.util.HashMap;
import java.util.List;
import java.lang.Math;
import java.util.Collection;


class UCBSeg implements BanditAlgorithm{

	HashMap<Integer, HashMap<String, Integer>> counts; 
	HashMap<Integer, HashMap<String, Double>> totalRewards; 
	double alpha;

	static final int NUM_GROUPS = 5;


	public UCBSeg(double alpha) {
		counts = new HashMap<Integer, HashMap<String, Integer>>();
		totalRewards = new HashMap<Integer, HashMap<String, Double>>();
		for(int i = 0; i < NUM_GROUPS; i++) {
			counts.put(i, new HashMap<String, Integer>());
			totalRewards.put(i, new HashMap<String, Double>());
		}
		this.alpha = alpha;
	}

	public Article chooseArm(User user, List<Article> articles) {
			
		//find out the group that the user is in
		double[] features = user.getFeatures();
		int group = 1;
		for(int i = 1; i < NUM_GROUPS+1; i++) {
			if (features[i] > features[group]) {
				group = i;
			}
		}
		group -= 1;

		// keep track of best article
		Article bestA = null;
		double bestVal = Double.MIN_VALUE;

		for(Article a : articles) {
			String aId = a.getId();

			// if we have seen this article before
			if(counts.get(group).containsKey(aId)) {
				double count = counts.get(group).get(aId);
				double confidence = alpha / Math.sqrt(count);
				double mean = totalRewards.get(group).get(aId) / count;

				// if this article has a higher upper confidence bound than the best one so far
				if(mean + confidence > bestVal) {
					bestA = a;
					bestVal = mean + confidence;
				} 
			}

			// if we have never seen an article before, assume it has infinite value
			else {
				bestA = a;
				bestVal = Double.MAX_VALUE;
			}
		}

		return bestA;
	}

	public void updateReward(User user, Article a, boolean clicked) {
		
		//find out the group that the user is in
		double[] features = user.getFeatures();
		int group = 1;
		for(int i = 1; i < NUM_GROUPS+1; i++) {
			if (features[i] > features[group]) {
				group = i;
			}
		}
		group -= 1;

		double value = clicked ? 1 : 0;
		String aId = a.getId();

		// if we have seen this article before, update it
		if(counts.get(group).containsKey(aId)) {
			counts.get(group).put(aId, counts.get(group).get(aId) + 1);
			totalRewards.get(group).put(aId, totalRewards.get(group).get(aId) + value);
		}

		//otherwise, add it
		else {
			counts.get(group).put(aId, 1);
			totalRewards.get(group).put(aId, value);
		}
	}

}
