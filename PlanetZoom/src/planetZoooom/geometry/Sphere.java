//http://www.binpress.com/tutorial/creating-an-octahedron-sphere/162

package planetZoooom.geometry;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Sphere extends GameObject3D {
	public final static int MAX_SUBDIVISIONS = 7;
	public final static int MIN_SUBDIVISIONS = 7;

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
		if(subdivisions > MAX_SUBDIVISIONS) this.subdivisions = MAX_SUBDIVISIONS;
		else if (subdivisions < MIN_SUBDIVISIONS) this.subdivisions = MIN_SUBDIVISIONS;
		else this.subdivisions = subdivisions;

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

		Vertex3D v1, v2, v3;

		for(int i = 0; i < nodes.size(); i++)
		{
			SphereGraph.TriangleNode currentNode = nodes.get(i);

			v1 = new Vertex3D(currentNode.v1, uvDummy, currentNode.faceNormal, colorDummy);
			v1.getPosition().scale(radius);
			notifyListeners(v1);
			vertexData.add(v1);
			indices[metaIndex] = metaIndex;
			metaIndex++;

			v2 = new Vertex3D(currentNode.v2, uvDummy, currentNode.faceNormal, colorDummy);
			v2.getPosition().scale(radius);
			notifyListeners(v2);
			vertexData.add(v2);
			indices[metaIndex] = metaIndex;
			metaIndex++;

			v3 = new Vertex3D(currentNode.v3, uvDummy, currentNode.faceNormal, colorDummy);
			v3.getPosition().scale(radius);
			notifyListeners(v3);
			vertexData.add(v3);
			indices[metaIndex] = metaIndex;
			metaIndex++;
		}
	}

	public int getTotalTriangleCount(){
		return (int) (8 * Math.pow(4, subdivisions));
	}

	public int getActualTriangleCount(){
		return indices.length / 3;
	}
}
