package geometry;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import engine.utils.GameUtils;

public class Graph {
	
	int subdivisions;
	Vector3f cameraAngle; //is this enough or should we consider the planet being behind the camera?
	ArrayList<Node> nodes;
	float angleTolerance = -10f; //90 degrees is standard
	
	public Graph(int subdivisions, Vector3f cameraAngle){
		this.subdivisions = subdivisions;
		this.cameraAngle = cameraAngle;
		
		createOctahedron();
	}
	
	private void createOctahedron() {
		new Node(0, Vertex3D.up(), Vertex3D.front(), Vertex3D.right()); //front up right
		new Node(0, Vertex3D.up(), Vertex3D.left(), Vertex3D.front()); //front up left
		new Node(0, Vertex3D.front(), Vertex3D.down(), Vertex3D.right()); //front down right 
		new Node(0, Vertex3D.front(), Vertex3D.left(), Vertex3D.down()); //front down left
		new Node(0, Vertex3D.back(), Vertex3D.up(), Vertex3D.right()); //back up right
		new Node(0, Vertex3D.back(), Vertex3D.left(), Vertex3D.up()); //back up left
		new Node(0, Vertex3D.down(), Vertex3D.back(), Vertex3D.right()); //back down right
		new Node(0, Vertex3D.down(), Vertex3D.left(), Vertex3D.back()); //back down left
	}

	class Node {
		int depth;
		Vector3f v1, v2, v3;
		Node child1, child2, child3, child4;
		Vector3f faceNormal;
		
		public Node(int currentDepth, Vector3f point1, Vector3f point2, Vector3f point3){
			depth = currentDepth;
			v1 = point1;
			v2 = point2;
			v3 = point3;
		
			if(isVisible()){
				if(depth >= subdivisions){
					addNodeToArrayList();
				}
				else{
					createChildren();
				}
			}
		}

		private void createChildren() {
			child1 = new Node(depth + 1, v1, Vertex3D.lerp(v1, v2, 0.5f), Vertex3D.lerp(v1, v2, 0.5f));
			child2 = new Node(depth + 1, Vertex3D.lerp(v1, v2, 0.5f), v2, Vertex3D.lerp(v2, v3, 0.5f));
			child3 = new Node(depth + 1, Vertex3D.lerp(v1, v3, 0.5f), Vertex3D.lerp(v2, v3, 0.5f), v3);
			child4 = new Node(depth + 1, Vertex3D.lerp(v1, v2, 0.5f), Vertex3D.lerp(v2, v3, 0.5f), Vertex3D.lerp(v3, v1, 0.5f));
		}
		
		private void addNodeToArrayList(){
			nodes.add(this);
		}
		
		private boolean isVisible() {
			Vector3f lhs = new Vector3f();
			Vector3f rhs = new Vector3f();
			
			Vector3f.sub(v2, v1, lhs);
			Vector3f.sub(v3, v1, rhs);
			Vector3f.cross(lhs, rhs, faceNormal);
			
			double angle = Vector3f.angle(cameraAngle, faceNormal);
			return angle > Math.PI * (90 + angleTolerance) / 180 && angle < Math.PI * (270 - angleTolerance) / 180;
		}
	}
}

