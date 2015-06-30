package engine;

import org.lwjgl.util.vector.Vector3f;

public class HeadsUpDisplay
{
	private Vector3f cameraPosition;
	private Vector3f cameraLookAt;
	private float distanceToPlanetSurface;
	private int totalTriangles;
	private int	actualTriangles;
	private float trianglePercentage;
	private int fps;
	
	private static final int STANDARD_POSITION_X = 0;
	private static final int STANDARD_POSITION_Y = 0;
	private static final String  STANDARD_FONT = "arial_nm.png";
	
	private int position_x;
	private int position_y;
	private String font;
	private Text2D text;
	
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
	
	public HeadsUpDisplay(int x, int y, String font, Vector3f cameraPosition, Vector3f cameraLookAt, float distanceToPlanet, int actualTriangles, int totalTriangles, int fps)
	{
	    this(x, y, font);
		this.cameraPosition = cameraPosition;
		this.cameraLookAt = cameraLookAt;
		this.distanceToPlanetSurface = distanceToPlanet;
		this.actualTriangles = 0;
		this.totalTriangles = 0;
		this.trianglePercentage = 0f;
		this.fps = fps;  
		
		text = new Text2D(getHUDText(), font, position_x, position_y, 16);
	
	}
	
	public void update(Vector3f cameraPosition, Vector3f cameraLookAt, float distanceToPlanet, int actualTriangles, int totalTriangles, int fps)
	{
		this.cameraPosition = cameraPosition;
		this.cameraLookAt = cameraLookAt;
		this.distanceToPlanetSurface = distanceToPlanet;
		this.actualTriangles = actualTriangles;
		this.totalTriangles = totalTriangles;
		this.trianglePercentage = actualTriangles * 100f / totalTriangles;
		this.fps = fps;  
		
		text.update(getHUDText());
	}
	
	private String getHUDText()
	{
		return String.format("Position: %.2f / %.2f / %.2f\n"
							+ "Look at: %.2f / %.2f / %.2f\n"
							+ "Distance: %.2f\n"
							+ "Triangles: %d / %d (%.2f%%)\n"
							+ "FPS: %d",
			cameraPosition.x, cameraPosition.y, cameraPosition.z, 
			cameraLookAt.x, cameraLookAt.y, cameraLookAt.z,
			distanceToPlanetSurface,
			actualTriangles, totalTriangles, trianglePercentage,
			fps
		);
	}
	public Text2D getMesh()
	{
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
	
	public void setFont(String font)
	{
	    this.font = font;
	    text.setFont(font);
	}
}