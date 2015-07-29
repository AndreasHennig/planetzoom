package planetZoooom.geometry;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Rectangle2D;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import planetZoooom.interfaces.IGameObjectListener;
import planetZoooom.utils.Info;

public class MasterSphere
{
	private static final int FIRST_CHECK = 4;
	private static final double VIEW_FRUSTUM_OFFSET = 1;
	private static final float VIEW_FRUSTUM_CHECK_OFFSET = 0.2f;
	static private final int ANGLE_TOLERANCE = 50;
	
	//To be inherited by superclass
	protected List<IGameObjectListener> listeners;
	private float[] positions;

	private Matrix4f modelMatrix;
	private int[] indices;

	private	Vector3f lhs, rhs, normal;
	private Vector3f v1,v2,v3,n1,n2,n3;
	
	private float radius;
	
	private int positionPointer; 
	private int triangleIndexCount;
	private int minTriangles;
	private VA va;

	private Matrix4f mv; //modelViewMatrix
	
	public MasterSphere(float radius, int minTriangles) 
	{
		positions = new float[minTriangles * 3 * 4 * 2];

		listeners = new ArrayList<>();
		
		
		lhs = new Vector3f();
		rhs = new Vector3f();
		normal = new Vector3f();
		mv = new Matrix4f();
		modelMatrix = new Matrix4f();
		v1 = new Vector3f();
		v2 = new Vector3f();
		v3 = new Vector3f();
		n1 = new Vector3f();
		n2 = new Vector3f();
		n3 = new Vector3f();
		
		positionPointer = 0;
		this.minTriangles = minTriangles;
		va = new VA(new float[]{}, 0, new int[]{});
		this.radius = radius;
		update();
	}
	
	public void addListener(IGameObjectListener listener) {
		listeners.add(listener);
	}
	
	public void update()
	{						
		Matrix4f.mul( Info.camera.getViewMatrix(),modelMatrix, mv);
		
		positionPointer = 0;
		//Indices - 3 make up a triangle

		int[] t = createOctahedron();
		triangleIndexCount = t.length;
		int depth = 0;
		while(triangleIndexCount < minTriangles * 3)
		{
			t = subdivide(t, triangleIndexCount, depth++);
			if(t.length == 0)
				break;
		}

		indices = t;

		va.update(positions, positionPointer, indices, triangleIndexCount);
	}
	
	
	
	public void render(int mode)
	{
		va.render(mode);
	}
	
	public int getTriangleCount()
	{
		return indices.length / 3;
	}
	
	public int getVertexCount()
	{
		return positionPointer / 3;
	}
	
	private int[] createOctahedron()
	{
		int[] indices = new int[]
				{
					2,4,1,
					2,0,4,
					4,3,1,
					4,0,3,
					5,2,1,
					5,0,2,
					3,5,1,
					3,0,5
				};
		
		writePosition((Vector3f) Vertex.left().scale(radius));
		writePosition((Vector3f) Vertex.right().scale(radius));
		writePosition((Vector3f) Vertex.up().scale(radius));
		writePosition((Vector3f) Vertex.down().scale(radius));
		writePosition((Vector3f) Vertex.front().scale(radius));
		writePosition((Vector3f) Vertex.back().scale(radius));
	
		return indices;
	}
	
	private int writePosition(Vector3f pos)
	{
		notifyListeners(pos);
		
		this.positions[positionPointer++] = pos.x;
		this.positions[positionPointer++] = pos.y;
		this.positions[positionPointer++] = pos.z;
		
		return (positionPointer-3) / 3;
	}
		
	private float[] getPositions(int[] triangleIndices)
	{
		float[] triangle = new float[9];
		
		for(int i = 0; i < 9; i++)
			triangle[i] = positions[(triangleIndices[i / 3] * 3) + (i % 3)];
		
		return triangle;
		
	}
	
