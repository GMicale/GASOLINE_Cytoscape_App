import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.service.util.CyServiceRegistrar;
import java.util.Properties;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.application.swing.CytoPanelName;

public class AlignmentTask extends AbstractTask 
{
	private CySwingAppAdapter adapter;
	private int sigma;
	private double overlapThresh;
	private int iterIterative;
	private int minAlignSize;
	private double densityThresh;
	private Vector<String> pathNets;
	private Vector<String> nameNets;
	private String homologyFile;
	private String outputFolder;
	private TaskMonitor taskMonitor = null;
	private OrderedList<Alignment> rankAlign;
	private Vector<String> ontFiles;
	private AlignmentPanel aPane;
    
	public AlignmentTask(int sigma, double overlapThresh, int iterIterative, int minAlignSize, 
	double densityThresh, Vector<String> pathNets, Vector<String> nameNets, String homologyFile, 
	String outputFolder, AlignmentPanel aPane, Vector<String> ontFiles, CySwingAppAdapter adapter) 
	{
		this.sigma=sigma;
		this.overlapThresh=overlapThresh;
		this.iterIterative=iterIterative;
		this.minAlignSize=minAlignSize;
		this.densityThresh=densityThresh;
		this.pathNets=pathNets;
		this.nameNets=nameNets;
		this.homologyFile=homologyFile;
		this.outputFolder=outputFolder;
		this.aPane=aPane;
		this.ontFiles=ontFiles;
		this.adapter=adapter;
	}   
    
	public void run(TaskMonitor taskMonitor) 
	{
		if (taskMonitor==null)
			throw new IllegalStateException("Task Monitor is not set.");
		else if(taskMonitor!=null) 
		{
			taskMonitor.setProgress(0.0);
			taskMonitor.setStatusMessage("Aligning");
		}
		try 
		{
			//Read input graphs
			FileManager fileMan=new FileManager();
			Graph[] g=new Graph[pathNets.size()];
			for(int i=0; i<g.length; i++)
				g[i]=fileMan.readGraph(pathNets.get(i), nameNets.get(i));
			
			rankAlign=GASOLINE.startAlignment(sigma, overlapThresh, iterIterative, minAlignSize, densityThresh, g, homologyFile, outputFolder, taskMonitor);
			if(rankAlign!=null)
			{
				CytoPanel resPanel=adapter.getCySwingApplication().getCytoPanel(CytoPanelName.EAST);
				resPanel.setState(CytoPanelState.DOCK);
				ResultsPanel alignRes = new ResultsPanel(g, rankAlign, ontFiles, adapter);
				CyServiceRegistrar csr=adapter.getCyServiceRegistrar();
				csr.registerService(alignRes,CytoPanelComponent.class, new Properties());
			}
			aPane.enable();
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(), "Alignment Error");
			aPane.enable();
			return;
		}
	}
	
	public void cancel() 
	{}

	public String getTitle() 
	{
		return "Aligning networks...";
	}
    
}