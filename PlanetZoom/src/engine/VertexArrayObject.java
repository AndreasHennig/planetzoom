package engine;

import geometry.Vertex2D;
import geometry.Vertex3D;

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
	
	private static final int POSITION_ATTRIBUTE_LOCATION = 0;
	private static final int UV_ATTRIBUTE_LOCATION = 1;
	private static final int NORMAL_ATTRIBUTE_LOCATION = 2;
	private static final int COLOR_ATTRIBUTE_LOCATION = 3;
	
	public VertexArrayObject(GameObject2D object2D)
	{
		is3D = false;
		id = GL30.glGenVertexArrays();	
		initBuffers2D(object2D.getVertices(), object2D.getIndices());
		indexCount = object2D.getIndices().length;
	}
	
	public VertexArrayObject(GameObject3D object3D)
	{
		is3D = true;
		id = GL30.glGenVertexArrays();	
		initBuffers3D(object3D.getVertices(), object3D.getIndices());
		indexCount = object3D.getIndices().length;
	}
	
	public void bindBuffers()
	{
		GL30.glBindVertexArray(id); 
		
		if(is3D)
			bindArrayBuffer(POSITION_ATTRIBUTE_LOCATION, 3, positionHandle, positionBuffer);
		else
			bindArrayBuffer(POSITION_ATTRIBUTE_LOCATION, 2, positionHandle, positionBuffer);
		
		bindArrayBuffer(UV_ATTRIBUTE_LOCATION, 2, uvHandle, uvBuffer);
		bindArrayBuffer(NORMAL_ATTRIBUTE_LOCATION, 2, normalHandle, normalBuffer);
		bindArrayBuffer(COLOR_ATTRIBUTE_LOCATION, 4, colorHandle, colorBuffer);
		bindIndexBuffer(indexHandle, indexBuffer);
		
		GL30.glBindVertexArray(0); 
	}
	
	public void delete()
	{
        // Disable the VBO index from the VAO attributes list
        GL20.glDisableVertexAttribArray(0);
         
        // Delete the VBOs
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(positionHandle);
        GL15.glDeleteBuffers(uvHandle);
        GL15.glDeleteBuffers(normalHandle);
        GL15.glDeleteBuffers(colorHandle);
         
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(indexHandle);
         
        // Delete the VAO
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(id);
	}

	private void createBuffers(int vertexCount, boolean is3D, int indexCount)
	{
		positionHandle = GL15.glGenBuffers();
		uvHandle = GL15.glGenBuffers();
		indexHandle = GL15.glGenBuffers();
		normalHandle = GL15.glGenBuffers();
		colorHandle = GL15.glGenBuffers();
		
		if(is3D)
			positionBuffer = BufferUtils.createFloatBuffer(vertexCount * 3);
		else
			positionBuffer = BufferUtils.createFloatBuffer(vertexCount * 2);
		
		uvBuffer = BufferUtils.createFloatBuffer(vertexCount * 2);
		normalBuffer = BufferUtils.createFloatBuffer(vertexCount * 3);
		colorBuffer = BufferUtils.createFloatBuffer(vertexCount * 4);
		indexBuffer = BufferUtils.createIntBuffer(indexCount);
	}
	
	private void initBuffers3D(ArrayList<Vertex3D> vertices, int[] indices)
	{		
		createBuffers(vertices.size(), true, indices.length);
		
		indexBuffer.put(indices);

		for (int i = 0; i < vertices.size(); i++)
		{
			positionBuffer.put(asFloats(vertices.get(i).getPosition()));
			normalBuffer.put(asFloats(vertices.get(i).getNormal()));
			uvBuffer.put(asFloats(vertices.get(i).getUv()));
			colorBuffer.put(asFloats(vertices.get(i).getColorRGBA()));
		}
		
		positionBuffer.flip();
		uvBuffer.flip();
		indexBuffer.flip();		
		colorBuffer.flip();
		normalBuffer.flip();
	}
	
	private void initBuffers2D(ArrayList<Vertex2D> vertices, int[] indices)
	{		
		createBuffers(vertices.size(), false, indices.length);
		indexBuffer.put(indices);

		for (int i = 0; i < vertices.size(); i++)
		{
			positionBuffer.put(asFloats(vertices.get(i).getPosition()));
			normalBuffer.put(asFloats(vertices.get(i).getNormal()));
			uvBuffer.put(asFloats(vertices.get(i).getUv()));
		}
		
		positionBuffer.flip();
		uvBuffer.flip();
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
		return new float[]{v.w, v.x, v.y, v.z};
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

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getVboIndexHandle()
	{
		return indexHandle;
	}

	public void setVboIndexHandle(int vboIndexHandle)
	{
		this.indexHandle = vboIndexHandle;
	}
	
	public int getIndexCount()
	{
		return indexCount;
	}
	

}
