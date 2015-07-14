package planetZoooom.gameContent;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import planetZoooom.geometry.Sphere;
import planetZoooom.geometry.Vertex;
import planetZoooom.interfaces.IGameObjectListener;
import planetZoooom.utils.CustomNoise;
import planetZoooom.utils.GameUtils;
import planetZoooom.utils.Info;

public class Planet implements IGameObjectListener 
{
	final static float MIN_AMPLITUDE = 1;
	final static float MIN_LAMBDA_BASE_FACTOR = 0.1f;
	final static int MIN_OCTAVES = 1;
	final static float MIN_MOUNTAIN_HEIGHT = 0.0014f;
	
	private Sphere sphere;
	private Vector3f position;
	private Atmosphere atmosphere;
	
	private float amplitude;
	private int octaves;
	private float lambdaBaseFactor;
	private float noiseSeed;
	private float mountainHeight;
	
	public float getAmplitude() {
		return amplitude;
	}

	public void setAmplitude(float amplitude) {
		if(amplitude < MIN_AMPLITUDE)
			this.amplitude = MIN_AMPLITUDE;
		else
			this.amplitude = amplitude;
		
		System.out.printf("Noise Amplitude: %.2f\n", this.amplitude);
	}

	public int getOctaves() {
		return octaves;
	}

	public void setOctaves(int octaves) {
		if(octaves < MIN_OCTAVES)
			this.octaves = MIN_OCTAVES;
		else
			this.octaves = octaves;
		
		System.out.println("Noise Octaves: " + this.octaves);
	}

	public float getLambdaBaseFactor() {
		return lambdaBaseFactor;
	}

	public void setLambdaBaseFactor(float lambdaBaseFactor) {
		if(lambdaBaseFactor < MIN_LAMBDA_BASE_FACTOR)
			this.lambdaBaseFactor = MIN_LAMBDA_BASE_FACTOR;
		else
			this.lambdaBaseFactor = lambdaBaseFactor;
		
		System.out.printf("Noise Lambda: %.2f\n",this.lambdaBaseFactor);
	}

	public float getNoiseSeed() {
		return noiseSeed;
	}

	public void setNoiseSeed(float noiseSeed) {
		this.noiseSeed = noiseSeed;
		
		System.out.println("Noise Seed: " + this.noiseSeed);
	}

	public void setAtmosphere(Atmosphere atmosphere) {
		this.atmosphere = atmosphere;
	}
	
	public float getMountainHeight() {
		return mountainHeight;
	}

	public void setMountainHeight(float mountainHeight) {
		if(mountainHeight < MIN_MOUNTAIN_HEIGHT)
			this.mountainHeight = MIN_MOUNTAIN_HEIGHT;
		else
			this.mountainHeight = mountainHeight;
		
		System.out.printf("Mountain Height: %.4f %%\n", this.mountainHeight);
	}
	
	public Planet(float radius, Vector3f position) 
	{
		this.position = position;
		this.sphere = new Sphere(radius);
		this.atmosphere = new Atmosphere(this);
		sphere.addListener(this);
		
		lambdaBaseFactor = 0.75f;
		octaves = 3;
		amplitude = 3.07f;
		noiseSeed = 0;
		mountainHeight = MIN_MOUNTAIN_HEIGHT;
	}

	public void update(int subdivisions)
	{
		sphere.update(subdivisions, Info.camera.getLookAt());
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
		Vertex v3d = (Vertex) v;
		Vector3f position = v3d.getPosition();
		float planetRadius = this.getRadius();
		
		final float lambda = lambdaBaseFactor * planetRadius;
		float noise = (float) CustomNoise.perlinNoise(position.x + noiseSeed, position.y + noiseSeed, position.z + noiseSeed, octaves, lambda, amplitude);

		noise = (noise + 1) / 2;
		
		// 0.14 % = 8 km von 6000 km
		position.scale(1 + noise * mountainHeight);

	}
}
