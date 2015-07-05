package planetZoooom;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL20.glUseProgram;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import planetZoooom.engine.CoreEngine;
import planetZoooom.engine.Renderer;
import planetZoooom.gameContent.FreeCamera;
import planetZoooom.gameContent.HeadsUpDisplay;
import planetZoooom.gameContent.Planet;
import planetZoooom.gameContent.BillBoard;
import planetZoooom.graphics.ShaderProgram;
import planetZoooom.graphics.Texture;
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

	// TEXTURES
	private Texture planetTexture;
	private Texture sunTexture;
	private Texture sunGlowTexture;

	// SHADERS
	private ShaderProgram planetShader;
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
	
	public static void main(String[] args) 
	{
		game = new CoreEngine(new Game());
		game.start();
	}

	@Override
	public void init() 
	{
		printVersionInfo();

		Info.camera = new FreeCamera(0.0f, 0.0f, 20000f);
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
	}

	private void initTextures() 
	{
		planetTexture = new Texture("src/res/textures/uv_test.png");
		sunTexture = new Texture("src/res/textures/sun.png");
		sunGlowTexture = new Texture("src/res/textures/sunGlow.png");
	}

	private void initShaders() 
	{
		hudShader = new ShaderProgram("HUDShader");
		planetShader = new ShaderProgram("planetShader");
		sunShader = new ShaderProgram("sunShader");
		sunGlowShader = new ShaderProgram("sunGlowShader");
		atmosphereShader = new ShaderProgram("atmosphereShader");
	}

	private void initGameObjects() 
	{
		planet = new Planet(6000.0f, new Vector3f(0f, 0f, 0f));
		hud = new HeadsUpDisplay(0, 0, "arial_nm.png", Info.camera.getPosition(), new Vector3f(0.0f, 0.0f, 0.0f), 0f, 0, 0, 0);
		sun = new BillBoard(new Vector3f(-100000.0f, 0.0f, 0.0f), 100000.0f);
		sunGlow = new BillBoard(new Vector3f(-99000.0f, 0.0f, 0.0f), 1.0f);
	}

	@Override
	public void update(int deltaTime) 
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //DO NOT MOVE THIS LINE! ....THERE IS A REASON THAT IT IS NOT IN RENDERER;
		
		cameraControl = Info.camera.getCameraControl();
		Info.camera = cameraControl.handleInput(deltaTime);
		
		Matrix4f.mul(Info.camera.getViewMatrix(), sun.getModelMatrix(), modelViewMatrix);
		
		glDisable(GL_DEPTH_TEST);
	    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_CONSTANT_ALPHA);
		
		glUseProgram(sunGlowShader.getId());
		{
			sunGlowShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
			sunGlowShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
			sunGlowShader.loadUniformVec3f(sunGlow.getPosition(), "billboardCenter");
			renderer.renderGameObject(sunGlow, sunGlowTexture, GL_TRIANGLES);
		}
		glUseProgram(0);

		glUseProgram(sunShader.getId());
		{
			sunShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
			sunShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
			sunShader.loadUniformVec3f(sun.getPosition(), "billboardCenter");
			sunShader.loadUniformVec3f(Info.camera.getLocalUpVector(), "cameraUp");
			sunShader.loadUniformVec3f(Info.camera.getLocalRightVector(), "cameraRight");	
			renderer.renderGameObject(sun, sunTexture, GL_TRIANGLES);
		}
		glUseProgram(0);
		
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		Matrix4f.mul(Info.camera.getViewMatrix(), planet.getAtmosphere().getSphere().getModelMatrix(), modelViewMatrix);
		
		Vector3f.sub(sun.getPosition(), planet.getAtmosphere().getPosition(), lightDirection);
		lightDirection.normalise();
		glFrontFace(GL_CW);
		glUseProgram(atmosphereShader.getId());
		{
			planet.getAtmosphere().loadSpecificUniforms(atmosphereShader);
			atmosphereShader.loadUniform1f(GameUtils.getDistanceBetween(planet.getPosition(), Info.camera.getPosition()), "cameraHeight");
			atmosphereShader.loadUniformVec3f(lightDirection, "lightDirection");
			atmosphereShader.loadUniformVec3f(Info.camera.getPosition(), "cameraPosition");
			atmosphereShader.loadUniform1f(1.0f / (planet.getAtmosphere().getSphere().getRadius() - planet.getRadius()), "fScale");
			//		atmosphereShader.loadUniform1f(planet.getAtmosphere().getSphere().getRadius() * 0.25f, "fScale");
			atmosphereShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
			atmosphereShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
			atmosphereShader.loadUniformMat4f(normalMatrix, "normalMatrix", true);
			atmosphereShader.loadUniformVec3f(Info.camera.getPosition(), "cameraPosition");
			atmosphereShader.loadUniform1f(planet.getRadius(), "planetRadius");
			renderer.renderGameObject(planet.getAtmosphere().getSphere(), null, GL_TRIANGLES);
		}
		glUseProgram(0);
		glFrontFace(GL_CCW);
		
		glEnable(GL_DEPTH_TEST);
		
		planetCamDistance = GameUtils.getDistanceBetween(planet.getPosition(), Info.camera.getPosition()) - planet.getRadius();

		Matrix4f.mul(Info.camera.getViewMatrix(), planet.getMesh().getModelMatrix(), modelViewMatrix);
		Matrix4f.invert(modelViewMatrix, normalMatrix);
		planet.update(planetCamDistance, false);

		glUseProgram(planetShader.getId());
		{
			planetShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
			planetShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
			planetShader.loadUniformMat4f(normalMatrix, "normalMatrix", true);
			planetShader.loadUniformVec3f(sun.getPosition(), "lightPosition");
			planetShader.loadUniformVec3f(Info.camera.getPosition(), "cameraPosition");
			planetShader.loadUniform1f(planet.getRadius(), "radius");
			renderer.renderGameObject(planet.getMesh(), planetTexture, GL_TRIANGLES);
		}
		glUseProgram(0);

		
		hud.update(Info.camera.getPosition(), Info.camera.getLookAt(), planetCamDistance, planet.getActualTriangleCount(), planet.getTotalTriangleCount(), game.timer.getFPS());
		glUseProgram(hudShader.getId());
		{
			hudShader.loadUniformMat4f(orthographicProjectionMatrix, "projectionMatrix", false);
			hudShader.loadUniformMat4f(hud.getMesh().getModelMatrix(), "modelViewMatrix", false);
			hud.getMesh().draw(GL_TRIANGLES);
		}
		glUseProgram(0);
	}

	private void printVersionInfo() 
	{
		System.out.println("GPU Vendor: " + glGetString(GL_VENDOR));
		System.out.println("OpenGL version: " + glGetString(GL_VERSION));
		System.out.println("GLSL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
	}
}
