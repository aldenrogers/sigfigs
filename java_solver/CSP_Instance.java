import java.util.ArrayList;
import java.util.Arrays;

public class CSP_Instance {
	CSP csp;
	int nVars;
	boolean[][] domain;
	Clause[] clauses;
	ArrayList<InstR> rels;
	
	public CSP_Instance(CSP csp_, int nVars_, Clause... clauses_){
		csp = csp_;
		nVars = nVars_;
		domain = new boolean[nVars][csp.D];
		
		for(int i=nVars;i-->0;)
			for(int j=csp.D;j-->0;)
				domain[i][j] = true;
		
		clauses = clauses_;
		
		rels = new ArrayList<>();
		for(Clause c : clauses_){
			rels.add(new InstR(csp.R[c.type], c.args));
		}
	}
	
	private CSP_Instance(){}
	
	static class Clause {
		int type;
		int[] args;
		
		public Clause(int type_, int... args_){
			type = type_;
			args = args_;
		}
	}
	
	//Relation specific to this instance
	public class InstR {
		int k, arity;
		int[][] allowed;
		int[] vars;
		
		public InstR(CSP.Relation orig, int... args){
			k = orig.k;
			arity = orig.arity;
			
			allowed = Util.deepClone(orig.allowed);
			
			vars = args.clone();
			collapseVars();
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
						dest++;
					}
				}
			}
			
			collapseVars();
		}

		//basically "clone", but because it's an inner class, it's a constructor.
		public InstR(InstR orig){
			k = orig.k;
			arity = orig.arity;
			allowed = Util.deepClone(orig.allowed);
			vars = vars.clone();
		}
		
		//Check that the relation is subdirect. If it was, return false. If it wasn't,
		//it shrinks the domain and returns true (for "progress made").
		public boolean enforceSubdirect(){
			boolean progressEver = false;
			boolean progress;
			
			do {
				progress = false;
				checkDomain();
				
				for(int var=0; var<arity; var++){
					for(int val=0; val<csp.D; val++){
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
							progressEver = true;
						}
					}
				}
			} while(progress);
			
			return progressEver;
		}
		
		public boolean checkDomain(){
			boolean progress = false;
			for(int i=0; i<allowed.length; i++){
				boolean okay = true;
				
				for(int v=0; v<arity; v++){
					if(!domain[vars[v]][allowed[i][v]]){
						okay = false;
						continue;
					}
				}
				
				if(!okay){
					allowed = Util.remove(allowed, i--);
					k--;
					progress = true;
				}
			}
			return progress;
		}
		
		public void collapseVars(){
			for(int i=0; i<arity; i++){
				for(int j=i+1; j<arity; j++){
					
					if(vars[i] == vars[j]){
						//Collapse those two together
						//First, filter out all "allowed" where they aren't equal
						for(int a=0; a<k; a++){
							if(allowed[a][i] != allowed[a][j]){
								//Bad! Delete this
								allowed = Util.remove(allowed, a--);
								k--;
							} else {
								//Collapse the "allowed" to ignore this variable
								allowed[a] = Util.remove(allowed[a], j);
							}
						}
						
						vars = Util.remove(vars, j--);
						arity--;
					}
				}
			}
		}
		
		public boolean supports(int... varsAndVals){
			if(varsAndVals.length != 2*arity)
				throw new RuntimeException();
			
			int[] sortedVals = new int[arity];
			for(int i=0; i<arity; i++){
				sortedVals[Util.find(vars, varsAndVals[2*i])] = varsAndVals[2*i + 1];
			}
			
			for(int[] opt : allowed){
				if(Arrays.equals(opt, sortedVals))
					return true;
			}
			return false;
		}
		
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append("Rel{");
			sb.append("arity=" + arity+",");
			sb.append("k=" + k+",");
			sb.append("vars="+Arrays.toString(vars));
			sb.append(" | ");
			for(int[] opt : allowed){
				sb.append(Arrays.toString(opt));
			}
			sb.append("}");
			return sb.toString();
		}
	}
	
	void addRel(InstR r){
		rels.add(r);
	}
	
	void dump(){
		for(int v=0;v<nVars;v++){
			System.out.print("Domain of variable v"+v+": ");
			for(int d=0;d<csp.D;d++){
				if(domain[v][d])
					System.out.print(d+" ");
			}
			System.out.println();
		}
		System.out.println("Relations:");
		for(InstR r : rels){
			System.out.println(r);
		}
	}
	
	public static CSP_Instance clone(CSP_Instance orig){
		CSP_Instance res = new CSP_Instance();
		
		res.csp = orig.csp;
		res.nVars = orig.nVars;
		res.domain = Util.deepClone(orig.domain);
		res.clauses = orig.clauses.clone();
		
		res.rels = new ArrayList<>();
		for(int i=0; i<res.rels.size(); i++)
			res.rels.add(res.new InstR(orig.rels.get(i)));
		
		return res;
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
	
	//Constructs a 3-variable 2SAT instance which
	//can be completely solved from (2,3)-consistency.
	static CSP_Instance get2SATSmall(){
		//(-A | B) & (-B | C) & (-C | -A) 
		Clause c1 = new Clause(1, 1, 0);
		Clause c2 = new Clause(1, 2, 1);
		Clause c3 = new Clause(2, 2, 0);
		
		Clause[] cs = {c1,c2,c3};
		
		CSP csp = CSP.get2SAT_3();
		
		CSP_Instance inst = new CSP_Instance(csp, 3, cs);
		return inst;
	}
	
	static CSP_Instance get2SATLoop(){
		Clause c1 = new Clause(1, 0, 1);
		Clause c2 = new Clause(1, 1, 2);
		Clause c3 = new Clause(1, 2, 3);
		Clause c4 = new Clause(1, 3, 4);
		Clause c5 = new Clause(1, 4, 5);
		Clause c6 = new Clause(1, 5, 0);
		Clause c7 = new Clause(0, 3, 3);
		
		Clause[] cs = {c1,c2,c3,c4,c5,c6};//,c7};
		
		CSP csp = CSP.get2SAT_3();
		
		CSP_Instance inst = new CSP_Instance(csp, 6, cs);
		return inst;
	}
}
