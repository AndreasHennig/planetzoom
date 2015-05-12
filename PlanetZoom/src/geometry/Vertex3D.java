package geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Vertex3D extends Vertex
{
	private Vector3f position;

	public Vertex3D(Vector3f position, Vector2f uv, Vector3f normal)
	{
		super(uv, normal);
		this.position = position;
	}
	
	public Vector3f getPosition()
	{
		return position;
	}

	public void setPosition(Vector3f position)
	{
		this.position = position;
	}
}
