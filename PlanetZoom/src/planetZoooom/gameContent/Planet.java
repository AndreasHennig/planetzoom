package planetZoooom.gameContent;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import planetZoooom.geometry.Sphere;
import planetZoooom.geometry.Vertex;
import planetZoooom.geometry.Vertex3D;
import planetZoooom.interfaces.IGameObjectListener;
import planetZoooom.utils.CustomNoise;
import planetZoooom.utils.GameUtils;
import planetZoooom.utils.Info;

public class Planet implements IGameObjectListener 
{
	private Sphere sphere;
	private Vector3f position;
	private Atmosphere atmosphere;
	
	public Planet(float radius, Vector3f position) 
	{
		this.position = position;
		this.sphere = new Sphere(radius);
		this.atmosphere = new Atmosphere(this);
		sphere.addListener(this);
	}
	
	public void update(int subdivisions)
	{
		sphere.update(subdivisions, Info.camera.getLookAt());
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

	@Override
	public void vertexCreated(Vertex v) 
	{
		Vertex3D v3d = (Vertex3D) v;
		Vector3f position = v3d.getPosition();
		float planetRadius = this.getRadius();
		
		// TODO apply noise to sphere
		final int octaves = 1;
		final float lambda = 1f * planetRadius;
		final float amplitude = 4.07f;
		
		float noise = (float) CustomNoise.perlinNoise(position.x, position.y, position.z, octaves, lambda, amplitude);

		noise = (noise + 1) / 2;
		
		// 0.000014 = 0.0014 % = 8.850 km vs. 6000 km
		position.scale(1 + noise * 0.05f);
		v3d.setColorRGBA(new Vector4f(noise, noise, noise, 1));
	}
}
