package planetZoooom.gameContent;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import planetZoooom.geometry.Sphere;
import planetZoooom.geometry.StaticSphere;
import planetZoooom.geometry.Vertex3D;
import planetZoooom.graphics.ShaderProgram;


public class Atmosphere 
{
	private StaticSphere sphere;
	private Vector3f position;
	
	private static final int ATMOSPHERE_SPHERE_SUBDIVISIONS = 6; 
//	private static final float ATMOSPHERE_PLANET_DISTANCE = 0.025f;
	
	private static final int SAMPLE_RAYS = 2;						// Number of sample rays to use in integral equation
	private static final float RAYLEIGH_SCATTERING = 0.0025f;		// Rayleigh scattering constant (Kr)
	private static final float MIE_SCATTERING = 0.0010f;			// Mie scattering constant (Km)
	private static final float SUN_BRIGHTNESS = 10.0f;
	private static final float MIE_PHASE_ASYMETRY_FACTOR = -0.990f;	// The Mie phase asymmetry factor
	private static final float EXPOSURE = 2.0f;
	private static final float RAYLEIGH_SCALE_DEPTH = 0.25f;
	private static final float MIE_SCALE_DEPTH = 0.1f;
		
	private static final float WAVELENGTH_RED = 0.65f;
	private static final float WAVELENGTH_GREEN = 0.57f;
	private static final float WAVELENGTH_BLUE = 0.475f;
	
	private static final float[] WAVELENGTHS = new float[] 
			{WAVELENGTH_RED, 
			WAVELENGTH_GREEN, 
			WAVELENGTH_BLUE, 
			(float) Math.pow(WAVELENGTH_RED, 4), 
			(float) Math.pow(WAVELENGTH_GREEN, 4), 
			(float) Math.pow(WAVELENGTH_BLUE, 4)};
	
	
	public Atmosphere(Planet planet)
	{
		sphere = new StaticSphere(ATMOSPHERE_SPHERE_SUBDIVISIONS, planet.getRadius() * 1.025f);
		position = planet.getPosition();	
		setColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
	}
	
	public void loadSpecificUniforms(ShaderProgram atmosphereShader)
	{
		atmosphereShader.loadUniformVec3f(new Vector3f(1.0f/WAVELENGTHS[3], 1.0f/WAVELENGTHS[4], 1.0f/WAVELENGTHS[5]), "inverseWavelength");
		atmosphereShader.loadUniform1f(MIE_SCATTERING * SUN_BRIGHTNESS, "mieScattering");
		atmosphereShader.loadUniform1f((float)(MIE_SCATTERING * 4 * Math.PI), "mieScattering4Pi");
		atmosphereShader.loadUniform1f(RAYLEIGH_SCATTERING * SUN_BRIGHTNESS, "rayleighScattering");
		atmosphereShader.loadUniform1f((float)(RAYLEIGH_SCATTERING * 4 * Math.PI), "rayleighScattering4Pi");
		atmosphereShader.loadUniform1f(sphere.getRadius(), "atmosphereRadius");
		atmosphereShader.loadUniform1f(RAYLEIGH_SCALE_DEPTH, "scaleDepth");
		atmosphereShader.loadUniform1f(MIE_PHASE_ASYMETRY_FACTOR, "miePhaseAsymetryFactor");
		atmosphereShader.loadUniform1f(SAMPLE_RAYS, "sampleRays");
	}
	 
	
	private void setColor(Vector4f color)
	{
		ArrayList<Vertex3D> vertices = sphere.getVertices();
		
		for(int i = 0; i < vertices.size(); i++)
			vertices.get(i).setColorRGBA(color);
		
		sphere.createVAO();
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	public StaticSphere getSphere()
	{
		return sphere;
	}

}
