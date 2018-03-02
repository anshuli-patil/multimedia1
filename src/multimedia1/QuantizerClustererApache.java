package multimedia1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

public class QuantizerClustererApache {

	public static final int INITIAL_LEVELS_Q = 256;
	private static final int MAX_COLOR_VAL = 255;
	

	public byte[] quantize(ImageColorScheme scheme, int q) {
		RGBImageChannel channel = new RGBImageChannel(scheme);
		
		List<CentroidCluster<RGBColorCluster>> clusteredColors = new KMeansPlusPlusClusterer<RGBColorCluster>(q)
				.cluster(channel.getClusterableColors());
		
		int height = scheme.getDisplayImage().getHeight();
		int width = scheme.getDisplayImage().getWidth();
		int totalBytes = height * width * 3;
		byte [] resultImage = new byte[totalBytes];
		
		// set corresponding cluster Index
		for (int i = 0; i < channel.colors.size(); i++) {
			RGBColor color = channel.colors.get(i);
			double minDistance = Double.MAX_VALUE;
			for (int j = 0; j < clusteredColors.size(); j++) {
				double dist = new RGBColor(clusteredColors.get(j).getCenter().getPoint()).getDistance(color);
				if(dist < minDistance) {
					minDistance = dist;
					color.clusterIndex = j;
				}
			}
		}
		
		for(int i = 0; i < height * width; i++) {
			int ithColorIndex = channel.colors.get(i).clusterIndex;
			CentroidCluster<RGBColorCluster> centroid = clusteredColors.get(ithColorIndex);
			
			RGBColor ithColor = new RGBColor(centroid.getCenter().getPoint());
			
			resultImage[i] = (byte) quantizeValue(ithColor.colors[0], q);
			resultImage[i + height * width] = (byte) quantizeValue(ithColor.colors[1], q);
			resultImage[i + height * width * 2] = (byte) quantizeValue(ithColor.colors[2], q);

			if(i < 10) {
				System.out.println(i + " " + Arrays.toString(channel.colors.get(i).colors));
				System.out.println(i + " " + Arrays.toString(ithColor.colors));
				//System.out.println(i + " " + Math.rint(ithColor.colors[0]));
				//System.out.println(i + " " + ((byte) Math.rint(ithColor.colors[0])) & 0xff);
				System.out.println(i + " " + (resultImage[i] & 0xff) + " " + (resultImage[i + height * width] & 0xff) + " " + (resultImage[i + height * width * 2] & 0xff));
			}
		}
		return resultImage;
	}
	
	public int quantizeValue(double inputDouble, int q) {
		int input = (int) Math.rint(inputDouble);

		if (q < INITIAL_LEVELS_Q && q > 0) {
			int compressionMultiplier = (256 / q);
			if (input > (255 - compressionMultiplier)) {
				return (255 - compressionMultiplier);
			}
		}
		if (input < 0) {
			input = 0;
		} else if (input >= INITIAL_LEVELS_Q) {
			input = MAX_COLOR_VAL;
		}

		return input;
	}

}
