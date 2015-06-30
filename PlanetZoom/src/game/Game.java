package game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClear;
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

public class Game implements IGame {
	private static CoreEngine game;
	private Renderer renderer;

	private float fovParam = 45.0f;

	// GAMEOBJECTS
	private Planet planet;
	private Sun sun;
	private HeadsUpDisplay hud;

	// TEXTURES
	private Texture planetTexture;
	private Texture sunTexture;

	// SHADERS
	private ShaderProgram toonShader;
	private ShaderProgram hudShader;
	private ShaderProgram sunShader;
	
	// MATRICES
	private Matrix4f modelViewMatrix;
	private Matrix4f normalMatrix;
	
	public static void main(String[] args) 
	{
		game = new CoreEngine(new Game());
		game.start();
	}

	@Override
	public void init() {
		printVersionInfo();

		Info.camera = new FreeCamera(0.0f, 0.0f, 5.0f);
		Info.projectionMatrix = engine.utils.MatrixUtils.perspectiveProjectionMatrix(fovParam, game.getWindowWidth(), game.getWindowHeight());
		modelViewMatrix = new Matrix4f();
		normalMatrix = new Matrix4f();
		renderer = new Renderer();
		
	
		initTextures();
		initShaders();
		initGameObjects();
		
		Info.planet = planet;
	}

	private void initTextures() {
		planetTexture = new Texture("src/res/textures/uv_test.png");
		sunTexture = new Texture("src/res/textures/sun.png");
	}

	private void initShaders() {
		hudShader = new ShaderProgram("HUDShader");
		toonShader = new ShaderProgram("toonShader");
		sunShader = new ShaderProgram("sunShader");
	}

	private void initGameObjects() {
		planet = new Planet(3f, new Vector3f(0f, 0f, 0f));
		sun = new Sun(new Vector3f(10.0f, 0.0f, 0.0f));
		hud = new HeadsUpDisplay(0, 0, "arial_nm.png", Info.camera.getPosition(), new Vector3f(0.0f, 0.0f, 0.0f), 0f, 0, 0, 0);
	}

	@Override
	public void update(int deltaTime) {

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //DO NOT MOVE THIS LINE! ....THERE IS A REASON THAT IT IS NOT IN RENDERER;
		
		ICameraControl cameraControl = Info.camera.getCameraControl();
		Info.camera = cameraControl.handleInput(deltaTime);

		float planetCamDistance = GameUtils.getDistanceBetween(planet.getPosition(), Info.camera.getPosition()) - planet.getRadius();

		Matrix4f.mul(planet.getMesh().getModelMatrix(), Info.camera.getViewMatrix(), modelViewMatrix);
		Matrix4f.invert(modelViewMatrix, normalMatrix);

		planet.update(Info.camera, planetCamDistance, false);

		glUseProgram(toonShader.getId());
			toonShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
			toonShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
			toonShader.loadUniformMat4f(normalMatrix, "normalMatrix", true);
			toonShader.loadUniformVec3f(Info.camera.getPosition(), "cameraPosition");
			toonShader.loadUniform1f(planet.getRadius(), "radius");
			renderer.renderGameObject(planet.getMesh(), planetTexture, GL_TRIANGLES);
		glUseProgram(0);

		Matrix4f.mul(sun.getModelMatrix(), Info.camera.getViewMatrix(), modelViewMatrix);
		
		glUseProgram(sunShader.getId());
			sunShader.loadUniformMat4f(Info.projectionMatrix, "projectionMatrix", false);
			sunShader.loadUniformMat4f(modelViewMatrix, "modelViewMatrix", false);
			renderer.renderGameObject(sun, sunTexture, GL_TRIANGLES);
		glUseProgram(0);

		hud.update(Info.camera.getPosition(), Info.camera.getLookAt(), planetCamDistance, planet.getActualTriangleCount(), planet.getTotalTriangleCount(), game.timer.getFPS());

		glUseProgram(hudShader.getId());
			Matrix4f orthographicProjectionMatrix = MatrixUtils.orthographicProjectionMatrix(0, -game.getWindowWidth(), -game.getWindowHeight(), 0.0f, -1.0f, 1.0f);
			hudShader.loadUniformMat4f(orthographicProjectionMatrix, "projectionMatrix", false);
			hudShader.loadUniformMat4f(new Matrix4f(), "modelViewMatrix", false);
			hud.getMesh().draw(GL_TRIANGLES);
		glUseProgram(0);
	}

	private void printVersionInfo() {
		System.out.println("GPU Vendor: " + glGetString(GL_VENDOR));
		System.out.println("OpenGL version: " + glGetString(GL_VERSION));
		System.out.println("GLSL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
	}
}
