package planetZoooom.gameContent;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import planetZoooom.engine.VertexArray;

public class BillBoard
{
	private Matrix4f modelMatrix;
	private Vector3f position;
	private VertexArray mesh;

	public BillBoard(Vector3f position, float size)
	{
		float localSize = size / 2.0f;
		this.position = position;
		modelMatrix = new Matrix4f();
		modelMatrix.translate(position);
		
		float[] vertices = new float[] 
		{
			-localSize, -localSize, 0,
			-localSize,  localSize, 0,
			 localSize,  localSize, 0,
			 localSize, -localSize, 0
		};
		
		float[] normals = new float[]
		{
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f
		};
		
		float[] uvCoords = new float[]
		{
			0.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 0.0f,
			1.0f, 1.0f
		};
		
		int[] indices = new int[]
		{
			2, 1, 0, 
			0, 3, 2
		};
		
		mesh = new VertexArray(vertices, normals, uvCoords, indices);

		//init();
	}
	
	public void render(int mode)
	{
		mesh.render(mode);
	}
	
	public Matrix4f getModelMatrix()
	{
		return modelMatrix;
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	public void setPosition(Vector3f position)
	{
		this.position = position;
		modelMatrix.translate(position);
	}
}
