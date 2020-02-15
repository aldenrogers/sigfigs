public class CSP {
	int D;
	
	static class Relation {
		int k, arity;
		int[][] allowed;	
	}
	
	int numR;
	Relation[] R;
	
	public static CSP get2SAT(){
		CSP res = new CSP();
		res.D = 2;
		
		Relation True = new Relation();
		True.k = 1;
		True.arity = 1;
		True.allowed = new int[][]{{1}};
		
		Relation Neg = new Relation();
		Neg.k = 2;
		Neg.arity = 2;
		Neg.allowed = new int[][]{{0,1},{1,0}};
		
		Relation Clause = new Relation();
		Clause.k = 3;
		Clause.arity = 2;
		Clause.allowed = new int[][]{{1,1},{0,1},{1,0}};
		
		res.R = new Relation[]{True,Neg,Clause};
		res.numR = res.R.length;
		return res;
	}
	
	public static CSP get3Color(){
		CSP res = new CSP();
		res.D = 3;
		
		Relation Edge = new Relation();
		Edge.k = 6;
		Edge.arity = 2;
		Edge.allowed = new int[][]{{0,1},{0,2},{1,0},{1,2},{2,0},{2,1}};
		
		res.R = new Relation[]{Edge};
		res.numR = res.R.length;
		return res;
	}
	
	public static CSP getDigraphConnected(){
		CSP res = new CSP();
		res.D = 2;
		
		Relation Edge = new Relation();
		Edge.k = 3;
		Edge.arity = 2;
		Edge.allowed = new int[][]{{0,0},{0,1},{1,1}};
		
		Relation Source = new Relation();
		Source.k = 1;
		Source.arity = 1;
		Source.allowed = new int[][]{{1}};
		
		Relation Sink = new Relation();
		Sink.k = 1;
		Sink.arity = 1;
		Sink.allowed = new int[][]{{0}};
		
		res.R = new Relation[]{Edge,Source,Sink};
		res.numR = res.R.length;
		return res;
	}
	
	//https://www.sciencedirect.com/science/article/pii/S0004370298000228
	//Example 4.8
	//Note that this CSP would kind of dumb without the unary constraints, because then we could just
	//map everything to the constant 0, and now the constraints are all satisfied. With the unary constraints
	//we could force certain things to be 0/1/2/3, and now it's actually interesting. It turns
	//out to be in P!
	public static CSP getJeavonsExample(){
		CSP res = new CSP();
		res.D = 4;
		
		Relation R = new Relation();
		R.k = 10;
		R.arity = 2;
		R.allowed = new int[][]{{0,0},{0,1},{0,2},{0,3},{1,0},{1,1},{2,0},{2,2},{3,0},{3,3}};
		
		Relation C0 = new Relation(); C0.k=1; C0.arity=1; C0.allowed = new int[][]{{0}};
		Relation C1 = new Relation(); C1.k=1; C1.arity=1; C1.allowed = new int[][]{{1}};
		Relation C2 = new Relation(); C2.k=1; C2.arity=1; C2.allowed = new int[][]{{2}};
		Relation C3 = new Relation(); C3.k=1; C3.arity=1; C3.allowed = new int[][]{{3}};
		
		res.R = new Relation[]{R, C0, C1, C2, C3};
		res.numR = res.R.length;
		return res;
	}
}