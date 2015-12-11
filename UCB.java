import java.util.HashMap;
import java.util.List;
import java.lang.Math;
import java.util.Collection;
class UCB implements BanditAlgorithm{

	HashMap<String, Integer> counts; 
	HashMap<String, Double> totalRewards; 
	double alpha;


	public UCB(double alpha) {
		counts = new HashMap<String, Integer>();
		totalRewards = new HashMap<String, Double>();
		this.alpha = alpha;
	}

	public Article chooseArm(User user, List<Article> articles) {
			
		// keep track of best article
		Article bestA = null;
		double bestVal = Double.MIN_VALUE;

		for(Article a : articles) {
			String aId = a.getId();

			// if we have seen this article before
			if(counts.containsKey(aId)) {
				double count = counts.get(aId);
				double confidence = alpha / Math.sqrt(count);
				double mean = totalRewards.get(aId) / count;

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
		double value = clicked ? 1 : 0;
		String aId = a.getId();

		// if we have seen this article before, update it
		if(counts.containsKey(aId)) {
			counts.put(aId, counts.get(aId) + 1);
			totalRewards.put(aId, totalRewards.get(aId) + value);
		}

		//otherwise, add it
		else {
			counts.put(aId, 1);
			totalRewards.put(aId, value);
		}
	}

}
