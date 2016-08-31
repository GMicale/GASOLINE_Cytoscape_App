import java.util.HashMap;
import java.util.Vector;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.cytoscape.work.TaskMonitor;

public class GASOLINE
{
	static int SIGMA=3;
	static double OVERLAP_THRESH=0.5;
	static int ITER_ITERATIVE=20;
	static int MIN_ALIGN_SIZE=4;
	static double DENSITY_THRESH=0.7;
        
        public static OrderedList<Alignment> startAlignment(int sigma, double overlapThresh, int iterIterative, int minAlignSize, 
	double densityThresh, Graph[] g, String homologyFile, String outputFolder, TaskMonitor taskMon) throws Exception
	{
		SIGMA=sigma;
		OVERLAP_THRESH=overlapThresh;
		ITER_ITERATIVE=iterIterative;
		MIN_ALIGN_SIZE=minAlignSize;
		DENSITY_THRESH=densityThresh;
		
                int i=0, j=0, k=0;
		double indIter=0.0;
		FileManager fileMan=new FileManager();
		OrderedList<Alignment> rankAlign=new OrderedList<Alignment>();
		
		//Read homology scores
		HashMap<String,Double> mapHomology=null;
		if(homologyFile!=null)
			mapHomology=fileMan.readHomologyScores(homologyFile);
		
		//Compute the number of required iteration for Gibbs sampling
		int iterGibbs=0;
		double prob=1.0;
		while(true)
		{
			prob=prob*((double)(g.length-1)/g.length);
			iterGibbs++;
			if(prob<0.001)
				break;
		}
		GibbsSampler gibbs=new GibbsSampler(iterGibbs, mapHomology);
		
		//Start algorithm
		//double start=System.currentTimeMillis();
		
		//Filter starting nodes
		HashSet<String>[] startingNodes=filterStartingNodes(g, SIGMA);
		
		//Compute max number of executions of Gibbs sampling
		int maxPercent=Integer.MAX_VALUE;
		for(i=0;i<startingNodes.length;i++)
		{
			if(startingNodes[i].size()<maxPercent)
				maxPercent=startingNodes[i].size();
		}
		
		for(indIter=1;indIter<=maxPercent;indIter++)
		{
			double currentPercent=indIter/maxPercent;
			taskMon.setProgress(currentPercent);
            
			//PHASE 1) Search for optimal seeds
			String[][] candidates=new String[startingNodes.length][];
			for(i=0;i<startingNodes.length;i++)
			{
				candidates[i]=new String[startingNodes[i].size()];
				j=0;
				Iterator<String> it=startingNodes[i].iterator();
				while(it.hasNext())
				{
					String id=it.next();
					candidates[i][j]=id;
					j++;
				}
			}
			String[] alignNodes=gibbs.runGibbs(candidates,null);
			
			//Check if some aligned nodes has no homologs, and skip extension if necessary. Otherwise, save partial alignment and try to extend it
			Vector<String>[] finalAlign=new Vector[g.length];
			boolean different=areNotAllOrthologs(alignNodes,mapHomology);
			if(!different)
			{
				for(i=0;i<finalAlign.length;i++)
				{
					finalAlign[i]=new Vector<String>();
					finalAlign[i].add(alignNodes[i]);
				}
				//Save current alignment
				double scoreTopology=getScoreTopology(g,finalAlign);
				Vector<String>[] copyAlign=new Vector[g.length];
				for(i=0;i<finalAlign.length;i++)
				{
					copyAlign[i]=new Vector<String>();
					for(j=0;j<finalAlign[i].size();j++)
						copyAlign[i].add(finalAlign[i].get(j));
				}
				Alignment a=new Alignment(copyAlign, copyAlign[0].size(), scoreTopology);
			
				//PHASE 2) Iterative phase
				for(i=0;i<ITER_ITERATIVE;i++)
				{
					if(finalAlign[0].size()>=3)
						removeNode(g,finalAlign,DENSITY_THRESH);
					extendSeeds(g,finalAlign,mapHomology,iterGibbs,DENSITY_THRESH);
					//Update best alignment if necessary
					if(finalAlign[0].size()>a.getAlignSize())
					{
						scoreTopology=getScoreTopology(g,finalAlign);
						copyAlign=new Vector[g.length];
						for(i=0;i<finalAlign.length;i++)
						{
							copyAlign[i]=new Vector<String>();
							for(j=0;j<finalAlign[i].size();j++)
								copyAlign[i].add(finalAlign[i].get(j));
						}
						a=new Alignment(copyAlign, copyAlign[0].size(), scoreTopology);
					}
				}
                
				//Print result and save alignment to final list
				Vector<String>[] mapping=a.getMapping();
				if(mapping[0].size()>=MIN_ALIGN_SIZE)
				{
					rankAlign.insertOrdered(a);
					/*System.out.println("ALIGNMENT SIZE: "+a.getAlignSize());
					System.out.println("ISC_SCORE: "+a.getIscScore());    
					System.out.println("ITERATION: "+indIter+"\n");*/
				}
			}
			//Remove starting seeds
			for(i=0;i<alignNodes.length;i++)
				startingNodes[i].remove(alignNodes[i]);
			
		}
		
		//PHASE 4) Postprocessing
		//Filter overlapping complexes
		checkOverlap(g, rankAlign, OVERLAP_THRESH);
        
		//Print final set of local alignments
		fileMan.writeAlignments(g, rankAlign, outputFolder);
		
		//End algorithm
		/*double end=System.currentTimeMillis();
		System.out.println("Running time: "+(((float)(end-start))/1000)+" sec \n");*/
                
                return rankAlign;
	}
    
