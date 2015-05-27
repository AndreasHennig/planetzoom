package lenz.utils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class ShaderProgram 
{
	private int shaderID;

	public ShaderProgram(String resourceNameWithoutExtension) 
	{
		this(resourceNameWithoutExtension + ".ver", resourceNameWithoutExtension + ".geo", resourceNameWithoutExtension + ".fra");
	}

	public ShaderProgram(String vertexResourceName, String fragmentResourceName) 
	{
		this(vertexResourceName, null, fragmentResourceName);
	}

	public ShaderProgram(String vertexResourceName, String geometryResourceName, String fragmentResourceName) 
	{
		shaderID = glCreateProgram();
		compileFromSourceAndAttach(vertexResourceName, GL_VERTEX_SHADER);
		compileFromSourceAndAttach(fragmentResourceName, GL_FRAGMENT_SHADER);
		compileFromSourceAndAttach(geometryResourceName, GL_GEOMETRY_SHADER);

		glLinkProgram(shaderID);
		if (glGetProgrami(shaderID, GL_LINK_STATUS) == GL_FALSE) {
			throw new RuntimeException(glGetProgramInfoLog(shaderID, glGetProgrami(shaderID, GL_INFO_LOG_LENGTH)));
		}
	}

	public int getId() {
		return shaderID;
	}

	public void bind()
	{
		glUseProgram(shaderID);
	}
	
	public void unbind()
	{
		glUseProgram(0);
	}
	
	private InputStream getInputStreamFromResourceName(String resourceName) {
		return getClass().getResourceAsStream("/res/shaders/" + resourceName);
	}

	private void compileFromSourceAndAttach(String resourceName, int type) {
		InputStream inputStreamFromResourceName = getInputStreamFromResourceName(resourceName);
		if (inputStreamFromResourceName == null) {
			if (type != GL_GEOMETRY_SHADER) {
				throw new RuntimeException("Shader source file " + resourceName + " not found!");
			}
			return;
		}
		try (Scanner in = new Scanner(inputStreamFromResourceName)) {
			String source = in.useDelimiter("\\A").next();
			int shaderId = glCreateShader(type);
			glShaderSource(shaderId, source);
			glCompileShader(shaderId);

			String compileLog = glGetShaderInfoLog(shaderId, glGetShaderi(shaderId, GL_INFO_LOG_LENGTH));
			if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
				throw new RuntimeException("Shader " + resourceName + " not compiled: " + compileLog);
			}
			if (!compileLog.isEmpty()) {
				System.err.println(resourceName + ": " + compileLog);
			}

			glAttachShader(shaderID, shaderId);
		}
	}

	public void bindAttributeLocations(String... variableNames) 
	{
		int i = 0;
		for (String var : variableNames) 
		{
			glBindAttribLocation(shaderID, i, var);
			++i;
		}
		glLinkProgram(shaderID);
		if (glGetProgrami(shaderID, GL_LINK_STATUS) == GL_FALSE) {
			throw new RuntimeException(glGetProgramInfoLog(shaderID, glGetProgrami(shaderID, GL_INFO_LOG_LENGTH)));
		}
	}
	
	public static void loadUniformMat4f(int shaderId, Matrix4f matrix, String name, boolean transpose)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		matrix.store(buffer);
		buffer.flip();
		int location = glGetUniformLocation(shaderId, name);
		
		glUniformMatrix4fv(location, transpose, buffer);
	}

	public static void loadUniformVec3f(int shaderId, Vector3f vector, String name)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
		vector.store(buffer);
		buffer.flip();		
		int location = 	glGetUniformLocation(shaderId, name);		
	
		glUniform3fv(location, buffer);
	}

	public static void loadUniform1f(int shaderId, float value, String name)
	{		
		int location = 	glGetUniformLocation(shaderId, name);			
		
		glUniform1f(location, value);
	}
	
}
