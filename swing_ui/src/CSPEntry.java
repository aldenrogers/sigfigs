import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public class CSPEntry extends JPanel {
	private static void lookNimbus() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void lookSeaglass() {
		try {
			UIManager.setLookAndFeel(com.seaglasslookandfeel.SeaGlassLookAndFeel.class.getCanonicalName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		lookSeaglass();
		JFrame window = new JFrame("Siggers figures it out");
		window.add(new CSPEntry());
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	private JSpinner domainSize;
	private RelationsEntry relations;
	private JButton run;
	private JLabel result;
	private Box box;

	private JComponent makeDomainEditor() {
		JPanel pan = new JPanel();
		pan.add(new JLabel("Domain size: "));
		domainSize = new JSpinner(new SpinnerNumberModel(2, 1, 9, 1));
		pan.add(domainSize);
		return pan;
	}
	
	private static class NamedCSP {
		public String name;
		public CSP problem;
		
		public NamedCSP(String name, CSP problem) {
			this.name = name;
			this.problem = problem;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}

	private static NamedCSP[] presets = {
			new NamedCSP("2-SAT", CSP.get2SAT_Neg()),
			new NamedCSP("3-coloring", CSP.get3Color()),
			new NamedCSP("Digraph connectivity", CSP.getDigraphConnected()),
			new NamedCSP("Jeavons' example", CSP.getJeavonsExample()),
			new NamedCSP("Algebra Mod-3", CSP.getLIN3())
	};
	
	private JComponent makePresets() {
		Box box = Box.createHorizontalBox();
		box.add(new JLabel("Presets: "));
		JButton clear = new JButton("Clear");
		box.add(clear);
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				domainSize.setValue(2);
				relations.clear();
			}
		});
		for (NamedCSP preset : presets) {
			JButton button = new JButton(preset.name);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					loadCSP(preset.problem);
				}
			});
			box.add(button);
		}
		return box;
	}

	public void loadCSP(CSP problem) {
		domainSize.setValue(problem.D);
		relations.setRelations(problem.R);
	}

	public CSP buildCSP() {
		CSP problem = new CSP();
		problem.D = (Integer) domainSize.getValue();
		problem.R = relations.getRelations();
		problem.numR = problem.R.length;
		return problem;
	}
	
	private String color(String text, String color){
		return "<html><div style='color: "+ color+ ";'>"+ text + "</div></html>";
	}

	private void run() {
		result.setText(color("Searching for Siggers polymorphism...","#AAAA00"));
//		box.pack();
		
		new Thread(){public void run(){
			SiggersToCNF writer = new SiggersToCNF();
			writer.writeProblem(buildCSP());
			
			//Solve the CNF to find (or fail) the Siggers formula
			String solString;
			try {
				solString = new Glucose(writer).getSolution(true);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			if(solString == null){
				result.setText(color("Proved NP-Complete! This problem is hard!","#CC0000"));
				return;
			}
			
			ArrayList<Integer> trues = Glucose.solutionVars(solString);
			writer.describe(trues);
			SiggersPoly sigger = writer.getSiggers(trues);
			result.setText(color("Found Siggers polymorphism.\n Code to efficiently solve this problem has been generated.","#00CC00"));
			return;
		}}.start();
	}

	public CSPEntry() {
		box = Box.createVerticalBox();
		box.add(makePresets());
		box.add(makeDomainEditor());
		box.add(relations = new RelationsEntry());
		box.add(run = new JButton("Run"));
		
		JPanel resPan = new JPanel();
		resPan.add(result = new JLabel("Waiting for problem input", SwingConstants.LEFT));
		box.add(resPan);
		add(box);

		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				run();
			}
		});
	}
}
