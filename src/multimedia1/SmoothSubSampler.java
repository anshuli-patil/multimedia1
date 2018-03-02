package multimedia1;

import java.util.Arrays;

public class SmoothSubSampler {
	
	double increments;
	
	/**
	 * 
	 * @param channels the pixels in YUV format
	 * @param subsampleY the ratio for subsampling in Y channel. 
	 * For eg. 2 means subsample Y channel by 2. Delete every other bit value.
	 * @param subsampleU the ratio for subsampling in U channel
	 * @param subsampleV the ratio for subsampling in V channel
	 * @return 
	 */
	public ImageColorScheme subsample(ImageColorScheme scheme, int subsampleY, int subsampleU, int subsampleV) {
		int height = scheme.getDisplayImage().getHeight();
		int width = scheme.getDisplayImage().getWidth();
		
		subsampleChannel(scheme.getY(), subsampleY, height, width);
		subsampleChannel(scheme.getU(), subsampleU, height, width);
		subsampleChannel(scheme.getV(), subsampleV, height, width);
		return scheme;
	}
	
	private void subsampleChannel(double[] channel, int sampleRate, int height, int width) {
		for(int row = 0; row < height; row++) {
			for(int col = 0; col < width; col += sampleRate) {

				setNeighborhoodSampleParams(channel, row, col, sampleRate, height, width);
				for(int diff = 1; diff < Math.min(sampleRate, width - col); diff++) {
					channel[row * width + col + diff] = channel[row * width + col] + increments * diff;
				}
			}
		}
	}

	private void setNeighborhoodSampleParams(double [] channel, int row, int col, int sampleRate, int height, int width) {
		double startValue = channel[row * width + col]; // Corresponding to the block's intact color
		
		if(col + sampleRate < width) { // If neighbor sample is on the same row
			double endValue = channel[(row * width + col) + sampleRate];
			increments = (endValue - startValue) / sampleRate;
		} else {
			increments = 0; // no neighbor to average out, copy over MAIN sample
		}
	}
	
	public static void main(String[] args) {
		double [] channel = {1, 1, 1, 0, 0, 0, 0, 0, 1};
		new SmoothSubSampler().subsampleChannel(channel, 3, 3, 3);
		System.out.println(Arrays.toString(channel));
	}

}
