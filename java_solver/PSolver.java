import java.util.Arrays;
import java.util.LinkedList;

public class PSolver {

	CSP_Instance inst;
	CSP_Instance.InstR[][] rhos;
	public PSolver(CSP_Instance inst_){ inst = inst_; }
	
	public boolean solve(){
		removeUnary();
		
		if(!check23Consistency())
			return false;
		
		//Check (for recursive calls) if it's now fully determined.
		if(isSolved())
			return true;
		
		int linkedResult = checkLinked();
		if(linkedResult == 1)
			return true;
		if(linkedResult == 2)
			return false;
		//else linkedResult == 0. Continue processing
		
		if(checkFragmentation())
			return false;
		
		if(checkIrreducibility())
			return false;
		
		Universe.reduceBinaryAbsorption(inst);
		
		Universe.reduceCenter();
		
		//Check for polynomially complete clones
		if(isPolynomiallyComplete())
			return reduceCompleteClones();
		
		//Solve linear system
		solveLinearSystem();
		
//		inst.dump();
		inst.dumpSolution();
		return true;
	}

	public static void main(String[] args) {
//		CSP_Instance inst = CSP_Instance.get2SAT11();
//		CSP_Instance inst = CSP_Instance.get2SATSmall();
		CSP_Instance inst = CSP_Instance.get2SATLoop();
		new PSolver(inst).solve();
	}
	
	//Take all unary constraints and remove them by reducing domain
	public void removeUnary(){
		for(int ri=0; ri<inst.rels.size(); ri++){
			CSP_Instance.InstR r = inst.rels.get(ri);
			if(r.arity == 1){
				//Strip domain
				int var = r.vars[0];
				for(int v=0; v<inst.csp.D; v++){
					if(!r.supports(var,v)){
						inst.domain[var][v] = false;
					}
				}
				//Remove the relation
				inst.rels.remove(ri);
				ri--;
			}
		}
	}
	
	public boolean isSolved() {
		//Check if each variable has domain size 1.
		for(int var=inst.nVars; var-->0; ){
			int domainSize = 0;
			for(int val=inst.csp.D; val-->0; ){
				if(inst.domain[var][val])
					domainSize++;
			}
			if(domainSize != 1)
				return false;
		}

		System.out.println("Instance SOLVED");
		inst.dumpSolution();
		return true;
	}
	
	//Provide (2,3)-consistency, as per https://arxiv.org/pdf/1803.07465.pdf, section 3.2
	//Returns true if successful. Returns false if a contradiction was reached (empty domain)
	public boolean check23Consistency(){
		System.out.println("Applying (2,3)-consistency");
		
		int N = inst.nVars; int D = inst.csp.D;
		
		//rho_ij(i,j,a,b) is true if (a,b) is allowed on (vi,vj)
		boolean[][][][] rho_domains = new boolean[N][N][D][D];
		rhos = new CSP_Instance.InstR[N][N];
		
		for(CSP_Instance.InstR r : inst.rels){
			r.enforceSubdirect();
		}
		
		for(int i=N; i-->0; ){
			for(int j=i; j-->0; ){
				
				//Check each pair of values on this pair of variables
				for(int a=D;a-->0;){
					if(!inst.domain[i][a])
						continue;
					
					for(int b=D;b-->0;){
						if(!inst.domain[j][b])
							continue;
						
						//We're going to see if they allow (a,b)
						boolean okay = true;
						//Check each clause on these
						for(CSP_Instance.InstR r : inst.rels){
							int ia = Util.find(r.vars, i);
							int ib = Util.find(r.vars, j);
							
							if(ia==-1 && ib==-1) //it doesn't interact with either variable
								continue;
							
							if(ia>=0 && ib==-1){ //it interacts with a.
								//domain projections already done
								continue;
							}
							
							if(ia==-1 && ib>=0){ //it interacts with b.
								//domain projections already done
								continue;
							}
							
							if(ia>=0 && ib>=0){
								//It touches both variables
								boolean thisClauseOkay = false;
								for(int[] opt : r.allowed){
									if(opt[ia] == a && opt[ib] == b){
										thisClauseOkay = true;
										break;
									}
								}
								
								if(!thisClauseOkay){
									okay = false;
									break;
								}
							}
						}
						
						//We've determined if this pair of values is acceptable
						if(okay){
							rho_domains[i][j][a][b] = true;
						}
					}
				}
				//Create an instance relation using this rho
				CSP_Instance.InstR rho_rel = inst.new InstR(rho_domains[i][j], i, j);
//				System.out.println("On vars "+i+","+j+" found rho = "+Arrays.deepToString(rho_domains[i][j]));
				rhos[j][i] = rhos[i][j] = inst.addRel(rho_rel);
			}
		}
		
		boolean progress;
		
		do{
			progress=false;
			
			for(int i=N; i-->0; ){
				for(int j=i; j-->0; ){
					
					CSP_Instance.InstR rho_ij = rhos[i][j];
					
					for(int optI=0; optI<rho_ij.allowed.length; optI++){
						int a = rho_ij.allowed[optI][0];
						int b = rho_ij.allowed[optI][1];
						if(i > j){
							int t=a; a=b; b=t;
						}
						
						//Is (a,b) okay at a level of 3-consistency?
						boolean isABokay = true;
						
						for(int k=N; k-->0; ){
							if(k == i || k == j) continue;
							//For each other index k, it must be compatible.

							CSP_Instance.InstR rho_ik = rhos[i][k];
							CSP_Instance.InstR rho_jk = rhos[j][k];
							boolean isABKokay = false;
							//Check there is some value k that is compatible
							for(int vk=0; vk<D; vk++){
								if(!inst.domain[k][vk]) continue;
								boolean works = rho_ik.supports(i,a,k,vk) && rho_jk.supports(j,b,k,vk);
								if(works){
									isABKokay = true;
									break;
								}
							}
							
							if(!isABKokay){
								isABokay = false;
								break;
							}
						}
					
						//This assignment is not allowed. Reduce.
						if(!isABokay){
//							System.out.println("Removing "+Arrays.toString(rho_ij.allowed[optI])+" from "+rho_ij);
							rho_ij.allowed = Util.remove(rho_ij.allowed, optI);
							rho_ij.k--;
							optI--;
							progress=true;
						}
						
						progress |= rho_ij.enforceSubdirect();
					}
				}
			}
			
			//Check for empty domains
			for(int i=N; i-->0; ){
				boolean isEmpty = true;
				for(int val=D;val-->0;)
					if(inst.domain[i][val]){
						isEmpty = false;
						break;
					}
				
				if(isEmpty){
					System.out.println("Cycle consistency checking reach an empty domain. Unsat.");
					return false;
				}
			}
			
		} while(progress);
		
		//We enforced cycle consistency and never reach an empty domain. Great, return true.
		return true;
	}
	
