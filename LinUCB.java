import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class LinUCB implements BanditAlgorithm {
	HashMap<String, RealMatrix> AMap;
	HashMap<String, RealMatrix> bMap;
	double alpha;

	public LinUCB(double alpha) {
		this.alpha = alpha;
		AMap = new HashMap<String, RealMatrix>();
		bMap = new HashMap<String, RealMatrix>();
	}

	@Override
	public Article chooseArm(List<Article> articles) {
		Article bestA = null;
		double bestArmP = Double.MIN_VALUE;
		RealMatrix Aa;
		RealMatrix ba;
		for (Article a : articles) {
			
			String aId = a.getId();
			double[] articleFeatureV = a.getFeatures();

			// If not contained, then make new identity matrix and zero vector
			if (!AMap.containsKey(aId)) {
				Aa = MatrixUtils
						.createRealIdentityMatrix(articleFeatureV.length);
				double[] zeros = { 0, 0, 0, 0, 0, 0 };
				AMap.put(aId, Aa); //set as identity for now and we will update in reward
				ba = MatrixUtils.createColumnRealMatrix(zeros);
				bMap.put(aId, ba);
			} else {
				Aa = AMap.get(aId);
				ba = bMap.get(aId);
			}
			// Make column vector out of features
			RealMatrix xta = MatrixUtils
					.createColumnRealMatrix(articleFeatureV);
			RealMatrix theta = MatrixUtils.inverse(Aa).multiply(ba);
			// Will have to index into matrix of one value after multiplication
			double newP = theta.transpose().multiply(xta).getData()[0][0]
					+ alpha
					* Math.sqrt(xta.transpose()
							.multiply(MatrixUtils.inverse(Aa)).multiply(xta)
							.getData()[0][0]);
			// Update argmax
			if (newP > bestArmP) {
				bestArmP = newP;
				bestA = a;
			}
		}
		return bestA;
	}

	@Override
	public void updateReward(Article a, boolean clicked) {
		String aId = a.getId();
		RealMatrix xta = MatrixUtils
				.createColumnRealMatrix(a.getFeatures());
		AMap.put(aId, AMap.get(aId).add(xta.multiply(xta.transpose())));
		if (clicked){
			bMap.put(aId, bMap.get(aId).add(xta));
		}
		
		
		

	}

}