	private int[] subdivide(int[] triangles, int triangleCount, int depth)
	{
		int[] newTriangles = new int[triangleCount * 4];
		int trianglePointer = 0;
		int[] triangleIndices;
		int[][] childIndices;
		
		for (int i = 0; i < triangleCount; i += 3)
		{
			triangleIndices = new int[]{triangles[i], triangles[i+1], triangles[i+2]};
			
			if (depth > FIRST_CHECK)
			{
				if(!isFacingTowardsCamera(triangleIndices))
					continue;
				if (!isInViewFrustum(triangleIndices))
					continue; // clip
			}

			
			childIndices = createChildTriangleIndices(triangleIndices, createChildVertices(triangleIndices));

			for (int j = 0; j < childIndices.length; j++)
			{
				newTriangles[trianglePointer++] = childIndices[j][0];
				newTriangles[trianglePointer++] = childIndices[j][1];
				newTriangles[trianglePointer++] = childIndices[j][2];
			}
		}
		
		this.triangleIndexCount = trianglePointer;
		return newTriangles;
	}
	
	

	
	private void setVec(Vector3f v, float x, float y, float z)
	{
		v.x = x;
		v.y = y;
		v.z = z;
	}
	private int[] createChildVertices(int[] triangleIndices)
	{	
		float[] positions = getPositions(triangleIndices);
		int[] newIndices = new int[3];
		setVec(v1, positions[0], positions[1], positions[2]);
		setVec(v2, positions[3], positions[4], positions[5]);
		setVec(v3, positions[6], positions[7], positions[8]);

		n1 = Vertex.lerp(v3, v1, 0.5f);
		n2 = Vertex.lerp(v1, v2, 0.5f);
		n3 = Vertex.lerp(v2, v3, 0.5f);
		
		n1.normalise();
		n2.normalise();
		n3.normalise();
		
		n1.scale(radius);
		n2.scale(radius);
		n3.scale(radius);
		
		newIndices[0] = writePosition(n1);
		newIndices[1] = writePosition(n2);
		newIndices[2] = writePosition(n3);
		
		return newIndices;
	} 
	
	private int[][] createChildTriangleIndices(int[] parentIndices, int[] childIndices)
	{
		return new int[][]
				{
					new int[] {parentIndices[0], childIndices[1], childIndices[0]},
					new int[] {childIndices[1], parentIndices[1], childIndices[2]},
					new int[] {childIndices[0], childIndices[2], parentIndices[2]},
					new int[] {childIndices[0], childIndices[1], childIndices[2]}
				};
	}


	
	private boolean isInViewFrustum(float[] positions) 
	{
		Matrix4f p = Info.projectionMatrix;
		float x,y,z;


		float[] w = new float[3];
		for(int i = 0; i < positions.length; i+=3)
		{
			//Object space -> world space -> camera space
			x = (mv.m00 * positions[i]) + (mv.m10 * positions[i+1]) + (mv.m20 * positions[i+2]) + (mv.m30);
			y = (mv.m01 * positions[i]) + (mv.m11 * positions[i+1]) + (mv.m21 * positions[i+2]) + (mv.m31);
			z = (mv.m02 * positions[i]) + (mv.m12 * positions[i+1]) + (mv.m22 * positions[i+2]) + (mv.m32);
			
			w[i/3] = -z;
			
			//camera space -> clip space
			x = (p.m00 * x) + (p.m10 * y) + (p.m20 * z) + (p.m30);
			y = (p.m01 * x) + (p.m11 * y) + (p.m21 * z) + (p.m31);
			z = (p.m02 * x) + (p.m12 * y) + (p.m22 * z) + (p.m32);

			x/= w[i/3];
			y/= w[i/3];
		
			
			if ((x <= VIEW_FRUSTUM_OFFSET && x >= -VIEW_FRUSTUM_OFFSET) && (y <= VIEW_FRUSTUM_OFFSET && y >= -VIEW_FRUSTUM_OFFSET) && z >= 0)
				return true;
								
			positions[i] = x;
			positions[i+1] = y;
			positions[i+2] = z;
		}
		
		if(intersectsNDCPlane(positions))
			return true;
		
		return false;
	}

	private boolean intersectsNDCPlane(float[] triangle)
	{
		float[] x = {-1, 1, 1, -1};
		float[] y = {1, 1, -1, -1};
 
		for(int i = 0; i < 3; i++)
		{
			if(lineIntersection(x[i], y[i], x[i+1], y[i+1], triangle[0], triangle[1], triangle[3], triangle[4]))
				return true;
			if(lineIntersection(x[i], y[i], x[i+1], y[i+1], triangle[3], triangle[4], triangle[6], triangle[7]))
				return true;
			if(lineIntersection(x[i], y[i], x[i+1], y[i+1], triangle[6], triangle[7], triangle[0], triangle[1]))
				return true;
		}
		
		if(lineIntersection(x[3], y[3], x[0], y[0], triangle[0], triangle[1], triangle[3], triangle[4]))
			return true;
		if(lineIntersection(x[3], y[3], x[0], y[0], triangle[3], triangle[4], triangle[6], triangle[7]))
			return true;
		if(lineIntersection(x[3], y[3], x[0], y[0], triangle[6], triangle[7], triangle[0], triangle[1]))
			return true;
		
		return false;
	}
	
