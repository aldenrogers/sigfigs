
public class PSolver {

	CSP_Instance inst;
	public PSolver(CSP_Instance inst_){ inst = inst_; }
	
	public void solve(){
		
	}
	
	//Provide (2,3)-consistency, as per https://arxiv.org/pdf/1803.07465.pdf, section 3.2
	public boolean check23Consistency(){
		
		int N = inst.nVars; int D = inst.csp.D;
		
		//rho_ij(i,j,a,b) is true if (a,b) is allowed on (vi,vj)
		boolean[][][][] rho_ij = new boolean[N][N][D][D];
		for(int i=N;i-->0;){
			for(int j=N;j-->0;){
				
				//Check each pair of values on this pair of variables
				for(int a=D;a-->0;){
					if(!inst.domain[i][a])
						continue;
					
					for(int b=D;b-->0;){
						if(!inst.domain[i][b])
							continue;
						//We're gonna see if they allow (a,b)
						boolean okay = true;
						//Check each clause on these
						for(CSP_Instance.Clause c : inst.clauses){
							int ia = find(c.args, a);
							int ib = find(c.args, b);
							
							if(ia==-1 && ib==-1) //it doesn't interact with either variable
								continue;
							
							if(ia>=0 && ib==-1){ //it interacts with a. Check the projection
								//boolean[] 
							}
						}
						//rho_ij[i][j]
					}
				}
			}
		}
		
		return false; //TODO
	}
	
	private int find(int[] l, int v){
		for(int i=0;i<l.length;i++) if(l[i]==v) return i;
		return -1;
	}
	
	public boolean[] project1(CSP.Relation r, int var){
		return null;
	}
	
	public static void main(String[] args) {
		CSP_Instance inst = CSP_Instance.get2SAT11();
		new PSolver(inst).solve();
	}

}
