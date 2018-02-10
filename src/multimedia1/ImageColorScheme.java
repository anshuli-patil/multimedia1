package multimedia1;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * The matrix-vector multiplication should use floating point or double precision, 
 * but the input RGB values are expected to be in the range of 0-255.  
 * For instance, for the RGB to YUV conversion, you need to make sure the matrix and the input RGB are in floating point or double, 
 * then apply the matrix multiplication and produce a YUV result in floating point precision. After you subsample/upsample as in the flow chart, 
 * you can then apply the inverse matrix YUV to RGB on the YUV floating point or double precision values to obtain floating point RGB values. 
 * But before display or saving,  you need to perform the RGB quantization and then convert back to bytes or unsigned char (C++).
 */
public class ImageColorScheme {

	private static final double [][] RGB_TO_YUV_MATRIX = {{0.299, 0.587, 0.114}, {0.596, -0.274, -0.322}, {0.211, -0.523, 0.312}};
	private static final double [][] YUV_TO_RGB_MATRIX = {{1.000, 0.956, 0.621}, {1.000, -0.272, -0.647}, {1.000, -1.106, 1.703}};
	
	private enum SCHEME_TYPE {
		RGB(1), YUV(2);
		
		@SuppressWarnings("unused")
		private int typeCode;

		SCHEME_TYPE(int typeCode) { 
			this.typeCode = typeCode;
		}
	}
	
	private SCHEME_TYPE type;
	private double [][] channels;
	private BufferedImage displayImage;
	
	private Matrix rgbToYuvMatrix;
	private Matrix yuvToRgbMatrix;
	private int bitsPerChannel;
	private Matrix channelsMatrix;
	
	ImageColorScheme(double [][] channels, BufferedImage img) {
		this.type = SCHEME_TYPE.RGB;
		this.channels = channels;
		
		this.rgbToYuvMatrix = new Matrix(3, 3, RGB_TO_YUV_MATRIX);
		this.yuvToRgbMatrix = new Matrix(3, 3, YUV_TO_RGB_MATRIX);
		
		this.bitsPerChannel = channels[0].length;
		this.channelsMatrix = new Matrix(3, bitsPerChannel, this.channels);
		this.displayImage = img;
	}
	
	public double[] getY() {
		checkSchemeAgreement(SCHEME_TYPE.YUV);
		return channels[0];
	}

	public double[] getU() {
		checkSchemeAgreement(SCHEME_TYPE.YUV);
		return channels[1];
	}
	
	public double[] getV() {
		checkSchemeAgreement(SCHEME_TYPE.YUV);
		return channels[2];
	}
	
	public double[] getR() {
		checkSchemeAgreement(SCHEME_TYPE.RGB);
		return channels[0];
	}

	public double[] getG() {
		checkSchemeAgreement(SCHEME_TYPE.RGB);
		return channels[1];
	}
	
	public double[] getB() {
		checkSchemeAgreement(SCHEME_TYPE.RGB);
		return channels[2];
	}

	private void checkSchemeAgreement(SCHEME_TYPE type) {
		if (this.type == type) {
		} else {
			convertScheme(type);
		}
	}
	
	private void convertScheme(SCHEME_TYPE targetType) {
		if(targetType == SCHEME_TYPE.RGB) {
			this.channelsMatrix = yuvToRgbMatrix.multiply(channelsMatrix);
		} else {
			this.channelsMatrix = rgbToYuvMatrix.multiply(channelsMatrix);
		}
		
		this.channels = this.channelsMatrix.getMatrix();
		this.type = targetType;
	}

	public BufferedImage getDisplayImage() {
		return displayImage;
	}
	
	private boolean inRange(int index, int height, int width) {
		if(index >= 0 && index < height * width) {
			return true;
		}
		return false;
	}
	
	public void prefilter(int channelIndex, int height, int width, int sampleRate) {
		int numPixels = channels[channelIndex].length;
		double [] filteredChannel = new double[numPixels];
		
		int filtered = 0;
		// use a convolutional kernel to compute prefiltered values.
		for(int i = 0; i < numPixels; i++) {
			boolean edgeChannel = false;
			
			// skip prefiltering for main samples
			if(((i % width) + 1) % sampleRate == 0) {
				edgeChannel = true;
			}
			
			double channelValue = channels[channelIndex][i] * 0.12;
			if (inRange(i - width, height, width)) {
				channelValue += channels[channelIndex][i - width] * 0.11;
			} else {
				edgeChannel = true;
			}
			if (inRange(i - width - 1, height, width)) {
				channelValue += channels[channelIndex][i - width - 1] * 0.11;
			} else {
				edgeChannel = true;
			}
			if (inRange(i - width + 1, height, width)) {
				channelValue += channels[channelIndex][i - width + 1] * 0.11;
			} else {
				edgeChannel = true;
			}
			if (inRange(i + width, height, width)) {
				channelValue += channels[channelIndex][i + width] * 0.11;
			} else {
				edgeChannel = true;
			}
			if (inRange(i + width - 1, height, width)) {
				channelValue += channels[channelIndex][i + width - 1] * 0.11;
			} else {
				edgeChannel = true;
			}
			if (inRange(i + width + 1, height, width)) {
				channelValue += channels[channelIndex][i + width + 1] * 0.11;
			} else {
				edgeChannel = true;
			}
			if (inRange(i - 1, height, width)) {
				channelValue += channels[channelIndex][i - 1] * 0.11;
			} else {
				edgeChannel = true;
			}
			if (inRange(i + 1, height, width)) {
				channelValue += channels[channelIndex][i + 1] * 0.11;
			} else {
				edgeChannel = true;
			}
			if (!edgeChannel) {
				filteredChannel[i] = channelValue;
			} else {
				filteredChannel[i] = channels[channelIndex][i];
				filtered++;
			}
		}
		System.out.println(filtered + " number of pixels not prefiltered");
		// Copy over the values of the prefiltered channel
		for(int i = 0; i < numPixels; i++) {
			channels[channelIndex][i] = filteredChannel[i];
		}
	}
	
	public static void main(String[] args) {
		double [][] channels = {{0.299, 0.587, 0.114}, {0.596, -0.274, -0.322}, {0.211, -0.523, 0.312}};
		ImageColorScheme sc = new ImageColorScheme(channels, null);
		sc.convertScheme(SCHEME_TYPE.YUV);
		sc.convertScheme(SCHEME_TYPE.RGB);
		sc.convertScheme(SCHEME_TYPE.YUV);
		sc.convertScheme(SCHEME_TYPE.RGB);
		
		System.out.println(Arrays.toString(sc.getR()));
		System.out.println(Arrays.toString(sc.getG()));
		System.out.println(Arrays.toString(sc.getB()));
	}
}