	public static boolean areNotAllOrthologs(String[] alignedNodes, HashMap<String,Double> mapHomology)
	{
		boolean different=false;
		int i=0, j=0;
		for(i=0;i<alignedNodes.length-1;i++)
		{
			boolean hasHomologs=false;
			for(j=i+1;j<alignedNodes.length;j++)
			{
				if(mapHomology!=null && mapHomology.containsKey(alignedNodes[i]+"-"+alignedNodes[j]))
					hasHomologs=true;
				else if(mapHomology==null && alignedNodes[i].equals(alignedNodes[j]))
					hasHomologs=true;
			}
			if(!hasHomologs)
				different=true;
		}
		return different;
	}
	
	public static HashSet<String>[] filterStartingNodes(Graph[] g, int thresh)
	{
		int i=0;
		HashSet<String>[] startingNodes=new HashSet[g.length];
		for(i=0;i<g.length;i++)
		{
			startingNodes[i]=new HashSet<String>();
			HashMap<String,Node> mapNodes=g[i].getMapNodes();
			Iterator<String> it=mapNodes.keySet().iterator(); 
			while(it.hasNext())
			{
				String id=it.next();
				int degree=mapNodes.get(id).degree();
				if(degree>=thresh)
					startingNodes[i].add(id);
			}
			System.out.println(startingNodes[i].size());
		}
		return startingNodes;
	}
	
	public static HashSet<String>[] getAdiacSeed(Graph[] g, Vector<String>[] seed, double densityThresh)
	{
		HashSet<String>[] adiacSeed=new HashSet[g.length];
		int i=0, j=0, k=0;
		for(i=0;i<adiacSeed.length;i++)
		{
			adiacSeed[i]=new HashSet<String>();
			for(j=0;j<seed[i].size();j++)
			{
				HashMap<String,Double> adiac=g[i].getMapNodes().get(seed[i].get(j)).getAdiacs();
				Iterator<String> it=adiac.keySet().iterator();
				while(it.hasNext())
				{
					String adiacNode=it.next();
					double numConn=0;
					HashMap<String,Double> adiacNodeMap=g[i].getMapNodes().get(adiacNode).getAdiacs();
					for(k=0;k<seed[i].size();k++)
					{
						if(adiacNodeMap.containsKey(seed[i].get(k)))
							numConn++;
					}
					if(numConn/seed[i].size()>=densityThresh-0.1)
						adiacSeed[i].add(adiacNode);
				}
			}
			for(j=0;j<seed[i].size();j++)
				adiacSeed[i].remove(seed[i].get(j));
		}
		return adiacSeed;
	}
    
	public static void buildMaps(Graph[] g, Vector<String>[] seed, HashSet<String>[] adiacSeed, String[][] labels, double[][][] adiacMaps)
	{
		int i=0, j=0, k=0;
		for(i=0;i<adiacSeed.length;i++)
		{
			labels[i]=new String[adiacSeed[i].size()];
			adiacMaps[i]=new double[adiacSeed[i].size()][seed[i].size()];
			Iterator<String> it=adiacSeed[i].iterator();
			j=0;
			while(it.hasNext())
			{
				String id=it.next();
				labels[i][j]=id;
				adiacMaps[i][j]=getAdiacMap(g[i],seed[i],id);;
				j++;
			}
		}
	}
    
