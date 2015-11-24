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
	
	private static final int SAMPLE_RAYS = 3;						// Number of sample rays to use in integral equation
	
	private static final float MIE_PHASE_ASYMETRY_FACTOR = -0.990f;	// The Mie phase asymmetry factor
	private static final float RAYLEIGH_SCALE_DEPTH = 0.15f;
	private static final float MIE_SCATTERING = 0.0001f; 
			
	private float rayleighScattering;
	private float waveLengthRed;
	private float waveLengthGreen;
	private float waveLengthBlue;
	private float sunBrightness;

	
	private float[] wavelengths = new float[3];
	
	
	public Atmosphere(Planet planet)
	{
		sphere = new StaticSphere(ATMOSPHERE_SPHERE_SUBDIVISIONS, planet.getRadius() * (1 + ATMOSPHERE_PLANET_DISTANCE));
		position = planet.getPosition();
		modelMatrix = new Matrix4f().translate(position);
		
		setWaveLengthRed(0.95f);
		setWaveLengthGreen(0.75f);
		setWaveLengthBlue(0.555f);
		sunBrightness = 5.0f;
		rayleighScattering = 0.0035f;
	}
	
	public void update(int mode)
	{
		switch(mode)
		{
			case Planet.STYLE_DUNE:
			case Planet.STYLE_EARTH:	setWaveLengthRed(0.95f);
										setWaveLengthGreen(0.75f);
										setWaveLengthBlue(0.555f);
										sunBrightness = 5.0f;
										rayleighScattering = 0.0035f;
										break;
			case Planet.STYLE_UNICOLOR:
			case Planet.STYLE_MARS:		setWaveLengthRed(0.670f);
										setWaveLengthGreen(1.030f);
										setWaveLengthBlue(1.275f);
										sunBrightness = 5.0f;
										rayleighScattering = 0.0035f;
										break;
			
			default: 					throw new IllegalArgumentException();
		}
	}


	public void loadSpecificUniforms(ShaderProgram atmosphereShader)
	{
		atmosphereShader.loadUniformVec3f(new Vector3f(wavelengths[0],wavelengths[1], wavelengths[2]), "inverseWavelength");
		atmosphereShader.loadUniform1f(MIE_SCATTERING * sunBrightness, "mieScattering");
		atmosphereShader.loadUniform1f((float)(MIE_SCATTERING * 4 * Math.PI), "mieScattering4Pi");
		atmosphereShader.loadUniform1f(rayleighScattering * sunBrightness, "rayleighScattering");
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

	public float getSunBrightness()
	{
		return sunBrightness;
	}

	public void setSunBrightness(float brightness)
	{
		this.sunBrightness = brightness;
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