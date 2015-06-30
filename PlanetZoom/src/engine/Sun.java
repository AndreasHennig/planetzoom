package engine;

import geometry.Vertex3D;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Sun extends GameObject3D
{
	private Vector3f position;
	
	private static final float SIZE = 100000.0f;

	public Sun(Vector3f position)
	{
		this.position = position;
		init();
	}

	public Vector3f getPosition()
	{
		return position;
	}
	
	private void init()
	{		
		float localSize = SIZE / 2.0f;
		Vector3f normal = new Vector3f(0.0f, 0.0f, 1.0f);
		Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
		vertexData.add(new Vertex3D(new Vector3f(-localSize, -localSize, 0), new Vector2f(0.0f, 1.0f), normal, color));
		vertexData.add(new Vertex3D(new Vector3f(-localSize, localSize, 0), new Vector2f(0.0f, 0.0f), normal, color));
		vertexData.add(new Vertex3D(new Vector3f(localSize, localSize, 0), new Vector2f(1.0f, 0.0f), normal, color));
		vertexData.add(new Vertex3D(new Vector3f(localSize, -localSize, 0), new Vector2f(1.0f, 1.0f), normal, color));
		indices = new int[]{2, 1, 0, 0, 3, 2};	
		
		modelMatrix.translate(position);
		createVAO();
	}
	
	
	
	
	
	
}
