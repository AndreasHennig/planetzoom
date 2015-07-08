package planetZoooom.geometry;

import java.util.ArrayList;

import planetZoooom.engine.VertexArrayObject;

public class GameObject3D extends GameObject
{
	protected ArrayList<Vertex> vertexData;
	
	public GameObject3D()
	{
		super();
		this.vertexData = new ArrayList<Vertex>();
	}
	
	public GameObject3D(ArrayList<Vertex> vertices, int[] indices)
	{
		super();
		this.vertexData= vertices;
		this.indices = indices;
	}


	public ArrayList<Vertex> getVertices()
	{
		return vertexData;
	}


	public void setVertices(ArrayList<Vertex> vertices)
	{
		this.vertexData = vertices;
	}

	@Override
	public void createVAO()
	{
		this.vao = new VertexArrayObject(this);
	}	
}
