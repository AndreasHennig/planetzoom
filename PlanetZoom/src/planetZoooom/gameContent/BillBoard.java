package planetZoooom.gameContent;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import planetZoooom.engine.MeshObject;
import planetZoooom.engine.VertexArray;

public class BillBoard extends MeshObject
{

	public BillBoard(Vector3f position, float sizeX, float sizeY)
	{
		float localSizeX = sizeX / 2.0f;
		float localSizeY = sizeY / 2.0f;
		this.position = position;
		modelMatrix = new Matrix4f();
		setPosition(position);
		
		vertices = new float[] 
		{
			-localSizeX, -localSizeY, 0,
			-localSizeX,  localSizeY, 0,
			localSizeX,  localSizeY, 0,
			localSizeX, -localSizeY, 0
		};
		
		normals = new float[]
		{
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f
		};
		
		uvCoords = new float[]
		{
			0.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 0.0f,
			1.0f, 1.0f
		};
		
		indices = new int[]
		{
			2, 1, 0, 
			0, 3, 2
		};
		
		mesh = new VertexArray(vertices, normals, uvCoords, indices);
	}
	
}
