
public class CSP_Instance {
	CSP csp;
	int nVars;
	boolean[][] domain;
	Clause[] clauses;
	InstR[] rels;
	
	public CSP_Instance(CSP csp_, int nVars_, Clause... clauses_){
		csp = csp_;
		nVars = nVars_;
		domain = new boolean[nVars][csp.D];
		
		for(int i=nVars;i-->0;)
			for(int j=csp.D;j-->0;)
				domain[i][j] = true;
		
		clauses = clauses_;
	}
	
	static class Clause {
		int type;
		int[] args;
		
		public Clause(int type_, int... args_){
			type = type_;
			args = args_;
		}
	}
	
	//Relation specific to this instance
	class InstR {
		int k, arity;
		int[][] allowed;
		int[] vars;
		
		public InstR(CSP.Relation orig, int... args){
			k = orig.k;
			arity = orig.arity;
			allowed = new int[k][];
			
			for(int i=0;i<k;i++)
				allowed[i] = orig.allowed[i].clone();
			
			vars = args.clone();
		}
		
		public InstR(boolean[][] rho, int v1, int v2){
			arity = 2;
			vars = new int[]{v1,v2};
			
			k = 0;
			for(boolean[] a:rho)
				for(boolean b:a)
					if(b) k++;
			
			allowed = new int[k][2];
			
			int dest = 0;
			for(int i=0;i<rho.length;i++){
				for(int j=0;j<rho[i].length;j++){
					if(rho[i][j]){
						allowed[dest][0] = i;
						allowed[dest][1] = j;
					}
				}
			}
		}
		
		//Check that the relation is subdirect. If it was, return false. If it wasn't,
		//it shrinks the domain and returns true (for "progress made").
		public boolean enforceSubdirect(){
			boolean progress = false;
			for(int var=0; var<arity; var++){
				for(int val=0; val<csp.D; val++){
					//We ignore anything already precluded by domain reduction
					if(!domain[vars[var]][val])
						continue;
					
					//Check that variable "var" is allowed to have value "val"
					boolean canHas = false;
					for(int[] c : allowed){
						if(c[var] == val){
							canHas = true; break;
						}
					}
					
					if(!canHas){
						//Reduce the domain
						domain[vars[var]][val] = false;
						progress = true;
					}
				}
			}
			return progress;
		}
		
		public boolean checkDomain(){
//			for(int i){
//				if(c[var] == val){
//					canHas = true; break;
//				}
//			}
			return false; //TODO
		}
	}
	
	//Constructs the example problem from
	// https://en.wikipedia.org/wiki/2-satisfiability#Problem_representations
	static CSP_Instance get2SAT11(){
		Clause c1 = new Clause(0, 0, 2);
		Clause c2 = new Clause(1, 0, 3);
		Clause c3 = new Clause(1, 1, 3);
		Clause c4 = new Clause(1, 1, 4);
		Clause c5 = new Clause(1, 2, 4);
		Clause c6 = new Clause(1, 0, 5);
		Clause c7 = new Clause(1, 1, 5);
		Clause c8 = new Clause(1, 2, 5);
		Clause c9 = new Clause(0, 3, 6);
		Clause c10 = new Clause(0, 4, 6);
		Clause c11 = new Clause(0, 5, 6);
		
		Clause[] cs = {c1,c2,c3,c4,c5,c6,c7,c8,c9,c10,c11};
		
		CSP csp = CSP.get2SAT_3();
		
		CSP_Instance inst = new CSP_Instance(csp, 7, cs);
		return inst;
	}
}
