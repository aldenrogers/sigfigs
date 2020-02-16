import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class Main {

	CSP csp;
	SiggersToCNF writer;
	
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
		String solString = new Glucose(writer).getSolution(true);
		
		if(solString == null){
			System.out.println();
			System.out.println("NP-Complete");
			return;
		}
		
		ArrayList<Integer> trues = Glucose.solutionVars(solString);
		writer.describe(trues);
		SiggersPoly sigger = writer.getSiggers(trues);
		
		//Turn the Siggers polymorphism into a WNU polymorphism
	}
	
	public static void main(String[] args) throws IOException {
		new Main();
	}
}
