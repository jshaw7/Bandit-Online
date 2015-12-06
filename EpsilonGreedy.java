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
	public Article chooseArm(List<Article> articles) {
		return null;
	}

	// Observe a reward when a given arm is pulled.
	public void updateReward(Article a, boolean clicked) {
	}
}
