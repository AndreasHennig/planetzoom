package engine;

import geometry.Vertex2D;

import java.util.ArrayList;

import lenz.utils.ShaderProgram;
import lenz.utils.Texture;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class GameObject2D extends GameObject
{
	private ArrayList<Vertex2D> vertices;

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
	public static GameObject2D getTestObject2D(float x, float y)
	{
		ArrayList<Vector2f> positions = new ArrayList<Vector2f>();
		positions.add(new Vector2f(x, y + 0.2f));
		positions.add(new Vector2f(x, y));
		positions.add(new Vector2f(x + 0.2f, y));
		positions.add(new Vector2f(x + 0.2f, y + 0.2f));
        
        ArrayList<Vector2f> uvs = new ArrayList<>();      
        uvs.add(new Vector2f(0.0f, 1.0f));
        uvs.add(new Vector2f(0.0f, 0.0f));
        uvs.add(new Vector2f(1.0f, 0.0f));
        uvs.add(new Vector2f(1.0f, 1.0f));
        
        Vector3f normal = new Vector3f(0, 0, 1);
        
        ArrayList<Vertex2D> vertices = new ArrayList<>();
        vertices.add(new Vertex2D(positions.get(0), uvs.get(1), normal));
        vertices.add(new Vertex2D(positions.get(1), uvs.get(0), normal));
        vertices.add(new Vertex2D(positions.get(2), uvs.get(3), normal));
        vertices.add(new Vertex2D(positions.get(3), uvs.get(2), normal));
        
        
        // OpenGL expects to draw vertices in counter clockwise order by default
        int[] indices = 
        	{
                // Left bottom triangle
                0, 1, 2,
                
                // Right top triangle
                2, 3, 0
        };
        
        GameObject2D object = new GameObject2D(vertices, indices);
        object.setTexture(new Texture("crypt_wall.png"));
        return object;
	}

	public ArrayList<Vertex2D> getVertices()
	{
		return vertices;
	}

	public void setVertices(ArrayList<Vertex2D> vertices)
	{
		this.vertices = vertices;
	}
	

}