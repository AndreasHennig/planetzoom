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
import org.lwjgl.util.vector.Vector4f;

import planetZoooom.geometry.GameObject;
import planetZoooom.geometry.GameObject2D;
import planetZoooom.geometry.GameObject3D;
import planetZoooom.geometry.Vertex2D;
import planetZoooom.geometry.Vertex3D;

public class VertexArrayObject
{
	private int id;

	private int normalHandle;
	private int positionHandle;
	private int uvHandle;
	private int indexHandle;
	private int colorHandle;
	
	private FloatBuffer normalBuffer;
	private FloatBuffer positionBuffer;
	private FloatBuffer uvBuffer;
	private FloatBuffer colorBuffer;
	private IntBuffer indexBuffer;
	
	private int indexCount;
	private boolean is3D;
	
	public static final int POSITION_LOCATION = 0;
	public static final int UV_LOCATION = 1;
	public static final int NORMAL_LOCATION = 2;
	public static final int COLOR_LOCATION = 3;
	
	private VertexArrayObject(GameObject gameObject)
	{
		id = GL30.glGenVertexArrays();	
		indexCount = gameObject.getIndices().length;
		createHandles();
	}
	
	public VertexArrayObject(GameObject2D object2D)
	{
		this((GameObject)object2D);
		initBuffers2D(object2D.getVertices(), object2D.getIndices());
	}
	
	public VertexArrayObject(GameObject3D object3D)
	{
		this((GameObject)object3D);
		is3D = true;
		initBuffers3D(object3D.getVertices(), object3D.getIndices());
	}
	
	/**
	 * binds the VAO and it's buffers
	 */
	public void bind()
	{
		GL30.glBindVertexArray(id); 
		
		if(is3D)
			bindArrayBuffer(POSITION_LOCATION, 3, positionHandle, positionBuffer);
		else
			bindArrayBuffer(POSITION_LOCATION, 2, positionHandle, positionBuffer);
		
		bindArrayBuffer(UV_LOCATION, 2, uvHandle, uvBuffer);
		bindArrayBuffer(NORMAL_LOCATION, 3, normalHandle, normalBuffer);
		bindArrayBuffer(COLOR_LOCATION, 4, colorHandle, colorBuffer);
		
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
        GL15.glDeleteBuffers(colorHandle);
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
	
	private void initBuffers3D(ArrayList<Vertex3D> vertices, int[] indices)
	{		
		createBuffers(vertices.size(), true, indices.length);	
		indexBuffer.put(indices);

		for (int i = 0; i < vertices.size(); i++)
		{
			positionBuffer.put(asFloats(vertices.get(i).getPosition()));
			uvBuffer.put(asFloats(vertices.get(i).getUv()));
			normalBuffer.put(asFloats(vertices.get(i).getNormal()));
			colorBuffer.put(asFloats(vertices.get(i).getColorRGBA()));
		}
		
		flipBuffers();
	}
	
	private void initBuffers2D(ArrayList<Vertex2D> vertices, int[] indices)
	{		
		createBuffers(vertices.size(), false, indices.length);
		
		indexBuffer.put(indices);

		for (int i = 0; i < vertices.size(); i++)
		{
			positionBuffer.put(asFloats(vertices.get(i).getPosition()));
			uvBuffer.put(asFloats(vertices.get(i).getUv()));
			normalBuffer.put(asFloats(vertices.get(i).getNormal()));
		}
		
		flipBuffers();	
	}
	
	private void createHandles()
	{
		positionHandle = GL15.glGenBuffers();
		uvHandle = GL15.glGenBuffers();
		indexHandle = GL15.glGenBuffers();
		normalHandle = GL15.glGenBuffers();
		colorHandle = GL15.glGenBuffers();
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
		colorBuffer = BufferUtils.createFloatBuffer(vertexCount * 4);

	}
	
	private void flipBuffers()
	{
		positionBuffer.flip();
		uvBuffer.flip();
		normalBuffer.flip();	
		colorBuffer.flip();
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
	
	private float[] asFloats(Vector4f v) 
	{
		return new float[]{v.x, v.y, v.z, v.w};
	}	
}
