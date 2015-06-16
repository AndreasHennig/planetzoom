package engine;


public class Info
{
	private Planet planet;
	private ICamera camera;
	
	public Info(Planet planet, ICamera camera)
	{
		this.camera = camera;
		this.planet = planet;
	}
	public Planet getPlanet()
	{
		return planet;
	}
	public ICamera getCamera()
	{
		return camera;
	}
	

}