	public static double[] getAdiacMap(Graph g, Vector<String> seed, String nodeRef)
	{
		double[] mapRef=new double[seed.size()];
		HashMap<String,Double> adiacRef=g.getMapNodes().get(nodeRef).getAdiacs();
		int k=0;
		for(k=0;k<seed.size();k++)
		{
			if(adiacRef.containsKey(seed.get(k)))
				mapRef[k]=adiacRef.get(seed.get(k));
			else
				mapRef[k]=0.0;
		}
		return mapRef;
	}
	
	public static void extendSeeds(Graph[] g, Vector<String>[] seed, HashMap<String,Double> mapHomology, int iterGibbs, double densityThresh)
	{
		int i=0, j=0;
		boolean extend=true;
		while(extend)
		{
			//Check if extension is possible
			HashSet<String>[] adiacSeed=getAdiacSeed(g,seed,densityThresh);
			for(i=0;i<adiacSeed.length;i++)
			{
				if(adiacSeed[i].isEmpty())
					extend=false;
			}
			if(extend)
			{
				double[][][] adiacMaps= new double[g.length][][];
				String[][] labels=new String[g.length][];
				buildMaps(g, seed, adiacSeed, labels, adiacMaps);
				for(i=0;i<labels.length;i++)
				{
					if(labels[i].length==0)
						extend=false;
				}
				if(extend)
				{
					GibbsSampler gibbs=new GibbsSampler(iterGibbs,mapHomology);
					String[] alignedNodes=gibbs.runGibbs(labels,adiacMaps);
					//Check if some aligned nodes has no homologs, and skip extension if necessary
					boolean different=areNotAllOrthologs(alignedNodes,mapHomology);
					if(!different)
					{
						double avgDens=getAvgDens(g,seed,alignedNodes);
						if(avgDens>=densityThresh)
						{
							//Perform extension
							for(i=0;i<alignedNodes.length;i++)
								seed[i].add(alignedNodes[i]);
						}
						else
							extend=false;
					}
					else
						extend=false;
				}
			}
		}
	}
	public static double getAvgDens(Graph[] g, Vector<String>[] seed, String[] alignedNodes)
	{
		int i=0, j=0, k=0;
		double avgDens=0.0;
		for(i=0;i<seed.length;i++)
		{
			double numEdges=0;
			for(j=0;j<seed[i].size();j++)
			{
				if(g[i].hasEdge(seed[i].get(j),alignedNodes[i]))
					numEdges++;
			}
			avgDens+=numEdges/seed[0].size();
		}
		avgDens=avgDens/seed.length;
		return avgDens;
	}
	public static double getScoreTopology(Graph[] g, Vector<String>[] seed)
	{
		int i=0, j=0, k=0;
		double scoreTopology=0.0;
		for(i=0;i<seed.length;i++)
	        {
			for(j=i+1;j<seed.length;j++)
			{
				double scorePair=0.0;
				for(k=0;k<seed[i].size();k++)
				{
					double[] mapSource=getAdiacMap(g[i],seed[i],seed[i].get(k));
					double[] mapDest=getAdiacMap(g[j],seed[j],seed[j].get(k));
					scorePair+=getPairwiseScore(mapSource,mapDest);
				}
				scoreTopology+=scorePair;
			}
		}
		scoreTopology=scoreTopology/(seed[0].size()*g.length*(g.length-1)/2);
	        return scoreTopology;
	}
	
	public static double getPairwiseScore(double[] mapSource, double[] mapDest)
	{
		double mapScore=0.0;
		int k=0;
		for(k=0;k<mapSource.length;k++)
		{
			if((mapSource[k]!=0.0 && mapDest[k]!=0.0)||(mapSource[k]==0.0 && mapDest[k]==0.0))
				mapScore++;
		}
		mapScore=mapScore/mapSource.length;
		return mapScore;
	}
	
