import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class HybridLinUCB implements BanditAlgorithm {
	/**
	 * Implements HybridLinUCB from Li, 2010 Written by @smsachs
	 */
	HashMap<String, RealMatrix> AMap;
	HashMap<String, RealMatrix> bMap;
	HashMap<String, RealMatrix> BMap;
	RealMatrix A0;
	RealMatrix b0;
	RealMatrix BetaHat;
	double alpha;

	public HybridLinUCB(double alpha) {
		this.alpha = alpha;
		AMap = new HashMap<String, RealMatrix>();
		bMap = new HashMap<String, RealMatrix>();
		BMap = new HashMap<String, RealMatrix>();

		// Need to double check that it is 6 long
		double[] zeroArrayKLong = new double[36];
		Double zero = new Double(0);
		Arrays.fill(zeroArrayKLong, zero);
		A0 = MatrixUtils.createRealIdentityMatrix(36);
		b0 = MatrixUtils.createColumnRealMatrix(zeroArrayKLong);
		BetaHat = MatrixUtils.inverse(A0).multiply(b0);

	}

	@Override
	public Article chooseArm(User user, List<Article> articles) {
		Article bestA = null;
		double bestArmP = Double.MIN_VALUE;

		RealMatrix Aa;
		RealMatrix Ba;
		RealMatrix ba;

		for (Article a : articles) {
			String aId = a.getId();
			if (!AMap.containsKey(aId)) {
				Aa = MatrixUtils.createRealIdentityMatrix(6);
				AMap.put(aId, Aa); // set as identity for now and we will update
									// in reward

				double[] zeros = { 0, 0, 0, 0, 0, 0 };
				ba = MatrixUtils.createColumnRealMatrix(zeros);
				bMap.put(aId, ba);

				double[][] BMapZeros = new double[6][36];
				for (double[] row : BMapZeros) {
					Arrays.fill(row, 0.0);
				}
				Ba = MatrixUtils.createRealMatrix(BMapZeros);
				BMap.put(aId, Ba);
			} else {
				Aa = AMap.get(aId);
				ba = bMap.get(aId);
				Ba = BMap.get(aId);
			}

			// Make column vector out of features
			RealMatrix xta = MatrixUtils
					.createColumnRealMatrix(a.getFeatures());
			RealMatrix zta = makeZta(
					MatrixUtils.createColumnRealMatrix(user.getFeatures()), xta);

			// Set up common variables
			RealMatrix A0Inverse = MatrixUtils.inverse(A0);
			RealMatrix AaInverse = MatrixUtils.inverse(Aa);
			RealMatrix ztaTranspose = zta.transpose();
			RealMatrix BaTranspose = Ba.transpose();
			RealMatrix xtaTranspose = xta.transpose();

			// Find theta
			RealMatrix theta = AaInverse.multiply(ba.subtract(Ba
					.multiply(BetaHat)));
			// Find sta
			RealMatrix staMatrix = ztaTranspose.multiply(A0Inverse).multiply(
					zta);
			staMatrix = staMatrix.subtract(ztaTranspose.multiply(A0Inverse)
					.multiply(BaTranspose).multiply(AaInverse).multiply(xta)
					.scalarMultiply(2));
			staMatrix = staMatrix.add(xtaTranspose.multiply(AaInverse)
					.multiply(xta));
			staMatrix = staMatrix.add(xtaTranspose.multiply(AaInverse)
					.multiply(Ba).multiply(A0Inverse).multiply(BaTranspose)
					.multiply(AaInverse).multiply(xta));

			// Find pta for arm
			RealMatrix ptaMatrix = ztaTranspose.multiply(BetaHat);
			ptaMatrix = ptaMatrix.add(xtaTranspose.multiply(theta));
			double ptaVal = ptaMatrix.getData()[0][0];
			double staVal = staMatrix.getData()[0][0];
			ptaVal = ptaVal + alpha * Math.sqrt(staVal);

			// Update argmax
			if (ptaVal > bestArmP) {
				bestArmP = ptaVal;
				bestA = a;
			}
		}
		return bestA;
	}

	@Override
	public void updateReward(User user, Article a, boolean clicked) {
		String aId = a.getId();
		// Collect Variables
		RealMatrix xta = MatrixUtils.createColumnRealMatrix(a.getFeatures());
		RealMatrix zta = makeZta(
				MatrixUtils.createColumnRealMatrix(user.getFeatures()), xta);

		RealMatrix Aa = AMap.get(aId);
		RealMatrix ba = bMap.get(aId);
		RealMatrix Ba = BMap.get(aId);

		// Find common transpose/inverse to save computation
		RealMatrix AaInverse = MatrixUtils.inverse(Aa);
		RealMatrix BaTranspose = Ba.transpose();
		RealMatrix xtaTranspose = xta.transpose();
		RealMatrix ztaTranspose = zta.transpose();

		// Update
		A0 = A0.add(BaTranspose.multiply(AaInverse).multiply(Ba));
		b0 = b0.add(BaTranspose.multiply(AaInverse).multiply(ba));
		Aa = Aa.add(xta.multiply(xtaTranspose));
		AMap.put(aId, Aa);
		Ba = Ba.add(xta.multiply(ztaTranspose));
		BMap.put(aId, Ba);
		if (clicked) {
			ba = ba.add(xta);
			bMap.put(aId, ba);
		}

		// Update A0 and b0 with the new values
		A0 = A0.add(zta.multiply(ztaTranspose)).subtract(
				Ba.transpose().multiply(MatrixUtils.inverse(Aa).multiply(Ba)));
		b0 = b0.subtract(Ba.transpose().multiply(MatrixUtils.inverse(Aa))
				.multiply(ba));
		if (clicked) {
			b0 = b0.add(zta);
		}
	}

	public RealMatrix makeZta(RealMatrix userFeature, RealMatrix articleFeature) {
		RealMatrix product = userFeature.multiply(articleFeature.transpose());
		double[][] productData = product.getData();
		double[] productVector = new double[36];
		int count = 0;
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 6; col++) {
				productVector[count] = productData[row][col];
				count++;
			}
		}
		return MatrixUtils.createColumnRealMatrix(productVector);
	}

}
