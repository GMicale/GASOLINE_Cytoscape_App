import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.application.swing.CytoPanelComponent;
import java.util.Properties;
import org.cytoscape.view.model.events.NetworkViewDestroyedListener;
import org.cytoscape.view.model.events.NetworkViewDestroyedEvent;
import org.cytoscape.model.events.NetworkDestroyedEvent;
import org.cytoscape.event.CyEventHelper;

public class MultiNetAlignmentPlugin
{
    
	public MultiNetAlignmentPlugin(CySwingAppAdapter adapter) 
	{
		super();
		//create a new action to respond to menu activation
		MultiNetAlignmentAction action = new MultiNetAlignmentAction(adapter);
		//set the preferred menu
		action.setPreferredMenu("Apps");
		//and add it to the menus
		adapter.getCySwingApplication().addAction(action);
	}
    
	public class MultiNetAlignmentAction extends AbstractCyAction 
	{
		private boolean opened=false;
		private final CySwingAppAdapter adapter;
		public MultiNetAlignmentAction(CySwingAppAdapter adapter) 
		{
			super("GASOLINE");
			this.adapter=adapter;
		}
		public void actionPerformed(ActionEvent ae) 
		{
			if(!opened)
			{
				AlignmentPanel cPanel = new AlignmentPanel(adapter);
				//Add it to the control panel.
				CyServiceRegistrar csr=adapter.getCyServiceRegistrar();
				csr.registerService(cPanel,CytoPanelComponent.class, new Properties());
				//ctrlPanel.add("Gasoline", cPanel);
				//ctrlPanel.setSelectedIndex(ctrlPanel.indexOfComponent("Gasoline"));
				NetDestrViewListener ndwl=new NetDestrViewListener(adapter);
				//NetDestrListener ndl=new NetDestrListener();
				csr.registerService(ndwl, NetworkViewDestroyedListener.class, new Properties());
				//csr.registerService(ndl, NetworkDestroyedListener.class, new Properties());
				opened=true;
			}
			else
				JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(), "PlugIn is already running.");
		}
	}
    
	public class NetDestrViewListener implements NetworkViewDestroyedListener
	{
		private CySwingAppAdapter adapter;
		public NetDestrViewListener(CySwingAppAdapter adapter)
		{
			this.adapter=adapter;
		}
		public void handleEvent(NetworkViewDestroyedEvent e) 
		{
			CyEventHelper helper=adapter.getCyEventHelper();
			helper.fireEvent(new NetworkDestroyedEvent(adapter.getCyNetworkManager()));
			/*CyNetworkViewManager netViewMan=e.getSource();
			Set<CyNetworkView> views=netMan.getNetworkViewSet();
			Iterator<CyNetworkView> it=views.iterator();
			while(it.hasNext())
			{
				CyNetworkView netView=it.next();
				//CyNetwork cyNetwork = networkView.getNetwork();
				//Cytoscape.getDesktop().getSwingPropertyChangeSupport().removePropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_DESTROYED, this);
				netMan.destroyNetworkView(netView);
				//Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_DESTROYED, this);
			}*/
		}
	}
}