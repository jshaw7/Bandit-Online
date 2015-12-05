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
	public String chooseArm(List<String> arms, List<Article> articles) {
		String bestArm = "";
		double bestArmP = Double.MAX_VALUE;
		RealMatrix Aa;
		RealMatrix ba;
		for (int armNum = 0; armNum < arms.size(); armNum++) {
			String arm = arms.get(armNum);
			double[] articleFeatureV = articles.get(armNum).getFeatures();
			// If not contained, then make new identity matrix and zero vector
			if (!AMap.containsKey(arm)) {
				Aa = MatrixUtils
						.createRealIdentityMatrix(articleFeatureV.length);
				double[] zeros = { 0, 0, 0, 0, 0, 0 };
				ba = MatrixUtils.createColumnRealMatrix(zeros);
			} else {
				Aa = AMap.get(arm);
				ba = bMap.get(arm);
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
				bestArm = arm;
			}
		}
		return bestArm;
	}

	@Override
	public void updateReward(String arm, boolean clicked) {
		// TODO Auto-generated method stub

	}

}
