package multimedia1;

public class RGBQuantizer {
	
	public static final int INITIAL_LEVELS_Q = 256;
	
	public byte[] quantize(ImageColorScheme scheme, int q) {
		int qLevels = powerOf2Ceil(q);
		float compressionRatio = INITIAL_LEVELS_Q / qLevels;
		
		int height = scheme.getDisplayImage().getHeight();
		int width = scheme.getDisplayImage().getWidth();
		int totalBytes = height * width * 3;
		byte [] resultImage = new byte[totalBytes];
		
		// Quantize by channel
		quantizeChannel(scheme.getR(), 0, compressionRatio, resultImage); //R channel quantization
		quantizeChannel(scheme.getG(), height * width, compressionRatio, resultImage);
		quantizeChannel(scheme.getB(), height * width * 2, compressionRatio, resultImage);
		
		return resultImage;
	}
	
	private void quantizeChannel(double[] channel, int channelStartIndex, float compressionRatio, byte [] resultImage) {

		for(int i = 0; i < channel.length; i++) {
			int intPrecisionSample;
			
			// TODO clamp down the values?
			double sampleValue = channel[i];
			if(sampleValue > 127) {
				channel[i] = 127;
			} else if (sampleValue < -128) {
				channel[i] = -128;
			}
			
			sampleValue = channel[i] / compressionRatio;
			double fractionalDiff = sampleValue - (long) sampleValue;
			if(fractionalDiff < 0.5) {
				intPrecisionSample = (int) Math.floor(sampleValue);
			} else {
				intPrecisionSample = (int) Math.ceil(sampleValue);
			}

			resultImage[i + channelStartIndex] = (byte) intPrecisionSample;			
		}
	}

	private int powerOf2Ceil(int q) {
		return (int) Math.pow(2, Math.ceil(Math.log(q)/Math.log(2)));
	}

}
