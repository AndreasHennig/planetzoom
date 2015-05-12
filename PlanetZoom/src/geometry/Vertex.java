package geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public abstract class Vertex
{
	protected Vector2f uv;
	protected Vector3f normal;
	
	public Vertex(Vector2f uv, Vector3f normal)
	{
		this.uv = uv;
		this.normal = normal;
	}
	
	public Vector2f getUv()
	{
		return uv;
	}
	public void setUv(Vector2f uv)
	{
		this.uv = uv;
	}
	public Vector3f getNormal()
	{
		return normal;
	}
	public void setNormal(Vector3f normal)
	{
		this.normal = normal;
	}
}
