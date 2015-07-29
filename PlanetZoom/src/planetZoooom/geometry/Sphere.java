package planetZoooom.geometry;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.awt.geom.Line2D;
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

public class Sphere
{
	private static final int CHECK_INTERVAL = 4;
	private static final int FIRST_CHECK = 3;
	private static final double VIEW_FRUSTUM_OFFSET = 2; 
	
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

	private int currentDepth;
	private int minTriangles;
	private VA va;

	private Matrix4f modelViewMatrix;
	
	public Sphere(float radius, int minTriangles) 
	{
		positions = new float[minTriangles * 12 * 2];

		listeners = new ArrayList<>();
		
		
		lhs = new Vector3f();
		rhs = new Vector3f();
		normal = new Vector3f();
		modelViewMatrix = new Matrix4f();
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
		Matrix4f.mul( Info.camera.getViewMatrix(), modelMatrix, modelViewMatrix);
		
		positionPointer = 0;
		
		int[] t = createOctahedron();
		triangleIndexCount = t.length;
		
		int depth = 0;
		while(triangleIndexCount < (minTriangles * 3))
		{
			t = subdivide(t, triangleIndexCount, depth++);
			if(t.length == 0)
				break;
		}
		currentDepth = depth;
		
		indices = t;

		va.update(positions, positionPointer, indices, triangleIndexCount);
	}
	
	public int getSubdivisions()
	{
		return currentDepth;
	}
	public int getTotalTriangleCount()
	{
		int totalTriangles = 8;
		for(int i = 0; i < currentDepth; i++)
			totalTriangles = totalTriangles << 2;
				
		return totalTriangles;
	}
	
	public void render(int mode)
	{
		va.render(mode);
	}
	
