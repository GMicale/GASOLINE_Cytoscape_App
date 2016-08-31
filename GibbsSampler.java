import java.util.HashMap;
import java.util.Random;

public class GibbsSampler
{
	private int numIter;
	private HashMap<String,Double> mapHomology;
	private Random random;
	
	public GibbsSampler(int numIter, HashMap<String,Double> mapHomology)
	{
		this.numIter=numIter;
		this.mapHomology=mapHomology;
		this.random=new Random();
	}
		
	public String[] runGibbs(String[][] labels, double[][][] adiacMaps)
	{
		//Build initial alignment
		int[] alignment=new int[labels.length];
		int i=0, j=0;
		for(i=0;i<alignment.length;i++)
		{
			int row=random.nextInt(labels[i].length);
			alignment[i]=row;
		}
		
		//Start gibbs sampling
		int indIter=0;
		double bestScoreAlign=getScoreAlignment(alignment,labels,adiacMaps);
		int[] bestAlign=new int[alignment.length];
		for(i=0;i<alignment.length;i++)
			bestAlign[i]=alignment[i];
		for(indIter=0;indIter<numIter;indIter++)
		{
			int indexSost=random.nextInt(alignment.length);
			double[] probs =computeLR(alignment, indexSost, labels, adiacMaps);
			int[] probs_ids=new int[probs.length];
			for(i=0;i<probs_ids.length;i++)
				probs_ids[i]=i;
			insertionSort(probs, probs_ids);
			alignment[indexSost]=sample(probs,probs_ids);
			double scoreAlign=getScoreAlignment(alignment,labels,adiacMaps);
			if(scoreAlign>bestScoreAlign)
			{
				bestScoreAlign=scoreAlign;
				for(i=0;i<alignment.length;i++)
					bestAlign[i]=alignment[i];
			}
		}
		//Return the final alignment
		String[] finalAlign=new String[alignment.length];
		for(i=0;i<finalAlign.length;i++)
			finalAlign[i]=labels[i][bestAlign[i]];
		return finalAlign;
	}
	
	public double[] computeLR(int[] alignment, int index, String[][] labels, double[][][] adiacMaps)
	{
		double[] probSelect=new double[labels[index].length];
		int i=0, j=0, k=0;
		double denom=0.0;
		for(i=0;i<labels[index].length;i++)
		{ 
			String refNode=labels[index][i];
			double scoreHomology=1.0;
			double scoreAdiacs=1.0;
			for(j=0;j<alignment.length;j++)
			{
				if(j!=index)
				{
					String alignNode=labels[j][alignment[j]];
					if(mapHomology!=null && mapHomology.containsKey(refNode+"-"+alignNode))
						scoreHomology*=Math.pow(mapHomology.get(refNode+"-"+alignNode),3);
					if(mapHomology==null && refNode.equals(alignNode))
						scoreHomology*=100;
					if(adiacMaps!=null)
					{
						double pairAdiacs=0.0;
						for(k=0;k<adiacMaps[index][i].length;k++)
						{
							if(adiacMaps[j][alignment[j]][k]!=0.0 && adiacMaps[index][i][k]!=0.0)
								pairAdiacs+=adiacMaps[j][alignment[j]][k]*adiacMaps[index][i][k];
						}
						if(pairAdiacs==0.0)
							pairAdiacs+=0.01;
						scoreAdiacs*=pairAdiacs;
					}
				}
			}
			probSelect[i]=scoreHomology*scoreAdiacs;
			denom+=probSelect[i];
		}
		for(i=0;i<probSelect.length;i++)
			probSelect[i]=probSelect[i]/denom;
		return probSelect;
	}
	
	public int sample(double[] prob, int[] probs_ids)
	{
		int i=0;
		int indSample=0;
		boolean found=false;
		double rand=random.nextDouble();
		while(i<prob.length && !found)
		{
			rand=rand-prob[probs_ids[i]];
			if(rand<0)
			{
				indSample=probs_ids[i];
				found=true;
			}
			i++;
		}
		return indSample;
	}
	
	public void insertionSort(double[] probs, int[] probs_ids)
	{
		int i=0, j=0;
		for(j=1;j<probs.length;j++)
		{
			int key=probs_ids[j];
			i=j-1;
			while(i>=0 && probs[probs_ids[i]]>probs[key])
			{
				probs_ids[i+1]=probs_ids[i];
				i=i-1;
			}
			probs_ids[i+1]=key;
		}
	}
	
	public double getScoreAlignment(int[] alignment, String[][] labels, double[][][] adiacMaps)
	{
		double scoreAlign=0.0;
		int i=0, j=0, k=0;
		for(i=0;i<alignment.length;i++)
		{
			String sourceNode=labels[i][alignment[i]];
			for(j=i+1;j<alignment.length;j++)
			{
				double scorePair=1.0;
				String destNode=labels[j][alignment[j]];
				if(mapHomology!=null && mapHomology.containsKey(sourceNode+"-"+destNode))
					scorePair*=mapHomology.get(sourceNode+"-"+destNode);
				if(mapHomology==null && sourceNode.equals(destNode))
					scorePair*=100;
				if(adiacMaps!=null)
				{
					double pairAdiacs=0.0;
					for(k=0;k<adiacMaps[i][alignment[i]].length;k++)
					{
						if(adiacMaps[j][alignment[j]][k]!=0.0 && adiacMaps[i][alignment[i]][k]!=0.0)
							pairAdiacs+=adiacMaps[j][alignment[j]][k]*adiacMaps[i][alignment[i]][k];
					}
					if(pairAdiacs==0.0)
						pairAdiacs+=0.01;
					scorePair*=pairAdiacs;
				}
				scoreAlign+=scorePair;
			}
		}
		return scoreAlign;
	}
}