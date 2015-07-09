package planetZoooom.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class VertexArray 
{
	private int count;
	
	private int vaoHandle;
	private int vboHandle;
	private int uvboHandle;
	private int nboHandle;
	private int iboHandle;
	
	public static final int VERTEX_LOCATION = 0;
	public static final int UV_LOCATION = 1;
	public static final int NORMAL_LOCATION = 2;
	
	public VertexArray(float[] vertices, float[] normals, float[] uvCoords, int[] indices)
	{
		count = indices.length;
		
		vaoHandle = glGenVertexArrays();
		glBindVertexArray(vaoHandle);
		
		vboHandle = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
		vertexBuffer.put(vertices).flip();
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(VERTEX_LOCATION, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(VERTEX_LOCATION);
		
		uvboHandle = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, uvboHandle);
		FloatBuffer uvBuffer = BufferUtils.createFloatBuffer(uvCoords.length);
		uvBuffer.put(uvCoords).flip();
		glBufferData(GL_ARRAY_BUFFER, uvBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(UV_LOCATION, 2, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(UV_LOCATION);
		
		nboHandle = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, nboHandle);
		FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(normals.length);
		normalBuffer.put(normals).flip();
		glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(NORMAL_LOCATION, 3, GL_FLOAT, true, 0, 0);	//true for normalized
		glEnableVertexAttribArray(NORMAL_LOCATION);
		
		iboHandle = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboHandle);
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
		indexBuffer.put(indices).flip();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		
		//UNBIND
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	public void bind()
	{
		glBindVertexArray(vaoHandle);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboHandle);
	}

	public void unbind()
	{
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	public void draw(int mode)
	{
		glDrawElements(mode, count, GL_UNSIGNED_INT, 0);
	}
	
	public void render(int mode)
	{
		bind();
		draw(mode);
		unbind();
	}
}
