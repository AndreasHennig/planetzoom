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
	private int colorHandle;
	private int iboHandle;
	
	public static final int VERTEX_LOCATION = 0;
	public static final int UV_LOCATION = 1;
	public static final int NORMAL_LOCATION = 2;
	public static final int COLOR_LOCATION = 3;
	
	public VertexArray(float[] vertices, float[] normals, float[] uvCoords, int[] indices)
	{
		initBufferHandles();
		doBufferStuff(vertices, normals, uvCoords, indices);
	}
	
	public VertexArray(float[] vertices, float[] normals, float[] uvCoords, float[] colors, int[] indices)
	{
		initBufferHandles();
		doBufferStuff(vertices, normals, uvCoords, colors, indices);
	}
	
	public void update(float[] vertices, float[] normals, float[] uvCoords, int[] indices)
	{
		doBufferStuff(vertices, normals, uvCoords, indices);
	}
	
	public void update(float[] vertices, float[] normals, float[] uvCoords, float[] colors, int[] indices)
	{
		doBufferStuff(vertices, normals, uvCoords, colors, indices);
	}
	
	private void initBufferHandles()
	{
		vaoHandle = glGenVertexArrays();
		vboHandle = glGenBuffers();
		nboHandle = glGenBuffers();
		uvboHandle = glGenBuffers();
		colorHandle = glGenBuffers();
		iboHandle = glGenBuffers();
	}
	
	private void fillFloatBuffer(int handle, float[] data, int location, int stride, boolean normalize)
	{
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data).flip();
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(location, stride, GL_FLOAT, normalize, 0, 0);
		glEnableVertexAttribArray(location);
	}
	
	private void doBufferStuff(float[] vertices, float[] normals, float[] uvCoords, float[] colors, int[] indices)
	{

		glBindVertexArray(vaoHandle);	
		fillFloatBuffer(colorHandle, colors, COLOR_LOCATION, 4, false);
		glBindVertexArray(0);
		doBufferStuff(vertices, normals, uvCoords, indices);
		
	}
	private void doBufferStuff(float[] vertices, float[] normals, float[] uvCoords, int[] indices)
	{
		count = indices.length;
		
		glBindVertexArray(vaoHandle);
		
		fillFloatBuffer(vboHandle, vertices, VERTEX_LOCATION, 3, false);
		fillFloatBuffer(nboHandle, normals, NORMAL_LOCATION, 3, true);
		fillFloatBuffer(uvboHandle, uvCoords, UV_LOCATION, 2, false);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboHandle);
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
		indexBuffer.put(indices).flip();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	private void bind()
	{
		glBindVertexArray(vaoHandle);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboHandle);
	}

	private void unbind()
	{
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	private void draw(int mode)
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
