import java.util.*;
import java.io.*;

/**
  * Sets up and runs the experiment with the provided BanditAlgorithm.
  */
public class ExperimentRunner {
	// The algorithm we want to run the experiment with!
	private BanditAlgorithm algorithm;
	private BufferedReader dataReader;
	private String[] dataFiles;
	private int curFile;
	private int t;

	public static void main(String[] args) {
		// Take in the input files and an algorithm, run UCB on it.
		LinUCB algo = new LinUCB(0.2);
		String[] dF = args;
		int t = 10000;
		ExperimentRunner expR = new ExperimentRunner(algo, dF, t);
		double ctr = expR.runAlgorithm();
		System.out.println("UCB achieved a CTR of " + ctr + " in "
				+ t + " trials");
	}

	public ExperimentRunner(BanditAlgorithm algo, String[] dF, int t) {
		this.algorithm = algo;
		this.dataFiles = dF;
		this.t = t;
		curFile = 0;
		// Set up our buffered reader...
		setUpReader(curFile);
	}

	// Set up the buffered reader to read from file k
	public void setUpReader(int k) {
		try {
			InputStream is = new FileInputStream(dataFiles[k]);
			InputStreamReader isr = new InputStreamReader(is);
			dataReader = new BufferedReader(isr);
		} catch (IOException e) {
			System.err.println("Failed to read file " + dataFiles[k]
					+ ", exiting...");
			System.exit(1);
		}
	}

	// Run the experiment! Keep reading lines from the files and running
	// the bandit algorithm provided.
	public double runAlgorithm() {
		// Keep track of the click-through rate...
		int numTrials = 0;
		int numClicks = 0;
		// Only run t trials total...
		while(numTrials < t) {
			numTrials++;
			// Recover the reward for this arm...
			boolean r_t = runTrial();
			if (r_t) {
				numClicks++;
			}
		}
		// Finally, return click-through rate.
		double ctr = (double) numClicks / (double) numTrials;
		return ctr;
	}

	// Go through the data set and work through the files until we
	// find the user/article combo we want.
	private boolean runTrial() {	
		// Read lines and parse until we find choice...
		boolean r_t = false;
		String articleId = "b";
		User user = null;
		Article choice = null;
		do {
			String curLine = readNextLine();
			String[] articles = curLine.split(" \\|");
			articleId = articles[0].split(" ")[1];
			r_t = articles[0].split(" ")[2].equals("1");
			user = new User(articles[1]);
			// Yank out the articles, send them to the bandit
			// algorithm we're testing, forget it if our algorithm
			// failed to choose the actual choice we made.
			List<Article> articleList = new ArrayList<Article>();
			for (int it = 2; it < articles.length; it++) {
				// Put together the article colleciton...
				
				articleList.add(new Article(articles[it]));

			}
			choice = algorithm.chooseArm(user, articleList);

		} while (!articleId.equals(choice.getId()));
		// Recover the reward from result...
		algorithm.updateReward(user, choice, r_t);
		return r_t;
	}

	// Fetch the next line in the file / the next file...
	private String readNextLine() {
		try {
			String curLine = dataReader.readLine();
			if (curLine == null) {
				// Fetch the next file...
				curFile++;
				if (curFile >= dataFiles.length) {
					curFile = 0;
				}
				setUpReader(curFile);
				curLine = dataReader.readLine();
			}
			return curLine;
		} catch (IOException e) {
			System.err.println("Failed to read line from file "
					+ dataFiles[curFile] + "!");
			System.exit(1);
		}
		return null;
	}
}
