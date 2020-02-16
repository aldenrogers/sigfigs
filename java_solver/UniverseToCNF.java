import java.io.IOException;
import java.util.ArrayList;

public class UniverseToCNF extends CNFWriter{
	int D;
	CSP_Instance inst;
	
	public boolean isBinaryTerm(CSP_Instance inst_, boolean[] subuniverse){
		inst = inst_;
		D = inst.csp.D;
		
		int nextVar = 1;
	
		//Create unique function variable
		nextVar += D*D*D;
		for(int x1=0; x1<D; x1++){
			for(int x2=0; x2<D; x2++){
				//This is one value of the function
				//Pick up all the variables
				int[] vars = new int[D];
				for(int o=0; o<D; o++){
					vars[o] = getVar(x1,x2,o);
				}
				
				//Disallow any pairs
				for(int o1=0; o1<D; o1++){
					for(int o2=o1+1; o2<D; o2++){
						appendClause(-vars[o1], -vars[o2]);
					}
				}
				
				//At least one is true
				appendClause(vars);
			}
		}
		
		//Ensure binary absorption
		for(int x1=0; x1<D; x1++){
			if(!subuniverse[x1])
				continue;
			
			for(int x2=0; x2<D; x2++){
				//f(a,b) is in b
				for(int o=0; o<D; o++){
					if(subuniverse[o])
						continue;
					
					int v1 = getVar(x1,x2,o);
					appendClause(-v1);

					int v2 = getVar(x2,x1,o);
					appendClause(-v2);
				}
			}
		}
		
		//Enforce polymorphism for each relation
		for(CSP_Instance.InstR r : inst.rels){
			int k = r.k; //number of allowed entries in the relation
			int a = r.arity; // number of variables in a relation
			
			//For each 2-tuple of valid input constraints,
			for(int x2=0; x2<k; x2++){
				for(int x1=0; x1<k; x1++){
					//Confirm that the image is a valid relation as well.
					
					//Do this by creating a variable for each possible result
					int[] vars = new int[k];
					for(int o=0; o<k; o++){
						vars[o] = nextVar++;
					}
					
					//Disallow any pairs
					for(int o1=0; o1<k; o1++){
						for(int o2=o1+1; o2<k; o2++){
							appendClause(-vars[o1], -vars[o2]);
						}
					}
					
					//At least one is true
					appendClause(vars);
					
					//State each variable is given by the AND of the appropriate parts
					for(int o=0; o<k; o++){
						
						//load up the variables for the function evaluations
						int[] evalVars = new int[a];
						for(int v=0; v<a; v++){
							int fIn1 = r.allowed[x1][v];
							int fIn2 = r.allowed[x2][v];
							int fOutput = r.allowed[o][v];
							evalVars[v] = getVar(fIn1, fIn2, fOutput);
						}
						
						//A = B && C && D && E ... is written in k-SAT as
						//A | -B | -C | -D ...
						//-A | B
						//-A | C
						// ...
						
						//So first do all the two-variable clauses
						for(int v=0; v<a; v++){
							appendClause(-vars[o], evalVars[v]);
						}
						//Now make one more big clause
						int[] bigClauseVars = new int[a+1];
						//Fill it in with the -B, -C, ... -E
						for(int v=0; v<a; v++){
							bigClauseVars[v] = -evalVars[v];
						}
						//Fill in the last one, A
						bigClauseVars[a] = vars[o];
					}
				}
			}
		}
		
		String solString;
		try {
			solString = new Glucose(this).getSolution(false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		if(solString == null){
			return false; //no binary absorbing subuniverse
		}
		
		ArrayList<Integer> trues = Glucose.solutionVars(solString);
		describe(trues);
		return true;
	}
	

	public void describe(ArrayList<Integer> trues) {
		for(Integer v : trues){
			v -= 1;
			if(v < D*D*D){
				int o = v % D; v/=D;
				int x2 = v % D; v/=D;
				int x1 = v % D; v/=D;
				System.out.println("f("+x1+","+x2+") = "+o);
				continue;
			}
			v -= D*D;
			
			//TODO describe the polymorphism actions on relations
		}
	}
	
	int getVar(int x1, int x2,  int o){
		return (x1*D + x2)*D + o + 1;
	}
	
	protected String appendClause(int... vars){
		String clause = "";
		for(int i:vars)
			clause += i+" ";
		clause+="0";
		clauses.add(clause);
		return clause;
	}

}
