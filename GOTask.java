import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.model.CyRow;

class GOTask extends AbstractTask
{
	private Vector<String> ontFiles;
	private CyTable nodeTable;
	private CySwingAppAdapter adapter;
    
	public GOTask(Vector<String> ontFiles, CyTable nodeTab, CySwingAppAdapter adapter) 
	{
		this.ontFiles=ontFiles;
		this.nodeTable=nodeTab;
		this.adapter=adapter;
	}

	public void run(TaskMonitor taskMonitor) 
	{
		if(taskMonitor==null)
			throw new IllegalStateException("Task Monitor is not set.");
		else if(taskMonitor!=null) 
		{
			taskMonitor.setProgress(-1);
			taskMonitor.setStatusMessage("Importing GOs");
		}
		
		nodeTable.createColumn("Description", String.class, false);
		nodeTable.createColumn("GO_Components", String.class, false);
		nodeTable.createColumn("GO_Functions", String.class, false);
		nodeTable.createColumn("GO_Processes", String.class, false);
		
		for(String fname: ontFiles)
		{
			try 
			{
				BufferedReader in=new BufferedReader(new FileReader(fname));
				String line;
				while((line=in.readLine())!=null)
				{
					String id, descr, comp, func, proc;
					String[] campi=line.split("\t");
					id=campi[0].trim();
					descr=campi[1].trim();
					comp=campi[2].trim();
					func=campi[3].trim();
					proc=campi[4].trim();
					Collection<CyRow> rowDest=nodeTable.getMatchingRows("Protein_name",id);
					Iterator<CyRow> it=rowDest.iterator();
					if(it.hasNext())
					{
						CyRow row=it.next();
						row.set("Description", descr);
						row.set("GO_Components", comp);
						row.set("GO_Functions", func);
						row.set("GO_Processes", proc);
					}
				}
			} 
			catch (FileNotFoundException fnf) 
			{
				fnf.printStackTrace();
				JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(), "Error: File "+fname+" not found");
			} 
			catch(IOException ioe)
			{
				ioe.printStackTrace();
				JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(), "Error reading file");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(), "Format Error in file "+fname);
			}
		}
	}

	public void cancel() 
	{}

	public String getTitle() 
	{
		return "Insert Gene Ontologies";
	}
    
}