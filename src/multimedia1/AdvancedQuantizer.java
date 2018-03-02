package multimedia1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

public class AdvancedQuantizer {

	public static final int INITIAL_LEVELS_Q = 256;
	private static final int MAX_COLOR_VAL = 255;
	

	public byte[] quantize(ImageColorScheme scheme, int q) {
		RGBImageChannel channel = new RGBImageChannel(scheme);
		
		RGBImageChannel channelCopy = new RGBImageChannel(scheme);
		channelCopy.sort(0, channelCopy.colors.size());
		List<RGBColor> clusterCenters = new ArrayList<RGBColor>();
		for(int i = 0; i < q; i++) {
			clusterCenters.add(channelCopy.colors.get(i * (channelCopy.colors.size() / q)));
		}
		
		List<RGBColor> clusteredColors = new KMeans().cluster(channel.colors, q, clusterCenters);
		//List<CentroidCluster<RGBColorCluster>> clusteredColors = new KMeansPlusPlusClusterer<RGBColorCluster>(q).cluster(channel.colors);
		
		int height = scheme.getDisplayImage().getHeight();
		int width = scheme.getDisplayImage().getWidth();
		int totalBytes = height * width * 3;
		byte [] resultImage = new byte[totalBytes];
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(int i = 0; i < height * width; i++) {
			RGBColor ithColor = clusteredColors.get(i);
			resultImage[i] = (byte) quantizeValue(ithColor.colors[0], q);
			resultImage[i + height * width] = (byte) quantizeValue(ithColor.colors[1], q);
			resultImage[i + height * width * 2] = (byte) quantizeValue(ithColor.colors[2], q);
			/*
			resultImage[i] = (byte) (((byte) (Math.rint(quantizeValue(ithColor.colors[0])))) & 0xff);
			resultImage[i + height * width] = (byte) (((byte) Math.rint(quantizeValue(ithColor.colors[1]))) & 0xff);
			resultImage[i + height * width * 2] = (byte) (((byte) Math.rint(quantizeValue(ithColor.colors[2]))) & 0xff);
			*/
			if(i < 10) {
				System.out.println(i + " " + Arrays.toString(ithColor.colors));
				//System.out.println(i + " " + Math.rint(ithColor.colors[0]));
				//System.out.println(i + " " + ((byte) Math.rint(ithColor.colors[0])) & 0xff);
				System.out.println(i + " " + (resultImage[i] & 0xff) + " " + (resultImage[i + height * width] & 0xff) + " " + (resultImage[i + height * width * 2] & 0xff));
			}
			map.put(ithColor.getPix() + " ", 1);
		}
		
		System.out.println("no of colors " + map.size());
		
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
