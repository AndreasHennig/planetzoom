package engine;

import geometry.Vertex3D;

import java.util.ArrayList;

public class GameObject3D extends GameObject
{
	protected ArrayList<Vertex3D> vertexData;
	
	public GameObject3D()
	{
		super();
		this.vertexData= new ArrayList<Vertex3D>();
	}
	
	public GameObject3D(ArrayList<Vertex3D> vertices, int[] indices)
	{
		super();
		this.vertexData= vertices;
		this.indices = indices;
	}


	public ArrayList<Vertex3D> getVertices()
	{
		return vertexData;
	}


	public void setVertices(ArrayList<Vertex3D> vertices)
	{
		this.vertexData = vertices;
	}

	@Override
	public void createVAO()
	{
		this.vao = new VertexArrayObject(this);
	}	
	
}
