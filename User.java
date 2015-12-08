import java.util.*;

public class User {
	private double[] features;

	public User(String info) {
		// Parse out the features!
		String[] components = info.split(" ");
		features = new double[6];
		for (int it = 1; it < components.length; it++) {
			int index = Integer.parseInt(components[it].substring(0, 1)) - 1;
			double val = Double.parseDouble(components[it].substring(2));
			features[index] = val;
		}
	}

	public double[] getFeatures() {
		return features;
	}
}
