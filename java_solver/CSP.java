public class CSP {
	int D;
	String[] Dnames;
	
	static class Relation {
		int k, arity;
		int[][] allowed;
		String name;
		
		public Relation(){};
		public Relation(String name_){
			name = name_;
		}
	}
	
	int numR;
	Relation[] R;
	
	public CSP(){}
	public CSP(String... Dnames_){
		Dnames = Dnames_;
	}
	
	public static CSP get2SAT_Neg(){
		CSP res = new CSP("F","T");
		res.D = 2;
		
		Relation True = new Relation("x is true");
		True.k = 1;
		True.arity = 1;
		True.allowed = new int[][]{{1}};
		
		Relation Neg = new Relation("x = !y");
		Neg.k = 2;
		Neg.arity = 2;
		Neg.allowed = new int[][]{{0,1},{1,0}};
		
		Relation Clause = new Relation("x || y");
		Clause.k = 3;
		Clause.arity = 2;
		Clause.allowed = new int[][]{{1,1},{0,1},{1,0}};
		
		res.R = new Relation[]{True,Neg,Clause};
		res.numR = res.R.length;
		return res;
	}
	
	public static CSP get2SAT_3(){
		CSP res = new CSP("F","T");
		res.D = 2;
		
		Relation PP = new Relation("x || y");
		PP.k = 3;
		PP.arity = 2;
		PP.allowed = new int[][]{{1,1},{0,1},{1,0}};
		
		Relation PN = new Relation("x || !y");
		PN.k = 3;
		PN.arity = 2;
		PN.allowed = new int[][]{{1,0},{0,0},{1,1}};
		
		Relation NN = new Relation("!x || !y");
		NN.k = 3;
		NN.arity = 2;
		NN.allowed = new int[][]{{0,0},{1,0},{0,1}};
		
		res.R = new Relation[]{PP, PN, NN};
		res.numR = res.R.length;
		return res;
	}
	
	public static CSP get3Color(){
		CSP res = new CSP("R","G","B");
		res.D = 3;
		
		Relation Edge = new Relation("Edge");
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
		
		Relation Edge = new Relation("Edge");
		Edge.k = 3;
		Edge.arity = 2;
		Edge.allowed = new int[][]{{0,0},{0,1},{1,1}};
		
		Relation Source = new Relation("Source");
		Source.k = 1;
		Source.arity = 1;
		Source.allowed = new int[][]{{1}};
		
		Relation Sink = new Relation("Sink");
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
		CSP res = new CSP("a","b","c","d");
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
	
	//Linear equations mod 3
	public static CSP getLIN3(){
		CSP res = new CSP();
		res.D = 3;
		
		Relation Eq = new Relation("a + b = c (mod 3)");
		Eq.k = 9;
		Eq.arity = 3;
		Eq.allowed = new int[][]{{0,0,0},{0,1,1},{0,2,2},{1,0,1},{1,1,2},{1,2,0},{2,0,2},{2,1,0},{2,2,1}};
		
		Relation C0 = new Relation("x=0"); C0.k=1; C0.arity=1; C0.allowed = new int[][]{{0}};
		Relation C1 = new Relation("x=1"); C1.k=1; C1.arity=1; C1.allowed = new int[][]{{1}};
		
		res.R = new Relation[]{Eq, C0, C1};
		res.numR = res.R.length;
		return res;
	}
}