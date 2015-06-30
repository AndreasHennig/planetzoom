package game;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
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
import engine.ICamera;
import engine.IGame;
import engine.Info;
import engine.Planet;
import engine.Renderer;
import engine.utils.GameUtils;
import engine.utils.MatrixUtils;
import engine.utils.Texture;

public class Game implements IGame {
	private static CoreEngine game;
	private Renderer renderer;

	private float fovParam = 45.0f;

	// GAMEOBJECTS
	private Planet planet;
	private HeadsUpDisplay hud;

	// TEXTURES
	private Texture planetTexture;

	// SHADERS
	private ShaderProgram toonShader;
	private ShaderProgram hudShader;

	public static void main(String[] args) {
		game = new CoreEngine(new Game());
		game.start();
	}

	@Override
	public void init() {
		printVersionInfo();

		Info.camera = new FreeCamera(0.0f, 0.0f, 5.0f);
		
		Info.projectionMatrix = engine.utils.MatrixUtils.perspectiveProjectionMatrix(fovParam, game.getWindowWidth(), game.getWindowHeight());
		
		renderer = new Renderer();
		
		initTextures();
		initShaders();
		initGameObjects();

		planet = new Planet(3f, new Vector3f(0f, 0f, 0f));
		Info.planet = planet;
	}

	/**
	 * loads textures
	 */
	private void initTextures() {
		planetTexture = new Texture("src/res/textures/crypt_wall.png");
	}

	/**
	 * loads shaderPrograms
	 */
	private void initShaders() {
		hudShader = new ShaderProgram("HUDShader");
		toonShader = new ShaderProgram("toonShader");
	}

	private void initGameObjects() {
		planet = new Planet(3f, new Vector3f(0f, 0f, 0f));
		hud = new HeadsUpDisplay(0, 0, "arial_nm.png", Info.camera.getPosition(), new Vector3f(0.0f, 0.0f, 0.0f), 0, 0);
	}

	@Override
	public void update(int deltaTime) {
		// 3D Rendering.
		ICameraControl cameraControl = Info.camera.getCameraControl();
		Info.camera = cameraControl.handleInput(deltaTime);

		float planetCamDistance = GameUtils.getDistanceBetween(planet.getPosition(), Info.camera.getPosition()) - planet.getRadius();

		Matrix4f modelViewMatrix = new Matrix4f();
		Matrix4f.mul(planet.getMesh().getModelMatrix(), Info.camera.getViewMatrix(), modelViewMatrix);
		Matrix4f normalMatrix = new Matrix4f();
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

		// 2D Rendering.
		hud.update(Info.camera.getPosition(), Info.camera.getLookAt(), planetCamDistance, game.timer.getFPS());

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
