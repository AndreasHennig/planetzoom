package engine;

import geometry.Vertex2D;
import geometry.Vertex3D;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class VertexArrayObject
{
	private int id;

	private int vboNormalHandle;
	private int vboVertexHandle;
	private int vboUVHandle;
	private int vboIndexHandle;
	
	
	private FloatBuffer normalBuffer;
	private FloatBuffer vertexBuffer;
	private FloatBuffer uvBuffer;
	private ByteBuffer indexBuffer;
	
	private int indexCount;
	private boolean is3D;
	
	private final int vertexAttributeLocation = 0;
	private final int uvAttributeLocation = 1;
	private final int normalAttributeLocation = 2;
	
	public VertexArrayObject(Object2D object2D)
	{
		is3D = false;
		id = GL30.glGenVertexArrays();	
		initBuffers2D(object2D.getVertices(), object2D.getIndices());
		indexCount = object2D.getIndices().length;
	}
	
	public VertexArrayObject(Object3D object3D)
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
			bindArrayBuffer(vertexAttributeLocation, 3, vboVertexHandle, vertexBuffer);
		else
			bindArrayBuffer(vertexAttributeLocation, 2, vboVertexHandle, vertexBuffer);
		
		bindArrayBuffer(uvAttributeLocation, 2, vboUVHandle, uvBuffer);
		bindArrayBuffer(normalAttributeLocation, 2, vboNormalHandle, normalBuffer);
		bindIndexBuffer(vboIndexHandle, indexBuffer);
		
		GL30.glBindVertexArray(0); 
	}
	
	public void delete()
	{
        // Disable the VBO index from the VAO attributes list
        GL20.glDisableVertexAttribArray(0);
         
        // Delete the VBOs
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(vboVertexHandle);
        GL15.glDeleteBuffers(vboUVHandle);
        GL15.glDeleteBuffers(vboNormalHandle);
         
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(vboIndexHandle);
         
        // Delete the VAO
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(id);
	}

	private void initBuffers3D(ArrayList<Vertex3D> vertices, byte[] indices)
	{		
		vboVertexHandle = GL15.glGenBuffers();
		vboUVHandle = GL15.glGenBuffers();
		vboIndexHandle = GL15.glGenBuffers();
		vboNormalHandle = GL15.glGenBuffers();

		vertexBuffer = BufferUtils.createFloatBuffer(vertices.size() * 3);
		uvBuffer = BufferUtils.createFloatBuffer(vertices.size() * 2);
		normalBuffer = BufferUtils.createFloatBuffer(vertices.size() * 3);
		indexBuffer = BufferUtils.createByteBuffer(indices.length);
		
		indexBuffer.put(indices);

		for (int i = 0; i < vertices.size(); i++)
		{
			vertexBuffer.put(asFloats(vertices.get(i).getPosition()));
			normalBuffer.put(asFloats(vertices.get(i).getNormal()));
			uvBuffer.put(asFloats(vertices.get(i).getUv()));
		}
		
		vertexBuffer.flip();
		uvBuffer.flip();
		indexBuffer.flip();		
	}
	
	private void initBuffers2D(ArrayList<Vertex2D> vertices, byte[] indices)
	{		
		vboVertexHandle = GL15.glGenBuffers();
		vboUVHandle = GL15.glGenBuffers();
		vboIndexHandle = GL15.glGenBuffers();
		vboNormalHandle = GL15.glGenBuffers();

		vertexBuffer = BufferUtils.createFloatBuffer(vertices.size() * 2);
		uvBuffer = BufferUtils.createFloatBuffer(vertices.size() * 2);
		normalBuffer = BufferUtils.createFloatBuffer(vertices.size() * 3);
		indexBuffer = BufferUtils.createByteBuffer(indices.length);
		
		indexBuffer.put(indices);

		for (int i = 0; i < vertices.size(); i++)
		{
			vertexBuffer.put(asFloats(vertices.get(i).getPosition()));
			normalBuffer.put(asFloats(vertices.get(i).getNormal()));
			uvBuffer.put(asFloats(vertices.get(i).getUv()));
		}
		
		vertexBuffer.flip();
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
	
	private void bindArrayBuffer(int location, int dataSize, int handle, FloatBuffer buffer) 
	{ 
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); 
		GL20.glVertexAttribPointer(location, dataSize, GL11.GL_FLOAT, false, 0, 0); 
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void bindIndexBuffer(int handle, ByteBuffer buffer) 
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
		return vboIndexHandle;
	}

	public void setVboIndexHandle(int vboIndexHandle)
	{
		this.vboIndexHandle = vboIndexHandle;
	}
	
	public int getIndexCount()
	{
		return indexCount;
	}
	

}