	//Return 0 if it was all linked.
	//Return 1 if it was not-linked, and solvable.
	//Return 2 if it was not-linked, and unsolvable.
	public int checkLinked(){
		//Build graph of all (v,vi) pairs for variables v and values vi in Di
		//Connect vertices if a constraint shares them
		//Divide graph into connected components and recurse
		
		int D = inst.csp.D;
		boolean[][] visited = new boolean[inst.nVars][D];
		//Find a valid starting node
		int firstVar = 0;
		int firstVal;
		for(firstVal=inst.csp.D; firstVal-->0;){
			if(inst.domain[firstVar][firstVal]){
				//Great, this is a starting point
				break;
			}
		}
	
		LinkedList<Integer> queue = new LinkedList<>();
		queue.add(D*firstVar + firstVal);
		
		while(queue.size() != 0){
			Integer pair = queue.pop();
			int var = pair/D; int val = pair%D;
			if(visited[var][val])
				continue;
			
			visited[var][val] = true;
			
			//Find neighbors in the constraint graph
			for(CSP_Instance.InstR r : inst.rels){
				//Check if this clause uses this neighbor
				int vi = Util.find(r.vars, var);
				if(vi == -1)
					continue;
				
				for(int[] opt : r.allowed){
					if(opt[vi] != val)
						continue;
					//We have an edge supported by some constraint. Check that it's allowed.
					//CSP_Instance.InstR rho = rhos[]
					//Add all the neighbors in.
					for(int i=0;i<r.arity;i++){
						int neighborVar = r.vars[i];
						int neighborVal = opt[i];
						int neighbor = D*neighborVar + neighborVal;
						queue.add(neighbor);
					}
				}
			}
		}
		
		//Great, we've done a flood fill from our first thing. Check if it was covering.
		for(int var=inst.nVars; var-->0; ){
			for(int val=D;val-->0;){
				if(!inst.domain[var][val]) //it's not in the domain anyway, loop back
					continue;
				
				if(visited[var][val]) //great, we covered it
					continue;
				
				//Oh snap! This instance is not linked! We will now split the instance.
				CSP_Instance split = createSplitInstance(visited);
				System.out.println("Unlinked constraint graph; splitting and recursing");
				//Solve the split instance completely:
				boolean splitSolution = new PSolver(split).solve();
				if(splitSolution){
					//We got a solution that works, return true
					return 1;
				}
				//Nope, okay, so we'll try solving what's left here
				boolean leftSolution = new PSolver(inst).solve();
				if(leftSolution){
					//We got a solution that works, return true
					return 1;
				} else {
					//Neither split worked! Return false
					return 2;
				}
			}
		}
		
		//It was all linked, nice. Proceed
		return 0;
	}
	
	boolean checkFragmentation(){
		return false;
	}
	
	boolean checkIrreducibility(){
		return false;
	}
	
	//Check if the algebra is equivalent to a finite field or if it's complete.
	boolean isPolynomiallyComplete(){
		return true;
	}

	public void solveLinearSystem() {
		//TODO
	}
	
	boolean reduceCompleteClones(){
		//This means just setting one variable and recursing.
		
		//Find a variable we can fix.
		for(int setVar=0; true; setVar++){
			//Check if this variable has domain > 1.
			int setVal=-1;
			int domainSize=0;
			for(int val=inst.csp.D; val-->0;){
				if(inst.domain[setVar][val]){
					setVal = val;
					domainSize++;
				}
			}
			
			if(domainSize > 1){
				//Okay! We'll set this and recurse.
				for(int val=inst.csp.D; val-->0;){
					if(val != setVal)
						inst.domain[setVar][val] = false;
				}
				//We forced by restricting the domain.
				System.out.println("Polynomially complete clone: v"+setVar+" = "+setVal);
				//Solve.
				return solve();
			}
		}
	}
	
	//Given the connected component, create a new instance of just that linked
	//instance. Remove it from this instance.
	CSP_Instance createSplitInstance(boolean[][] component){
		CSP_Instance res = CSP_Instance.clone(inst);
		
		//Filter res to only have the component
		//And we don't have the component at all
		for(int var=0; var<inst.nVars; var++){
			for(int val=0; val<inst.csp.D; val++){
				if(component[var][val])
					inst.domain[var][val] = false;
				else
					res.domain[var][val] = false;
			}
		}
		
		return res;
	}

}
