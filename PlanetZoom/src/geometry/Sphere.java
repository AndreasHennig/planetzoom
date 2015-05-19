//http://www.binpress.com/tutorial/creating-an-octahedron-sphere/162

package geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engine.FirstPersonCamera;
import engine.GameObject3D;

public class Sphere extends GameObject3D
{
	final static int AMOUNT_VALUES_PER_COLOR = 3;
	final static int AMOUNT_VALUES_PER_VERTEX = 3;
	public final static int MAX_SUBDIVISIONS = 9;
	public final static int MIN_SUBDIVISIONS = 1;
		
	private int subdivisions;
	private float radius;
	
	private Vector3f[] vertices;
	private Vector4f vertexColor;
	
	public float getRadius() {
		return radius;
	}
	
	private static Vector3f[] directions = 
	{
		Vertex3D.left(),
		Vertex3D.back(),
		Vertex3D.right(),
		Vertex3D.front()
	};
	
	public Sphere() {
		this(1, new Vector4f(1, 1, 1, 1), 1);
	}
	
	public Sphere(int subdivisions, Vector4f color, float radius)
	{
		this.vertexColor = color;
		this.radius = radius;
		
		update(subdivisions);
	}
	
	public void update(int subdivisions) {
		if(subdivisions == this.subdivisions)
			return;
		
		this.subdivisions = subdivisions > MAX_SUBDIVISIONS ? MAX_SUBDIVISIONS : subdivisions;
		
		int resolution = 1 << this.subdivisions;
		vertices =  new Vector3f[(resolution + 1) * (resolution + 1) * 4 - (resolution * 2 - 1) * 3];
		indices = new int[(1 << (this.subdivisions * 2 + 3)) * 3];
		
		createOctahedron(resolution);
		
		// very slow!! TO FIX
		vertexData.clear();
		
		for(int i = 0; i < vertices.length; i++)
			vertexData.add(new Vertex3D(vertices[i], new Vector2f(0.0f, 0.0f), vertices[i], vertexColor));	
	}
	
	/**
	 * Gives a value between 0 and 1 that depends on the distance between
	 * a point and the sphere. Can be used to compute a subdivision that
	 * looks "okay" from to given point.
	 * The distance gets clamped to a max value.
	 * @param distanceToSphere
	 * @return value between 0 and 1
	 */
	public float getSubdivisionCoefficient(float distanceToSphere) {
		// distances over 100 don't affect the planets resolution
		float maxDistance = 100;
		distanceToSphere = distanceToSphere > maxDistance ? maxDistance : distanceToSphere;
		
		float subdivisionCoefficient = (maxDistance - distanceToSphere) / 100;
		
		// (100 - x) ^ 5 / (100 ^ 5)
		float curveSlope = 3f;
		subdivisionCoefficient = (float) (Math.pow(maxDistance - distanceToSphere, curveSlope) / Math.pow(maxDistance, curveSlope));
		
		return subdivisionCoefficient;
	}
	
	private void createOctahedron(int resolution)
	{		
		int v = 0;
		int vBottom = 0;
		int t = 0;
		
		for(int i = 0; i < 4; i++)
		{
			vertices[v++] = Vertex3D.down();
		}
		
		//LOWERSPHERE
		for(int i = 1; i <= resolution; i++)
		{
			float progress = (float)i / resolution;
			Vector3f from;
			Vector3f to;
			vertices[v++] = to = Vertex3D.lerp(Vertex3D.down(), Vertex3D.front(), progress);
			
			for(int d = 0; d < 4; d++)
			{
				from = to;
				to = Vertex3D.lerp(Vertex3D.down(), directions[d], progress);
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
			vertices[v++] = to = Vertex3D.lerp(Vertex3D.up(), Vertex3D.front(), progress);
			
			for(int d = 0; d < 4; d++)
			{
				from = to;
				to = Vertex3D.lerp(Vertex3D.up(), directions[d], progress);
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
			
			vertices[v++] = Vertex3D.up();
		}
		
		for(int i = 0; i < vertices.length; i++)
		{
			vertices[i].normalise();
			
			if(radius != 1)
				vertices[i].scale(radius);
		}
	}
	
	private int createVertexLine(Vector3f from, Vector3f to, int steps, int v)
	{
		for(int i = 1; i <= steps; i++)
		{
			vertices[v++] = Vertex3D.lerp(from, to, (float)i / steps);
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