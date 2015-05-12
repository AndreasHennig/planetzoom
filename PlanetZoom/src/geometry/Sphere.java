//http://www.binpress.com/tutorial/creating-an-octahedron-sphere/162

package geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engine.GameObject3D;

public class Sphere extends GameObject3D
{
	final static int AMOUNT_VALUES_PER_COLOR = 3;
	final static int AMOUNT_VALUES_PER_VERTEX = 3;
	final static int MAX_SUBDIVISIONS = 11;
		
	private int subdivisions = 1;
	
	private Vector3f[] positions;
	
	private static Vector3f[] directions = 
	{
		Vertex3D.left,
		Vertex3D.back,
		Vertex3D.right,
		Vertex3D.forward
	};
	
	public Sphere(int subdivisions)
	{
		if(subdivisions > MAX_SUBDIVISIONS)
		{
			this.subdivisions = MAX_SUBDIVISIONS;
		}
		
		else
		{
			this.subdivisions = subdivisions;
		}
		
		int resolution = 1 << this.subdivisions;
		positions =  new Vector3f[(resolution + 1) * (resolution + 1) * 4 - (resolution * 2 - 1) * 3];
		indices = new int[(1 << (this.subdivisions * 2 + 3)) * 3];
		
		createOctahedron(resolution);
		
		for(int i = 0; i < positions.length; i++)
			vertices.add(new Vertex3D(positions[i], new Vector2f(0.0f, 0.0f), positions[i]));	
	}
	
	
	private void createOctahedron(int resolution)
	{		
		int v = 0;
		int vBottom = 0;
		int t = 0;
		
		for(int i = 0; i < 4; i++)
		{
			positions[v++] = Vertex3D.down;
		}
		
		//LOWERSPHERE
		for(int i = 1; i <= resolution; i++)
		{
			float progress = (float)i / resolution;
			Vector3f from;
			Vector3f to;
			positions[v++] = to = Vertex3D.lerp(Vertex3D.down, Vertex3D.forward, progress);
			
			for(int d = 0; d < 4; d++)
			{
				from = to;
				to = Vertex3D.lerp(Vertex3D.down, directions[d], progress);
				t = createLowerStrip(i, v, vBottom, t);
				v = createVertexLine(from, to, i, v);
				vBottom += i > 1 ? (i - 1) : 1;
			}
			vBottom = v - 1 - i * 4;
		}
		
		//UPPERSPHERE
		for(int i = resolution - 1; i >= 1; i--)
		{
			float progress = (float)i / resolution;
			Vector3f from;
			Vector3f to;
			positions[v++] = to = Vertex3D.lerp(Vertex3D.up, Vertex3D.forward, progress);
			
			for(int d = 0; d < 4; d++)
			{
				from = to;
				to = Vertex3D.lerp(Vertex3D.up, directions[d], progress);
				t = createUpperStrip(i, v, vBottom, t);
				v = createVertexLine(from, to, i, v);
				vBottom += i + 1;
			}
			vBottom = v - 1 - i * 4;
		}
	
		for(int i = 0; i < 4; i++)
		{
			indices[t++] = vBottom;
			indices[t++] = v;
			indices[t++] = ++vBottom;
			
			positions[v++] = Vertex3D.up;
		}
		
		for(int i = 0; i < positions.length; i++)
		{
			positions[i].normalise();
		}
		
	}
	
	private int createVertexLine(Vector3f from, Vector3f to, int steps, int v)
	{
		for(int i = 1; i <= steps; i++)
		{
			positions[v++] = Vertex3D.lerp(from, to, (float)i / steps);
		}
		
		return v;
	}
	
	private int createUpperStrip(int steps, int vTop, int vBottom, int t)
	{
		indices[t++] = vBottom;
		indices[t++] = vTop - 1;
		indices[t++] = ++vBottom;
		
		for(int i = 1; i <= steps; i++)
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
		for(int i = 1; i < steps; i++)
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
}