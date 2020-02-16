import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class Main {

	static final String glucose_cmd = "bash -c '/mnt/d/Timeroot/Documents/glucose-syrup-4.1/parallel/glucose-syrup_static -model '";
	
	CSP csp;
	SiggersToCNF writer;
	
	protected String getSolution(boolean echoOn) throws IOException{
		Process child = Runtime.getRuntime().exec(glucose_cmd);
		
		OutputStream writer = child.getOutputStream();
		PrintStream ps = new PrintStream(writer);
		printDIMACS(ps);
		writer.close(); ps.close();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(child.getInputStream()));
		String line;
		while((line=br.readLine()).length()==0 || line.charAt(0)!='v'){
			if(echoOn) System.out.println(line );
			if(line.equals("s UNSATISFIABLE")){
				child.destroy();
				return null;
			}
		}
		return line.substring(1); //remove initial 'v'
	}

	protected void printDIMACS(PrintStream ps){
		ps.println("p cnf "+(writer.nVars)+" "+writer.clauses.size());
		for(String cl : writer.clauses){
			ps.println(cl);
		}
	}
	protected ArrayList<Integer> solutionVars(String solutionClause) {
		String[] parts = solutionClause.split(" ");
		ArrayList<Integer> res = new ArrayList<>();
		for(String v : parts){
			if(v.length() > 0 && v.charAt(0)!='-' && !v.equals("0")){
				int val = Integer.valueOf(v);
				res.add(val);
			}
		}
		return res;
	}

	public Main() throws IOException{
		//Choose problem
		//csp = CSP.get2SAT_Neg();
		csp = CSP.get3Color();
		//csp = CSP.getDigraphConnected();
		//csp = CSP.getJeavonsExample();
		
		//Create Siggers CNF formula
		writer = new SiggersToCNF();
		writer.writeProblem(csp);
		
		//Solve the CNF to find (or fail) the Siggers formula
		String solString = getSolution(true);
		ArrayList<Integer> trues = solutionVars(solString);
		writer.describe(trues);
		SiggersPoly sigger = writer.getSiggers(trues);
		
		//Turn the Siggers polymorphism into a WNU polymorphism
	}
	
	public static void main(String[] args) throws IOException {
		new Main();
	}
}
