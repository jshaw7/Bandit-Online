import java.util.*;

/**
  * Basic interface for Bandit Algorithms: Should be able to, given a choice of
  * k arms, select one of them, and then update based on the observed reward.
  */
public interface BanditAlgorithm {
	// Choose an arm, given a choice of k arms.
	public String chooseArm(List<String> arms, List<Article> articles);

	// Observe a reward when a given arm is pulled.
	public void updateReward(String arm, boolean clicked);
}
