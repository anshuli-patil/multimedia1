package multimedia1;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class ImageReader {

	public static final int width = 352;
	public static final int height = 288;
	
	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;

	/**
	 * @param imgFile
	 *            image file to be read
	 * @param width
	 *            of the image
	 * @param height
	 *            of the image
	 * @param withChannels
	 *            if the image description includes a channels representation
	 * @return Image description for subsampling/quanitization operations
	 */
	public ImageColorScheme readImage(File imgFile, int width, int height, boolean withChannels) {
		double[][] channels = null;

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		try (InputStream is = new FileInputStream(imgFile)) {

			long len = imgFile.length();
			byte[] bytes = new byte[(int) len];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			if (withChannels) {
				channels = new double[3][height * width];
			}
			int ind = 0;
			byte maxRByte = bytes[0];
			byte minRByte = bytes[0];
			for (int y = 0; y < height; y++) {

				for (int x = 0; x < width; x++) {
					if(maxRByte < bytes[ind]) {
						maxRByte = bytes[ind];
					}
					if(minRByte > bytes[ind]) {
						minRByte = bytes[ind];
					}
					byte r = bytes[ind];
					byte g = bytes[ind + height * width];
					byte b = bytes[ind + height * width * 2];

					if (withChannels) {
						channels[0][ind] = (double) r;
						channels[1][ind] = (double) g;
						channels[2][ind] = (double) b;
						
						//channels[1][ind + height * width] = (double) g;
						//channels[2][ind + height * width * 2] = (double) b;
					}

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x, y, pix);
					ind++;
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ImageColorScheme originalImgScheme = new ImageColorScheme(channels, img);
		return originalImgScheme;
	}

	/**
	 * Displays the before and after images, side by side on a JFrame. 
	 * Assumes that the after file is saved with format <INPUT_FILENAME>_after.rgb
	 * @param args commandline args, call as java ImageReader <IMAGE_FILENAME>.rgb <WIDTH> <HEIGHT>
	 */
	public void showIms(String[] args) {
		//int width = Integer.parseInt(args[1]);
		//int height = Integer.parseInt(args[2]);

		File file = new File(args[0]);
		ImageColorScheme inputImageScheme = readImage(file, width, height, true);

		// Use labels to display the images
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		JLabel lbText1 = new JLabel("Original image (Left)");
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lbText2 = new JLabel("Image after modification (Right)");
		lbText2.setHorizontalAlignment(SwingConstants.CENTER);
		BufferedImage beforeImage = inputImageScheme.getDisplayImage();
		lbIm1 = new JLabel(new ImageIcon(beforeImage));

		// use pipeline for SubSampling, Quant to produce a final image.

		inputImageScheme.getY(); //converts to YUV scheme
		int subsampleY = Integer.parseInt(args[1]); // subsample + upsample in the Y channel
		int subsampleU = Integer.parseInt(args[2]); // similarly subsample + upsample for other channels
		int subsampleV = Integer.parseInt(args[3]);

		new SubSampler().subsample(inputImageScheme, subsampleY, subsampleU, subsampleV);

		inputImageScheme.getR(); //converts to RGB space
		
		int quantLevel = Integer.parseInt(args[4]);
		byte[] finalChannel = new RGBQuantizer().quantize(inputImageScheme, quantLevel);

		// set display image for the output image scheme
		BufferedImage afterImage = pixelsToImage(finalChannel, height, width);

		// load a result image in second window, for final comparison.
		lbIm2 = new JLabel(new ImageIcon(afterImage)); 

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		frame.getContentPane().add(lbText2, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		frame.getContentPane().add(lbIm2, c);

		frame.pack();
		frame.setVisible(true);
	}
	
	private BufferedImage pixelsToImage(byte[] channels, int height, int width) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int ind = 0;
		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {

				byte r = channels[ind];
				byte g = channels[ind + height * width];
				byte b = channels[ind + height * width * 2];

				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
				img.setRGB(x, y, pix);
				ind++;
			}
		}
		
		return img;
	}

	public static void main(String[] args) {
		ImageReader ren = new ImageReader();
		ren.showIms(args);
	}

}
