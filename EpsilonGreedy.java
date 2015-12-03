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
	public String chooseArm(Collection<String> arms) {
		boolean explore = Math.random();
		if (explore 
	}

	// Observe a reward when a given arm is pulled.
	public void updateReward(String arm, boolean clicked) {
	}
}
