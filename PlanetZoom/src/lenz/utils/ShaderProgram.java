package lenz.utils;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class ShaderProgram 
{
	private int id;

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
		id = glCreateProgram();
		compileFromSourceAndAttach(vertexResourceName, GL_VERTEX_SHADER);
		compileFromSourceAndAttach(fragmentResourceName, GL_FRAGMENT_SHADER);
		compileFromSourceAndAttach(geometryResourceName, GL_GEOMETRY_SHADER);

		glLinkProgram(id);
		if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
			throw new RuntimeException(glGetProgramInfoLog(id, glGetProgrami(id, GL_INFO_LOG_LENGTH)));
		}
	}

	public int getId() {
		return id;
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

			glAttachShader(id, shaderId);
		}
	}

	public void bindAttributeLocations(String... variableNames) 
	{
		int i = 0;
		for (String var : variableNames) 
		{
			glBindAttribLocation(id, i, var);
			++i;
		}
		glLinkProgram(id);
		if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
			throw new RuntimeException(glGetProgramInfoLog(id, glGetProgrami(id, GL_INFO_LOG_LENGTH)));
		}
	}
	
	public static void loadMatrix4f(int shaderId, Matrix4f matrix, String name)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		matrix.store(buffer);
		buffer.flip();
		int location = glGetUniformLocation(shaderId, name);

		//if(location < 0)
			//System.out.println("Matrix4f " + name + " not loaded into shader"); //May occur when not used in shader
		
		glUniformMatrix4fv(location, false, buffer);
	}
	
	public static void loadVector3f(int shaderId, Vector3f vector, String name)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
		vector.store(buffer);
		buffer.flip();		
		int location = 	glGetUniformLocation(shaderId, name);		
		
		//if(location < 0)
			//System.out.println("Vector3f " + name + " not loaded into shader"); //May occur when not used in shader
		
		glUniform3fv(location, buffer);
	}
}
