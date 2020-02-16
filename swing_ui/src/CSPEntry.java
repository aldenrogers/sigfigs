import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
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
			new NamedCSP("Jeavon's example", CSP.getJeavonsExample())
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

	private void run() {
		// TODO: build the CSP and classify it. Display result somehow
	}

	public CSPEntry() {
		Box b = Box.createVerticalBox();
		b.add(makePresets());
		b.add(makeDomainEditor());
		b.add(relations = new RelationsEntry());
		b.add(run = new JButton("Run"));
		add(b);

		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				run();
			}
		});
	}
}
