package gadeSolverApp;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;

import java.awt.Insets;

import javax.swing.SwingConstants;
import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractListModel;
import javax.swing.border.CompoundBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.JTextPane;


public class GadeSolverApp { 
	private JFrame frame;
	private JPanel panelIn; 
	private JTextField input_k;
	private JTextField input_t;
	private JTextField input_y;
	private JTextField input_n;
	private JTextField input_v;
	private JTextField input_u;
	private JTextField input_d;
	private JTextField input_c;
	private JTextField input_cd;
	private JTextPane resultArea;
	private JTextField commandLine;
	private JTextField input_in;
	private JTextField input_tr;
	private JRadioButton input_ng;
	private JRadioButton input_nn;
	private JRadioButton input_kml;
    private JList<String> exampleList;
    
    // 	Helper
    public static String join (String delim, String ... data) {
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < data.length; i++) {
    		if (data[i].length() == 0)
    			continue;
    		sb.append(data[i]);
    		if (i >= data.length-1) {break;}
    		sb.append(delim);
    	}
    	return sb.toString();
    }

    static String[] storedArgs;
    
    /**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		storedArgs = args;
		
		// This is main. We can either run gui or non gui. To run non-gui, first arg MUSt be the switch -nogui
		if (args.length > 0 && args[0].equals("-nogui")) {
			GadeSolver gs = new GadeSolver(args);
			gs.run();
		} else {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						GadeSolverApp window = new GadeSolverApp();
						GadeSolver.setRef(window);
						window.frame.setVisible(true);
						window.frame.setTitle("GadeSolverApp");
						window.setArguments(storedArgs);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * Create the application.
	 */
	public GadeSolverApp() {
		try {
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Output(String s) {
		Document doc = resultArea.getDocument();
		try {
			doc.insertString(doc.getLength(), s, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}	

	public void ClearInput()  {
		input_k.setText("");
		input_d.setText("");
		input_v.setText("");
		input_u.setText("");
		input_y.setText("");
		input_n.setText("");
		input_c.setText("");
		input_cd.setText("");
		input_t.setText("");
		input_tr.setText("");
		input_in.setText("");
		input_ng.setSelected(false);
		input_nn.setSelected(true);
		input_kml.setSelected(false);
	}
	
	public void ClearOutput() {
		resultArea.setText("");
	}
	
	private int gridY = 0;
	private JTextField addTextInputField(String label, String legend, String tooltip) {
		JLabel field = new JLabel(label);
		GridBagConstraints gbc_field = new GridBagConstraints();
		gbc_field.anchor = GridBagConstraints.WEST;
		gbc_field.insets = new Insets(0, 0, 5, 5);
		gbc_field.gridx = 0;
		gbc_field.gridy = gridY;
		panelIn.add(field, gbc_field);
		field.setFont(new Font("Tahoma", Font.PLAIN, 14));
		field.setToolTipText("");
		field.setHorizontalAlignment(SwingConstants.LEFT);
		
		JLabel leg = new JLabel(legend);
		GridBagConstraints gbc_leg = new GridBagConstraints();
		gbc_leg.anchor = GridBagConstraints.WEST;
		gbc_leg.insets = new Insets(0, 0, 5, 5);
		gbc_leg.gridx = 1;
		gbc_leg.gridy = gridY;
		panelIn.add(leg, gbc_leg);
		leg.setFont(new Font("Tahoma", Font.ITALIC, 11));
		leg.setHorizontalAlignment(SwingConstants.LEFT);
		
		JTextField input = new JTextField();
		GridBagConstraints gbc_input = new GridBagConstraints();
		gbc_input.fill = GridBagConstraints.HORIZONTAL;
		gbc_input.insets = new Insets(0, 0, 5, 0);
		gbc_input.gridx = 2;
		gbc_input.gridy = gridY++;
		panelIn.add(input, gbc_input);
		input.setFont(new Font("Tahoma", Font.BOLD, 14));
		input.setToolTipText(tooltip);
		input.setColumns(10);
		
		return input;
	}
	
	private JRadioButton addRadioInputField(String label, String legend, boolean selected) {
		JLabel lbl = new JLabel(label);
		GridBagConstraints gbc_lbl = new GridBagConstraints();
		gbc_lbl.anchor = GridBagConstraints.WEST;
		gbc_lbl.insets = new Insets(0, 0, 5, 5);
		gbc_lbl.gridx = 0;
		gbc_lbl.gridy = gridY;
		panelIn.add(lbl, gbc_lbl);
		lbl.setFont(new Font("Tahoma", Font.PLAIN, 14));

		JRadioButton input = new JRadioButton(legend);
		GridBagConstraints gbc_input_nn = new GridBagConstraints();
		gbc_input_nn.anchor = GridBagConstraints.NORTHWEST;
		gbc_input_nn.insets = new Insets(0, 0, 5, 5);
		gbc_input_nn.gridx = 1;
		gbc_input_nn.gridy = gridY++;
		panelIn.add(input, gbc_input_nn);
		input.setSelected(selected);
		input.setFont(new Font("Tahoma", Font.BOLD, 14));

		return input;
	}
	
	/** 
	 * Decode command line and set GUI fields accordindly.
	 * @param cl Command line
	 */
	private void setArguments(String cl) {
		ClearInput();
		List<String> matchList = new ArrayList<String>();
		Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
		Matcher regexMatcher = regex.matcher(cl);
		while (regexMatcher.find()) {
			String t = regexMatcher.group();
			matchList.add(t.replaceAll("[\"]", ""));
		} 
		String[] args = new String[ matchList.size() ];
		matchList.toArray(args);
		setArguments(args);
	}
	
	/**
	 * setArguments, only with prepared array
	 */
	private void setArguments(String[] args) {
		for (int i=0; i<args.length;) {
			String arg = args[i++];
			
			if (arg.equals("-k")) {
				input_k.setText(join(" ", "-k", args[i++], args[i++]));
			} else if (arg.equals("-d")) {
				input_d.setText(join(" ", input_d.getText(), "-d", args[i++], args[i++]));
			} else if (arg.equals("-v")) {
				input_v.setText(join(" ", input_v.getText(), "-v", args[i++], args[i++]));
			} else if (arg.equals("-u")) {
				input_u.setText(join(" ", input_u.getText(), "-u", args[i++]));
			} else if (arg.equals("-t")) {
				input_t.setText(join(" ", input_t.getText(), "-t", "\"" + args[i++] + "\""));
			} else if (arg.equals("-tr")) {
				input_tr.setText("-tr \"" + args[i++] + "\"");
			} else if (arg.equals("-y")) {
				input_y.setText(join(" ", input_y.getText(), "-y", args[i++]));
			} else if (arg.equals("-n")) {
				input_n.setText(join(" ", input_n.getText(), "-n", args[i++]));
			} else if (arg.equals("-c")) {
				input_c.setText("-c \"" + args[i++] + "\"");
			} else if (arg.equals("-cd")) {
				input_cd.setText(join(" ", input_cd.getText(), "-cd", args[i++]));
			} else if (arg.equals("-md")) {
				input_cd.setText(join(" ", input_cd.getText(), "-md", args[i++]));
			} else if (arg.equals("-nn")) {
				input_nn.setSelected(false);
			} else if (arg.equals("-ng")) {
				input_ng.setSelected(true);;
			} else if (arg.equals("-kml")) {
				input_kml.setSelected(true);
			} else if (arg.equals("-sg")) {
				input_t.setText(join(" ", input_t.getText(), "-sg"));
			} else if (arg.equals("-in")) {
				input_in.setText(join(" ", input_in.getText(), "-in", args[i++], args[i++], args[i++]));
			} else if (arg.equals("-nogui")) {
				// ignore
			} else {
				return;
			}
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1100, 620);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{308, 0};
		gridBagLayout.rowHeights = new int[]{31, 20, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(new CompoundBorder());
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.ipadx = 5;
		gbc_tabbedPane.anchor = GridBagConstraints.NORTH;
		gbc_tabbedPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		frame.getContentPane().add(tabbedPane, gbc_tabbedPane);
		
		panelIn = new JPanel();
		tabbedPane.addTab("Input", null, panelIn, null);
		panelIn.setBorder(new TitledBorder(null, "Input Values", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gbl_panelIn = new GridBagLayout();
		gbl_panelIn.columnWidths = new int[]{201, 150, 709, 0};
		gbl_panelIn.rowHeights = new int[]{20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 120, 0};
		gbl_panelIn.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panelIn.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		panelIn.setLayout(gbl_panelIn);
		
		input_k = addTextInputField("Number of input digits", "-k <min> <max>", "e.g. \"-k 5 8\"");
		input_t = addTextInputField("Template (use lower case for digits)", "-t \"<template>\", $=k, #=input digits, %=gade", "e.g.: -t \"N55 4b.che E012 20.abc\"");
		input_tr = addTextInputField("Template result for searching", "-tr \"<template matching>\"", "e.g. -tr \"N55 12.123 E012 23.234\"");
		input_y = addTextInputField("Digits that MUST be in input (including repetition)", "-y <known input digits, including any repetitions>, e.g. -y 6642", "e.g. -y 4662");  
		input_n = addTextInputField("Digits that must NOT be in input", "-n <digits known NOT to be in niput>, e.g. -n 57", "e.g. -n 46");
		input_v = addTextInputField("Known digit value(s) for resulting gade", "{-v <letter> {digits}}, e.g. -v a 01 -v c 23", "e.g. -v a 0 -v c 12");
		input_u = addTextInputField("Uniqueness digit constraints (groups)", "{-u {letters}}, e.g. -u ab -u cde", "e.g. -u ab -u cde");
		input_d = addTextInputField("Digital root checks, enter letters and or digits", "{-d {letters/digits} <digital root digit>}, e.g.  -d 56ab 3 -d 12bci9h 8", "e.g. -d 56ab 3 -d 12bci9h 8");
		input_c = addTextInputField("Center for distance check", "-c <center coordinate>, e.g. -c \"N55 40.123 E012 20.234\"", "e.g. -c \"N55 40.123 E012 23.234\"");
		input_cd = addTextInputField("Distance check diameter in meters", "-cd <max_distance> -md <min_distance>", "e.g. -d 3000");
		input_in = addTextInputField("Known digits instance ranges", "{-in <digit> <min num of> <max num of>}", "e.g. -in 4 2 4 -in 1 3 6");
	
		int gridYGo = gridY;
		
		input_nn = addRadioInputField("Print solution number per line along with template", "Print solution number", true);
		input_ng = addRadioInputField("Print number of possible given k", "Print number of solutions for k", false); 
		input_kml = addRadioInputField("KML output (e.g. for google earth)", "KML output", false); 

		JButton action_go = new JButton("Go !");
		GridBagConstraints gbc_action_go = new GridBagConstraints();
		gbc_action_go.insets = new Insets(0, 0, 5, 0);
		gbc_action_go.gridx = 2;
		gbc_action_go.gridy = gridYGo;
		panelIn.add(action_go, gbc_action_go);
		action_go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String cl = join(" ", 		
						input_k.getText(),   
						input_d.getText(), 
						input_v.getText(),
						input_u.getText(),
						input_y.getText(),
						input_n.getText(),
						input_c.getText(),
						input_cd.getText(),
						input_t.getText(),
						input_tr.getText(),
						input_in.getText());
				if (input_ng.isSelected())
					cl += " -ng";
				if (!input_nn.isSelected())
					cl += " -nn";
				if (input_kml.isSelected())
					cl += " -kml";
				commandLine.setText(cl);

				List<String> matchList = new ArrayList<String>();
				Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
				Matcher regexMatcher = regex.matcher(cl);
				while (regexMatcher.find()) {
					String t = regexMatcher.group();
					matchList.add(t.replaceAll("[\"]", ""));
				} 
				String[] argArray = new String[ matchList.size() ];
				matchList.toArray(argArray);
				ClearOutput();
				GadeSolver gs = new GadeSolver(argArray);
				new Thread(gs).start();
			}
		});
		action_go.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		JButton btnNewButton = new JButton("Reset Inputs");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ClearInput();
			}
		});
		btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = gridYGo + 1;
		panelIn.add(btnNewButton, gbc_btnNewButton);
		
		JLabel lblGadesolverCommandLine = new JLabel("gadeSolver command line");
		lblGadesolverCommandLine.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_lblGadesolverCommandLine = new GridBagConstraints();
		gbc_lblGadesolverCommandLine.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblGadesolverCommandLine.insets = new Insets(0, 0, 5, 5);
		gbc_lblGadesolverCommandLine.gridx = 0;
		gbc_lblGadesolverCommandLine.gridy = gridY;
		panelIn.add(lblGadesolverCommandLine, gbc_lblGadesolverCommandLine);
		
		commandLine = new JTextField();
		commandLine.setFont(new Font("Tahoma", Font.ITALIC, 14));
		commandLine.setEditable(false);
		GridBagConstraints gbc_commandLine = new GridBagConstraints();
		gbc_commandLine.anchor = GridBagConstraints.NORTHWEST;
		gbc_commandLine.insets = new Insets(0, 0, 5, 0);
		gbc_commandLine.fill = GridBagConstraints.HORIZONTAL;
		gbc_commandLine.gridx = 2;
		gbc_commandLine.gridy = gridY++;
		panelIn.add(commandLine, gbc_commandLine);
		commandLine.setColumns(10);
		
		exampleList = new JList<String>();
		exampleList.setModel(new AbstractListModel<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			String[] values = new String[] {
					"GC3NZZH: Kan du cracke en gade step 1", 
					"GC3NZZH: Kan du cracke en gade step 2 (metode 1)", 
					"GC3NZZH: Kan du cracke en gade step 2 (metode 2)",
					"GC3NZZH: Kan du cracke en gade step 3",
					"GC3NZZH: Kan du cracke en gade step 4",
					"GC3NZZH: Kan du cracke en gade FINAL",
					"GCQHG5: Langt ude p� landet"
					// ,"GCGC5D6AQ: Den sv�reste gade"
					};
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				return values[index];
			}
		});
		exampleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		GridBagConstraints gbc_exampleList = new GridBagConstraints();
		gbc_exampleList.anchor = GridBagConstraints.NORTH;
		gbc_exampleList.fill = GridBagConstraints.BOTH;
		gbc_exampleList.gridx = 2;
		gbc_exampleList.gridy = gridY;
		panelIn.add(new JScrollPane(exampleList), gbc_exampleList);
		
		JScrollPane resultPane = new JScrollPane();
		tabbedPane.addTab("Result", null, resultPane, null);
		
		resultArea = new JTextPane();
		resultArea.setFont(new Font("Tahoma", Font.PLAIN, 14));
		resultPane.setViewportView(resultArea);
	
		JButton action_example = new JButton("Use Selected Example");
		GridBagConstraints gbc_action_example = new GridBagConstraints();
		gbc_action_example.anchor = GridBagConstraints.NORTHWEST;
		gbc_action_example.insets = new Insets(0, 0, 0, 5);
		gbc_action_example.gridx = 1;
		gbc_action_example.gridy = gridY++;
		action_example.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelIn.add(action_example, gbc_action_example);
		
		action_example.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ClearInput();
								
				switch (exampleList.getSelectedIndex()) {
				case 0: // Kan du cracke, step 1
					input_k.setText("-k 4 4");
					input_t.setText("-t \"N cc ce.gjc E fg fh.hcf   A=a, B=b\"");  // N CC CE.GJC, E FG FH.HCF
					input_d.setText("-d cccegjc 4 -d fgfhhcf 4");
					input_v.setText("-v c 5 -v f 1 -v g 2");
					break;
				case 1: // Kan du cracke, step 2, metode 1
					input_k.setText("-k 7 7");
					input_t.setText("-t \"N dd jg.gcl E ac bm.lfi   E=e, H=h\"");
					input_v.setText("-v d 5 -v a 1 -v c 2 -v b 1 -v j 4 -v g 789 -v m 678");
					break;
				case 2: // Kan du cracke, step 2, metode 2
					input_k.setText("-k 7 7");
					input_t.setText("-t \"Ndd jg.gcl E0ac bm.lfi\"");
					// input_v.setText("-v d 5 -v a 1 -v c 2 -v b 1 -v j 4 -v g 789 -v m 678");
					input_c.setText("-c \"N55 48.408 E012 17.772\"");
					input_cd.setText("-cd 3000");
					break;
				case 3: // Kan du cracke, step 3
					input_k.setText("-k 8 8");
					input_t.setText("-t \"N55 5b.cen E012 1f.ahi\"");  
					input_nn.setSelected(false);
					input_c.setText("-c \"N55 50.537 E012 17.048\"");
					input_cd.setText("-cd 3000");  // N55 50.679 E12 17.081
					// input_v.setText("-v b 0 -v f 7");  //  -v c 6 -v h 8
					break;
				case 4: // Kan du cracke, step 4
					input_k.setText("-k 1 10");
					input_ng.setSelected(true);
					break;
				case 5: // Kan du cracke, final
					input_k.setText("-k 11 11");
					input_y.setText("-y <skriv de tal du har fundet>");
					input_t.setText("-t \"N55 5f.keg E012 2l.haf\""); // N 55 5F.KEG, E 12 2L.HAF
					input_c.setText("-c \"N55 53.333 E012 21.555\"");
					input_cd.setText("-cd 3000");
					break;
				case 6: // Langt ude p� landet.
					input_k.setText("-k 3 4");
					input_t.setText("-t \"N55 41.hhj E012 3e.hjg\""); // N 55�41.hhj - E 012�3e.hjg
					input_d.setText("-d 5541hhj 7 -d 0123ehjg 7");
					break;
				case 7: // GC5D6AQ: Den sv�reste gade ....
					setArguments("-k 8 8 -tr \"N55 47.@1@0@3 E012 22.@2@4@0\" -sg");
					break;
					// Total number of possible gades: 3135. Max: 5005	N55 47.dcf E012 22.ega. Min: 215	N55 47.aik E012 22.jli	
					// Kode 	5005	+	3135 = 	8140
				default:
					Output("Example: " + exampleList.getSelectedIndex());
					break;
				}
			}});
	}
}
