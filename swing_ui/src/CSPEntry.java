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

public class CSPEntry extends JPanel {
	private static void lookGood() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) {
		lookGood();
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
	
	private void run() {
		CSP problem = new CSP();
		problem.D = (Integer) domainSize.getValue();
		problem.R = relations.getRelations();
		problem.numR = problem.R.length;
	}

	public CSPEntry() {
		Box b = Box.createVerticalBox(); 
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
