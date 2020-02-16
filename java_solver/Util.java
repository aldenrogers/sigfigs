import java.util.Arrays;

public class Util {
	public static int find(int[] l, int v){
		for(int i=0;i<l.length;i++) if(l[i]==v) return i;
		return -1;
	}
	
	public static <T> T[] remove(T[] arr, int l){
		T[] res = Arrays.copyOf(arr, arr.length-1);
		for(int i = 0; i < l; i++){
			res[i] = arr[i];
		}
		
		for(int i = l; i < arr.length-1; i++){
			res[i] = arr[i + 1];
		}
		return res;
	}
	
	public static int[] remove(int[] arr, int l){
		int[] res = Arrays.copyOf(arr, arr.length-1);
		for(int i = 0; i < l; i++){
			res[i] = arr[i];
		}
		
		for(int i = l; i < arr.length-1; i++){
			res[i] = arr[i + 1];
		}
		return res;
	}
	
	public static int[][] deepClone(int[][] in){
		int[][] res = new int[in.length][];
		
		for(int i=0;i<in.length;i++)
			res[i] = in[i].clone();
		
		return res;
	}
	
	public static boolean[][] deepClone(boolean[][] in){
		boolean[][] res = new boolean[in.length][];
		
		for(int i=0;i<in.length;i++)
			res[i] = in[i].clone();
		
		return res;
	}
}
