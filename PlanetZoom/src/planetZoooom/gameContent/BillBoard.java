package planetZoooom.gameContent;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import planetZoooom.geometry.GameObject3D;
import planetZoooom.geometry.Vertex3D;

public class BillBoard extends GameObject3D
{
	private Vector3f position;
	private float size = 1.0f;

	public BillBoard(Vector3f position, float size)
	{
		this.position = position;
		this.size = size;
		init();
	}

	public Vector3f getPosition()
	{
		return position;
	}
	
	private void init()
	{		
		float localSize = size / 2.0f;
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
