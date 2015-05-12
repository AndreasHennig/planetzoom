package geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Vertex3D extends Vertex
{
	private Vector3f position;
	
	public static final Vector3f forward = new Vector3f(0, 0, 1);
	public static final Vector3f back = new Vector3f(0, 0, -1);
	
	public Vertex3D(Vector3f position, Vector2f uv, Vector3f normal)
	{
		super(uv, normal);
		this.position = position;
	}
	
	public Vertex3D(Vector3f position, Vector2f uv, Vector3f normal, Vector4f color)
	{
		super(uv, normal);
		this.position = position;
		this.colorRGBA = color;
	}
	
	public Vector3f getPosition()
	{
		return position;
	}

	public void setPosition(Vector3f position)
	{
		this.position = position;
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
