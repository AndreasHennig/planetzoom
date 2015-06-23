package engine;

import geometry.Vertex2D;
import java.util.ArrayList;

public class GameObject2D extends GameObject
{
	protected ArrayList<Vertex2D> vertices;

	public GameObject2D()
	{
		super();
		vertices = new ArrayList<>();
	}
	
	public GameObject2D(ArrayList<Vertex2D> vertices, int[] indices)
	{
		this.vertices = vertices;
		this.indices = indices;
	}

	public ArrayList<Vertex2D> getVertices()
	{
		return vertices;
	}

	public void setVertices(ArrayList<Vertex2D> vertices)
	{
		this.vertices = vertices;
	}

	@Override
	public void createVAO()
	{
		this.vao = new VertexArrayObject(this);
	}
	

}
