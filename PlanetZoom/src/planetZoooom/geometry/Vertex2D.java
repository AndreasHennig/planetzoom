package planetZoooom.geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;


public class Vertex2D extends Vertex
{
	private Vector2f position;

	public Vertex2D(Vector2f position, Vector2f uv, Vector3f normal)
	{
		super(uv, normal);
		this.position = position;
	}
	
	public Vector2f getPosition()
	{
		return position;
	}

	public void setPosition(Vector2f position)
	{
		this.position = position;
	}
	
}
