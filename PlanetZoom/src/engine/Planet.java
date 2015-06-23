package engine;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engine.utils.GameUtils;
import geometry.Sphere;
import geometry.Vertex;
import geometry.Vertex3D;

public class Planet
{
	private Sphere sphere;
	private Vector3f position;
	private int lastNoisedSubdivion;
	
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
		if(subdivisions == sphere.getSubdivision())
			return;
		
		sphere.update(subdivisions);
			
		int octaves = 4;
		float lambda = 0.5f;
		float amplitude = 2.07f;
		Vector4f[] vertexColors = sphere.getColors();
			
		// TODO apply noise to sphere
		for(Vertex3D v : sphere.getVertices()) {
			double noise = engine.utils.CustomNoise.perlinNoise(v.getPosition().getX(), 
																v.getPosition().getY(), 
																v.getPosition().getZ(), 
																octaves, lambda, amplitude);
				
			double color = (float) ((noise + 1) / 2.0);
			v.setColorRGBA(new Vector4f((float)color, (float)color, (float) color, 1f));
		}
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
