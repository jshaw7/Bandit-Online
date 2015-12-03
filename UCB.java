import java.util.HashMap;
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

	public String chooseArm(Collection<String> arms) {
		String bestArm = "";
		double bestVal = Double.MIN_VALUE;

		for(String arm : arms) {
			if(counts.containsKey(arm)) {
				double count = counts.get(arm);
				double confidence = alpha / Math.sqrt(count);
				double mean = totalRewards.get(arm) / count;
				if(mean + confidence > bestVal) {
					bestArm = arm;
					bestVal = mean + confidence;
				} 
			}
			else {
				bestArm = arm;
				bestVal = Double.MAX_VALUE;
			}
		}

		return bestArm;
	}

	public void updateReward(String arm, boolean clicked) {
		double value = clicked ? 1 : 0;
		if(counts.containsKey(arm)) {
			counts.put(arm, counts.get(arm) + 1);
			totalRewards.put(arm, totalRewards.get(arm) + value);
		}
		else {
			counts.put(arm, 1);
			totalRewards.put(arm, value);
		}
	}

}
