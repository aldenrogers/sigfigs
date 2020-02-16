import java.util.ArrayList;

public class SiggersToCNF {
	CSP csp;
	int D;
	ArrayList<String> clauses = new ArrayList<String>();
	int nVars = 0;
	
	public void writeProblem(CSP csp_){
		csp = csp_;
		D = csp.D;
		
		int nextVar = 1;
		
		//Create unique function variable
		nextVar += (D*D)*(D*D)*D;
		for(int x1=0; x1<D; x1++){
			for(int x2=0; x2<D; x2++){
				for(int x3=0; x3<D; x3++){
					for(int x4=0; x4<D; x4++){
						//This is one value of the function
						//Pick up all the variables
						int[] vars = new int[D];
						for(int o=0; o<D; o++){
							vars[o] = getVar(x1,x2,x3,x4,o);
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
			}
		}
		
		//Ensure Siggers
		for(int x1=0; x1<D; x1++){
			for(int x2=0; x2<D; x2++){
				for(int x3=0; x3<D; x3++){
					//f(r,a,r,e) = f(a,r,e,a)
					//f(x1,x2,x1,x3) = f(x2,x1,x3,x2)
					for(int o=0; o<D; o++){
						int v1 = getVar(x1,x2,x1,x3,o);
						int v2 = getVar(x2,x1,x3,x2,o);
						//(v1 == v2) is equivalent to ((v1 | -v2) & (v2 | -v1))
						appendClause(v1,-v2);
						appendClause(v2,-v1);
					}
				}
			}
		}
		
		//The unanimity property, f(x,x,x,x)=x, is not required
		//in the definition of Siggers operation, but is logically implied. We add
		//the constraint in here to acceleate solving.
		for(int o=0; o<D; o++){
			int v = getVar(o,o,o,o,o);
			appendClause(v);
		}
		
		//Enforce polymorphism for each relation
		for(CSP.Relation r : csp.R){
			int k = r.k; //number of allowed entries in the relation
			int a = r.arity; // number of variables in a relation
			
			//For each 4-tuple of valid input constraints,
			for(int x1=0; x1<k; x1++){
				for(int x2=0; x2<k; x2++){
					for(int x3=0; x3<k; x3++){
						for(int x4=0; x4<k; x4++){
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
									int fIn3 = r.allowed[x3][v];
									int fIn4 = r.allowed[x4][v];
									int fOutput = r.allowed[o][v];
									evalVars[v] = getVar(fIn1, fIn2, fIn3, fIn4, fOutput);
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
			}
		}
		
		nVars = nextVar;
	}
	
	int getVar(int x1, int x2, int x3, int x4, int o){
		return (((x1*D + x2)*D + x3)*D + x4)*D + o + 1;
	}
	
	protected String appendClause(int... vars){
		String clause = "";
		for(int i:vars)
			clause += i+" ";
		clause+="0";
		clauses.add(clause);
		return clause;
	}

	public void describe(ArrayList<Integer> trues) {
		for(Integer v : trues){
			v -= 1;
			if(v < D*D*D*D*D){
				int o = v % D; v/=D;
				int x4 = v % D; v/=D;
				int x3 = v % D; v/=D;
				int x2 = v % D; v/=D;
				int x1 = v % D; v/=D;
				System.out.println("f("+x1+","+x2+","+x3+","+x4+") = "+o);
				continue;
			}
			v -= D*D*D*D*D;
			
			//TODO describe the polymorphism actions on relations
		}
	}
	
	public SiggersPoly getSiggers(ArrayList<Integer> trues) {
		SiggersPoly res = new SiggersPoly(D);
		
		for(Integer v : trues){
			v -= 1;
			if(v < D*D*D*D*D){
				int o = v % D; v/=D;
				int x4 = v % D; v/=D;
				int x3 = v % D; v/=D;
				int x2 = v % D; v/=D;
				int x1 = v % D; v/=D;
				res.data[x1][x2][x3][x4] = o;
				continue;
			}
		}
		return res;
	}
	
	public static void main(String[] args){
		CSP csp = CSP.get2SAT_Neg();
		SiggersToCNF writer = new SiggersToCNF();
		writer.writeProblem(csp);
		writer.clauses.stream().forEach(System.out::println);
	}
}
