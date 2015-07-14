package planetZoooom.geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import planetZoooom.engine.MeshObject;
import planetZoooom.engine.VertexArray;

public class StaticSphere extends MeshObject
{
	private float radius;
	private Vector3f[] verticesVec;
	private Vector3f[] normalsVec;
	private Vector2f[] uvCoordsVec;
	
	private static Vector3f[] directions = { Vertex.left(), Vertex.back(),
			Vertex.right(), Vertex.front() };

	public StaticSphere()
	{
		this(4, 1);
	}

	public StaticSphere(int subdivisions, float radius)
	{
		this.radius = radius;
		int resolution = 1 << subdivisions;
		verticesVec = new Vector3f[(resolution + 1) * (resolution + 1) * 4 - (resolution * 2 - 1) * 3];
		normalsVec = new Vector3f[verticesVec.length];
		uvCoordsVec = new Vector2f[verticesVec.length];
		indices = new int[(1 << (subdivisions * 2 + 3)) * 3];
	
		createOctahedron(resolution);
		normalizeVerticesAndCreateNormals();
		createUVs();
		applyMeshModifications();
		createVerticesAndUVFloatArrays();
	
		mesh = new VertexArray(vertices, normals, uvCoords, indices);
	}

	private void createOctahedron(int resolution)
	{
		int v = 0;
		int vBottom = 0;
		int t = 0;
		for (int i = 0; i < 4; i++)
		{
			verticesVec[v++] = Vertex.down();
		}
		// LOWERSPHERE
		for (int i = 1; i <= resolution; i++)
		{
			float progress = (float) i / resolution;
			Vector3f from;
			Vector3f to;
			verticesVec[v++] = to = Vertex.lerp(Vertex.down(),
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
			verticesVec[v++] = to = Vertex.lerp(Vertex.up(), Vertex.front(),
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
			verticesVec[v++] = Vertex.up();
		}
	}

	private int createVertexLine(Vector3f from, Vector3f to, int steps, int v)
	{
		for (int i = 1; i <= steps; i++)
		{
			verticesVec[v++] = Vertex.lerp(from, to, (float) i / steps);
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

	public void normalizeVerticesAndCreateNormals()
	{
		for (int i = 0; i < verticesVec.length; i++)
		{
			verticesVec[i].normalise();
			normals = new float[verticesVec.length * 3];
			normals[i * 3    ] = verticesVec[i].x;
			normals[i * 3 + 1] = verticesVec[i].y;
			normals[i * 3 + 2] = verticesVec[i].z;
		}
	}
	
	private void createUVs()
	{
		float previousX = 1f;
		for (int i = 0; i < verticesVec.length; i++)
		{
			Vector3f vector = new Vector3f(verticesVec[i]);
			if (vector.x == previousX)
			{
				uvCoordsVec[i - 1].x = 1f;
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
			uvCoordsVec[i] = texCoords;
		}
		uvCoordsVec[verticesVec.length - 4].x = uvCoordsVec[0].x = 0.125f;
		uvCoordsVec[verticesVec.length - 3].x = uvCoordsVec[1].x = 0.375f;
		uvCoordsVec[verticesVec.length - 2].x = uvCoordsVec[2].x = 0.625f;
		uvCoordsVec[verticesVec.length - 1].x = uvCoordsVec[3].x = 0.875f;
	}

	public void applyMeshModifications()
	{
		for(int i = 0; i < verticesVec.length; i++)
			verticesVec[i].scale((float) (radius));
	}
	
	private void createVerticesAndUVFloatArrays()
	{
		vertices = new float[verticesVec.length * 3]; 
		uvCoords = new float[verticesVec.length * 2];
		for (int i = 0; i < verticesVec.length; i++)
		{
			vertices[i * 3    ] = verticesVec[i].x;
			vertices[i * 3 + 1] = verticesVec[i].y;
			vertices[i * 3 + 2] = verticesVec[i].z;
					
			uvCoords[i * 2    ] = uvCoordsVec[i].x;
			uvCoords[i * 2 + 1] = uvCoordsVec[i].y;
		}
	}
	
	public void render(int mode)
	{
		mesh.render(mode);
	}
	
	public float getRadius()
	{
		return radius;
	}
}
