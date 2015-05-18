package engine;

import geometry.Vertex3D;

import java.util.ArrayList;

import lenz.utils.ShaderProgram;
import lenz.utils.Texture;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class GameObject3D extends GameObject
{
	protected ArrayList<Vertex3D> vertices;
	
	public GameObject3D()
	{
		this.vertices= new ArrayList<Vertex3D>();
	}
	
	public GameObject3D(ArrayList<Vertex3D> vertices, int[] indices)
	{
		this.vertices= vertices;
		this.indices = indices;
	}

	
	public static GameObject3D getTestObject3D()
	{
		
		ArrayList<Vector3f> positions = new ArrayList<Vector3f>();
		
		float p = 0.5f;
		float m = -0.5f;
		
		positions.add(new Vector3f(m, p, m)); //0
		positions.add(new Vector3f(m, p, p)); //1
		positions.add(new Vector3f(p, p, p)); //2
		positions.add(new Vector3f(p, p, m)); //3

		positions.add(new Vector3f(m, m, m)); //4
		positions.add(new Vector3f(m, m, p)); //5
		positions.add(new Vector3f(p, m, p)); //6
		positions.add(new Vector3f(p, m, m)); //7
		
		ArrayList<Vector2f> uvs = new ArrayList<Vector2f>();
		
		uvs.add(new Vector2f(0f, 0f));
		uvs.add(new Vector2f(1f, 0f));
		uvs.add(new Vector2f(1f, 1f));
		uvs.add(new Vector2f(0f, 1f));
		
		
		ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
		
		normals.add(new Vector3f(0, 0, 1));  //0
		normals.add(new Vector3f(0, 0, -1)); //1
		normals.add(new Vector3f(-1, 0, 0)); //2
		normals.add(new Vector3f(1, 0, 0));  //3
		normals.add(new Vector3f(0, 1, 0));  //4
		normals.add(new Vector3f(0, -1, 0)); //5
		
		ArrayList<Vertex3D> vertices = new ArrayList<>();
		
		//Top
		vertices.add(new Vertex3D(positions.get(0), uvs.get(3), normals.get(4)));
		vertices.add(new Vertex3D(positions.get(1), uvs.get(0), normals.get(4)));
		vertices.add(new Vertex3D(positions.get(2), uvs.get(1), normals.get(4)));
		vertices.add(new Vertex3D(positions.get(3), uvs.get(2), normals.get(4)));
		
		//Front
		vertices.add(new Vertex3D(positions.get(0), uvs.get(0), normals.get(1)));	//4
		vertices.add(new Vertex3D(positions.get(3), uvs.get(1), normals.get(1)));	//5
		vertices.add(new Vertex3D(positions.get(4), uvs.get(3), normals.get(1)));	//6
		vertices.add(new Vertex3D(positions.get(7), uvs.get(2), normals.get(1)));	//7
		
		int[] indices = new int[]
				{
					//Top
					0, 1, 2,
					2, 3, 0,
					
					//Back
					4, 5, 6,
					5, 7, 6,
					
					//Front
					
				};
		

        GameObject3D object = new GameObject3D(vertices, indices);
        //object.shader = new ShaderProgram("testShader");
        object.setTexture(new Texture("woodenBox.jpg"));
        return object;
	}


	public ArrayList<Vertex3D> getVertices()
	{
		return vertices;
	}


	public void setVertices(ArrayList<Vertex3D> vertices)
	{
		this.vertices = vertices;
	}	
}
