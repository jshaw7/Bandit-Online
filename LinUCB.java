import java.util.Collection;
import java.util.HashMap;

public class LinUCB implements BanditAlgorithm {
	HashMap<String, Integer> counts;
	double alpha;

	public LinUCB(double alpha) {
		this.alpha = alpha;
	}

	@Override
	public String chooseArm(Collection<String> arms) {
		String bestArm = "";

		for (String arm : arms) {
			if (!counts.containsKey(arm)) {
			}
		}
		return null;
	}

	@Override
	public void updateReward(String arm, boolean clicked) {
		// TODO Auto-generated method stub

	}

}
