package multimedia1;

public class RGBColor implements Comparable<RGBColor> {

	double [] colors;
	int maxRangeChannel = -1;

	int clusterIndex;
	
	RGBColor(double [] colors) {
		this.colors = colors;
		clusterIndex = -1;
	}
	
	RGBColor(double r, double g, double b) {
		colors = new double [3];
		colors[0] = r;
		colors[1] = g;
		colors[2] = b;
		clusterIndex = -1;
	}
	
	public int getPix() {
		int pix = 0xff000000 | (((int) Math.rint(colors[0]) & 0xff) << 16) | (((int) Math.rint(colors[1]) & 0xff) << 8)
				| ((int) Math.rint(colors[2]) & 0xff);
		return pix;
	}
	
	public double getDistance(RGBColor o) {
		//return Math.pow(this.getPix() - o.getPix(), 2);
		return Math.pow(o.colors[0] - colors[0], 2) + Math.pow(o.colors[1] - colors[1], 2) + Math.pow(o.colors[2] - colors[2], 2);
	}
	
	@Override
	public int compareTo(RGBColor o) {
		return (int) Math.rint(this.colors[maxRangeChannel] - o.colors[maxRangeChannel]);
	}
	
	public String toString() {
		return this.colors[0] + " " + this.colors[1] + " " + this.colors[2] + " " + clusterIndex + "\n";
	}
}
