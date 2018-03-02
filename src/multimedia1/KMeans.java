package multimedia1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class KMeans {
	
	public List<RGBColor> cluster(List<RGBColor> colors, int q, List<RGBColor> clusterCentersInit) {
		/*
		if(clusterCentersInit != null) {
			for(int i = 0; i < clusterCentersInit.size(); i++) {
				System.out.println(Arrays.toString(clusterCentersInit.get(i).colors) + " " + (byte) clusterCentersInit.get(i).getPix());
			}
		}
		*/
		
		List<RGBColor> clusterCenters;
		
		if(clusterCentersInit == null) {
		clusterCenters = new ArrayList<RGBColor>();
			// start with random cluster centers
			
			//int diff = Math.max(0, (int) Math.rint(colors.size() / q) - 1);
			for(int i = 0; i < q; i++) {
				int randomNum = ThreadLocalRandom.current().nextInt(0, colors.size());
				clusterCenters.add(colors.get(randomNum));
				//clusterCenters.add(colors.get(i * diff));
			}
		} else {
			clusterCenters = clusterCentersInit;
		}
		
		
		int reassignmentTotal = colors.size();
		int iterations = 0;
		while(reassignmentTotal != 0 || iterations < 20) {
			iterations += 1;
			
			reassignmentTotal = 0;
			// find distances to new cluster centers
			for(int i = 0; i < colors.size(); i++) {
				RGBColor ithColor = colors.get(i);
				
				// initialize the cluster distance to minimum noted 
				double minDistance = Double.MAX_VALUE;
				if(ithColor.clusterIndex >= 0) {
					minDistance = clusterCenters.get(ithColor.clusterIndex).getDistance(ithColor);
				}
				
				boolean reassigned = false;
				
				// assign each color to its closest cluster
				for(int j = 0; j < clusterCenters.size(); j++) {
					double distance = ithColor.getDistance(clusterCenters.get(j));
					
					if (distance < minDistance) {
						ithColor.clusterIndex = j;
						minDistance = distance;
						reassigned = true;
					}
				}
				if(reassigned) {
					reassignmentTotal += 1;
				}
			}
			
			// compute new cluster centers by averaging
			double[][] clusterRGBSum = new double[3][q];
			int[] clusterMembersCount = new int[q];
			for(int i = 0; i < colors.size(); i++) {
				RGBColor color = colors.get(i);
				int clusterIndex = color.clusterIndex;
				for(int j = 0; j < 3; j++) {
					clusterRGBSum[j][clusterIndex] += color.colors[j];
				}
				clusterMembersCount[clusterIndex] += 1;
			}
			
			// actually change the cluster centers
			for(int i = 0; i < q; i++) {
				double r = clusterRGBSum[0][i] / (double) clusterMembersCount[i];
				double g = clusterRGBSum[1][i] / (double) clusterMembersCount[i];
				double b = clusterRGBSum[2][i] / (double) clusterMembersCount[i];
				RGBColor c = new RGBColor(r, g, b);
				c.clusterIndex = i;
				clusterCenters.set(i, c);
			}
 		}
		
		//System.out.println("iterations for KMeans " + iterations);
		
		/*
		System.out.println(clusterCenters.get(0));
		System.out.println(clusterCenters.get(1));
		System.out.println(clusterCenters.get(2));
		*/
		// replace all the colors by the value of their cluster center
		for(int i = 0; i < colors.size(); i++) {
			int clusterIndex = colors.get(i).clusterIndex;
			colors.set(i, clusterCenters.get(clusterIndex));
		}
		
		return colors;
	}
	
	public static void main(String[] args) {
		List<RGBColor> l = new ArrayList<RGBColor>();
		l.add(new RGBColor(255, 255, 123));
		l.add(new RGBColor(255, 255, 121));
		l.add(new RGBColor(255, 255, 121));
		l.add(new RGBColor(3, 4, 7));
		l.add(new RGBColor(2, 1, 3));
		l.add(new RGBColor(244, 221, 129));
		System.out.println(new KMeans().cluster(l, 3, null));
	}

}