	public Matrix4f getModelMatrix()
	{
		return modelMatrix;
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
			
//			if(newTriangles.length > minTriangles) //Most likely last subdivide
//			{
//				if(!isFacingTowardsCamera(triangleIndices))
//					continue;
//				if (!isInViewFrustum(triangleIndices))
//					continue; // clip
//			}
//			
			if (depth % CHECK_INTERVAL == 0 || depth == FIRST_CHECK)
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
				
//		uvs[0] = new Vector2f((v3.getUv().x + v1.getUv().x)/2.0f, (v3.getUv().y + v1.getUv().y)/2.0f);
//		uvs[1] = new Vector2f((v1.getUv().x + v2.getUv().x)/2.0f, (v1.getUv().y + v2.getUv().y)/2.0f);
//		uvs[2] = new Vector2f((v3.getUv().x + v2.getUv().x)/2.0f, (v3.getUv().y + v2.getUv().y)/2.0f);
		
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

	private boolean isInViewFrustum(float a, float b, float c, int depth) 
	{
		Matrix4f p = Info.projectionMatrix;
		float x,y,z,w;
		float d = 1;

		//Object space -> world space -> camera space
		x = (modelViewMatrix.m00 * a) + (modelViewMatrix.m10 * b) + (modelViewMatrix.m20 * c) + (modelViewMatrix.m30 * d);
		y = (modelViewMatrix.m01 * a) + (modelViewMatrix.m11 * b) + (modelViewMatrix.m21 * c) + (modelViewMatrix.m31 * d);
		z = (modelViewMatrix.m02 * a) + (modelViewMatrix.m12 * b) + (modelViewMatrix.m22 * c) + (modelViewMatrix.m32 * d);

		w= -z;
		
		if(depth < 2)
			w*= 1 + VIEW_FRUSTUM_OFFSET/1;
		else if (depth < 8)
			w*= 1 + VIEW_FRUSTUM_OFFSET/1.5;
		else if (depth < 12)
			w*= 1 + VIEW_FRUSTUM_OFFSET/3;
		
		

		//camera space -> clip space
		x = (p.m00 * x) + (p.m10 * y) + (p.m20 * z) + (p.m30 * d);
		y = (p.m01 * x) + (p.m11 * y) + (p.m21 * z) + (p.m31 * d);
		z = (p.m02 * x) + (p.m12 * y) + (p.m22 * z) + (p.m32 * d);

		return (x <= w && x >= -w) && (y <= w && y >= -w);
	}

	
	//TODO implement correct frustum culling maybe sutherland hodgen style
	//http://de.slideshare.net/Tejasmistry19/clipping-algorithm-in-computer-graphics
	
	private boolean isInViewFrustum(int[] triangleIndices) 
	{
		
		float[] positions = getPositions(triangleIndices);
		
		Matrix4f p = Info.projectionMatrix;
		float x,y,z;


		float[] w = new float[3];
		for(int i = 0; i < positions.length; i+=3)
		{
			//Object space -> world space -> camera space
			x = (modelViewMatrix.m00 * positions[i]) + (modelViewMatrix.m10 * positions[i+1]) + (modelViewMatrix.m20 * positions[i+2]) + (modelViewMatrix.m30);
			y = (modelViewMatrix.m01 * positions[i]) + (modelViewMatrix.m11 * positions[i+1]) + (modelViewMatrix.m21 * positions[i+2]) + (modelViewMatrix.m31);
			z = (modelViewMatrix.m02 * positions[i]) + (modelViewMatrix.m12 * positions[i+1]) + (modelViewMatrix.m22 * positions[i+2]) + (modelViewMatrix.m32);
			
			w[i/3] = -z;
			
			//camera space -> clip space
			x = (p.m00 * x) + (p.m10 * y) + (p.m20 * z) + (p.m30);
			y = (p.m01 * x) + (p.m11 * y) + (p.m21 * z) + (p.m31);
			z = (p.m02 * x) + (p.m12 * y) + (p.m22 * z) + (p.m32);

			x/= w[i/3];
			y/= w[i/3];
		

			if ((x <= VIEW_FRUSTUM_OFFSET && x >= -VIEW_FRUSTUM_OFFSET) && (y <= VIEW_FRUSTUM_OFFSET && y >= -VIEW_FRUSTUM_OFFSET) && z > 0)
				return true;
								
			positions[i] = x;
			positions[i+1] = y;
			positions[i+2] = z;
		}
		
//		if(intersectsNDCPlane(positions))
//			return true;
		
		return false;
	}

	private boolean intersectsNDCPlane(float[] ndc)
	{
		float x0, x1;
		float y0, y1;
		for(int i = 0; i < ndc.length-3; i+=3)
		{
			x0 = ndc[i];
			y0 = ndc[i+1];
			x1 = ndc[i+3];
			y1 = ndc[i+4];
			if(foo(x0, y0, x1, y1))
				return true;
		}
		if(foo(ndc[6], ndc[7], ndc[0], ndc[1]))
			return true;
		
		return false;
	}
	
	private Rectangle2D NDCPlane = new Rectangle2D.Float(-1, -1, 2, 2);
	private Line2D l1 = new Line2D.Float();
	private boolean foo(float x0, float y0, float x1, float y1)
	{
		l1.setLine(x0,y0,x1,y1);
		if(l1.intersects(NDCPlane))
			return true;
		
		return false;
	}
		

	private boolean isFacingTowardsCamera(int[] triangleIndices) 
	{
		float[] a = getPositions(triangleIndices);
		setVec(v1, a[0], a[1], a[2]);
		setVec(v2, a[3], a[4], a[5]);
		setVec(v3, a[6], a[7], a[8]);
		
		Vector3f.sub(v3, v1, lhs);
		Vector3f.sub(v2, v1, rhs);
		Vector3f.cross(lhs, rhs, normal);
		
		Vector3f.sub(v1, Info.camera.getPosition(), n1);
		double angle = Vector3f.angle(n1, normal) * 180 / Math.PI;

		
		//float angleTolerance = 90 / (subdivions + 2); //as we go deeper, we need less tolerance 
		float angleTolerance = 10; //everything behind 90 degrees gets cut off. problems with noise?

		return !(angle > 90 + angleTolerance && angle < 270 - angleTolerance);
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
		public static final int NORMAL_LOCATION = 2;
		
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