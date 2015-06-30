package engine;

import org.lwjgl.util.vector.Vector3f;

import engine.utils.CustomNoise;
import engine.utils.GameUtils;
import geometry.Sphere;
import geometry.Vertex;
import geometry.Vertex3D;

public class Planet implements IGameObjectListener {
	private Sphere sphere;
	private Vector3f position;
	
	public float getRadius() {
		return sphere.getRadius();
	}

	public Vector3f getPosition() {
		return position;
	}

	public GameObject3D getMesh() {
		return sphere;
	}

	public Planet(float radius, Vector3f position) {
		this.position = position;
		this.sphere = new Sphere(radius);
		sphere.addListener(this);
	}

	public void update(ICamera camera, int subdivisions)
	{
		sphere.update(subdivisions, camera.getLookAt());

		sphere.createVAO();
	}

	public void update(ICamera camera, float planetCamDistance, boolean adjustCamSpeed) {
		float subdivisionCoefficient = GameUtils.getDistanceCoefficient(planetCamDistance);

		int subdivisions = (int) (subdivisionCoefficient / 1.2 * Sphere.MAX_SUBDIVISIONS);

		// clamp
		subdivisions = subdivisions < Sphere.MIN_SUBDIVISIONS ? Sphere.MIN_SUBDIVISIONS : subdivisions;
		this.update(camera, subdivisions);

		// TODO: adjust cam speed with subdivisionCoefficient if adjustCamSpeed
		// is true
	}
	
	public int getTotalTriangleCount() {
		return sphere.getTotalTriangleCount();
	}
	
	public int getActualTriangleCount() {
		return sphere.getActualTriangleCount();
	}

	@Override
	public void vertexCreated(Vertex v) {
		Vertex3D v3d = (Vertex3D) v;
		Vector3f position = v3d.getPosition();
		
		// TODO apply noise to sphere
		final int octaves = 4;
		final float lambda = 0.5f;
		final float amplitude = 2.07f;
		
		float noise = (float) CustomNoise.perlinNoise(position.x, position.y, position.z, octaves, lambda, amplitude);

//		position.scale(1 + noise/200);
	}
}