import java.util.*;

public class Article {
	private double[] features;
	private String id;

	public Article(String info) {
		// Parse out the features!
		String[] components = info.split(" ");
		id = components[0];
		features = new double[6];
		for (int it = 1; it < components.length; it++) {
			int index = Integer.parseInt(components[it].substring(0, 1)) - 1;
			double val = Double.parseDouble(components[it].substring(2));
			features[index] = val;
		}
	}

	public String getId() {
		return id;
	}

	public double[] getFeatures() {
		return features;
	}
}
