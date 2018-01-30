package multimedia1;

import java.util.Arrays;

public class ColorScheme {

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
	Matrix rgbToYuvMatrix;
	Matrix yuvToRgbMatrix;
	int bitsPerChannel;
	Matrix channelsMatrix;
	
	ColorScheme(double [][] channels) {
		this.type = SCHEME_TYPE.RGB;
		this.channels = channels;
		
		this.rgbToYuvMatrix = new Matrix(3, 3, RGB_TO_YUV_MATRIX);
		this.yuvToRgbMatrix = new Matrix(3, 3, YUV_TO_RGB_MATRIX);
		
		this.bitsPerChannel = channels[0].length;
		this.channelsMatrix = new Matrix(3, bitsPerChannel, this.channels);
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
	
	public static void main(String[] args) {
		double [][] channels = {{0.299, 0.587, 0.114}, {0.596, -0.274, -0.322}, {0.211, -0.523, 0.312}};
		ColorScheme sc = new ColorScheme(channels);
		sc.convertScheme(SCHEME_TYPE.YUV);
		sc.convertScheme(SCHEME_TYPE.RGB);
		sc.convertScheme(SCHEME_TYPE.YUV);
		sc.convertScheme(SCHEME_TYPE.RGB);
		
		System.out.println(Arrays.toString(sc.getR()));
		System.out.println(Arrays.toString(sc.getG()));
		System.out.println(Arrays.toString(sc.getB()));
	}

}
