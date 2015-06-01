package engine;

import org.lwjgl.util.vector.Vector3f;


public class HeadsUpDisplay
{
	private Vector3f cameraPosition;
	private Vector3f cameraLookAt;
	private float distanceToPlanetSurface;
	private int subdivisions;
	
	private static final int STANDARD_POSITION_X = 0;
	private static final int STANDARD_POSITION_Y = 0;
	private static final String  STANDARD_FONT = "arial_nm.png";
	
	private int position_x;
	private int position_y;
	private String font;
	
	
	public HeadsUpDisplay()
	{
		position_x = STANDARD_POSITION_X;
		position_y = STANDARD_POSITION_Y;
		font = STANDARD_FONT;
	}
	
	public HeadsUpDisplay(int x, int y, String font)
	{
		position_x = x;
		position_y = y;
		this.font = font;
	}
	
	public HeadsUpDisplay(int x, int y, String font, Vector3f cameraPosition, Vector3f cameraLookAt, float distanceToPlanet, int subdivisions)
	{
		position_x = x;
		position_y = y;
		this.font = font;
		this.cameraPosition = cameraPosition;
		this.cameraLookAt = cameraLookAt;
		this.distanceToPlanetSurface = distanceToPlanet;
		this.subdivisions = subdivisions;
	}
	
	public GameObject2D getText2D()
	{
		GameObject2D text = TextRenderer2D.textToObject2D(""
				+ "Position: " + cameraPosition.x + "/ " + cameraPosition.y + "/ " + cameraPosition.z + "\n"
				+ "Look at: " + cameraLookAt.x + "/ " + cameraLookAt.y + "/ " + cameraLookAt.z + "\n\n" 
				+ "Distance: " + distanceToPlanetSurface  + "\n"
				+ "Subdivisions: " + subdivisions,
				font, position_x, position_y, 16);
		
		return text;
	}

	public void setCameraPosition(Vector3f cameraPosition)
	{
		this.cameraPosition = cameraPosition;
	}

	public void setCameraLookAt(Vector3f cameraLookAt)
	{
		this.cameraLookAt = cameraLookAt;
	}

	public void setDistanceToPlanetSurface(float distanceToPlanetSurface)
	{
		this.distanceToPlanetSurface = distanceToPlanetSurface;
	}	
	
}
