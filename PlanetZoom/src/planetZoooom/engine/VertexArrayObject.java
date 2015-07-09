package planetZoooom.engine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import planetZoooom.geometry.GameObject;
import planetZoooom.geometry.Vertex;

//TODO get rid of toFloat methods!!
public class VertexArrayObject
{
	private int id;
	private int normalHandle;
	private int positionHandle;
	private int uvHandle;
	private int indexHandle;
	
	private FloatBuffer normalBuffer;
	private FloatBuffer positionBuffer;
	private FloatBuffer uvBuffer;
	private IntBuffer indexBuffer;
	
	private int indexCount;
	
	public static final int POSITION_LOCATION = 0;
	public static final int UV_LOCATION = 1;
	public static final int NORMAL_LOCATION = 2;
	
	public VertexArrayObject(GameObject gameObject)
	{
		id = GL30.glGenVertexArrays();	
		indexCount = gameObject.getIndices().length;
		createHandles();
		initBuffers3D(gameObject.getVertices(), gameObject.getIndices());
	}
	
	/**
	 * binds the VAO and it's buffers
	 */
	public void bind()
	{
		GL30.glBindVertexArray(id); 
		
		bindArrayBuffer(POSITION_LOCATION, 3, positionHandle, positionBuffer);
		bindArrayBuffer(UV_LOCATION, 2, uvHandle, uvBuffer);
		bindArrayBuffer(NORMAL_LOCATION, 3, normalHandle, normalBuffer);
		
		bindIndexBuffer(indexHandle, indexBuffer);
	}

	public void unbind()
	{
		 GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	        GL30.glBindVertexArray(0);
	}
	
	public void delete()
	{         
		//Delete handles
        GL15.glDeleteBuffers(positionHandle);
        GL15.glDeleteBuffers(uvHandle);
        GL15.glDeleteBuffers(normalHandle);
        GL15.glDeleteBuffers(indexHandle);
         
        // Delete the VAO
        GL30.glDeleteVertexArrays(id);
	}
	
	public int getIndexHandle()
	{
		return indexHandle;
	}

	public int getId()
	{
		return id;
	}

	public int getIndexCount()
	{
		return indexCount;
	}

	private void bindArrayBuffer(int location, int dataSize, int handle, FloatBuffer buffer) 
	{ 
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); 
		GL20.glVertexAttribPointer(location, dataSize, GL11.GL_FLOAT, false, 0, 0); 
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void bindIndexBuffer(int handle, IntBuffer buffer) 
	{ 
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, handle);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	private void initBuffers3D(ArrayList<Vertex> vertices, int[] indices)
	{		
		createBuffers(vertices.size(), true, indices.length);	
		indexBuffer.put(indices);

		for (int i = 0; i < vertices.size(); i++)
		{
			positionBuffer.put(asFloats(vertices.get(i).getPosition()));
			uvBuffer.put(asFloats(vertices.get(i).getUv()));
			normalBuffer.put(asFloats(vertices.get(i).getNormal()));
		}
		
		flipBuffers();
	}
	
//	private void initBuffers2D(ArrayList<Vertex2D> vertices, int[] indices)
//	{		
//		createBuffers(vertices.size(), false, indices.length);
//		
//		indexBuffer.put(indices);
//
//		for (int i = 0; i < vertices.size(); i++)
//		{
//			positionBuffer.put(asFloats(vertices.get(i).getPosition()));
//			uvBuffer.put(asFloats(vertices.get(i).getUv()));
//			normalBuffer.put(asFloats(vertices.get(i).getNormal()));
//		}
//		
//		flipBuffers();	
//	}
	
	private void createHandles()
	{
		positionHandle = GL15.glGenBuffers();
		uvHandle = GL15.glGenBuffers();
		indexHandle = GL15.glGenBuffers();
		normalHandle = GL15.glGenBuffers();
	}
	
	private void createBuffers(int vertexCount, boolean is3D, int indexCount)
	{
		
		if(is3D)
			positionBuffer = BufferUtils.createFloatBuffer(vertexCount * 3);
		else
			positionBuffer = BufferUtils.createFloatBuffer(vertexCount * 2);
		
		uvBuffer = BufferUtils.createFloatBuffer(vertexCount * 2);
		indexBuffer = BufferUtils.createIntBuffer(indexCount);
		normalBuffer = BufferUtils.createFloatBuffer(vertexCount * 3);

	}
	
	private void flipBuffers()
	{
		positionBuffer.flip();
		uvBuffer.flip();
		normalBuffer.flip();	
		indexBuffer.flip();	
	}
	
	private float[] asFloats(Vector2f v) 
	{
		return new float[]{v.x, v.y};
	}	
	
	private float[] asFloats(Vector3f v) 
	{
		return new float[]{v.x, v.y, v.z};
	}	
		
}