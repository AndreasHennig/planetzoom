package geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public abstract class Vertex
{
	protected Vector2f uv;
	protected Vector3f normal;
	protected Vector4f colorRGBA;
	
	public static final Vector3f down = new Vector3f(0, -1, 0);
	public static final Vector3f up = new Vector3f(0, 1, 0);
	public static final Vector3f left = new Vector3f(-1, 0, 0);
	public static final Vector3f right = new Vector3f(1, 0, 0);
	
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

	public Vector4f getColorRGBA()
	{
		return colorRGBA;
	}

	public void setColorRGBA(Vector4f colorRGBA)
	{
		this.colorRGBA = colorRGBA;
	}
	
}
