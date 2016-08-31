import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
import java.awt.Color;
import java.awt.Paint;
import javax.swing.JOptionPane;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.work.swing.PanelTaskManager;


public class CreateSubGraphTask extends AbstractTask
{
	private CySwingAppAdapter adapter;
	private Graph[] gList;
	private Alignment alignment;
	private int count;
	private CyTable nodeTable;
	private Vector<String> ontFiles;

	public CreateSubGraphTask(Graph[] gList, Alignment alignment, int count, CySwingAppAdapter adapter, Vector<String> ontFiles) 
	{
		this.gList = gList;
		this.alignment = alignment;
		this.count = count;
		this.adapter=adapter;
		this.ontFiles=ontFiles;
	}
    
	public void run(TaskMonitor taskMonitor) 
	{
		if(taskMonitor==null)
			throw new IllegalStateException("Task Monitor is not set.");
		else if(taskMonitor!=null)
		{
			taskMonitor.setProgress(-1);
			taskMonitor.setStatusMessage("Import");
		}
		Vector<String>[] mapping=alignment.getMapping();
		CyServiceRegistrar csr=adapter.getCyServiceRegistrar();
		CyNetworkFactory netFact=csr.getService(CyNetworkFactory.class);
		CyNetwork cyNetwork = netFact.createNetwork();
		cyNetwork.getRow(cyNetwork).set(CyNetwork.NAME,"Alignment_"+count);
		CyNetworkManager cnm=adapter.getCyNetworkManager();
		cnm.addNetwork(cyNetwork);
		
		//Table node attributes
		nodeTable=cyNetwork.getDefaultNodeTable();
		String attributeNode = "Protein_name"; 
		nodeTable.createColumn(attributeNode, String.class, false);
		attributeNode = "Network"; 
		nodeTable.createColumn(attributeNode, String.class, false);
		
		//Table edge attributes
		CyTable tableEdge=cyNetwork.getDefaultEdgeTable();
		String attributeEdge2 = "Weight"; 
		tableEdge.createColumn(attributeEdge2, Double.class, false);
		
		for(int k=0;k<mapping.length;k++)
		{
			Graph g=gList[k];
			Vector<String> nodi=mapping[k];
			int i=0, j=0;
			HashMap<String,Node> listaNodi=g.getMapNodes();
			for(i=0;i<nodi.size();i++)
			{
				CyNode node=cyNetwork.addNode();
				CyRow row = nodeTable.getRow(node.getSUID());
				row.set("Protein_name",nodi.get(i));
				row.set("Network",g.getName());
			}
			HashMap<String,Double> adiac=null;
			for(i=0;i<nodi.size();i++)
			{
				adiac=listaNodi.get(nodi.get(i)).getAdiacs();
				Collection<CyRow> rowSource=nodeTable.getMatchingRows("Protein_name",nodi.get(i));
				Iterator<CyRow> it=rowSource.iterator();
				String primaryKeySource = nodeTable.getPrimaryKey().getName();
				CyNode source=null;
				while(it.hasNext())
				{
					CyRow row=it.next();
					if(row.get("Network",String.class).equals(g.getName()))
					{
						source=cyNetwork.getNode(row.get(primaryKeySource, Long.class));
						break;
					}
				}
				for(j=0;j<nodi.size();j++)
				{
					if(adiac.containsKey(nodi.get(j)) && i<j)
					{
						Collection<CyRow> rowDest=nodeTable.getMatchingRows("Protein_name",nodi.get(j));
						it=rowDest.iterator();
						String primaryKeyDest = nodeTable.getPrimaryKey().getName();
						CyNode dest=null;
						while(it.hasNext())
						{
							CyRow row=it.next();
							if(row.get("Network",String.class).equals(g.getName()))
							{
								dest=cyNetwork.getNode(row.get(primaryKeyDest, Long.class));
								break;
							}
						}
						CyEdge edge=cyNetwork.addEdge(source, dest, false);
						CyRow row = tableEdge.getRow(edge.getSUID());
						Double weight=adiac.get(nodi.get(j));
						row.set("interaction","intra");
						row.set(attributeEdge2,weight);
					}

				}
			}
		}
		for(int j=0; j<mapping.length-1; j++)
		{
			Vector<String> nodi=mapping[j];
			Graph g=gList[j];
			HashMap<String,Node> listaNodi=g.getMapNodes();
			Vector<String> nodiNext=mapping[j+1];
			Graph gNext=gList[j+1];
			HashMap<String,Node> listaNodiNext=gNext.getMapNodes();
			for(int i=0; i<mapping[j].size(); i++)
			{
				Collection<CyRow> rowSource=nodeTable.getMatchingRows("Protein_name",nodi.get(i));
				Iterator<CyRow> it=rowSource.iterator();
				String primaryKeySource = nodeTable.getPrimaryKey().getName();
				CyNode source=null;
				while(it.hasNext())
				{
					CyRow row=it.next();
					if(row.get("Network",String.class).equals(g.getName()))
					{
						source=cyNetwork.getNode(row.get(primaryKeySource, Long.class));
						break;
					}
				}
				Collection<CyRow> rowDest=nodeTable.getMatchingRows("Protein_name",nodiNext.get(i));
				it=rowDest.iterator();
				String primaryKeyDest = nodeTable.getPrimaryKey().getName();
				CyNode dest=null;
				while(it.hasNext())
				{
					CyRow row=it.next();
					if(row.get("Network",String.class).equals(gNext.getName()))
					{
						dest=cyNetwork.getNode(row.get(primaryKeyDest, Long.class));
						break;
					}
				}
				CyEdge edge=cyNetwork.addEdge(source, dest, false);
				CyRow row = tableEdge.getRow(edge.getSUID());
				row.set("interaction","inter");
			}
		}
		
		//imposto visualStyle
		CyNetworkViewFactory cnvf=adapter.getCyNetworkViewFactory();
		CyNetworkView cyView = cnvf.createNetworkView(cyNetwork);
		CyNetworkViewManager cnvm=adapter.getCyNetworkViewManager();
		cnvm.addNetworkView(cyView);
		VisualMappingManager manager=adapter.getVisualMappingManager();
		VisualStyle defaultStyle=manager.getDefaultVisualStyle();
		VisualStyleFactory vsf=adapter.getVisualStyleFactory();
		VisualStyle vs= vsf.createVisualStyle("GASOLINE style");
		enrichVisualStyle(cyNetwork, vs);
		vs.setDefaultValue(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT, new Color(165,165,165));
		vs.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_SELECTED_PAINT, Color.WHITE);
		vs.setDefaultValue(BasicVisualLexicon.NODE_SIZE, 35.0);
		vs.setDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR, new Color(255,102,102));
		vs.setDefaultValue(BasicVisualLexicon.NODE_LABEL_FONT_SIZE, 10);
		vs.setDefaultValue(BasicVisualLexicon.EDGE_WIDTH, 2.0);
		manager.addVisualStyle(vs);
		manager.setCurrentVisualStyle(vs);
		vs.apply(cyView);
        
		//imposto layout
		CyLayoutAlgorithmManager clam=adapter.getCyLayoutAlgorithmManager();
		CyLayoutAlgorithm alg = clam.getLayout("kamada-kawai");
		TaskIterator ti=alg.createTaskIterator(cyView,alg.getDefaultLayoutContext(),CyLayoutAlgorithm.ALL_NODE_VIEWS,null);
		
		//Insert optional GO_info
		if(!ontFiles.isEmpty())
		{
			GOTask taskOnt = new GOTask(ontFiles, nodeTable, adapter);
			ti.append(taskOnt);
		}
		
		super.insertTasksAfterCurrentTask(ti);
		cyView.updateView();
	}

	public CyTable getNodeTable()
	{
		return nodeTable;
	}
	
	public void cancel() 
	{}

	public String getTitle() 
	{
		return "Create subgraph";
	}
	
	public void enrichVisualStyle(CyNetwork network, VisualStyle vs) 
	{
                // Passthrough Mapping - Visualize node labels
		VisualMappingFunctionFactory vmfFactoryP=adapter.getVisualMappingFunctionPassthroughFactory();
		PassthroughMapping labelMapping = (PassthroughMapping) vmfFactoryP.createVisualMappingFunction("Protein_name", String.class, BasicVisualLexicon.NODE_LABEL);
		
		// Discrete Mapping - Set edge target arrow shape
		VisualMappingFunctionFactory vmfFactoryD=adapter.getVisualMappingFunctionDiscreteFactory();
		DiscreteMapping lineStyleMapping = (DiscreteMapping) vmfFactoryD.createVisualMappingFunction("Interaction", String.class, BasicVisualLexicon.EDGE_LINE_TYPE);
                lineStyleMapping.putMapValue("intra", LineTypeVisualProperty.SOLID);
                lineStyleMapping.putMapValue("inter", LineTypeVisualProperty.EQUAL_DASH);

                // Continuous Mapping - set edge color
		VisualMappingFunctionFactory vmfFactoryC=adapter.getVisualMappingFunctionContinuousFactory();
		ContinuousMapping continuousMapping = (ContinuousMapping) vmfFactoryC.createVisualMappingFunction("Weight", Double.class, BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT);
		Color minColor = Color.GREEN;
		Color midColor = Color.YELLOW;
		Color maxColor = Color.RED;
		BoundaryRangeValues<Paint> bv0 = new BoundaryRangeValues<Paint>(minColor, minColor, minColor);
		BoundaryRangeValues<Paint> bv1 = new BoundaryRangeValues<Paint>(midColor, midColor, midColor);
		BoundaryRangeValues<Paint> bv2 = new BoundaryRangeValues<Paint>(maxColor, maxColor, maxColor);
		
		// Set the attribute point values associated with the boundary values
		continuousMapping.addPoint(0.0, bv0);
		continuousMapping.addPoint(0.5, bv1);
		continuousMapping.addPoint(1, bv2);
               
                // Add the new styles
		vs.addVisualMappingFunction(labelMapping);
		vs.addVisualMappingFunction(lineStyleMapping);
		vs.addVisualMappingFunction(continuousMapping);
        }
}
