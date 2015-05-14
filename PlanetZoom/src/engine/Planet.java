package engine;

import geometry.Sphere;

public class Planet 
{
	private Sphere sphere;
	
	public Planet(Sphere sphere)
	{
		this.sphere = sphere;
	}
	public Sphere getSphere()
	{
		return sphere;
	}
	public void setSphere(Sphere sphere)
	{
		this.sphere = sphere;
	}
}
