import java.util.Vector;

public class Alignment implements Comparable<Alignment>
{
	private Vector<String>[] mapping;
	private int alignSize;
	private double iscScore;
	
        public Alignment(Vector<String>[] mapping, int alignSize, double iscScore)
	{
		this.mapping=mapping;
		this.alignSize=alignSize;
		this.iscScore=iscScore;
	}
        
	public Vector<String>[] getMapping()
	{
		return mapping;
	}
        
	public int getAlignSize()
	{
		return alignSize;
	}
        
	public double getIscScore()
	{
		return iscScore;
	}
        
	public int compareTo(Alignment other)
	{
		if(this.alignSize>other.getAlignSize())
			return 1;
		else if(this.alignSize<other.getAlignSize())
			return -1;
		else
		{
			if(this.iscScore>other.getIscScore())
				return 1;
			else if(this.iscScore<other.getIscScore())
				return -1;
			else
				return 0;
		}
	}
        
	public String toString()
	{
		return "ALIGNMENT_SIZE = "+alignSize+" ; ISC_SCORE = "+iscScore;
	}
}