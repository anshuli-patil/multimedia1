package multimedia1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RGBImageChannel {
	List<RGBColor> colors;
	
	RGBImageChannel(ImageColorScheme scheme) {
		double[] rChannel = scheme.getR();
		double[] gChannel = scheme.getG();
		double[] bChannel = scheme.getB();
		colors = new ArrayList<RGBColor>();
		
		for(int i = 0; i < rChannel.length; i++) {
			colors.add(new RGBColor(rChannel[i], gChannel[i], bChannel[i]));
		}
	}
	
	List<RGBColorCluster> getClusterableColors() {
		List<RGBColorCluster> clustered = new ArrayList<RGBColorCluster>();
		for(int i = 0; i < colors.size(); i++) {
			clustered.add(new RGBColorCluster(colors.get(i).colors));
		}
		return clustered;
	}
	
	void sort(int startIndex, int endIndex) {
		List<RGBColor> sublistColors = colors.subList(startIndex, endIndex);
		maxRangeChannel(sublistColors); //sets the max range channel
		Collections.sort(sublistColors);
		
		// replace in original 
		for(int i = startIndex; i < endIndex; i++) {
			colors.set(i, sublistColors.get(i - startIndex));
		}
	}
	
	// inclusive range [startIndex, endIndex]
	private void maxRangeChannel(List<RGBColor> colorsList) {
		
		double [] minValues = new double[3];
		double [] maxValues = new double[3];
		
 		for(int i = 0; i <= colorsList.size(); i++) {
			for(int j = 0; i < 3; i++) {
				if(minValues[j] > colorsList.get(i).colors[j]) {
					minValues[j] = colorsList.get(i).colors[j];
				}
				if(maxValues[j] < colorsList.get(i).colors[j]) {
					maxValues[j] = colorsList.get(i).colors[j];
				}
			}
		}

 		// set max Range channel for each RGBColor object
 		int maxRange = 0;
 		int maxRangeChannel = -1;
 		for(int i = 0; i < 3; i++) {
 			maxRange = (int) Math.rint(Math.max(maxRange, maxValues[i] - minValues[i]));
 			maxRangeChannel = i;
 		}
 		
 		for(int i = 0; i < colorsList.size(); i++) {
 			colorsList.get(i).maxRangeChannel = maxRangeChannel;
 		}
	}
}
