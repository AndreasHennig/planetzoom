package planetZoooom.geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Vertex
{
	private Vector3f position;
	private Vector2f uv;
	private Vector3f normal;
	
	public Vertex(Vector3f position, Vector2f uv, Vector3f normal)
	{
		this.position = position;
		this.uv = uv;
		this.normal = normal;
	}
	
	public static Vector3f front() {
		return new Vector3f(0, 0, 1);
	}
	
	public static Vector3f back() {
		return new Vector3f(0, 0, -1);
	}
	
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
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	public void setPosition(Vector3f position)
	{
		this.position = position;
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
	
	public void multiply(float amount)
	{
		position.x *= amount;
		position.y *= amount;
		position.z *= amount;
	}
	
	public static Vector3f lerp(Vector3f a, Vector3f b, float step)
	{
		float x = a.x + (b.x - a.x) * step; 
		float y = a.y + (b.y - a.y) * step; 
		float z = a.z + (b.z - a.z) * step; 
		
		return new Vector3f(x, y, z);
	}
	
	
}
