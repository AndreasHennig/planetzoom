package engine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import Peter.TextureUsingPNGDecoder;

public abstract class GameObject
{
	protected TextureUsingPNGDecoder texture;
	protected int[] indices;
	protected int shaderID;
	protected Matrix4f modelMatrix;
	protected VertexArrayObject vao;
	
	public GameObject()
	{
		modelMatrix = new Matrix4f();
		modelMatrix.setIdentity();
	}
		
	public void draw(int mode)
	{
		if(texture != null)
			texture.bind();
		
    	vao.bind();

        enableVertexAttributeArrays();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vao.getIndexHandle());
         
        // Draw vertices
        GL11.glDrawElements(mode, vao.getIndexCount() , GL11.GL_UNSIGNED_INT, 0);
        
        //Clean up
		if(texture != null)
			texture.unbind();
        
		disableVertexAttributeArrays();
        vao.unbind();
	}
	
	public abstract void createVAO();
	
	private void enableVertexAttributeArrays()
	{
        GL20.glEnableVertexAttribArray(VertexArrayObject.POSITION_LOCATION);
        GL20.glEnableVertexAttribArray(VertexArrayObject.UV_LOCATION);
        GL20.glEnableVertexAttribArray(VertexArrayObject.NORMAL_LOCATION);
        GL20.glEnableVertexAttribArray(VertexArrayObject.COLOR_LOCATION);
	}
	
	private void disableVertexAttributeArrays()
	{
        GL20.glDisableVertexAttribArray(VertexArrayObject.POSITION_LOCATION);
        GL20.glDisableVertexAttribArray(VertexArrayObject.UV_LOCATION);
        GL20.glDisableVertexAttribArray(VertexArrayObject.NORMAL_LOCATION);
        GL20.glDisableVertexAttribArray(VertexArrayObject.COLOR_LOCATION);
	}
	
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
	
	public Matrix4f getModelMatrix()
	{
		return modelMatrix;
	}

	public void setModelMatrix(Matrix4f modelMatrix)
	{
		this.modelMatrix = modelMatrix;
	}
	
	public int getShaderID()
	{
		return shaderID;
	}
	
	public void setShaderID(int shaderID)
	{
		this.shaderID = shaderID;
	}
	
	
	
}
