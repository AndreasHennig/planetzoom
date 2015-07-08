package planetZoooom.geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public abstract class Vertex
{
	protected Vector2f uv;
	protected Vector3f normal;
	
	public static Vector3f up() {
		return new Vector3f(0, 1, 0);
	}
	
	public static Vector3f down() {
		return new Vector3f(0, -1, 0);
	}
	
	public static Vector3f left() {
		return new Vector3f(-1, 0, 0);
	}
	
	public static Vector3f right() {
		return new Vector3f(1, 0, 0);
	}
	
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
