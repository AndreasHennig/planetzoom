package planetZoooom.engine;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import planetZoooom.graphics.Texture;

public abstract class MeshObject 
{
	protected float[] vertices;
	protected float[] normals;
	protected float[] uvCoords;
	protected float[] colors;
	
	protected int[] indices;
	
	protected Matrix4f modelMatrix;
	protected Vector3f position;
	
	protected VertexArray mesh;
	private Texture texture;
	
	public void render(int mode)
	{
		if(texture != null){
			texture.bind();
		}
		
		mesh.render(mode);
		
		if(texture != null){
			texture.unbind();
		}
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public Matrix4f getModelMatrix(){
		return modelMatrix;
	}
	
	public Vector3f getPosition(){
		return position;
	}
	
	public void setPosition(Vector3f position){
		this.position = position;
		modelMatrix.m30 = position.x;
		modelMatrix.m31 = position.y;
		modelMatrix.m32 = position.z;
	}
	
}
