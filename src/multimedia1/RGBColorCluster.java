package multimedia1;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class RGBColorCluster implements Clusterable {

	double [] colors;

	RGBColorCluster(double [] colors) {
		this.colors = colors;
	}
	
	@Override
	public double[] getPoint() {
		return colors;
	}
}
