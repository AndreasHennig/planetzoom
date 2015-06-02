package engine;

import lenz.utils.Texture;

public abstract class GameObject
{
	protected Texture texture;
	protected int[] indices;
		
	public Texture getTexture()
	{
		return texture;
	}

	public void setTexture(Texture textTexture)
	{
		this.texture = textTexture;
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
