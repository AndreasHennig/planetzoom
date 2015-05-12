package engine;

import geometry.Vertex2D;

import java.util.ArrayList;

import lenz.utils.ShaderProgram;
import lenz.utils.Texture;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Object2D extends Object
{
	private ArrayList<Vertex2D> vertices;

	public Object2D()
	{
		super();
		vertices = new ArrayList<>();
	}
	
	public Object2D(ArrayList<Vertex2D> vertices, byte[] indices)
	{
		this.vertices = vertices;
		this.indices = indices;
	}
	public static Object2D getTestObject2D()
	{
		ArrayList<Vector2f> positions = new ArrayList<Vector2f>();
		positions.add(new Vector2f(0.6f, 0.8f));
		positions.add(new Vector2f(0.6f, 0.6f));
		positions.add(new Vector2f(0.8f, 0.6f));
		positions.add(new Vector2f(0.8f, 0.8f));
        
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
        byte[] indices = 
        	{
                // Left bottom triangle
                0, 1, 2,
                
                // Right top triangle
                2, 3, 0
        };
        
        Object2D object = new Object2D(vertices, indices);
        object.shader = new ShaderProgram("HUDShader");
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
