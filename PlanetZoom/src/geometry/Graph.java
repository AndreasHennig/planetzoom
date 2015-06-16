package geometry;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import engine.utils.GameUtils;

public class Graph {

	int subdivisions;
	Vector3f cameraAngle; // is this enough or should we consider the planet being behind the camera?
	ArrayList<Node> nodes;
		
	public ArrayList<Node> createGraph(int subdivisions, Vector3f cameraAngle) {
	
		this.subdivisions = subdivisions;
		this.cameraAngle = cameraAngle;
		
		nodes = new ArrayList<Node>();
		
		new Node(0, Vertex3D.up(), Vertex3D.front(), Vertex3D.right()); 	// front, up, right
		new Node(0, Vertex3D.up(), Vertex3D.left(), Vertex3D.front()); 		// front, up, left
		new Node(0, Vertex3D.front(), Vertex3D.down(), Vertex3D.right()); 	// front, down, right
		new Node(0, Vertex3D.front(), Vertex3D.left(), Vertex3D.down()); 	// front, down, left
		new Node(0, Vertex3D.back(), Vertex3D.up(), Vertex3D.right()); 		// back, up, right
		new Node(0, Vertex3D.back(), Vertex3D.left(), Vertex3D.up()); 		// back, up, left
		new Node(0, Vertex3D.down(), Vertex3D.back(), Vertex3D.right()); 	// back, down, right
		new Node(0, Vertex3D.down(), Vertex3D.left(), Vertex3D.back()); 	// back, down, left
		
		return nodes;
	}

	class Node {
		int depth;
		Vector3f v1, v2, v3;
		Vector3f faceNormal = new Vector3f();

		public Node(int currentDepth, Vector3f point1, Vector3f point2, Vector3f point3) {
			depth = currentDepth;
			//counter-clockwise
			v1 = point1;
			v2 = point2;
			v3 = point3;

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
			new Node(depth + 1, v1, Vertex3D.lerp(v1, v2, 0.5f), Vertex3D.lerp(v1, v3, 0.5f)); //top
			new Node(depth + 1, v2, Vertex3D.lerp(v2, v3, 0.5f), Vertex3D.lerp(v1, v2, 0.5f)); //left
			new Node(depth + 1, v3, Vertex3D.lerp(v1, v3, 0.5f), Vertex3D.lerp(v2, v3, 0.5f)); //right
			new Node(depth + 1, Vertex3D.lerp(v1, v2, 0.5f), Vertex3D.lerp(v2, v3, 0.5f), Vertex3D.lerp(v1, v3, 0.5f)); //center
		}

		private void addNodeToArrayList() {
			nodes.add(this);
		}

		private boolean isVisible() {
			return true;
			/*
			Vector3f lhs = new Vector3f();
			Vector3f rhs = new Vector3f();

			Vector3f.sub(v2, v1, lhs);
			Vector3f.sub(v3, v1, rhs);
			Vector3f.cross(lhs, rhs, faceNormal);
			
			
			System.out.println("cam: " + cameraAngle);
			System.out.println("normal: " + faceNormal);
			
			double angle = Vector3f.angle(cameraAngle, faceNormal) * 180 / Math.PI;
			
			System.out.println(angle);
			
			float angleTolerance = 0;//90 / (depth + 2);

			return angle > 90 - angleTolerance && angle < 270 + angleTolerance;
			
			/*
			 * Frustum: mit Projektionsmatrix -1<x<1; -1<y<1
			 * jeden Punkt des Dreiecks
			 * eventuell Rechnung vereinfachen
			 * eventuell nur alle drei Tiefenstufen (%3)
			 */
		}
	}
}
