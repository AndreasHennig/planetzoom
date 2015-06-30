package planetZoooom.graphics;

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
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class ShaderProgram 
{
	private int shaderID;
	private HashMap<String, Integer> uniformIDMap;

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
		
		uniformIDMap = new HashMap<String, Integer>();
		
		glLinkProgram(shaderID);
		if (glGetProgrami(shaderID, GL_LINK_STATUS) == GL_FALSE) {
			throw new RuntimeException(glGetProgramInfoLog(shaderID, glGetProgrami(shaderID, GL_INFO_LOG_LENGTH)));
		}
	}

	public int getId() {
		return shaderID;
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
	
	public void loadUniformMat4f(Matrix4f matrix, String name, boolean transpose)
	{
		if(!uniformIDMap.containsKey(name))
			uniformIDMap.put(name, glGetUniformLocation(shaderID, name));
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		matrix.store(buffer);
		buffer.flip();
		
		glUniformMatrix4fv(uniformIDMap.get(name), transpose, buffer);
	}

	public void loadUniformVec3f(Vector3f vector, String name)
	{
		if(!uniformIDMap.containsKey(name))
			uniformIDMap.put(name, glGetUniformLocation(shaderID, name));
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
		vector.store(buffer);
		buffer.flip();		
	
		glUniform3fv(uniformIDMap.get(name), buffer);
	}

	public void loadUniform1f(float value, String name)
	{		
		if(!uniformIDMap.containsKey(name))
			uniformIDMap.put(name, glGetUniformLocation(shaderID, name));
					
		glUniform1f(uniformIDMap.get(name), value);
	}
	
}
