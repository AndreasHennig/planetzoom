package engine;

import lenz.utils.Texture;
import Peter.TextureUsingPNGDecoder;

public abstract class GameObject
{
	protected TextureUsingPNGDecoder texture;
	protected int[] indices;
	
		
	public TextureUsingPNGDecoder getTexture()
	{
		return texture;
	}

	public void setTexture(TextureUsingPNGDecoder textTexture)
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
