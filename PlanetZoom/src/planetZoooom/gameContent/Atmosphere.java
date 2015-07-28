package planetZoooom.gameContent;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import planetZoooom.geometry.StaticSphere;
import planetZoooom.graphics.ShaderProgram;


public class Atmosphere 
{
	private Matrix4f modelMatrix;
	private StaticSphere sphere;
	private Vector3f position;
	
	private static final int ATMOSPHERE_SPHERE_SUBDIVISIONS = 6; 
	private static final float ATMOSPHERE_PLANET_DISTANCE = 0.150f;
	
	private static final int SAMPLE_RAYS = 2;						// Number of sample rays to use in integral equation
	private static final float SUN_BRIGHTNESS = 5.0f;
	private static final float MIE_PHASE_ASYMETRY_FACTOR = -0.990f;	// The Mie phase asymmetry factor
	private static final float EXPOSURE = 2.0f;
	private static final float RAYLEIGH_SCALE_DEPTH = 0.25f;
	private static final float MIE_SCALE_DEPTH = 0.1f;
			
	private float rayleighScattering;
	private float mieScattering;
	private float waveLengthRed;
	private float waveLengthGreen;
	private float waveLengthBlue;

	private float planetRadius;
	
	private float[] wavelengths = new float[3];
	
	
	public Atmosphere(Planet planet)
	{
		sphere = new StaticSphere(ATMOSPHERE_SPHERE_SUBDIVISIONS, planet.getRadius() * (1 + ATMOSPHERE_PLANET_DISTANCE));
		this.planetRadius = planet.getRadius();
		position = planet.getPosition();
		modelMatrix = new Matrix4f().translate(position);
		
		setWaveLengthRed(0.65f);
		setWaveLengthGreen(0.57f);
		setWaveLengthBlue(0.475f);
		mieScattering = 0.0f;
		rayleighScattering = 0.0035f;
	}
	
	public void update(int mode)
	{
		switch(mode)
		{
			case Planet.STYLE_DUNE:
			case Planet.STYLE_EARTH:	setWaveLengthRed(0.65f);
										setWaveLengthGreen(0.57f);
										setWaveLengthBlue(0.475f);
										mieScattering = 0.0f;
										rayleighScattering = 0.0035f;
										break;
			case Planet.STYLE_UNICOLOR:
			case Planet.STYLE_MARS:		setWaveLengthRed(0.480f);
										setWaveLengthGreen(0.78f);
										setWaveLengthBlue(0.955f);
										mieScattering = 0.0f;
										rayleighScattering = 0.0035f;
										break;
			
			default: 					throw new IllegalArgumentException();
		}
	}
	/**
	 * if rayleigh is true rayleigh scattering is calculated 
	 * otherwise mie scattering is calculated
	 * returns the "optical depth" that is the average atmospheric density across the ray from point Pa to point Pb multiplied by the length of the ray
	 */
	private void outScattering(boolean rayleigh, Vector3f pa, Vector3f pb, int samples)
	{		
		/*
		 * 
		 * The scattering equations have nested integrals that are impossible to solve analytically; 
		 * fortunately, it's easy to numerically compute the value of an integral with techniques such as the trapezoid rule. 
		 * Approximating an integral in this manner boils down to a weighted sum calculated in a loop. Imagine a line segment on a graph: 
		 * break up the segment into n sample segments and evaluate the integrand at the center point of each sample segment. 
		 * Multiply each result by the length of the sample segment and add them all up. 
		 * Taking more samples makes the result more accurate, but it also makes the integral more expensive to calculate.
		 */
		
		Vector3f ray = new Vector3f();
		Vector3f segment = new Vector3f();
		Vector3f samplePoint = new Vector3f();
		Vector3f.sub(pb, pa, ray);
		ray.scale(1.0f / samples);
		float offset = (1.0f / samples) / 2.0f;
		float segmentLength = segment.scale(1.0f / samples).length();
		double sum = 0;
		float height = 0;
		//calculate out-scattering integral
		
		for(int i = 0; i < samples; i++)
		{
			segment.x = ray.x;
			segment.y = ray.y;
			segment.z = ray.z;
			segment.scale(offset + i * (1.0f / samples));
		
			samplePoint.x  = pa.x + segment.x;
			samplePoint.y  = pa.y + segment.y;
			samplePoint.z  = pa.z + segment.z;
			
			height = samplePoint.length() - planetRadius;
			//set height parameter so 0 equals sea level and 1 is the top of the atmosphere
			sum += calculateAtmosphericDenisity(height, rayleigh) * segmentLength; 
		}
	}
	
	private void createLUT()
	{
		
	}
	private double calculateAtmosphericDenisity(float height, boolean rayleigh)
	{
		if(rayleigh)
			return Math.exp(height / RAYLEIGH_SCALE_DEPTH);
		else
			return Math.exp(height / MIE_SCALE_DEPTH);
	}

	
	public void loadSpecificUniforms(ShaderProgram atmosphereShader)
	{
		atmosphereShader.loadUniformVec3f(new Vector3f(wavelengths[0],wavelengths[1], wavelengths[2]), "inverseWavelength");
		atmosphereShader.loadUniform1f(mieScattering * SUN_BRIGHTNESS, "mieScattering");
		atmosphereShader.loadUniform1f((float)(mieScattering * 4 * Math.PI), "mieScattering4Pi");
		atmosphereShader.loadUniform1f(rayleighScattering * SUN_BRIGHTNESS, "rayleighScattering");
		atmosphereShader.loadUniform1f((float)(rayleighScattering * 4 * Math.PI), "rayleighScattering4Pi");
		atmosphereShader.loadUniform1f(sphere.getRadius(), "atmosphereRadius");
		atmosphereShader.loadUniform1f(RAYLEIGH_SCALE_DEPTH, "scaleDepth");
		atmosphereShader.loadUniform1f(MIE_PHASE_ASYMETRY_FACTOR, "miePhaseAsymetryFactor");
		atmosphereShader.loadUniform1f(SAMPLE_RAYS, "sampleRays");
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	public StaticSphere getSphere()
	{
		return sphere;
	}
	
	public Matrix4f getModelMatrix()
	{
		return modelMatrix;
	}

	public float getRayleighScattering()
	{
		return rayleighScattering;
	}

	public void setRayleighScattering(float rayleighScattering)
	{
		this.rayleighScattering = rayleighScattering;
	}

	public float getMieScattering()
	{
		return mieScattering;
	}

	public void setMieScattering(float mieScattering)
	{
		this.mieScattering = mieScattering;
	}

	public float getWaveLengthRed()
	{
		return waveLengthRed;
	}

	public void setWaveLengthRed(float waveLengthRed)
	{
		this.waveLengthRed = waveLengthRed;
		wavelengths[0] = (float) (1.0f / Math.pow(waveLengthRed, 4));
	}

	public float getWaveLengthGreen()
	{
		return waveLengthGreen;
	}

	public void setWaveLengthGreen(float waveLengthGreen)
	{
		this.waveLengthGreen = waveLengthGreen;
		wavelengths[1] = (float) (1.0f / Math.pow(waveLengthGreen, 4));
	}

	public float getWaveLengthBlue()
	{
		return waveLengthBlue;
	}

	public void setWaveLengthBlue(float waveLengthBlue)
	{
		this.waveLengthBlue = waveLengthBlue;
		wavelengths[2] = (float) (1.0f / Math.pow(waveLengthBlue, 4));
	}	

}
