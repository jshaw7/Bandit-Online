import java.util.*;
import java.io.*;

/**
  * Sets up and runs the experiment with the provided BanditAlgorithm.
  */
public class ExperimentRunner {
	// The algorithm we want to run the experiment with!
	private BanditAlgorithm algorithm;
	private BufferedReader[] dataReaders;
	private String[] dataFiles;
	private int curFile;
	// Approx. 45 million records... leave off 100K for safety's sake.
	private static int numRecords = 44900000;
	private static double trainingRatio = 0.2;

	public static void main(String[] args) {
		// Take in the input files and an algorithm, run UCB on it.
		BanditAlgorithm algo = new HybridLinUCB(0.4);
		//BanditAlgorithm algo = new EpsilonGreedy(0.3);
		String[] dF = args;
		double pct = 0.3;
		double[] ctr = ExperimentRunner.runExperiment(algo, pct, dF);
		System.out.println("Eps-greedy (learning) achieved a CTR of "
				+ ctr[0] + " using " + pct + " of the data.");
		System.out.println("Eps-greedy (deployment) achieved a CTR of "
				+ ctr[1] + " using " + pct + " of the data.");
	}

	public static double[] runExperiment(BanditAlgorithm algo,
			double pctData, String[] dF) {
		// Run algo on pctData of the input data...
		ExperimentRunner expRunner = new ExperimentRunner(algo, dF);
		int totalRecords = (int) (numRecords * pctData);
		int trainRecords = (int) (totalRecords * trainingRatio);
		int testRecords = totalRecords - trainRecords;
		// Run the algorithm and store both the learning and deployment
		// CTR values.
		double[] rv = new double[2];
		rv[0] = expRunner.runAlgorithm(trainRecords);
		rv[1] = expRunner.runAlgorithm(testRecords);
		return rv;
	}

	public ExperimentRunner(BanditAlgorithm algo, String[] dF) {
		this.algorithm = algo;
		this.dataFiles = dF;
		this.dataReaders = new BufferedReader[dF.length];
		curFile = 0;
		// Set up our buffered readers...
		for (int it = 0; it < dF.length; it++) {
			setUpReader(it);
		}
	}

	// Set up the buffered reader to read from file k
	public void setUpReader(int k) {
		try {
			InputStream is = new FileInputStream(dataFiles[k]);
			InputStreamReader isr = new InputStreamReader(is);
			dataReaders[k] = new BufferedReader(isr);
		} catch (IOException e) {
			System.err.println("Failed to read file " + dataFiles[k]
					+ ", exiting...");
			System.exit(1);
		}
	}

	private int recordsSeen;
	// Run the experiment! Keep reading lines from the files and running
	// the bandit algorithm provided.
	public double runAlgorithm(int k) {
		// Keep track of the click-through rate...
		int numTrials = 0;
		int numClicks = 0;
		recordsSeen = 0;
		// Only run t trials total...
		while(recordsSeen < k) {
			// Recover the reward for this arm...
			boolean r_t = runTrial();
			if (r_t) {
				numClicks++;
			}
			// Move to the next file in our data set.
			boolean allNull = !switchFile();
			if (allNull) {
				// No more data, just return the current CTR.
				break;
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
			recordsSeen++;
			// If we're out of data, readNextLine() returns null.
			if (curLine == null) {
				return false;
			}
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
			if (dataReaders[curFile] == null) {
				return null;
			}
			String curLine = dataReaders[curFile].readLine();
			if (curLine == null) {
				// No more data to read from current file.
				dataReaders[curFile].close();
				dataReaders[curFile] = null;
				// Fetch the next non-null file...
				boolean allNull = !switchFile();
				if (allNull) {
					return null;
				}
				curLine = readNextLine();
			}
			return curLine;
		} catch (IOException e) {
			System.err.println("Failed to read line from file "
					+ dataFiles[curFile] + "!");
			System.exit(1);
		}
		return null;
	}

	// Switch the file currently in use. If they're all null, return false.
	private boolean switchFile() {
		for (int it = 0; it < dataFiles.length; it++) {
			curFile++;
			if (curFile >= dataFiles.length) {
				curFile = 0;
			}
			// Not null? OK, we're done, break!
			if (dataReaders[curFile] != null) {
				return true;
			}
		}
		return false;
	}
}
