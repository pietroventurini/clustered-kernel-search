package graph;

import java.util.Objects;

/**
 * Implementation of a node
 * In general: a node can be whatever we want it to be, it MUST have a label in order to identify it univocally
 * @author Matteo
 *
 */
public class Node {
	private static final String format = "%s";
	private static final String linkedFormat = "-%s";
	
	private String label;
	private Object o;
	
	public Node(String s) {
		this.label = s;
		this.o = null;
	}
	
	public void attach(Object obj) {
		this.o = obj;
	}
	public boolean equals(Object obj) {
		if(obj==null || !Node.class.isAssignableFrom(obj.getClass()))
			return false;
		
		final Node other = (Node) obj;
		if(o==null)
			return label.equals(other.label);
		else
			return label.equals(other.label) && o.equals(other.o);
	}
	public int hashCode() {
		return Objects.hash(label, o);
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(format, label));
		if(o!=null) 
			sb.append(String.format(linkedFormat, o.toString()));
		return sb.toString();
	}
}
