public class Item
{
	private String name; // variable's name
	private double rc; // variables's reduced cost
	private double xr; // variable's value
	
	public Item(String name, double xr, double rc)
	{
		this.name = name;
		this.xr = xr;
		this.rc = rc;
	}
	
	public String getName()
	{
		return name;
	}

	public double getRc()
	{
		return rc;
	}
	
	public double getXr()
	{
		return xr;
	}
	
	public double getAbsoluteRC()
	{
		return Math.abs(rc);
	}
}