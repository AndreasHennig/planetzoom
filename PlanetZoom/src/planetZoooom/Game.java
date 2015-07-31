package planetZoooom;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import planetZoooom.engine.CoreEngine;
import planetZoooom.gameContent.Atmosphere;
import planetZoooom.gameContent.BillBoard;
import planetZoooom.gameContent.FreeCamera;
import planetZoooom.gameContent.HeadsUpDisplay;
import planetZoooom.gameContent.Planet;
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
	private ICameraControl cameraControl;
	private float fovParam = 45.0f;

	// GAMEOBJECTS
	private Planet planet;
	private HeadsUpDisplay hud;
	private BillBoard sun;
	private BillBoard sunGlow;

	// TEXTURES
	private Texture sunTexture;
	private Texture sunGlowTexture;

	// SHADERS
	private ShaderProgram earthShader;
	private ShaderProgram marsShader;
	private ShaderProgram dessertShader;
	private ShaderProgram uniColorPlanetShader;
	private ShaderProgram wireFrameShader;	
	private ShaderProgram hudShader;
	private ShaderProgram sunShader;
	private ShaderProgram sunGlowShader;
	private ShaderProgram atmosphereShader;
	private ShaderProgram colorShader;

	// MATRICES
	private Matrix4f modelViewMatrix;
	private Matrix4f normalMatrix;
	private Matrix4f orthographicProjectionMatrix;
	private Vector3f lightDirection;
	
	// CONTROLS
	private boolean wireframe = false;
	private boolean freezeUpdate = false;

	private static final int HUD_MODE_OFF = 0;
	private static final int HUD_MODE_INFO = 1;
	private static final int HUD_MODE_NOISE = 2;
	private static final int HUD_MODE_ATMOSPHERE = 3;
	
	private static final float[] HUD_BG_YELLOW = new float[] {0.8f, 0.62f, 0.00f, 0.9f};
	private static final float[] HUD_BG_WHITE = new float[] {0.6f, 0.6f, 0.6f, 0.9f};
	private static final float[] HUD_BG_GREY = new float[] {0.6f, 0.6f, 0.6f, 0.9f};
	private static final float[] HUD_BG_PURPLE = new float[] {0.73f, 0.47f, 0.8f, 0.9f};
	
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

		Info.camera = new FreeCamera(new Vector3f(0.0f,0.0f,20000.0f));
		Info.projectionMatrix = planetZoooom.utils.MatrixUtils.perspectiveProjectionMatrix(fovParam, game.getWindowWidth(), game.getWindowHeight());
		
		modelViewMatrix = new Matrix4f();
		normalMatrix = new Matrix4f();
		orthographicProjectionMatrix = MatrixUtils.orthographicProjectionMatrix(0, -game.getWindowWidth(), -game.getWindowHeight(), 0.0f, -1.0f, 1.0f);
		lightDirection = new Vector3f();
		
		initOpenGL();
		initTextures();
		initShaders();
		initGameObjects();

		Info.planet = planet;
		hudMode = 0;
	}

	private void initOpenGL()
	{
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glPointSize(2.5f);
	}
	
	private void initTextures() 
	{
		sunTexture = new Texture("src/res/textures/sun.png");
		sunGlowTexture = new Texture("src/res/textures/sunGlow3.png");
	}

	private void initShaders() 
	{
		hudShader = new ShaderProgram("HUDShader");
		earthShader = new ShaderProgram("earthShader");
		marsShader = new ShaderProgram("marsShader");
		dessertShader = new ShaderProgram("dessertShader");
		uniColorPlanetShader = new ShaderProgram("uniColorPlanetShader");
		wireFrameShader = new ShaderProgram("wireFrameShader");
		sunShader = new ShaderProgram("sunShader");
		sunGlowShader = new ShaderProgram("sunGlowShader");
		atmosphereShader = new ShaderProgram("atmosphereShader");
		colorShader = new ShaderProgram("testShader");
	}

	private void initGameObjects() 
	{
		planet = new Planet(6500.0f, new Vector3f(0f, 0f, 0f));
		hud = new HeadsUpDisplay(0, 0, "arial_nm.png", HUD_BG_WHITE);
		sun = new BillBoard(new Vector3f(-25000.0f, 0.0f, 0.0f), 30000.0f, 30000.0f);
		sun.setTexture(sunTexture);
		sunGlow = new BillBoard(new Vector3f(-24900.0f, 0.0f, 0.0f), 40000.0f, 30000.0f);
		sunGlow.setTexture(sunGlowTexture);
	}

	@Override
	public void update(int deltaTime) 
	{
		this.processKeyboardInputs(deltaTime);
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //DO NOT MOVE THIS LINE! ....THERE IS A REASON THAT IT IS NOT IN RENDERER;
		
		glDisable(GL_DEPTH_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_CONSTANT_ALPHA);

		if(!freezeUpdate) 
		{
			glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			
			drawSun();		
			planet.update();
		
			glFrontFace(GL_CW);
			drawAtmosphere();
			glFrontFace(GL_CCW);
		}
		else 
		{
			glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
		}
		glEnable(GL_DEPTH_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	
		drawPlanet();

		updateHud(hudMode);
		drawHUD();
	}

	private void drawSun()
	{
		Matrix4f.mul(Info.camera.getViewMatrix(), sun.getModelMatrix(), modelViewMatrix);
		

		glUseProgram(sunShader.getId());
		{
			sunShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
			sunShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
			sunShader.loadUniformVec3f(sun.getPosition(), "billboardCenter");
			sunShader.loadUniformVec3f(Info.camera.getLocalUpVector(), "cameraUp");
			sunShader.loadUniformVec3f(Info.camera.getLocalRightVector(), "cameraRight");	
			sun.render(GL_TRIANGLES);
			sunGlow.render(GL_TRIANGLES);
		}
//
//		glUseProgram(sunShader.getId());
//		{
//			sunShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
//			sunShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
//			sunShader.loadUniformVec3f(sunGlow.getPosition(), "billboardCenter");
//		}
	}
	
	private void drawAtmosphere()
	{
		Matrix4f.mul(Info.camera.getViewMatrix(), planet.getAtmosphere().getModelMatrix(), modelViewMatrix);
		Vector3f.sub(sun.getPosition(), planet.getAtmosphere().getPosition(), lightDirection);
		lightDirection.normalise();
		
		glUseProgram(atmosphereShader.getId());
		{
			float cameraHeight = GameUtils.getDistanceBetween(planet.getPosition(), Info.camera.getPosition());
			planet.getAtmosphere().loadSpecificUniforms(atmosphereShader);
			atmosphereShader.loadUniform1f(cameraHeight, "cameraHeight");
			atmosphereShader.loadUniformVec3f(lightDirection, "lightDirection");
			atmosphereShader.loadUniformVec3f(Info.camera.getPosition(), "cameraPosition");
			atmosphereShader.loadUniform1f(1.0f / (planet.getAtmosphere().getSphere().getRadius() - planet.getRadius()), "fScale");
			atmosphereShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
			atmosphereShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
			atmosphereShader.loadUniformMat4f(normalMatrix, "normalMatrix", true);
			atmosphereShader.loadUniformVec3f(Info.camera.getPosition(), "cameraPosition");
			atmosphereShader.loadUniform1f(planet.getRadius() + planet.getRadius() * 0.09f, "planetRadius");
				
			planet.getAtmosphere().getSphere().render(GL_TRIANGLES);
		}
	}
	
	private void loadPlanetShaderUniforms(ShaderProgram shader)
	{
		glUseProgram(shader.getId());
		shader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
		shader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
		shader.loadUniformMat4f(normalMatrix, "normalMatrix", true);
		shader.loadUniformVec3f(new Vector3f(-100000.0f, 0.0f, 0.0f), "lightPosition");
		shader.loadUniformVec3f(Info.camera.getPosition(), "cameraPosition");
		shader.loadUniform1f(planet.getRadius(), "radius");
		shader.loadUniform1f(planet.getMountainHeight(), "mountainHeight");
	}
	
	private void drawPlanet()
	{
		Matrix4f.mul(Info.camera.getViewMatrix(), planet.getSphere().getModelMatrix(), modelViewMatrix);
		Matrix4f.invert(modelViewMatrix, normalMatrix);
		
		switch(planet.getShaderMode())
		{
		case Planet.STYLE_EARTH:	loadPlanetShaderUniforms(earthShader);
									planet.setHasWater(true);
									break;
		case Planet.STYLE_MARS:		loadPlanetShaderUniforms(marsShader);
									planet.setHasWater(false);
									break;
		case Planet.STYLE_DUNE: 	loadPlanetShaderUniforms(dessertShader);
									planet.setHasWater(false);
									break;
		case Planet.STYLE_UNICOLOR: loadPlanetShaderUniforms(uniColorPlanetShader);
									planet.setHasWater(true);
									break;
		default: 					throw new IllegalArgumentException();
		}
		
		planet.getSphere().render(GL_TRIANGLES);
		
		if(wireframe)
		{
			glDepthFunc(GL_LEQUAL);
			glUseProgram(wireFrameShader.getId());
			wireFrameShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
			wireFrameShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
			wireFrameShader.loadUniform1f(0.5f, "greytone");
			planet.getSphere().render(GL_LINES);
			wireFrameShader.loadUniform1f(0.8f, "greytone");
			planet.getSphere().render(GL_POINTS);
			glDepthFunc(GL_LESS);
		}
	}
	
	private void drawHUD()
	{
		glUseProgram(colorShader.getId());
		{
			colorShader.loadUniformMat4f(orthographicProjectionMatrix, "projectionMatrix", false);
			colorShader.loadUniformMat4f(hud.getModelMatrix(), "modelViewMatrix", false);
			hud.getBackgroundMesh().render(GL_TRIANGLES);
		}
		glUseProgram(0);

		glUseProgram(hudShader.getId());
		{
			hudShader.loadUniformMat4f(orthographicProjectionMatrix, "projectionMatrix", false);
			hudShader.loadUniformMat4f(hud.getModelMatrix(), "modelViewMatrix", false);
			hud.getTextMesh().render(GL_TRIANGLES);
		}
		glUseProgram(0);
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
				if(freezeUpdate)
					hud.setBackgroundColor(HUD_BG_GREY);
				else
					hud.setBackgroundColor(HUD_BG_WHITE);
				hud.update(getInfoHUDText());
				return;
			}
			
			case HUD_MODE_NOISE:
			{
				hud.setBackgroundColor(HUD_BG_YELLOW);
				hud.update(getNoiseHUDText());
				return;
			}
			
			case HUD_MODE_ATMOSPHERE:
			{
				hud.setBackgroundColor(HUD_BG_PURPLE);
				hud.update(getAtmosphereHUDText());
				return;
			}
		}
		
		throw new IllegalArgumentException();
	}
	
	private String getInfoHUDText()
	{
		int triangleCount = planet.getSphere().getTriangleCount();
		int totalTriangleCount = planet.getSphere().getTotalTriangleCount();
		double trianglePercentage = triangleCount * 100 / (double) totalTriangleCount;
		
		return  String.format(
				"General information\n\n"
				+ "Distance:     %.2f\n"
				+ "Triangles:    %d / %d (%.2f%%)\n"
				+ "Vertices:     %d\n"
				+ "Subdivisions: %d\n"
				+ "FPS:          %d",
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
				+ "Sunbrightness: %.1f\n"
				+ "Scattering:    %.4f\n"
				+ "Wavelength 1:  %.3f\n"
				+ "Wavelength 2:  %.3f\n"
				+ "Wavelength 3:  %.3f\n",
				atmosphere.getSunBrightness(),
				atmosphere.getRayleighScattering(),
				atmosphere.getWaveLengthRed(),
				atmosphere.getWaveLengthGreen(),
				atmosphere.getWaveLengthBlue());
				
	}
	
	private String getNoiseHUDText()
	{
		return String.format(
				"Noise properties\n\n" +
				"Mountain height: %.4f\n" + 
				"Seed:            %.2f\n" +
				"Wavelength:      %.2f\n" + 
				"Octaves:         %d\n" + 
				"Amplitude:       %.2f\n" 
				,
				planet.getMountainHeight(),
				planet.getNoiseSeed(),
				planet.getLambdaBaseFactor(),
				planet.getOctaves(), 
				planet.getAmplitude()
				);
	}
	
	private void printVersionInfo() 
	{
		System.out.println("GPU Vendor: " + glGetString(GL_VENDOR));
		System.out.println("OpenGL version: " + glGetString(GL_VERSION));
		System.out.println("GLSL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
	}
	
	private void reset()
	{
		planet.setNoiseSeed(0);
		planet.resetPlanet();
		Info.camera = new FreeCamera(new Vector3f(0.0f,0.0f,20000.0f));
		planet.setShaderMode(0);
		wireframe = false;
		freezeUpdate = false;
	}
	
	private void processKeyboardInputs(int deltaTime) {
		
		cameraControl = Info.camera.getCameraControl();
		Info.camera = cameraControl.handleInput(deltaTime);
		
		if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_5)){ wireframe = !wireframe; }
		if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_9)){ freezeUpdate = !freezeUpdate; }
		if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_TAB)){ hudMode = (hudMode + 1) % 4; }
		if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_6)){ planet.setShaderMode((planet.getShaderMode() + 1) % 4); }
		if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_ENTER)){ planet.setNoiseSeed((float) (Math.random() * Integer.MAX_VALUE)); }
		if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_BACKSPACE)){ reset(); }
		
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
					atmosphere.setSunBrightness(atmosphere.getSunBrightness() + 0.5f);
				else if(Keyboard.isKeyPressedWithReset(GLFW.GLFW_KEY_G))
					atmosphere.setSunBrightness(atmosphere.getSunBrightness() - 0.5f);
				
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
