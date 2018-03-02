package multimedia1;

public class RGBQuantizer {
	
	public static final int INITIAL_LEVELS_Q = 256;
	private static final int MAX_COLOR_VAL = 255;
	
	public byte[] quantize(ImageColorScheme scheme, int q) {
		int qLevels = powerOf2Ceil(q);
		float compressionRatio = INITIAL_LEVELS_Q / qLevels;
		
		int height = scheme.getDisplayImage().getHeight();
		int width = scheme.getDisplayImage().getWidth();
		int totalBytes = height * width * 3;
		byte [] resultImage = new byte[totalBytes];
		
		// Quantize by channel
		quantizeChannel(scheme.getR(), 0, compressionRatio, resultImage, q); //R channel quantization
		quantizeChannel(scheme.getG(), height * width, compressionRatio, resultImage, q);
		quantizeChannel(scheme.getB(), height * width * 2, compressionRatio, resultImage, q);
		
		return resultImage;
	}
	
	public void quantizeChannel(double[] channel, int channelStartIndex, float compressionRatio, byte [] resultImage, int q) {

		for(int i = 0; i < channel.length; i++) {
			
			/*
			int intPrecisionSample;
			
			double sampleValue = (((byte) channel[i] & 0xff) + 1) / compressionRatio; 
			double fractionalDiff = sampleValue - (long) sampleValue;
			if(fractionalDiff < 0.5) {
				intPrecisionSample = (int) Math.floor(sampleValue);
			} else {
				intPrecisionSample = (int) Math.ceil(sampleValue);
			}
			
			resultImage[i + channelStartIndex] = (byte) Math.min(Math.max(0, intPrecisionSample - 1), q  - 1);
			*/

			//resultImage[i + channelStartIndex] = (byte) quantizeValue((int)((byte)channel[i] & 0xff), q);
			resultImage[i + channelStartIndex] = (byte) quantizeValue((int) Math.rint(channel[i]), q);
		}
	}

	private int powerOf2Ceil(int q) {
		return (int) Math.pow(2, Math.ceil(Math.log(q)/Math.log(2)));
	}

	// quantize per channel's value (R/G/B)
	private int quantizeValue(int input, int q) {
		if (q < INITIAL_LEVELS_Q && q > 0) {
			int compressionMultiplier = (256 / q);
			if (input > (255 - compressionMultiplier)) {
				return (255 - compressionMultiplier);
			}
			int qLevel = (int) Math.rint((input + 1.0) / compressionMultiplier);
			return compressionMultiplier * qLevel - 1;
		}
		if (input < 0) {
			input = 0;
		} else if (input >= INITIAL_LEVELS_Q) {
			input = MAX_COLOR_VAL;
		}

		return input;
	}
}
