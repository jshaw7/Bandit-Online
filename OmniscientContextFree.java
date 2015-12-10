import java.util.*;
import java.io.*;

/**
  * Epsilon-greedy: With probability epsilon, explore, and with probability
  * 1 - epsilon, exploit.
  */
public class OmniscientContextFree implements BanditAlgorithm {
	private HashMap<String, Integer> trials;
	private HashMap<String, Integer> clicks;

	public OmniscientContextFree(String[] dF) {
		// Set up HashMaps...
		trials = new HashMap<String, Integer>();
		clicks = new HashMap<String, Integer>();
		// Read/parse every single record and udpate trials/clicks.
		for (int it = 0; it < dF.length; it++) {
			try {
				InputStream is = new FileInputStream(dF[it]);
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader bR = new BufferedReader(isr);
				// Now, read all records in...
				String curLine = bR.readLine();
				do {
					// Parse and update trials/clicks.
					String[] art = curLine.split(" \\|");
					String[] entries = art[0].split(" ");
					String articleId = entries[1];
					int r_t = Integer.parseInt(entries[2]);
					updateVal(articleId, r_t);
					curLine = bR.readLine();
				} while (curLine != null);
			} catch(IOException e) {
				System.err.println("Failed to read file "
						+ dF[it] + "!");
				System.exit(1);
			}
		}
	}

	public void updateVal(String articleId, int val) {
		if (!trials.containsKey(articleId)) {
			trials.put(articleId, 0);
			clicks.put(articleId, 0);
		}
		trials.put(articleId, trials.get(articleId) + 1);
		clicks.put(articleId, trials.get(articleId) + val);
	}

	// Choose an arm, given a choice of k arms.
	public Article chooseArm(User user, List<Article> articles) {
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

	// Observe a reward when a given arm is pulled.
	public void updateReward(User user, Article a, boolean clicked) {
		// DO NOTHING - we already know what the best arms are.
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
			return 0.0;
		}
		return (double) clickCount / (double) trialCount;
	}
}