	private boolean lineIntersection(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3)
	{
		double d = (x0-x1)*(y2-y3) - (y0-y1)*(x2-x3);
		if(d == 0) //parallel oder kollinear?
			return false;
		double xi = ((x2-x3)*(x0*y1-y0*x1)-(x0-x1)*(x2*y3-y2*x3)) / d;
		double yi = ((y2-y3)*(x0*y1-y0*x1)-(y0-y1)*(x2*y3-y2*x3)) / d;
		if(inIntervall(xi, yi, x0, y0, x1, y1))
			if(inIntervall(xi, yi, x2, y2, x3, y3))
				return true;
		
		return false;
	}
	
	private boolean inIntervall(double xi, double yi, float x0, float y0, float x1, float y1)
	{
		
		float minX = Math.min(x0, x1);
		float maxX = Math.max(x0, x1);

		float deltaX = maxX - minX;
		
		float minY = Math.min(y0, y1);
		float maxY = Math.max(y0, y1);
		
		float deltaY = maxY - minY;
		
		if(xi <= maxX + deltaX * VIEW_FRUSTUM_CHECK_OFFSET && xi >= minX - deltaX * VIEW_FRUSTUM_CHECK_OFFSET) {
			if(yi <=maxY  + deltaY * VIEW_FRUSTUM_CHECK_OFFSET && yi >= minY - deltaY * VIEW_FRUSTUM_CHECK_OFFSET) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isInViewFrustum(int[] triangleIndices)
	{	
		float[] positions = getPositions(triangleIndices);
		
		return isInViewFrustum(positions);
	}
		

	private boolean isFacingTowardsCamera(int[] triangleIndices) 
	{
		float[] a = getPositions(triangleIndices);
		setVec(v1, a[0], a[1], a[2]);
		
		Vector3f.sub(Info.camera.getPosition(), v1, n1);
		double angle = Vector3f.angle(n1, v1) * 180 / Math.PI;
		
		if (angle > 180)
			angle = 360 - angle;
		

		return angle < 90 + ANGLE_TOLERANCE;
	}
	
	public void notifyListeners(Vector3f v) 
	{
		for(IGameObjectListener listener : listeners)
			listener.vertexCreated(v);
	}
	
	public float getRadius()
	{
		return radius;
	}
	
	public class VA
	{
		private int vertexCount;
		private int indexCount;
		private int vaoHandle;
		private int vboHandle;
		private int nboHandle;
		private int iboHandle;
		
		public static final int VERTEX_LOCATION = 0;
		public static final int NORMAL_LOCATION = 1;
		
		public VA(float[] vertices, int vertexCount, int[] indices)
		{
			initBufferHandles();
			this.vertexCount = vertexCount;
			doBufferStuff(vertices, indices);
		}
		
		public void update(float[] vertices, int vertexCount, int[] indices, int indexCount)
		{
			this.vertexCount = vertexCount;
			this.indexCount = indexCount;
			doBufferStuff(vertices, indices);
		}
		
		private void initBufferHandles()
		{
			vaoHandle = glGenVertexArrays();
			vboHandle = glGenBuffers();
			nboHandle = glGenBuffers();
			iboHandle = glGenBuffers();
		}
		
		private void doBufferStuff(float[] vertices, int[] indices)
		{		
			glBindVertexArray(vaoHandle);
			
			FloatBuffer buffer = BufferUtils.createFloatBuffer(vertexCount);
			buffer.put(vertices, 0, vertexCount).flip();
			
			glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
			glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
			glVertexAttribPointer(VERTEX_LOCATION, 3, GL_FLOAT, false, 0, 0);
			glEnableVertexAttribArray(VERTEX_LOCATION);

			glBindBuffer(GL_ARRAY_BUFFER, nboHandle);
			glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
			glVertexAttribPointer(NORMAL_LOCATION, 3, GL_FLOAT, true, 0, 0);
			glEnableVertexAttribArray(NORMAL_LOCATION);
			
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboHandle);
			IntBuffer indexBuffer = BufferUtils.createIntBuffer(indexCount);
			indexBuffer.put(indices, 0, indexCount).flip();
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);	
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		}
		
		public void render(int mode)
		{
			glBindVertexArray(vaoHandle);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboHandle);
			glDrawElements(mode, indexCount, GL_UNSIGNED_INT, 0);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		}
	}
}