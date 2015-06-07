package engine;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engine.utils.GameUtils;
import geometry.Sphere;

public class Planet
{
	private Sphere sphere;
	private Vector3f position;

	public float getRadius()
	{
		return sphere.getRadius();
	}

	public Vector3f getPosition()
	{
		return position;
	}

	public GameObject3D getMesh()
	{
		return sphere;
	}

	public Planet(float radius, Vector3f position)
	{
		this.position = position;
		this.sphere = new Sphere(3, new Vector4f(1f, 1f, 1f, 1f), radius);
		
	}

	public void update(int subdivisions)
	{
		sphere.update(subdivisions);
	}

	public void update(float planetCamDistance, boolean adjustCamSpeed)
	{
		float subdivisionCoefficient = GameUtils.getDistanceCoefficient(planetCamDistance);

		int subdivisions = (int) (subdivisionCoefficient / 1.2 * Sphere.MAX_SUBDIVISIONS);

		// clamp
		subdivisions = subdivisions < Sphere.MIN_SUBDIVISIONS ? Sphere.MIN_SUBDIVISIONS
				: subdivisions;
		this.update(subdivisions);

		// TODO: adjust cam speed with subdivisionCoefficient if adjustCamSpeed
		// is true
	}
}
