package planetZoooom;

import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_CONSTANT_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL20.glUseProgram;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import planetZoooom.engine.CoreEngine;
import planetZoooom.engine.Renderer;
import planetZoooom.gameContent.Atmosphere;
import planetZoooom.gameContent.BillBoard;
import planetZoooom.gameContent.FreeCamera;
import planetZoooom.gameContent.HeadsUpDisplay;
import planetZoooom.gameContent.Planet;
import planetZoooom.geometry.MasterSphere;
import planetZoooom.graphics.ShaderProgram;
import planetZoooom.graphics.Texture;
import planetZoooom.input.Keyboard;
import planetZoooom.interfaces.ICameraControl;
import planetZoooom.interfaces.IGame;
import planetZoooom.utils.GameUtils;
import planetZoooom.utils.Info;
import planetZoooom.utils.MatrixUtils;

public class Game implements IGame 
{
	private static CoreEngine game;
	private Renderer renderer;
	private ICameraControl cameraControl;
	private float fovParam = 45.0f;

	// GAMEOBJECTS
	private Planet planet;
	private HeadsUpDisplay hud;
	private BillBoard sun;
	private BillBoard sunGlow;
	private MasterSphere masterSphere;

	// TEXTURES
	private Texture planetTexture;
	private Texture sunTexture;
	private Texture sunGlowTexture;

	// SHADERS
	private ShaderProgram planetShader;
	private ShaderProgram wireFrameShader;	
	private ShaderProgram hudShader;
	private ShaderProgram sunShader;
	private ShaderProgram sunGlowShader;
	private ShaderProgram atmosphereShader;

	//Matrices
	private Matrix4f modelViewMatrix;
	private Matrix4f normalMatrix;
	private Matrix4f orthographicProjectionMatrix;
	
	private Vector3f lightDirection;
	private float planetCamDistance;
	
	//	CONTROLS
	private boolean wireframe = false;
	private boolean updateSphere = true;
	private float flatShading = 0.0f;
	
	
	
	
	private static final int HUD_MODE_OFF = 0;
	private static final int HUD_MODE_INFO = 1;
	private static final int HUD_MODE_NOISE = 2;
	private static final int HUD_MODE_ATMOSPHERE = 3;
	
	private int hudMode;
	
	public static void main(String[] args) 
	{
		game = new CoreEngine(new Game());
		game.start();
	}

	@Override
	public void init() 
	{
		printVersionInfo();

		Info.camera = new FreeCamera(0.0f, 0.0f, 10000);
		Info.projectionMatrix = planetZoooom.utils.MatrixUtils.perspectiveProjectionMatrix(fovParam, game.getWindowWidth(), game.getWindowHeight());
		
		modelViewMatrix = new Matrix4f();
		normalMatrix = new Matrix4f();
		orthographicProjectionMatrix = MatrixUtils.orthographicProjectionMatrix(0, -game.getWindowWidth(), -game.getWindowHeight(), 0.0f, -1.0f, 1.0f);

		renderer = new Renderer();		
		lightDirection = new Vector3f();
		
		initTextures();
		initShaders();
		initGameObjects();

		Info.planet = planet;
		hudMode = 0;
	}

	private void initTextures() 
	{
		planetTexture = new Texture("src/res/textures/uv_test.png");
		sunTexture = new Texture("src/res/textures/sun.png");
		sunGlowTexture = new Texture("src/res/textures/sunGlow3.png");
	}

	private void initShaders() 
	{
		hudShader = new ShaderProgram("HUDShader");
		planetShader = new ShaderProgram("planetShader");
		wireFrameShader = new ShaderProgram("testShader");
		sunShader = new ShaderProgram("sunShader");
		sunGlowShader = new ShaderProgram("sunGlowShader");
		atmosphereShader = new ShaderProgram("atmosphereShader");
	}

