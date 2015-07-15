
package planetZoooom.geometry;

import java.util.ArrayList;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import planetZoooom.utils.Info;

public class SphereGraph {

	private int subdivisions;

	private ArrayList<TriangleNode> nodes;
	private Matrix4f modelViewProjectionMatrix = new Matrix4f();

	public ArrayList<TriangleNode> createGraph(int subdivisions, Vector3f cameraAngle) {
		modelViewProjectionMatrix = Matrix4f.mul(planetZoooom.utils.Info.projectionMatrix, planetZoooom.utils.Info.camera.getViewMatrix(), modelViewProjectionMatrix);
		Matrix4f.mul(modelViewProjectionMatrix, Info.planet.getMesh().getModelMatrix(), modelViewProjectionMatrix);

//		System.out.println(modelViewProjectionMatrix);

		this.subdivisions = subdivisions;

		nodes = new ArrayList<TriangleNode>();

		new TriangleNode(0, Vertex.up(), Vertex.front(), Vertex.right()); 	// front, up, right
		new TriangleNode(0, Vertex.up(), Vertex.left(), Vertex.front()); 		// front, up, left
		new TriangleNode(0, Vertex.front(), Vertex.down(), Vertex.right()); 	// front, down, right
		new TriangleNode(0, Vertex.front(), Vertex.left(), Vertex.down()); 	// front, down, left
		new TriangleNode(0, Vertex.back(), Vertex.up(), Vertex.right()); 		// back, up, right
		new TriangleNode(0, Vertex.back(), Vertex.left(), Vertex.up()); 		// back, up, left
		new TriangleNode(0, Vertex.down(), Vertex.back(), Vertex.right()); 	// back, down, right
		new TriangleNode(0, Vertex.down(), Vertex.left(), Vertex.back()); 	// back, down, left

		return nodes;
	}

	class TriangleNode {
		private int depth;
		public Vector3f v1, v2, v3;
		public Vector3f faceNormal;
		private static final int CHECK_INTERVAL = 4;
		private static final int FIRST_CHECK = 2;
		
		public TriangleNode(int currentDepth, Vector3f point1, Vector3f point2, Vector3f point3) {
			depth = currentDepth;
			//counter-clockwise
			v1 = point1;
			v2 = point2;
			v3 = point3;

			faceNormal = getSurfaceNormal();
			
			normalize();
			//noise();
			if (isVisible()) {
				if (depth == subdivisions) {
					// scale();
					addNodeToArrayList();
				} else {
					createChildren();
				}
			}
		}

		private void normalize() {
			v1.normalise();
			v2.normalise();
			v3.normalise();
		}

		private void createChildren() {
			new TriangleNode(depth + 1, v1, Vertex.lerp(v1, v2, 0.5f), Vertex.lerp(v1, v3, 0.5f)); //top
			new TriangleNode(depth + 1, v2, Vertex.lerp(v2, v3, 0.5f), Vertex.lerp(v1, v2, 0.5f)); //left
			new TriangleNode(depth + 1, v3, Vertex.lerp(v1, v3, 0.5f), Vertex.lerp(v2, v3, 0.5f)); //right
			new TriangleNode(depth + 1, Vertex.lerp(v1, v2, 0.5f), Vertex.lerp(v2, v3, 0.5f), Vertex.lerp(v1, v3, 0.5f)); //center
		}

		private void addNodeToArrayList() {
			nodes.add(this);
		}

		private boolean isVisible() {
			
			
			if (depth % CHECK_INTERVAL == FIRST_CHECK){
				boolean isInViewFrustum = isInViewFrustum();
				boolean isFacingTowardsCamera = isFacingTowardsCamera();
				return isInViewFrustum && isFacingTowardsCamera;
			}
			else return true;
		}

		private boolean isInViewFrustum() {
			// TODO Auto-generated method stub
			return true;

			/*
			 * Frustum: mit Projektionsmatrix -1<x<1; -1<y<1
			 * jeden Punkt des Dreiecks
			 */
		}

		private Vector3f getSurfaceNormal()
		{
			Vector3f lhs = new Vector3f();
			Vector3f rhs = new Vector3f();
			Vector3f result = new Vector3f();

			Vector3f.sub(v2, v1, lhs);
			Vector3f.sub(v3, v1, rhs);
			Vector3f.cross(lhs, rhs, result);
			
			return result;
		}
		
		private boolean isFacingTowardsCamera() {
			double angle = Vector3f.angle(Info.camera.getLookAt(), faceNormal) * 180 / Math.PI;

			float angleTolerance = 90 / (depth + 2); //as we go deeper, we need less tolerance 
			//float angleTolerance = 0; //everything behind 90 degrees gets cut off. problems with noise?

			return angle > 90 - angleTolerance && angle < 270 + angleTolerance;
		}
	}
}
