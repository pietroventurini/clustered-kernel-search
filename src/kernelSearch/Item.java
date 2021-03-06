package kernelSearch;

import java.util.Objects;

import graph.Node;

public class Item implements Comparable<Item>, Node {
	private String name; // variable's name
	private double rc; // variables's reduced cost
	private double xr; // variable's value
	
	public Item(String name, double xr, double rc){
		this.name = name;
		this.xr = xr;
		this.rc = rc;
	}
	
	public String getName(){
		return name;
	}

	public double getRc(){
		return rc;
	}
	
	public double getXr(){
		return xr;
	}
	
	public double getAbsoluteRC(){
		return Math.abs(rc);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj==null || !Item.class.isAssignableFrom(obj.getClass()))
			return false;
		final Item other = (Item) obj;
		return name.equals(other.name);
	}
	
	/*
	 * @see java.lang.Comparable<Item>{@link #compareTo(Item)}
	 */
	@Override
	public int compareTo(Item o) {
		return name.compareTo(o.name);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	/*
	public int hashCode() {
		return Objects.hash(name, rc, xr);
	}
	*/

	public int hashCode() {
		return Objects.hash(name);
	}
	
	@Override
	public String toString() {
		return name;
	}
}