	private void initGameObjects() 
	{
		planet = new Planet(6500.0f, new Vector3f(0f, 0f, 0f));
		hud = new HeadsUpDisplay(0, 0, "arial_nm.png");
		sun = new BillBoard(new Vector3f(-100000.0f, 0.0f, 0.0f), 100000.0f);
		sun.setTexture(sunTexture);
		sunGlow = new BillBoard(new Vector3f(-99000.0f, 0.0f, 0.0f), 1.0f);
		sunGlow.setTexture(sunGlowTexture);
	}

	
	@Override
	public void update(int deltaTime) 
	{
		this.processKeyboardInputs(deltaTime);
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //DO NOT MOVE THIS LINE! ....THERE IS A REASON THAT IT IS NOT IN RENDERER;
		
		glDisable(GL_DEPTH_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_CONSTANT_ALPHA);
		
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.setIdentity();
		Matrix4f.mul(Info.camera.getViewMatrix(), modelMatrix, modelViewMatrix);
		
		Matrix4f.mul(Info.camera.getViewMatrix(), sun.getModelMatrix(), modelViewMatrix);
		drawSun();		
		
		glFrontFace(GL_CW);
		Matrix4f.mul(Info.camera.getViewMatrix(), planet.getAtmosphere().getModelMatrix(), modelViewMatrix);
		Vector3f.sub(sun.getPosition(), planet.getAtmosphere().getPosition(), lightDirection);
		lightDirection.normalise();
		drawAtmosphere();
		glFrontFace(GL_CCW);
		glEnable(GL_DEPTH_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		Matrix4f.mul(Info.camera.getViewMatrix(), (Matrix4f) new Matrix4f().setIdentity(), modelViewMatrix);
		Matrix4f.invert(modelViewMatrix, normalMatrix);
		
		if(updateSphere)
			planet.update();
		
		drawPlanet();
		

		updateHud(hudMode);
		drawHUD();
	}
	

	private void updateHud(int mode)
	{

		switch(mode)
		{
			case HUD_MODE_OFF:
			{
				hud.update("");
				return;
			}
		
			case HUD_MODE_INFO:
			{
				hud.update(getInfoHUDText());
				return;
			}
			
			case HUD_MODE_NOISE:
			{
				hud.update(getNoiseHUDText());
				return;
			}
			
			case HUD_MODE_ATMOSPHERE:
			{
				hud.update(getAtmosphereHUDText());
				return;
			}
		}
		
		throw new IllegalArgumentException();
	}
	
	private String getInfoHUDText()
	{
		//TODO Distance to surface shall consider noise
		int triangleCount = planet.getSphere().getTriangleCount();
		int totalTriangleCount = planet.getSphere().getTotalTriangleCount();
		double trianglePercentage = triangleCount * 100 / (double) totalTriangleCount;
		
		return  String.format(
				"General information\n\n"
				+ "Distance: %.2f\n"
				+ "Triangles: %d / %d (%.2f%%)\n"
				+ "Vertices: %d\n"
				+ "Subdivisions: %d\n"
				+ "FPS: %d",
				GameUtils.getDistanceBetween(planet.getPosition(), Info.camera.getPosition()) - planet.getRadius(), 
				triangleCount, totalTriangleCount, trianglePercentage,
				planet.getSphere().getVertexCount(),
				planet.getSphere().getSubdivisions(),
				game.timer.getFPS());
	}
	
	private String getAtmosphereHUDText()
	{
		Atmosphere atmosphere = planet.getAtmosphere();
		return  String.format(
				"Atmosphere properties\n\n"
				+ "Mie Scattering: %.4f\n"
				+ "Rayleigh Scattering: %.4f\n"
				+ "Wavelength red:   %.3f\n"
				+ "Wavelength green: %.3f\n"
				+ "Wavelength blue:  %.3f\n",
				atmosphere.getMieScattering(),
				atmosphere.getRayleighScattering(),
				atmosphere.getWaveLengthRed(),
				atmosphere.getWaveLengthGreen(),
				atmosphere.getWaveLengthBlue());
				
	}
	
	private String getNoiseHUDText()
	{
		return String.format(
				"Noise properties\n\n" +
				"Mountain Height: %.4f\n" + 
				"Seed: %.2f\n" +
				"Wavelength: %.2f\n" + 
				"Octaves: %d\n" + 
				"Amplitude: %.2f\n" 
				,
				planet.getMountainHeight(),
				planet.getNoiseSeed(),
				planet.getLambdaBaseFactor(),
				planet.getOctaves(), 
				planet.getAmplitude()
				);
	}
	
	private void drawSun()
	{
		glUseProgram(sunGlowShader.getId());
		{
			sunGlowShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
			sunGlowShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
			sunGlowShader.loadUniformVec3f(sunGlow.getPosition(), "billboardCenter");
			sunGlow.render(GL_TRIANGLES);
		}

		glUseProgram(sunShader.getId());
		{
			sunShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
			sunShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
			sunShader.loadUniformVec3f(sun.getPosition(), "billboardCenter");
			sunShader.loadUniformVec3f(Info.camera.getLocalUpVector(), "cameraUp");
			sunShader.loadUniformVec3f(Info.camera.getLocalRightVector(), "cameraRight");	
			sun.render(GL_TRIANGLES);
		}
	}
	
	private void drawAtmosphere()
	{
		glUseProgram(atmosphereShader.getId());
		{
			float cameraHeight = GameUtils.getDistanceBetween(planet.getPosition(), Info.camera.getPosition());
			planet.getAtmosphere().loadSpecificUniforms(atmosphereShader);
			atmosphereShader.loadUniform1f(cameraHeight, "cameraHeight");
			atmosphereShader.loadUniformVec3f(lightDirection, "lightDirection");
			atmosphereShader.loadUniformVec3f(Info.camera.getPosition(), "cameraPosition");
			atmosphereShader.loadUniform1f(1.0f / (planet.getAtmosphere().getSphere().getRadius() - planet.getRadius()), "fScale");
			//		atmosphereShader.loadUniform1f(planet.getAtmosphere().getSphere().getRadius() * 0.25f, "fScale");
			atmosphereShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
			atmosphereShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
			atmosphereShader.loadUniformMat4f(normalMatrix, "normalMatrix", true);
			atmosphereShader.loadUniformVec3f(Info.camera.getPosition(), "cameraPosition");
//			if(cameraHeight > planet.getRadius())
//				atmosphereShader.loadUniform1f(planet.getRadius(), "planetRadius");
			atmosphereShader.loadUniform1f(planet.getRadius() + planet.getRadius() * 0.09f, "planetRadius");
				
			planet.getAtmosphere().getSphere().render(GL_TRIANGLES);
			
//			if(wireframe)
//			{
//				glUseProgram(wireFrameShader.getId());
//				wireFrameShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
//				wireFrameShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
//				wireFrameShader.loadUniform1f(0.4f, "greytone");
//				planet.getAtmosphere().getSphere().render(GL_LINE_STRIP);
//				wireFrameShader.loadUniform1f(1.0f, "greytone");
//				planet.getAtmosphere().getSphere().render(GL_POINTS);
//			}
		}
	}
	
	private void drawPlanet()
	{
		glUseProgram(planetShader.getId());
		{
			planetShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
			planetShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
			planetShader.loadUniformMat4f(normalMatrix, "normalMatrix", true);
			planetShader.loadUniformVec3f(sun.getPosition(), "lightPosition");
			planetShader.loadUniformVec3f(Info.camera.getPosition(), "cameraPosition");
			planetShader.loadUniform1f(planet.getRadius(), "radius");
			planetShader.loadUniform1f(flatShading, "flatShading");
			planetShader.loadUniform1f(planet.getMountainHeight(), "mountainHeight");
			planet.getSphere().render(GL_TRIANGLES);
			
			if(wireframe)
			{
				glUseProgram(wireFrameShader.getId());
				wireFrameShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
				wireFrameShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
				wireFrameShader.loadUniform1f(0.4f, "greytone");
				planet.getSphere().render(GL_LINES);
				wireFrameShader.loadUniform1f(1.0f, "greytone");
				planet.getSphere().render(GL_POINTS);
			}
		}
	}
	
	private void drawHUD()
	{
		glUseProgram(hudShader.getId());
		{
			hudShader.loadUniformMat4f(orthographicProjectionMatrix, "projectionMatrix", false);
			hudShader.loadUniformMat4f(hud.getModelMatrix(), "modelViewMatrix", false);
			hud.getTextMesh().render(GL_TRIANGLES);
		}
		glUseProgram(0);
	}
	
	private void printVersionInfo() 
	{
		System.out.println("GPU Vendor: " + glGetString(GL_VENDOR));
		System.out.println("OpenGL version: " + glGetString(GL_VERSION));
		System.out.println("GLSL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
	}
	
	private void processKeyboardInputs(int deltaTime) {
		
		cameraControl = Info.camera.getCameraControl();
		Info.camera = cameraControl.handleInput(deltaTime);
		
		if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_1)){ wireframe = !wireframe; }
		if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_2)){ flatShading = 1.0f; }	
		if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_3)){ flatShading = 0.0f; }
		if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_9)){ updateSphere = !updateSphere; }
		if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_TAB)){ hudMode = (hudMode + 1) % 4; }
		
		switch(hudMode)
		{
			case HUD_MODE_NOISE:
			{
				if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_O))
					planet.setAmplitude(planet.getAmplitude() + 0.02f);
				else if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_L))
					planet.setAmplitude(planet.getAmplitude() - 0.02f);
				
				if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_I))
					planet.setOctaves(planet.getOctaves() + 1);
				else if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_K))
					planet.setOctaves(planet.getOctaves() - 1);
				
				if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_U))
					planet.setLambdaBaseFactor(planet.getLambdaBaseFactor() + 0.001f);
				else if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_J))
					planet.setLambdaBaseFactor(planet.getLambdaBaseFactor() - 0.001f);
				
				if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_Y))
					planet.setNoiseSeed(planet.getNoiseSeed() + planet.getRadius() / 1000);
				else if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_H))
					planet.setNoiseSeed(planet.getNoiseSeed() - planet.getRadius() / 1000);
				
				if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_T))
					planet.setMountainHeight(planet.getMountainHeight() + 0.0005f);
				else if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_G))
					planet.setMountainHeight(planet.getMountainHeight() - 0.0005f);
				break;
			}
			case HUD_MODE_ATMOSPHERE:
			{
				Atmosphere atmosphere = planet.getAtmosphere();
				
				if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_T))
					atmosphere.setMieScattering(atmosphere.getMieScattering() + 0.0001f);
				else if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_G))
					atmosphere.setMieScattering(atmosphere.getMieScattering() - 0.0001f);
				
				if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_Y))
					atmosphere.setRayleighScattering(atmosphere.getRayleighScattering() + 0.0001f);
				else if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_H))
					atmosphere.setRayleighScattering(atmosphere.getRayleighScattering() - 0.0001f);
				
				if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_U))
					atmosphere.setWaveLengthRed(atmosphere.getWaveLengthRed() + 0.01f);
				else if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_J))
					atmosphere.setWaveLengthRed(atmosphere.getWaveLengthRed() - 0.01f);
				
				if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_I))
					atmosphere.setWaveLengthGreen(atmosphere.getWaveLengthGreen() + 0.01f);
				else if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_K))
					atmosphere.setWaveLengthGreen(atmosphere.getWaveLengthGreen() - 0.01f);
				
				if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_O))
					atmosphere.setWaveLengthBlue(atmosphere.getWaveLengthBlue() + 0.01f);
				else if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_L))
					atmosphere.setWaveLengthBlue(atmosphere.getWaveLengthBlue() - 0.01f);
				break;
			}
		}
	
	}
}
