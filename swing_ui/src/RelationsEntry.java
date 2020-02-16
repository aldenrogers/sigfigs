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
	
	private CSP.Relation parseRelation(String r) {
		String[] clauses = r.split(",");
		CSP.Relation rel = new CSP.Relation();
		rel.k = clauses.length;
		rel.arity = clauses[0].length();
		rel.allowed = new int[rel.k][rel.arity];
		for (int i = 0; i < rel.k; i++) {
			for (int j = 0; j < rel.arity; j++) {
				rel.allowed[i][j] = clauses[i].charAt(j) - '0';
			}
		}
		return rel;
	}
	
	public CSP.Relation[] getRelations() {
		String[] lines = text.getText().split("\\n");
		CSP.Relation[] relations = new CSP.Relation[lines.length];
		for (int i = 0; i < lines.length; i++) {
			relations[i] = parseRelation(lines[i]);
		}
		return relations;
	}
	
	public void setRelations(CSP.Relation[] relations) {
		StringBuilder sb = new StringBuilder();
		for (CSP.Relation r : relations) {
			for (int[] clause : r.allowed) {
				for (int e : clause) {
					sb.append(e);
				}
				sb.append(',');
			}
			sb.append('\n');
		}
		text.setText(sb.toString());
	}
	
	public void clear() {
		text.setText("");
	}
}