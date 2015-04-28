package lenz.utils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

import java.io.InputStream;
import java.util.Scanner;

public class ShaderProgram {
	private int id;

	public ShaderProgram(String resourceNameWithoutExtension) {
		this(resourceNameWithoutExtension + ".v", resourceNameWithoutExtension + ".g", resourceNameWithoutExtension + ".f");
	}

	public ShaderProgram(String vertexResourceName, String fragmentResourceName) {
		this(vertexResourceName, null, fragmentResourceName);
	}

	public ShaderProgram(String vertexResourceName, String geometryResourceName, String fragmentResourceName) {
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

	public void bindAttributeLocations(String... variableNames) {
		int i = 0;
		for (String var : variableNames) {
			glBindAttribLocation(id, i, var);
			++i;
		}
		glLinkProgram(id);
		if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
			throw new RuntimeException(glGetProgramInfoLog(id, glGetProgrami(id, GL_INFO_LOG_LENGTH)));
		}
	}
}
