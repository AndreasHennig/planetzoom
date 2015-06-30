package planetZoooom.geometry;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import planetZoooom.engine.VertexArrayObject;
import planetZoooom.interfaces.IGameObjectListener;

public abstract class GameObject
{
	protected int[] indices;
	protected Matrix4f modelMatrix;
	protected VertexArrayObject vao;
	protected List<IGameObjectListener> listeners;
	
	public GameObject()
	{
		listeners = new ArrayList<>();
		
		modelMatrix = new Matrix4f();
		modelMatrix.setIdentity();
	}
	
	public void addListener(IGameObjectListener listener) {
		listeners.add(listener);
	}
	
	public void draw(int mode)
	{
    	vao.bind();

        enableVertexAttributeArrays();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vao.getIndexHandle());
         
        // Draw vertices
        GL11.glDrawElements(mode, vao.getIndexCount() , GL11.GL_UNSIGNED_INT, 0);
      
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
	
	public void notifyListeners(Vertex v) {
		for(IGameObjectListener listener : listeners)
			listener.vertexCreated(v);
	}
}