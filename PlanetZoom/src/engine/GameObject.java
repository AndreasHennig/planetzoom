package engine;

import lenz.utils.ShaderProgram;
import lenz.utils.Texture;

public abstract class GameObject
{
	protected Texture texture;
	protected int[] indices;
	
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
	
	public int[] getIndices()
	{
		return indices;
	}

	public void setIndices(int[] indices)
	{
		this.indices = indices;
	}
}
