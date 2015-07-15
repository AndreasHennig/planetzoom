package planetZoooom.geometry;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import planetZoooom.engine.VertexArray;
import planetZoooom.utils.Info;

public class MasterSphere
{
	private static final int CHECK_INTERVAL = 4;
	private static final int FIRST_CHECK = 2;
	private static final int ARRAY_SIZE = 5000000;
	private static final double VIEW_FRUSTUM_OFFSET = 0.3; //Percent
	
	
	//To be inherited by superclass
	private float[] positions;

	private Matrix4f modelMatrix;
	private int[] indices;

	private	Vector3f lhs, rhs, normal;
	private Vector3f v1,v2,v3,n1,n2,n3;
	
	private float radius;
	
	private int positionPointer; 
	private int triangleIndexCount;
	private boolean firstCheck;
	private VA va;

	private Matrix4f mv; //modelViewMatrix
	
	public MasterSphere(float radius, int minTriangles) 
	{
		positions = new float[ARRAY_SIZE];

		
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
		va = new VA(new float[]{}, 0, new int[]{});
		this.radius = radius;
		update();
	}
	
	
	public void update()
	{						
		Matrix4f.mul(modelMatrix, Info.camera.getViewMatrix(), mv);
		
		positionPointer = 0;
		//Indices - 3 make up a triangle
		firstCheck = true;
		int[] t = createOctahedron();
		triangleIndexCount = t.length;
//		for(int depth = 0; depth < subdivisions; depth++)
		int depth = 0;
			while(t.length < 10000 * 3)
			{
				t = subdivide(t, triangleIndexCount, depth++);
				if(depth > 14)
					break;
			}
//			System.out.println("depth: " + depth);
		indices = t;

		va.update(positions, positionPointer, indices);
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
		
		writePosition(Vertex.left());
		writePosition(Vertex.right());
		writePosition(Vertex.up());
		writePosition(Vertex.down());
		writePosition(Vertex.front());
		writePosition(Vertex.back());
		
		return indices;
	}
	
	private int writePosition(Vector3f pos)
	{
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
			
			if (depth % CHECK_INTERVAL == FIRST_CHECK)
			{
				if(!isFacingTowardsCamera(triangleIndices))
					continue;
			}
			if(newTriangles.length > 10000)
				if (!isInViewFrustum(triangleIndices))
					continue; // clip
			
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
		
//		n1.scale(radius);
//		n2.scale(radius);
//		n3.scale(radius);

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

	
	private boolean isInViewFrustum(float a, float b, float c) 
	{
		Matrix4f p = Info.projectionMatrix;
		float x,y,z,w;
		float d = 1;

		//Object space -> world space -> camera space
		x = (mv.m00 * a) + (mv.m10 * b) + (mv.m20 * c) + (mv.m30 * d);
		y = (mv.m01 * a) + (mv.m11 * b) + (mv.m21 * c) + (mv.m31 * d);
		z = (mv.m02 * a) + (mv.m12 * b) + (mv.m22 * c) + (mv.m32 * d);

		w= -z;
		if(firstCheck)
		{
			w*= 1 + VIEW_FRUSTUM_OFFSET *4;	
			firstCheck = false;
		}
		else
			w*= 1 + VIEW_FRUSTUM_OFFSET;

		//camera space -> clip space
		x = (p.m00 * x) + (p.m10 * y) + (p.m20 * z) + (p.m30 * d);
		y = (p.m01 * x) + (p.m11 * y) + (p.m21 * z) + (p.m31 * d);
		z = (p.m02 * x) + (p.m12 * y) + (p.m22 * z) + (p.m32 * d);

		return (x <= w && x >= -w) && (y <= w && y >= -w);
	}
	
	private boolean isInViewFrustum(int[] triangleIndices)
	{	
		float[] positions = getPositions(triangleIndices);
		
		if(isInViewFrustum(positions[0], positions[1], positions[2]))
			return true;
		if(isInViewFrustum(positions[3], positions[4], positions[5]))
			return true;
		if(isInViewFrustum(positions[6], positions[7], positions[8]))
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
	
	
	public class VA
	{
		private int count;
		private int vertexCount;
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
		
		public void update(float[] vertices, int vertexCount, int[] indices)
		{
			this.vertexCount = vertexCount;
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
			count = indices.length;
			
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
			IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
			indexBuffer.put(indices).flip();
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);	
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		}
		
		public void render(int mode)
		{
			glBindVertexArray(vaoHandle);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboHandle);
			glDrawElements(mode, count, GL_UNSIGNED_INT, 0);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		}
	}
}
