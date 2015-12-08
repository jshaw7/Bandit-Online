import java.util.*;

/**
  * Epsilon-greedy: With probability epsilon, explore, and with probability
  * 1 - epsilon, exploit.
  */
public class EpsilonGreedy implements BanditAlgorithm {
	private double epsilon;
	private HashMap<String, Integer> trials;
	private HashMap<String, Integer> clicks;

	// Choose an arm, given a choice of k arms.
	public Article chooseArm(User user, List<Article> articles) {
		if (Math.random() < epsilon) {
			// Greedy choice...
			double best = -1.0;
			Article bestArm = null;
			for (Article a : articles) {
				double rwd = getReward(a);
				if (rwd >= best) {
					best = rwd;
					bestArm = a;
				}
			}
			return bestArm;
		}
		// Choose randomly...
		int randArm = (int) (Math.random() * articles.size());
		Article rv = articles.get(randArm);
		return rv;
	}

	// Observe a reward when a given arm is pulled.
	public void updateReward(User user, Article a, boolean clicked) {
		// Just make sure we have values for article a...
		getReward(a);
		// Update values.
		trials.put(a.getId(), trials.get(a) + 1);
		clicks.put(a.getId(), clicks.get(a) + 1);
	}

	// Get reward for arm...
	private double getReward(Article arm) {
		if (!trials.containsKey(arm.getId())) {
			trials.put(arm.getId(), 0);
			clicks.put(arm.getId(), 0);
		}
		int trialCount = trials.get(arm.getId());
		int clickCount = clicks.get(arm.getId());
		if (clickCount < 1) {
			return 0;
		}
		return (double) clickCount / (double) trialCount;
	}
}
