import org.cytoscape.app.swing.AbstractCySwingApp;
import org.cytoscape.app.swing.CySwingAppAdapter;


public class PluginWrapper extends AbstractCySwingApp 
{
	public PluginWrapper(CySwingAppAdapter adapter) 
	{
		super(adapter);
		MultiNetAlignmentPlugin mna=new MultiNetAlignmentPlugin(adapter);
	}
}
