import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class RelationsEntry extends JPanel {
	private JTextArea text;
	
	public RelationsEntry() {
		add(new JLabel("Relations: "));
		add(new JScrollPane(text = new JTextArea(5, 40)));
		text.setLineWrap(true);
	}
	
	private CSP.Relation parseRelation(String r, ArrayList<Character> tokens) {
		String[] clauses = r.split(",");
		CSP.Relation rel = new CSP.Relation();
		rel.k = clauses.length;
		rel.arity = clauses[0].length();
		rel.allowed = new int[rel.k][rel.arity];
		for (int i = 0; i < rel.k; i++) {
			for (int j = 0; j < rel.arity; j++) {
				rel.allowed[i][j] = tokens.indexOf(clauses[i].charAt(j));
			}
		}
		return rel;
	}
	
	public ArrayList<Character> getTokens(String s){
		HashSet<Character> tokens = new HashSet<>();
		for(String line : s.split("\\n")){
			for(String tok : line.split(",")){
				for(char c : tok.toCharArray()){
					tokens.add(c);
				}
			}
		}
		
		return new ArrayList<>(tokens);
	}
	
	public CSP.Relation[] getRelations() {
		ArrayList<Character> tokens = getTokens(text.getText());
		
		String[] lines = text.getText().split("\\n");
		CSP.Relation[] relations = new CSP.Relation[lines.length];
		for (int i = 0; i < lines.length; i++) {
			relations[i] = parseRelation(lines[i], tokens);
		}
		return relations;
	}
	
	public void setRelations(CSP csp) {
		StringBuilder sb = new StringBuilder();
		for (CSP.Relation r : csp.R) {
			for (int[] clause : r.allowed) {
				for (int e : clause) {
					String name = (csp.Dnames==null) ? ""+e : csp.Dnames[e];
					sb.append(name);
				}
				sb.append(',');
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append('\n');
		}
		sb.deleteCharAt(sb.length() - 1);
		text.setText(sb.toString());
	}
	
	public void clear() {
		text.setText("");
	}
}