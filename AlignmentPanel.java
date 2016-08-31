import java.io.*;
import java.util.*;
import java.util.zip.DataFormatException;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.Insets;
import java.awt.Component;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.work.Task;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.swing.PanelTaskManager;
import org.cytoscape.work.TaskIterator;
import org.osgi.framework.FrameworkUtil;

public class AlignmentPanel extends JPanel implements CytoPanelComponent
{
	private CySwingAppAdapter adapter;
	private JFileChooser fileChooser = new JFileChooser();
        private JFileChooser dirChooser = new JFileChooser();
        private JScrollPane scrollPane = new JScrollPane();
        private JPanel pane = new JPanel();
        private JPanel settingsPane = new JPanel();
        private JLabel densityThreshLabel = new JLabel();
        private JLabel sigmaLabel = new JLabel();
        private JTextField densityThreshText = new JTextField();
        private JTextField sigmaText = new JTextField();
        private JButton densityThreshTip = new JButton();
        private JButton sigmaTip = new JButton();
        private JButton alignButton = new JButton();
        private JPanel optionalSettingsPane = new JPanel();
        private JLabel overlapLabel = new JLabel();
        private JLabel refineLabel = new JLabel();
        private JLabel minComplexSizeLabel = new JLabel();
        private JCheckBox activeOptionalCheck = new JCheckBox();
        private JTextField overlapText = new JTextField();
        private JTextField refineText = new JTextField();
        private JTextField minComplexSizeText = new JTextField();
	private JButton overlapTip = new JButton();
        private JButton refineTip = new JButton();
        private JButton minComplexSizeTip = new JButton();
        private JPanel networksPane = new JPanel();
	private JTable tabNet=new JTable();
	private Vector<String> listPath=new Vector<String>();
	private Vector<String> listNet=new Vector<String>();
        private JButton addNetButton = new JButton();
	private JButton removeNetButton = new JButton();
	private JButton clearAllNetButton = new JButton();
        private JPanel filesPane = new JPanel();
	private JRadioButton homologyType = new JRadioButton("Use BLAST E-values",true);
	private JRadioButton noHomologyType = new JRadioButton("Use protein names", false);
        private JLabel homologyFileLabel = new JLabel();
        private JLabel destFolderLabel = new JLabel();
        private JTextField homologyFileText = new JTextField();
        private JTextField destFolderText = new JTextField();
	private JButton homologyTip=new JButton();
	private JButton destFolderTip=new JButton();
        private JPanel ontologiesPane = new JPanel();
        private JButton addOntButton = new JButton();
        private JButton removeOntButton = new JButton();
	private JButton clearAllOntButton = new JButton();
	private JTable tabOnt=new JTable();
	private Vector<String> ontFiles=new Vector<String>();
	private JPanel outputPane=new JPanel();
	
