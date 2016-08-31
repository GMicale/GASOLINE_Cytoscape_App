import java.awt.Font;
import java.awt.GridLayout;
import java.util.Vector;
import java.util.List;
import java.awt.Component;
import javax.swing.*;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.work.Task;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.swing.PanelTaskManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyTable;

public class ResultsPanel extends JPanel implements CytoPanelComponent
{
	private Graph[] g;
	private CySwingAppAdapter adapter;
	private OrderedList<Alignment> rankAlign;
	private JScrollPane scrollPane;
	private JPanel pane;
	private Vector<String> ontFiles;
    
	public ResultsPanel(Graph[] g, OrderedList<Alignment> rankAlign, Vector<String> ontFiles, CySwingAppAdapter adapter)
	{
		this.g=g;
		this.rankAlign=rankAlign;
		this.ontFiles=ontFiles;
		this.adapter=adapter;
		initComponents();
		setPane();
		setVisible(true);
	}
    
	public Component getComponent() 
	{
		return this;
	}
	public CytoPanelName getCytoPanelName() 
	{
		return CytoPanelName.EAST;
	}
	public String getTitle() 
	{
		return "Alignment results";
	}
	public Icon getIcon() 
	{
		return null;
	}
	
	private void initComponents()
	{
		pane=new JPanel();
		pane.setLayout(new GridLayout(0,4,10,10));
		scrollPane=new JScrollPane(pane);
		scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
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
    
	private void setPane()
	{
		JLabel align=new JLabel("Alignment #");
		align.setFont(new Font("Serif", Font.BOLD, 15));
		pane.add(align);
		JLabel complexSize=new JLabel("Complex Size");
		complexSize.setFont(new Font("Serif", Font.BOLD, 15));
		pane.add(complexSize);
		JLabel score=new JLabel("ISC Score");
		score.setFont(new Font("Serif", Font.BOLD, 15));
		pane.add(score);
		pane.add(new JLabel(""));
        
		NodeOrdList<Alignment> aux=rankAlign.getMax();
		int count=1;
        
		while(aux!=null)
		{
			final Alignment a=aux.getInfo();
			final int cont=count;
			pane.add(new JLabel("- Alignment "+count));
			pane.add(new JLabel(new Integer(a.getAlignSize()).toString()));
			double weight=a.getIscScore()*1000;
			weight=Math.round(weight);
			weight=weight/1000;
			if(weight==0.0)
				weight=0.001;
			pane.add(new JLabel(new Double(weight).toString()));
			JButton showButton=new JButton("Show");
			showButton.addActionListener(new java.awt.event.ActionListener() 
			{
				public void actionPerformed(java.awt.event.ActionEvent evt) 
				{
					CreateSubGraphTask taskSub = new CreateSubGraphTask(g, a,cont,adapter,ontFiles);
					CyServiceRegistrar csr=adapter.getCyServiceRegistrar();
					PanelTaskManager ptm=csr.getService(PanelTaskManager.class);
					TaskIterator taskIt=new TaskIterator();
					taskIt.append(taskSub);
					ptm.execute(taskIt);
				}
			});
			pane.add(showButton);
			aux=aux.getNext();
			count++;
		}
	}
}
