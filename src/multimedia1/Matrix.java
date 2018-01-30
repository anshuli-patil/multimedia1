package multimedia1;

import java.util.Arrays;

/**
 * Definition of a (m x n) matrix
 * 
 **/
public class Matrix {
	private int n;
	private int m;
	private double [][] matrix;
	
	public Matrix(int m, int n, double [][] values) {
		this.n = n;
		this.m = m;
		this.matrix = Arrays.copyOf(values, m);
	}
	
	/**
	 * matrix multiplication for small matrices. O(n^3) complexity.
	 * @param b another matrix 
	 * @return result of (this.matrix mul b.matrix)
	 */
	public Matrix multiply(Matrix b) {
		if(this.n != b.m) {
			throw new UnsupportedOperationException("Cannot multiply matrices. Check dimensions");
		}
		
		double [][] result = new double [this.m][b.n];
		for(int i = 0; i < this.m; i++) {
			for(int j = 0; j < b.n; j++) {
				for(int k = 0; k < this.n; k++) {
					result[i][j] += this.matrix[i][k] * b.matrix[k][j];
				}
			}
		}
		return new Matrix(this.m, b.n, result);
	}

	public double [][] getMatrix() {
		return this.matrix;
	}
	
	public String toString() {
		return Arrays.deepToString(this.matrix);
	}
	
	public static void main(String[] args) {
		double[][] matA = {{1.0, 2.0}};
		Matrix a = new Matrix(1, 2, matA);
		double[][] matB = {{1.0}, {2.0}};
		Matrix b = new Matrix(2, 1, matB);

		System.out.println(b.multiply(a));
		System.out.println(a.multiply(b));
	}
}