	public AlignmentPanel(CySwingAppAdapter adapter) 
	{
		this.adapter=adapter;
		initSimilarityPanel();
		initNetsPanel();
		initParamPanel();
		initOptionalParamPanel();
		initOntologiesPanel();
		initOutputPanel();
		initGasolinePanel();
		alignButton.setText("Align");
		alignButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				alignButtonActionPerformed(evt);
			}
		});
	}
	public Component getComponent() 
	{
		return this;
	}
	public CytoPanelName getCytoPanelName() 
	{
		return CytoPanelName.WEST;
	}
	public String getTitle() 
	{
		return "Gasoline";
	}
	public Icon getIcon() 
	{
		return null;
	}
	
	private void initParamPanel() 
	{
		scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		settingsPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Parameters setting"));

		densityThreshLabel.setText("Density threshold: ");
		sigmaLabel.setText("Sigma: ");

		densityThreshTip.setText("?");
		densityThreshTip.setToolTipText("Minimum average density of aligned complexes");
		densityThreshTip.setMargin(new Insets(0,0,0,0));
		densityThreshTip.setEnabled(false);
		sigmaTip.setText("?");
		sigmaTip.setToolTipText("Minimum node degree of initially aligned proteins");
		sigmaTip.setMargin(new Insets(0,0,0,0));
		sigmaTip.setEnabled(false);
	
		densityThreshText.setText("0.8");
		sigmaText.setText("3");

		javax.swing.GroupLayout settingsPaneLayout = new javax.swing.GroupLayout(settingsPane);
		settingsPane.setLayout(settingsPaneLayout);
		settingsPaneLayout.setHorizontalGroup(
			settingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsPaneLayout.createSequentialGroup()
					.addGroup(settingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(sigmaLabel)
						.addComponent(densityThreshLabel))
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(settingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
						.addComponent(sigmaText, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(densityThreshText, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(settingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
						.addComponent(sigmaTip, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(densityThreshTip, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGap(55, 55, 55))
		);
		settingsPaneLayout.setVerticalGroup(
			settingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(settingsPaneLayout.createSequentialGroup()
					.addGroup(settingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(densityThreshLabel)
						.addComponent(densityThreshText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(densityThreshTip, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(settingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(sigmaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(sigmaLabel)
						.addComponent(sigmaTip, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGap(8, 8, 8))
		);
	}

	public void initOptionalParamPanel()
	{
		optionalSettingsPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Optional parameters setting"));

		overlapLabel.setText("Overlap threshold: ");
		refineLabel.setText("Refine: ");
		minComplexSizeLabel.setText("Min Align Size: ");
	
		overlapTip.setText("?");
		overlapTip.setToolTipText("Maximum allowed percentage of common nodes between two alignments, in order to be cosidered distinct");
		overlapTip.setMargin(new Insets(0,0,0,0));
		overlapTip.setEnabled(false);
		refineTip.setText("?");
		refineTip.setToolTipText("Number of extensions followed by removal steps in the iterative phase");
		refineTip.setMargin(new Insets(0,0,0,0));
		refineTip.setEnabled(false);
		minComplexSizeTip.setText("?");
		minComplexSizeTip.setToolTipText("Minimum size of aligned complexes");
		minComplexSizeTip.setMargin(new Insets(0,0,0,0));
		minComplexSizeTip.setEnabled(false);
	
		activeOptionalCheck.setText("Active optional settings");
		activeOptionalCheck.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				activeOptionalCheckActionPerformed(evt);
			}
		});

		overlapText.setText("0.5");
		overlapText.setEnabled(false);
		refineText.setText("20");
		refineText.setEnabled(false);
		minComplexSizeText.setText("4");
		minComplexSizeText.setEnabled(false);

		javax.swing.GroupLayout optionalSettingsPaneLayout = new javax.swing.GroupLayout(optionalSettingsPane);
		optionalSettingsPane.setLayout(optionalSettingsPaneLayout);
		optionalSettingsPaneLayout.setHorizontalGroup(
			optionalSettingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(optionalSettingsPaneLayout.createSequentialGroup()
					.addComponent(activeOptionalCheck)
					.addContainerGap())
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, optionalSettingsPaneLayout.createSequentialGroup()
					.addGroup(optionalSettingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(overlapLabel)
						.addComponent(refineLabel)
						.addComponent(minComplexSizeLabel))
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(optionalSettingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
						.addComponent(refineText, javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(overlapText, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
						.addComponent(minComplexSizeText))
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(optionalSettingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
						.addComponent(refineTip, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(overlapTip, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(minComplexSizeTip, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGap(50, 50, 50))
		);
		optionalSettingsPaneLayout.setVerticalGroup(
			optionalSettingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(optionalSettingsPaneLayout.createSequentialGroup()
					.addComponent(activeOptionalCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(12, 12, 12)
					.addGroup(optionalSettingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(overlapLabel)
						.addComponent(overlapText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(overlapTip, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGap(8, 8, 8)
					.addGroup(optionalSettingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(refineText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(refineLabel)
						.addComponent(refineTip, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(optionalSettingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(minComplexSizeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(minComplexSizeLabel)
						.addComponent(minComplexSizeTip, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
	}

	public void initNetsPanel()
	{
		networksPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Networks"));
		
		String[] columns={"Networks"};
		Object[][] rows= {};
		DefaultTableModel dm = new DefaultTableModel(rows, columns) {
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		tabNet.setModel(dm);
		tabNet.setRowSorter(new TableRowSorter(dm));
		TableColumnModel cm=tabNet.getColumnModel();
		TableColumn tc1=cm.getColumn(0);
		tc1.setPreferredWidth(105);
		javax.swing.JScrollPane netScrollPane = new javax.swing.JScrollPane(tabNet);

		addNetButton.setText("Add");
		addNetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addNetButtonActionPerformed(evt);
			}
		});
	
		removeNetButton.setText("Remove selected");
		removeNetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				removeNetButtonActionPerformed(evt);
			}
		});
	
		clearAllNetButton.setText("Clear all");
		clearAllNetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clearAllNetButtonActionPerformed(evt);
			}
		});
	
		javax.swing.GroupLayout networksPaneLayout = new javax.swing.GroupLayout(networksPane);
		networksPane.setLayout(networksPaneLayout);
		networksPaneLayout.setHorizontalGroup(
			networksPaneLayout.createSequentialGroup()
				.addGroup(networksPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(javax.swing.GroupLayout.Alignment.LEADING, networksPaneLayout.createSequentialGroup()
						.addComponent(addNetButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(removeNetButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(clearAllNetButton))
					.addGap(15,15,15)
					.addComponent(netScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 130, javax.swing.GroupLayout.DEFAULT_SIZE)
					.addGap(20,20,20))
		);
		networksPaneLayout.setVerticalGroup(
			networksPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(javax.swing.GroupLayout.Alignment.LEADING, networksPaneLayout.createSequentialGroup()
				.addGroup(networksPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addComponent(addNetButton)
					.addGap(10,10,10)
					.addComponent(removeNetButton)
					.addGap(10,10,10)
					.addComponent(clearAllNetButton))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(netScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
		);
	}

	public void initSimilarityPanel()
	{
	
		filesPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Similarity information"));
	
		javax.swing.ButtonGroup bg=new javax.swing.ButtonGroup();
		bg.add(homologyType);
		bg.add(noHomologyType);
		homologyType.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				selectHomologyType(evt);
			}
		});
		noHomologyType.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				selectNoHomologyType(evt);
			}
		});
	
		homologyFileLabel.setText("Homology file: ");
		homologyTip.setText("?");
		homologyTip.setToolTipText("Text file with homology scores (BLAST E-values)");
		homologyTip.setMargin(new Insets(0,0,0,0));
		homologyTip.setEnabled(false);
		homologyFileText.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				homologyFileTextMouseClicked(evt);
			}
		});

		javax.swing.GroupLayout filesPaneLayout = new javax.swing.GroupLayout(filesPane);
		filesPane.setLayout(filesPaneLayout);
		filesPaneLayout.setHorizontalGroup(
			filesPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(filesPaneLayout.createSequentialGroup()
					.addComponent(homologyType)
					.addComponent(noHomologyType))
				.addGap(10)
				.addGroup(filesPaneLayout.createSequentialGroup()
					.addComponent(homologyFileLabel)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(homologyFileText, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(homologyTip, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(18,18,18))
		);
		filesPaneLayout.setVerticalGroup(
			filesPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(filesPaneLayout.createSequentialGroup()
					.addGroup(filesPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(homologyType)
						.addComponent(noHomologyType))
					.addGap(10,10,10)
					.addGroup(filesPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(homologyFileLabel)
						.addComponent(homologyFileText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(homologyTip, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
	}

	public void initOntologiesPanel()
	{
		ontologiesPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Ontologies (Optional)"));

		String[] columns2={"Gene Ontologies"};
		Object[][] rows2= {};
		DefaultTableModel dm2 = new DefaultTableModel(rows2, columns2) {
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		tabOnt.setModel(dm2);
		tabOnt.setRowSorter(new TableRowSorter(dm2));
		TableColumnModel cm2=tabOnt.getColumnModel();
		TableColumn tc2=cm2.getColumn(0);
		tc2.setPreferredWidth(105);
		JScrollPane ontoScrollPane = new JScrollPane(tabOnt);
	
		addOntButton.setText("Add");
		addOntButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addOntButtonActionPerformed(evt);
			}
		});

		removeOntButton.setText("Remove selected");
		removeOntButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				removeOntButtonActionPerformed(evt);
			}
		});
	
		clearAllOntButton.setText("Clear all");
		clearAllOntButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clearAllOntButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout ontologiesPaneLayout = new javax.swing.GroupLayout(ontologiesPane);
		ontologiesPane.setLayout(ontologiesPaneLayout);
		ontologiesPaneLayout.setHorizontalGroup(
			ontologiesPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.LEADING, ontologiesPaneLayout.createSequentialGroup()
					.addComponent(addOntButton)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(removeOntButton)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(clearAllOntButton))
				.addGap(15,15,15)
				.addComponent(ontoScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 130, javax.swing.GroupLayout.DEFAULT_SIZE)
				.addGap(20,20,20)
		);
		ontologiesPaneLayout.setVerticalGroup(
			ontologiesPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.LEADING, ontologiesPaneLayout.createSequentialGroup()
				.addGroup(ontologiesPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addComponent(addOntButton)
					.addGap(10,10,10)
					.addComponent(removeOntButton)
					.addGap(10,10,10)
					.addComponent(clearAllOntButton))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(ontoScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
		);
	}
	
	public void initOutputPanel()
	{
	
		outputPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Output"));
	
		destFolderLabel.setText("Output folder: ");
		destFolderTip.setText("?");
		destFolderTip.setToolTipText("Folder where final alignments will be saved");
		destFolderTip.setMargin(new Insets(0,0,0,0));
		destFolderTip.setEnabled(false);
		destFolderText.setText(System.getProperty("user.dir"));
		destFolderText.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				destFolderTextMouseClicked(evt);
			}
		});
	
		javax.swing.GroupLayout outputPaneLayout = new javax.swing.GroupLayout(outputPane);
		outputPane.setLayout(outputPaneLayout);
		outputPaneLayout.setHorizontalGroup(
			outputPaneLayout.createSequentialGroup()
				.addComponent(destFolderLabel)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(destFolderText, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(destFolderTip, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(10,10,10)
		);
		outputPaneLayout.setVerticalGroup(
			outputPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
				.addComponent(destFolderText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addComponent(destFolderLabel)
				.addComponent(destFolderTip, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(18,18,18)
		);
	}
	
	public void initGasolinePanel()
	{
		
		javax.swing.GroupLayout paneLayout = new javax.swing.GroupLayout(pane);
		pane.setLayout(paneLayout);
		paneLayout.setHorizontalGroup(
			paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(paneLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(paneLayout.createSequentialGroup()
						.addGroup(paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
							.addComponent(ontologiesPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(settingsPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(optionalSettingsPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(outputPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(filesPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(networksPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(alignButton, javax.swing.GroupLayout.Alignment.CENTER, 80, 80, 80))
						.addGap(0, 0, Short.MAX_VALUE))))
		);
		paneLayout.setVerticalGroup(
		paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(paneLayout.createSequentialGroup()
				.addGap(15, 15, 15)
				.addComponent(filesPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(networksPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(settingsPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(optionalSettingsPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(ontologiesPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(outputPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGap(15, 15, 15)
				.addComponent(alignButton)
				.addGap(20, 20, 20))
		);

		scrollPane.setViewportView(pane);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(scrollPane)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 933, Short.MAX_VALUE)
		);
	}

	private void selectHomologyType(java.awt.event.ActionEvent evt)
	{
		homologyFileText.setVisible(true);
		homologyFileLabel.setVisible(true);
		homologyTip.setVisible(true);
	}
	
	private void selectNoHomologyType(java.awt.event.ActionEvent evt)
	{
		homologyFileText.setVisible(false);
		homologyFileLabel.setVisible(false);
		homologyTip.setVisible(false);
	}
    
	private void alignButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{
		String homologyFileName=null;
		if(homologyType.isSelected())
			homologyFileName=homologyFileText.getText();
		String destFolderName=destFolderText.getText();
		int sigma;
		double overlap;
		int refine;
		int minComplexSize;
		double density;
		try
		{
			sigma=Integer.parseInt(sigmaText.getText());
			overlap=Double.parseDouble(overlapText.getText());
			refine=Integer.parseInt(refineText.getText());
			minComplexSize=Integer.parseInt(minComplexSizeText.getText());
			density=Double.parseDouble(densityThreshText.getText());
		}
		catch(NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(), "Please configure correctly all settings");
			return;
		}
        
		DefaultTableModel tm=(DefaultTableModel)tabNet.getModel();
		int numRows=tm.getRowCount();
		if(numRows<2)
		{
			JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(), "Please import at least two networks");
			return;
		}
        
		if(destFolderText.getText().isEmpty() || sigma<0 || minComplexSize<=0 || overlap<0 || refine<0 || density<=0)
		{
			JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(), "Please configure all necessary settings");
			return;
		}
		
		alignButton.setEnabled(false);
		addNetButton.setEnabled(false);
		removeNetButton.setEnabled(false);
		clearAllNetButton.setEnabled(false);
		addOntButton.setEnabled(false);
		removeOntButton.setEnabled(false);
		clearAllOntButton.setEnabled(false);
		homologyFileText.setEnabled(false);
		destFolderText.setEnabled(false);
		sigmaText.setEnabled(false);
		overlapText.setEnabled(false);
		refineText.setEnabled(false);
		minComplexSizeText.setEnabled(false);
		densityThreshText.setEnabled(false);
		activeOptionalCheck.setSelected(false);
		
		//Starting algorithm
		CyServiceRegistrar csr=adapter.getCyServiceRegistrar();
		PanelTaskManager ptm=csr.getService(PanelTaskManager.class);
		TaskIterator taskIt=new TaskIterator();
		AlignmentTask task = new AlignmentTask (sigma, overlap, refine, minComplexSize, density, listPath, listNet, homologyFileName, destFolderName, this, ontFiles, adapter);
		taskIt.append(task);
		ptm.execute(taskIt);
	}

	public void enable()
	{
		alignButton.setEnabled(true);
		addNetButton.setEnabled(true);
		removeNetButton.setEnabled(true);
		clearAllNetButton.setEnabled(true);
		addOntButton.setEnabled(true);
		removeOntButton.setEnabled(true);
		clearAllOntButton.setEnabled(true);
		if(homologyType.isSelected())
			homologyFileText.setEnabled(true);
		destFolderText.setEnabled(true);
		sigmaText.setEnabled(true);
		densityThreshText.setEnabled(true);
	}
	
	private void activeOptionalCheckActionPerformed(java.awt.event.ActionEvent evt) 
	{
		if(activeOptionalCheck.isSelected())
		{
			overlapText.setEnabled(true);
			refineText.setEnabled(true);
			minComplexSizeText.setEnabled(true);
		}
		else if(!activeOptionalCheck.isSelected())
		{
			overlapText.setEnabled(false);
			refineText.setEnabled(false);
			minComplexSizeText.setEnabled(false);
		}
	}

	private void homologyFileTextMouseClicked(java.awt.event.MouseEvent evt) 
	{
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		int response = fileChooser.showOpenDialog(adapter.getCySwingApplication().getJFrame()); 
		String fname = homologyFileText.getText();
		File file=null;
		if(response==JFileChooser.APPROVE_OPTION) 
		{ 
			int line=0;
			file = fileChooser.getSelectedFile(); 
			fname=file.getAbsolutePath();
				
			//Controllo formato
			File[] list=new File[1];
			list[0]=file;
			FileFormatTask task = new FileFormatTask(list,null);
			CyServiceRegistrar csr=adapter.getCyServiceRegistrar();
			PanelTaskManager ptm=csr.getService(PanelTaskManager.class);
			TaskIterator taskIt=new TaskIterator(task);
			ptm.execute(taskIt);
			try{
				Thread.sleep(1000);
			}
			catch(Exception e){}
			if(task.getError()!=null)
			{
				JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(), task.getError());
				return;
			}
		}
		homologyFileText.setText(fname);
	}

	private void addNetButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{
		int i=0;
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(true);
		int response = fileChooser.showOpenDialog(adapter.getCySwingApplication().getJFrame()); 
		if(response==JFileChooser.APPROVE_OPTION) 
		{ 
			File[] list=fileChooser.getSelectedFiles();
			DefaultTableModel tm=(DefaultTableModel)tabNet.getModel();
			//Controllo formato file
			FileFormatTask task = new FileFormatTask(list,null);
			CyServiceRegistrar csr=adapter.getCyServiceRegistrar();
			PanelTaskManager ptm=csr.getService(PanelTaskManager.class);
			TaskIterator taskIt=new TaskIterator(task);
			ptm.execute(taskIt);
			try{
				Thread.sleep(1000);
			}
			catch(Exception e){}
			if(task.getError()!=null)
				JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(), task.getError());
			else
			{
				for(i=0;i<list.length;i++)
				{
					//Aggiunta della rete, se non gia' presente
					if(!listPath.contains(list[i].getAbsolutePath()))
					{
						Vector row=new Vector();
						row.add(list[i].getName());
						tm.addRow(row);
						listPath.add(list[i].getAbsolutePath());
						listNet.add(list[i].getName().replace(".txt",""));
					}
				}
			}
		} 
	}
	
	private void removeNetButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{
		int row=0;
		DefaultTableModel tm=(DefaultTableModel)tabNet.getModel();
		while((row=tabNet.getSelectedRow())!=-1)
		{
			tm.removeRow(row);
			listNet.remove(row);
			listPath.remove(row);
		}
	}
	
	private void clearAllNetButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{
		int i=0;
		DefaultTableModel tm=(DefaultTableModel)tabNet.getModel();
		int numRows=tm.getRowCount();
		for(i=0;i<numRows;i++)
			tm.removeRow(0);
		listNet.removeAllElements();
		listPath.removeAllElements();
	}
	
	private void destFolderTextMouseClicked(java.awt.event.MouseEvent evt) 
	{
		dirChooser.setCurrentDirectory(new File(destFolderText.getText()));
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirChooser.setAcceptAllFileFilterUsed(false);
		int response = dirChooser.showSaveDialog(adapter.getCySwingApplication().getJFrame());
		String dirName = destFolderText.getText();
		if(response==JFileChooser.APPROVE_OPTION) 
		{ 
			try 
			{ 
				File f = dirChooser.getSelectedFile();
				dirName=f.getAbsolutePath();
			}
			catch(Exception e) 
			{
				System.out.println("Error choosing destination Folder");
			} 
		} 
		destFolderText.setText(dirName);
	}

	private void addOntButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{
		int i=0;
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(true);
		int response = fileChooser.showOpenDialog(adapter.getCySwingApplication().getJFrame()); 
		String fname = null;
		String name=null;
		if(response==JFileChooser.APPROVE_OPTION) 
		{ 
			File[] list=fileChooser.getSelectedFiles();
			DefaultTableModel tm=(DefaultTableModel)tabOnt.getModel();
			//Controllo formato file
			FileFormatTask task = new FileFormatTask(list, "[^\t]+[\t][^\t]+[\t][^\t]+[\t][^\t]+[\t][^\t]+");
			CyServiceRegistrar csr=adapter.getCyServiceRegistrar();
			PanelTaskManager ptm=csr.getService(PanelTaskManager.class);
			TaskIterator taskIt=new TaskIterator(task);
			ptm.execute(taskIt);
			try{
				Thread.sleep(1000);
			}
			catch(Exception e){}
			if(task.getError()!=null)
			{
				JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(), task.getError());
			}
			else
			{
				for(i=0;i<list.length;i++)
				{
					//Aggiunta del file delle ontologie, se non gia' presente
					if(!ontFiles.contains(list[i].getAbsolutePath()))
					{
						Vector row=new Vector();
						row.add(list[i].getName());
						tm.addRow(row);
						ontFiles.add(list[i].getAbsolutePath());
					}
				}
			}
		}
	}
	
	private void removeOntButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{
		int row=0;
		DefaultTableModel tm=(DefaultTableModel)tabOnt.getModel();
		while((row=tabOnt.getSelectedRow())!=-1)
		{
			tm.removeRow(row);
			ontFiles.remove(row);
		}
	}
	
	private void clearAllOntButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{
		int i=0;
		DefaultTableModel tm=(DefaultTableModel)tabOnt.getModel();
		int numRows=tm.getRowCount();
		for(i=0;i<numRows;i++)
			tm.removeRow(0);
		ontFiles.removeAllElements();
	}
}
