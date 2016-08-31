import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

public class Graph
{
	private String name;
	private HashMap<String,Node> mapNodes;    
	public Graph(String name)
	{
		mapNodes=new HashMap<String,Node>();
		this.name=name;
	}
	public boolean isEmpty()
	{
		return (numNodes()==0);
	}
	public int numNodes()
	{
		return mapNodes.size();
	}
	public int numEdges()
	{
		int numEdges=0;
		Iterator<String> it=mapNodes.keySet().iterator();
		while(it.hasNext())
		{
			String node=it.next();
			numEdges+=mapNodes.get(node).degree();
		}
		return numEdges/2;
	}
	public Vector<Vector<String>> getEdges()
	{
		Vector<Vector<String>> setEdges=new Vector<Vector<String>>();
		Iterator<String> itSource=mapNodes.keySet().iterator();
		while(itSource.hasNext())
		{
			String idSource=itSource.next();
			HashMap<String,Double> adiac=mapNodes.get(idSource).getAdiacs();
			Iterator<String> itDest=adiac.keySet().iterator();
			while(itDest.hasNext())
			{
				String idDest=itDest.next();
				if(idSource.compareTo(idDest)<0)
				{
					Vector<String> edge=new Vector<String>();
					edge.add(idSource);
					edge.add(idDest);
					edge.add(adiac.get(idDest).toString());
					setEdges.add(edge);
				}
			}
		}
		return setEdges;
	}
	public boolean hasEdge(String idSource, String idDest)
	{
		if(mapNodes.get(idSource).getAdiacs().containsKey(idDest))
			return true;
		else
			return false;
	}
	public String getName()
	{
		return name;
	}
	public HashMap<String,Node> getMapNodes()
	{
		return mapNodes;
	}
	public void addNode(String id)
	{
		if(!mapNodes.containsKey(id))
		{
			Node x=new Node(id);
			mapNodes.put(id,x);
		}
	}
	public void addArc(String idSource, String idDest, double weight)
	{
		Node source=mapNodes.get(idSource);
		Node dest=mapNodes.get(idDest);
		if(mapNodes.containsKey(idSource) && mapNodes.containsKey(idDest))
		{
			mapNodes.get(idSource).getAdiacs().put(idDest,weight);
			mapNodes.get(idDest).getAdiacs().put(idSource,weight);
		}
	}
	public Graph buildInducedSubgraph(Vector<String> nodes)
	{
		int i=0, j=0;
		Graph subGr=new Graph(name);
		for(i=0;i<nodes.size();i++)
			subGr.addNode(nodes.get(i));
		for(i=0;i<nodes.size();i++)
		{
			String source=nodes.get(i);
			HashMap<String,Double> adiac=mapNodes.get(source).getAdiacs();
			for(j=0;j<nodes.size();j++)
			{
				String dest=nodes.get(j);
				if(adiac.containsKey(dest))
					subGr.addArc(source,dest,adiac.get(dest));
			}
		}
		return subGr;
	}
	public String toString()
	{
		if(isEmpty())
			return "Empty graph";
		else 
		{
			String nodes="Nodes = {";
			String edges="Edges = {";
			Iterator<String> itSource=mapNodes.keySet().iterator();
			while(itSource.hasNext())
			{
				String idSource=itSource.next();
				nodes+=idSource+", ";
				HashMap<String,Double> adiac=mapNodes.get(idSource).getAdiacs();
				Iterator<String> itDest=adiac.keySet().iterator();
				while(itDest.hasNext())
				{
					String idDest=itDest.next();
					if(idSource.compareTo(idDest)<0)
						edges+="("+idSource+","+idDest+","+adiac.get(idDest)+"); ";
				}
			}
			if(nodes.length()!=9)
				nodes=nodes.substring(0,nodes.length()-2);
			nodes+="}\n";
			if(edges.length()!=9)
				edges=edges.substring(0,edges.length()-2);
			edges+="}\n";
			return nodes+edges;
		}
        }
}