	public static void removeNode(Graph[] g, Vector<String>[] seed, double densityThresh)
	{
		int i=0, j=0, k=0;
		int indexRemove=-1;
		double minGoodness=1000000;
		boolean[][] articulationPoints=new boolean[seed.length][];
		//Compute articulation points
		for(i=0;i<seed.length;i++)
		{
			HashMap<String,Node> mapNodes=g[i].getMapNodes();
			boolean[][] edges=new boolean[seed[i].size()][seed[i].size()];
			for(j=0;j<seed[i].size();j++)
			{
				HashMap<String,Double> adiacs=mapNodes.get(seed[i].get(j)).getAdiacs();
				for(k=j+1;k<seed[i].size();k++)
				{
					if(adiacs.containsKey(seed[i].get(k)))
						edges[j][k]=edges[k][j]=true;
				}
			}
			articulationPoints[i]=getArticulationPoints(edges);
		}
		//Compute goodness scores
		for(j=0;j<seed[0].size();j++)
		{
			boolean isRemovable=true;
			for(i=0;i<seed.length;i++)
			{
				if(articulationPoints[i][j])
				{
					isRemovable=false;
					break;
				}
			}
			if(isRemovable)
			{
				double goodness=0.0;
				for(i=0;i<seed.length;i++)
				{
					double density=0.0;
					HashMap<String,Double> adiac=g[i].getMapNodes().get(seed[i].get(j)).getAdiacs();
					for(k=0;k<seed[i].size();k++)
					{
						if(adiac.containsKey(seed[i].get(k)))
							density++;
					}
					density=density/(seed[i].size()-1);
					goodness+=density;
				}
				goodness=goodness/seed.length;
				if(goodness<densityThresh && goodness<minGoodness)
				{
					indexRemove=j;
					minGoodness=goodness;
				}
			}
                }
		//Remove a set of aligned nodes, if possible
		if(indexRemove!=-1)
		{
			for(i=0;i<seed.length;i++)
				seed[i].remove(indexRemove);
		}
	}
	
	public static void checkOverlap(Graph[] g, OrderedList<Alignment> rankAlign, double overlapThresh)
	{
		int i=0, j=0, k=0;
		NodeOrdList<Alignment> aux=rankAlign.getMax();
		HashSet<String> nodiOverlap=new HashSet<String>();
		while(aux!=null)
		{
			Vector<String>[] mapping=aux.getInfo().getMapping();
			double[] overlapping=new double[mapping.length];
			for(i=0;i<overlapping.length;i++)
				overlapping[i]=0.0;
			for(i=0;i<mapping.length;i++)
			{
				for(j=0;j<mapping[i].size();j++)
				{
					if(nodiOverlap.contains(mapping[i].get(j)))
						overlapping[i]++;
				}
			}
			double avg=0.0;
			for(i=0;i<overlapping.length;i++)
			{
				overlapping[i]=overlapping[i]/mapping[0].size();
				avg+=overlapping[i];
			}
			avg=avg/overlapping.length;
			if(avg<=overlapThresh)
			{
				for(i=0;i<mapping.length;i++)
				{
					for(j=0;j<mapping[i].size();j++)
						nodiOverlap.add(mapping[i].get(j));
				}
			}	
			else
				rankAlign.delete(aux.getInfo());
			aux=aux.getNext();
		}
	}
	
	public static void APUtil(int u, boolean[][] edge, boolean[] visited, int[] disc, int[] low, int[] parent, boolean[] ap, int time)
	{
		int children = 0;
		visited[u]=true;
		time++;
		disc[u]=low[u]=time;
		int v=0;
		for(v=0;v<edge[u].length;v++)
		{
			if(edge[u][v])
			{
				if(!visited[v])
				{
					children++;
					parent[v]=u;
					APUtil(v, edge, visited, disc, low, parent, ap, time);
					low[u] = Math.min(low[u],low[v]);
					if(parent[u]==-1 && children>1)
						ap[u]=true;
					if(parent[u]!=-1 && low[v]>=disc[u])
						ap[u]=true;
				}
				else if(parent[u]!=-1 && parent[u]!=v)
					low[u]=Math.min(low[u], disc[v]);
			}
		}
	}
 
	public static boolean[] getArticulationPoints(boolean[][] graph)
	{
		boolean[] visited=new boolean[graph.length];
		int[] disc=new int[graph.length];
		int[] low=new int[graph.length];
		int[] parent=new int[graph.length];
		int i=0;
		for(i=0;i<parent.length;i++)
			parent[i]=-1;
		boolean[] ap=new boolean[graph.length];
		int time=0;
		for(i=0;i<graph.length;i++)
		{
			if(!visited[i])
				APUtil(i, graph, visited, disc, low, parent, ap, time);
		}
		return ap;
	}
}