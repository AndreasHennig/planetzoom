package engine;

import lenz.utils.ShaderProgram;
import lenz.utils.Texture;

public abstract class Object
{
	protected Texture texture;
	protected byte[] indices;
	
	protected ShaderProgram shader; 

		
	public Texture getTexture()
	{
		return texture;
	}

	public void setTexture(Texture textTexture)
	{
		this.texture = textTexture;
	}

	public ShaderProgram getShader()
	{
		return shader;
	}

	public void setShader(ShaderProgram shader)
	{
		this.shader = shader;
	}
	
	public byte[] getIndices()
	{
		return indices;
	}

	public void setIndices(byte[] indices)
	{
		this.indices = indices;
	}
}
