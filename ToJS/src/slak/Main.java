package slak;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Main {
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setTitle("ToJS");
	    f.setLocation(300,100);
	    f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTextArea input = new JTextArea("<Input code here>");
		JTextArea tokenSpecs = new JTextArea("<Token list here>");
		JTextArea output = new JTextArea("<Output here>");
		output.setEditable(false);
		JButton run = new JButton("Transpile");
		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Parser p = new Parser(input.getText()+"\n", tokenSpecs.getText());
				try {p.parsePseudocode();}
				finally {output.setText(p.getOutput());}
			}
		});
		
		input.setBounds(0, 0, 300, 600);
		tokenSpecs.setBounds(305, 0, 300, 600);
		output.setBounds(610, 0, 300, 600);
		run.setBounds(0, 600, 915, 75);

		f.setLayout(null);
		f.add(input);
		f.add(tokenSpecs);
		f.add(output);
		f.add(run);
		
		f.setSize(915, 700);
		f.setVisible(true);
	}
}
