import java.util.HashMap;

public class Node
{
	private String id;
	private HashMap<String, Double> adiacs;
	public Node(String id)
	{
		this.id=id;
		adiacs=new HashMap<String,Double>();
	}
	public String getId()
	{
		return id;
	}
	public HashMap<String,Double> getAdiacs()
	{
		return adiacs;
	}
	public int degree()
	{
		return adiacs.size();
	}
	public String toString()
	{
		return id;
	}
}