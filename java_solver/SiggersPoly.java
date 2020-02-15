
public class SiggersPoly {
	final int[][][][] data;
	public SiggersPoly(int D){
		data = new int[D][D][D][D];
	}
	public int f(int a, int b, int c, int d){
		return data[a][b][c][d];
	}
}
