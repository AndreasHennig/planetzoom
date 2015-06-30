package engine;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engine.utils.GameUtils;
import geometry.Sphere;
import geometry.Vertex3D;

public class Planet 
{
	private Sphere sphere;
	private Vector3f position;
	private Atmosphere atmosphere;
	
	public Planet(float radius, Vector3f position) 
	{
		this.position = position;
		this.sphere = new Sphere();
		this.atmosphere = new Atmosphere(this);
	}
	
	public void update(int subdivisions)
	{
		sphere.update(subdivisions, Info.camera.getLookAt());
		// where to apply cam?
		
		// TODO apply noise to sphere
		int octaves = 4;
		float lambda = 0.5f;
		float amplitude = 2.07f;

		for(Vertex3D v : sphere.getVertices()) {
			double noise = engine.utils.CustomNoise.perlinNoise(v.getPosition().getX(),
																v.getPosition().getY(),
																v.getPosition().getZ(),
																octaves, lambda, amplitude);

			double color = (float) ((noise + 1) / 2.0);
			v.setColorRGBA(new Vector4f((float) color, (float) color, (float) color, 1f));
		}

		sphere.createVAO();
	}

	public void update(float planetCamDistance, boolean adjustCamSpeed) 
	{
		float subdivisionCoefficient = GameUtils.getDistanceCoefficient(planetCamDistance);

		int subdivisions = (int) (subdivisionCoefficient / 1.2 * Sphere.MAX_SUBDIVISIONS);

		// clamp
		subdivisions = subdivisions < Sphere.MIN_SUBDIVISIONS ? Sphere.MIN_SUBDIVISIONS : subdivisions;
		this.update(subdivisions);

		// TODO: adjust cam speed with subdivisionCoefficient if adjustCamSpeed
		// is true
	}
	
	public float getRadius() 
	{
		return sphere.getRadius();
	}

	public Vector3f getPosition() 
	{
		return position;
	}

	public Sphere getMesh() 
	{
		return sphere;
	}
	
	public int getTotalTriangleCount() 
	{
		return sphere.getTotalTriangleCount();
	}
	
	public int getActualTriangleCount() 
	{
		return sphere.getActualTriangleCount();
	}
	
	public Atmosphere getAtmosphere()
	{
		return atmosphere;
	}
}