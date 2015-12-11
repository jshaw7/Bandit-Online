import java.util.*;

/**
  * Epsilon-greedy: With probability epsilon, explore, and with probability
  * 1 - epsilon, exploit.
  */
public class EpsilonGreedySeg implements BanditAlgorithm {
	private double epsilon;
	private HashMap<Integer, HashMap<String, Integer>> trials;
	private HashMap<Integer, HashMap<String, Integer>> clicks;

	private static final int NUM_GROUPS = 5;

	public EpsilonGreedySeg(double eps) {
		epsilon = eps;
		trials = new HashMap<Integer, HashMap<String, Integer>>();
		clicks = new HashMap<Integer, HashMap<String, Integer>>();
		for(int i = 0; i < NUM_GROUPS; i++) {
			trials.put(i, new HashMap<String, Integer>());
			clicks.put(i, new HashMap<String, Integer>());
		}
	}

	// Choose an arm, given a choice of k arms.
	public Article chooseArm(User user, List<Article> articles) {
		if (Math.random() > epsilon) {
			// Greedy choice...

			//find out the group that the user is in
			double[] features = user.getFeatures();
			int group = 1;
			for(int i = 1; i < NUM_GROUPS+1; i++) {
				if (features[i] > features[group]) {
					group = i;
				}
			}
			group -= 1;

			double best = -1.0;
			Article bestArm = null;
			for (Article a : articles) {
				double rwd = getReward(a, group);
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
		
		//find out the group that the user is in
		double[] features = user.getFeatures();
		int group = 1;
		for(int i = 1; i < NUM_GROUPS+1; i++) {
			if (features[i] > features[group]) {
				group = i;
			}
		}
		group -= 1;
		System.out.println(group);

		// Just make sure we have values for article a...

		getReward(a, group);
		// Update values.
		trials.get(group).put(a.getId(), trials.get(group).get(a.getId()) + 1);
		clicks.get(group).put(a.getId(), clicks.get(group).get(a.getId()) + 1);
	}

	// Get reward for arm...
	private double getReward(Article arm, int group) {
		if (!trials.get(group).containsKey(arm.getId())) {
			trials.get(group).put(arm.getId(), 0);
			clicks.get(group).put(arm.getId(), 0);
		}
		int trialCount = trials.get(group).get(arm.getId());
		int clickCount = clicks.get(group).get(arm.getId());
		if (clickCount < 1) {
			return 0;
		}
		return (double) clickCount / (double) trialCount;
	}
}
