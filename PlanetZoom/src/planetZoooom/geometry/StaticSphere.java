package planetZoooom.geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class StaticSphere extends GameObject3D
{
	private float radius;
	private Vector3f[] vertices;
	private Vector3f[] normals;
	private Vector2f[] uv;

	private static Vector3f[] directions = { Vertex.left(), Vertex.back(),
			Vertex.right(), Vertex.front() };

	public StaticSphere()
	{
		this(4, 1);
		createVAO();
	}

	public StaticSphere(int subdivisions, float radius)
	{
		this.radius = radius;
		int resolution = 1 << subdivisions;
		vertices = new Vector3f[(resolution + 1) * (resolution + 1) * 4 - (resolution * 2 - 1) * 3];
		normals = new Vector3f[vertices.length];
		uv = new Vector2f[vertices.length];
		indices = new int[(1 << (subdivisions * 2 + 3)) * 3];
	
		createOctahedron(resolution);
		normalizeVerticesAndCreateNormals();
		createUVs();
		applyMeshModifications();
		addVertexDataToGameObject();
	
		createVAO();
	}

	public void normalizeVerticesAndCreateNormals()
	{
		for (int i = 0; i < vertices.length; i++)
		{
			vertices[i].normalise();
			normals[i] = (Vector3f) new Vector3f(vertices[i]);
		}
	}

	public void applyMeshModifications()
	{
		for(int i = 0; i < vertices.length; i++)
			vertices[i].scale((float) (radius));
	}
	
	private void createOctahedron(int resolution)
	{
		int v = 0;
		int vBottom = 0;
		int t = 0;
		for (int i = 0; i < 4; i++)
		{
			vertices[v++] = Vertex.down();
		}
		// LOWERSPHERE
		for (int i = 1; i <= resolution; i++)
		{
			float progress = (float) i / resolution;
			Vector3f from;
			Vector3f to;
			vertices[v++] = to = Vertex.lerp(Vertex.down(),
					Vertex.front(), progress);
			for (int d = 0; d < 4; d++)
			{
				from = to;
				to = Vertex.lerp(Vertex.down(), directions[d], progress);
				t = createLowerStrip(i, v, vBottom, t);
				v = createVertexLine(from, to, i, v);
				vBottom += i > 1 ? (i - 1) : 1;
			}
			vBottom = v - 1 - i * 4;
		}
		// UPPERSPHERE
		for (int i = resolution - 1; i >= 1; i--)
		{
			float progress = (float) i / resolution;
			Vector3f from;
			Vector3f to;
			vertices[v++] = to = Vertex.lerp(Vertex.up(), Vertex.front(),
					progress);
			for (int d = 0; d < 4; d++)
			{
				from = to;
				to = Vertex.lerp(Vertex.up(), directions[d], progress);
				t = createUpperStrip(i, v, vBottom, t);
				v = createVertexLine(from, to, i, v);
				vBottom += i + 1;
			}
			vBottom = v - 1 - i * 4;
		}
		for (int i = 0; i < 4; i++)
		{
			indices[t++] = vBottom;
			indices[t++] = v;
			indices[t++] = ++vBottom;
			vertices[v++] = Vertex.up();
		}
	}

	private int createVertexLine(Vector3f from, Vector3f to, int steps, int v)
	{
		for (int i = 1; i <= steps; i++)
		{
			vertices[v++] = Vertex.lerp(from, to, (float) i / steps);
		}
		return v;
	}

	private int createUpperStrip(int steps, int vTop, int vBottom, int t)
	{
		indices[t++] = vBottom;
		indices[t++] = vTop - 1;
		indices[t++] = ++vBottom;
		for (int i = 1; i <= steps; i++)
		{
			indices[t++] = vTop - 1;
			indices[t++] = vTop;
			indices[t++] = vBottom;
			indices[t++] = vBottom;
			indices[t++] = vTop++;
			indices[t++] = ++vBottom;
		}
		return t;
	}

	private int createLowerStrip(int steps, int vTop, int vBottom, int t)
	{
		for (int i = 1; i < steps; i++)
		{
			indices[t++] = vBottom;
			indices[t++] = vTop - 1;
			indices[t++] = vTop;
			indices[t++] = vBottom++;
			indices[t++] = vTop++;
			indices[t++] = vBottom;
		}
		indices[t++] = vBottom;
		indices[t++] = vTop - 1;
		indices[t++] = vTop;
		return t;
	}

	private void createUVs()
	{
		float previousX = 1f;
		for (int i = 0; i < vertices.length; i++)
		{
			Vector3f vector = new Vector3f(vertices[i]);
			if (vector.x == previousX)
			{
				uv[i - 1].x = 1f;
			}
			previousX = vector.x;
			Vector2f texCoords = new Vector2f();
			texCoords.x = (float) Math.atan2(vector.x, vector.z)
					/ (-2f * (float) Math.PI);
			if (texCoords.x < 0)
			{
				texCoords.x += 1f;
			}
			texCoords.y = (float) Math.asin(vector.y) / (float) Math.PI + 0.5f;
			uv[i] = texCoords;
		}
		uv[vertices.length - 4].x = uv[0].x = 0.125f;
		uv[vertices.length - 3].x = uv[1].x = 0.375f;
		uv[vertices.length - 2].x = uv[2].x = 0.625f;
		uv[vertices.length - 1].x = uv[3].x = 0.875f;
	}

	private void addVertexDataToGameObject()
	{
		for (int i = 0; i < vertices.length; i++)
		{
			vertexData.add(new Vertex(vertices[i], uv[i], normals[i]));
		}
	}
	
	public float getRadius()
	{
		return radius;
	}
}
