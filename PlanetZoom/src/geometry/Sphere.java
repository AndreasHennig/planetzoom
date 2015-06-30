//http://www.binpress.com/tutorial/creating-an-octahedron-sphere/162

package geometry;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engine.GameObject3D;

public class Sphere extends GameObject3D {
	public final static int MAX_SUBDIVISIONS = 8;
	public final static int MIN_SUBDIVISIONS = 1;

	private int subdivisions;
	private float radius;
	
	private SphereGraph graph;
	private ArrayList<SphereGraph.TriangleNode> nodes;
	
	private Vector3f[] vertices;
	private Vector3f[] normals;
	
	public Sphere()
	{
		this(1);
	}

	public Sphere(float radius)
	{
		this.radius = radius;
		graph = new SphereGraph();
	}

	public float getRadius()
	{
		return radius;
	}

	public void update(int subdivisions, Vector3f cameraAngle)
	{
		this.subdivisions = subdivisions > MAX_SUBDIVISIONS ? MAX_SUBDIVISIONS : subdivisions;
		this.subdivisions = subdivisions < MIN_SUBDIVISIONS ? MIN_SUBDIVISIONS : subdivisions;

		nodes = graph.createGraph(this.subdivisions, cameraAngle);
		
		vertices = new Vector3f[nodes.size() * 3];
		normals = new Vector3f[vertices.length];
		indices = new int[vertices.length];

		addNodeDataToGameObject();
		createVAO();
	}
		
	private void addNodeDataToGameObject()
	{
		int metaIndex = 0;
		
		Vector2f uvDummy = new Vector2f(0, 0);
		Vector4f colorDummy = new Vector4f(1,1,1,1);
		
		vertexData.clear();
		
		for(int i = 0; i < nodes.size(); i++)
		{
			SphereGraph.TriangleNode currentNode = nodes.get(i);
			
			vertexData.add(new Vertex3D(currentNode.v1, uvDummy, currentNode.faceNormal, colorDummy));
			indices[metaIndex] = metaIndex;
			metaIndex++;
			
			vertexData.add(new Vertex3D(currentNode.v2, uvDummy, currentNode.faceNormal, colorDummy));
			indices[metaIndex] = metaIndex;
			metaIndex++;
			
			vertexData.add(new Vertex3D(currentNode.v3, uvDummy, currentNode.faceNormal, colorDummy));
			indices[metaIndex] = metaIndex;
			metaIndex++;
		}
	}
	
	public int getTriangleCount(){
		return indices.length / 3;
	}
}