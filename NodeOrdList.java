public class NodeOrdList<E>
{
	private E info;
	private NodeOrdList<E> next;
	private NodeOrdList<E> prev;
    
	public NodeOrdList(E val)
	{
		this(val,null,null);
	}
    
	public NodeOrdList(E val,NodeOrdList<E> n, NodeOrdList<E> p)
	{
		info=val;
		next=n;
		prev=p;
	}
    
	public E getInfo()
	{
		return info;
	}
    
	public NodeOrdList<E> getNext()
	{
		return next;
	}
    
	public NodeOrdList<E> getPrev()
	{
		return prev;
	}
    
	public void setInfo(E val)
	{
		info=val;
	}
    
	public void setNext(NodeOrdList<E> n)
	{
		next=n;
	}
    
	public void setPrev(NodeOrdList<E> p)
	{
		prev=p;
	}
}