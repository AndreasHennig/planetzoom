package game;

import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL20.glUseProgram;
import input.ICameraControl;
import lenz.utils.ShaderProgram;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.CoreEngine;
import engine.FreeCamera;
import engine.HeadsUpDisplay;
import engine.IGame;
import engine.Info;
import engine.Planet;
import engine.Renderer;
import engine.Sun;
import engine.utils.GameUtils;
import engine.utils.MatrixUtils;
import engine.utils.Texture;

public class Game implements IGame 
{
	private static CoreEngine game;
	private Renderer renderer;
	ICameraControl cameraControl;
	private float fovParam = 45.0f;

	// GAMEOBJECTS
	private Planet planet;
	private HeadsUpDisplay hud;
	private Sun sun;

	// TEXTURES
	private Texture planetTexture;
	private Texture sunTexture;

	// SHADERS
	private ShaderProgram toonShader;
	private ShaderProgram hudShader;
	private ShaderProgram atmosphereShader;

	//Matrices
	private Matrix4f modelViewMatrix;
	private Matrix4f normalMatrix;
	private Matrix4f orthographicProjectionMatrix;
	
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
		
		Info.projectionMatrix = engine.utils.MatrixUtils.perspectiveProjectionMatrix(fovParam, game.getWindowWidth(), game.getWindowHeight());
		orthographicProjectionMatrix = MatrixUtils.orthographicProjectionMatrix(0, -game.getWindowWidth(), -game.getWindowHeight(), 0.0f, -1.0f, 1.0f);

		renderer = new Renderer();
		
		initTextures();
		initShaders();
		initGameObjects();

		Info.planet = planet;
	}

	/**
	 * loads textures
	 */
	private void initTextures() 
	{
		planetTexture = new Texture("src/res/textures/uv-test.png");
	}

	/**
	 * loads shaderPrograms
	 */
	private void initShaders() 
	{
		hudShader = new ShaderProgram("HUDShader");
		toonShader = new ShaderProgram("toonShader");
		atmosphereShader = new ShaderProgram("atmosphereShader");
	}

	private void initGameObjects() 
	{
		planet = new Planet(6000.0f, new Vector3f(0f, 0f, 0f));
		hud = new HeadsUpDisplay(0, 0, "arial_nm.png", Info.camera.getPosition(), new Vector3f(0.0f, 0.0f, 0.0f), 0f, 0, 0, 0);
		sun = new Sun(new Vector3f(-100000.0f, 0.0f, 0.0f));
	}

	@Override
	public void update(int deltaTime) 
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// 3D Rendering.
		cameraControl = Info.camera.getCameraControl();
		Info.camera = cameraControl.handleInput(deltaTime);

		float planetCamDistance = GameUtils.getDistanceBetween(planet.getPosition(), Info.camera.getPosition()) - planet.getRadius();

		modelViewMatrix = new Matrix4f();
		Matrix4f.mul(planet.getMesh().getModelMatrix(), Info.camera.getViewMatrix(), modelViewMatrix);
		normalMatrix = new Matrix4f();
		Matrix4f.invert(modelViewMatrix, normalMatrix);

		planet.update(planetCamDistance, false);

		glUseProgram(toonShader.getId());
		toonShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
		toonShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
		toonShader.loadUniformMat4f(normalMatrix, "normalMatrix", true);
		toonShader.loadUniformVec3f(Info.camera.getPosition(), "cameraPosition");
		toonShader.loadUniform1f(planet.getRadius(), "radius");
		renderer.renderGameObject(planet.getMesh(), planetTexture, GL_TRIANGLES);
		glUseProgram(0);
		
		modelViewMatrix = new Matrix4f();
		Matrix4f.mul(planet.getAtmosphere().getSphere().getModelMatrix(), Info.camera.getViewMatrix(), modelViewMatrix);
		
//		System.out.println(planet.getAtmosphere().getSphere().getRadius());
		glFrontFace(GL_CW);
		glUseProgram(atmosphereShader.getId());
		planet.getAtmosphere().loadSpecificUniforms(atmosphereShader);
		atmosphereShader.loadUniform1f(GameUtils.getDistanceBetween(planet.getPosition(), Info.camera.getPosition()), "cameraHeight");
		atmosphereShader.loadUniformVec3f((Vector3f) sun.getPosition().normalise(), "lightPosition");
		atmosphereShader.loadUniformVec3f(Info.camera.getPosition(), "cameraPosition");
		atmosphereShader.loadUniform1f(1.0f / (planet.getAtmosphere().getSphere().getRadius() - planet.getRadius()), "fScale");
//		atmosphereShader.loadUniform1f(planet.getAtmosphere().getSphere().getRadius() * 0.25f, "fScale");
		atmosphereShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
		atmosphereShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
		atmosphereShader.loadUniformMat4f(normalMatrix, "normalMatrix", true);
		atmosphereShader.loadUniformVec3f(Info.camera.getPosition(), "cameraPosition");
		atmosphereShader.loadUniform1f(planet.getRadius(), "planetRadius");
		renderer.renderGameObject(planet.getAtmosphere().getSphere(), null, GL_TRIANGLES);
		glUseProgram(0);
		glFrontFace(GL_CCW);
		
		// 2D Rendering.
		hud.update(Info.camera.getPosition(), Info.camera.getLookAt(), planetCamDistance, planet.getActualTriangleCount(), planet.getTotalTriangleCount(), game.timer.getFPS());
		glUseProgram(hudShader.getId());
		hudShader.loadUniformMat4f(orthographicProjectionMatrix, "projectionMatrix", false);
		hudShader.loadUniformMat4f(new Matrix4f(), "modelViewMatrix", false);
		hud.getMesh().draw(GL_TRIANGLES);
		glUseProgram(0);
	}

	private void printVersionInfo() 
	{
		System.out.println("GPU Vendor: " + glGetString(GL_VENDOR));
		System.out.println("OpenGL version: " + glGetString(GL_VERSION));
		System.out.println("GLSL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
	}
}
