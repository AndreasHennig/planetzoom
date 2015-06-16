package game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import input.ICameraControl;
import lenz.utils.ShaderProgram;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.CoreEngine;
import engine.FreeCamera;
import engine.HeadsUpDisplay;
import engine.ICamera;
import engine.IGame;
import engine.Planet;
import engine.Renderer;
import engine.utils.GameUtils;
import engine.utils.MatrixUtils;
import engine.utils.Texture;

public class Game implements IGame {
	private static CoreEngine game;
	private ICamera camera;
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

		camera = new FreeCamera(0.0f, 0.0f, 5.0f);
		renderer = new Renderer();

		initTextures();
		initShaders();
		initGameObjects();
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
		hud = new HeadsUpDisplay(0, 0, "arial_nm.png", this.camera.getPosition(), new Vector3f(0.0f, 0.0f, 0.0f), 0, 0);
	}

	@Override
	public void update(int deltaTime) {
		ICameraControl cameraControl = camera.getCameraControl();
		this.camera = cameraControl.handleInput(deltaTime);

		float planetCamDistance = GameUtils.getDistanceBetween(planet.getPosition(), camera.getPosition()) - planet.getRadius();
		;

		Matrix4f perspectiveProjectionMatrix = MatrixUtils.perspectiveProjectionMatrix(fovParam, game.getWindowWidth(), game.getWindowHeight());
		Matrix4f viewMatrix = camera.getViewMatrix();
		Matrix4f modelViewMatrix = new Matrix4f();
		Matrix4f.mul(planet.getMesh().getModelMatrix(), viewMatrix, modelViewMatrix);
		Matrix4f normalMatrix = new Matrix4f();
		Matrix4f.invert(modelViewMatrix, normalMatrix);

		planet.update(planetCamDistance, false);
		glUseProgram(toonShader.getId());
		ShaderProgram.loadUniformMat4f(toonShader.getId(), perspectiveProjectionMatrix, "projectionMatrix", false);
		ShaderProgram.loadUniformMat4f(toonShader.getId(), viewMatrix, "modelViewMatrix", false);
		ShaderProgram.loadUniformMat4f(toonShader.getId(), normalMatrix, "normalMatrix", true);
		ShaderProgram.loadUniformVec3f(toonShader.getId(), camera.getPosition(), "cameraPosition");
		ShaderProgram.loadUniform1f(toonShader.getId(), planet.getRadius(), "radius");
		glUseProgram(0);

		renderer.renderGameObject(planet.getMesh(), planetTexture, toonShader.getId(), GL_TRIANGLES);

		hud.update(this.camera.getPosition(), camera.getLookAt(), planetCamDistance, 0);
		glUseProgram(hudShader.getId());
		Matrix4f orthographicProjectionMatrix = MatrixUtils.orthographicProjectionMatrix(0, -game.getWindowWidth(), -game.getWindowHeight(), 0.0f, -1.0f, 1.0f);
		ShaderProgram.loadUniformMat4f(hudShader.getId(), orthographicProjectionMatrix, "projectionMatrix", false);
		ShaderProgram.loadUniformMat4f(hudShader.getId(), new Matrix4f(), "modelViewMatrix", false);
		hud.getMesh().draw(GL_TRIANGLES);
		glUseProgram(0);
	}

	private void printVersionInfo() {
		System.out.println("GPU Vendor: " + glGetString(GL_VENDOR));
		System.out.println("OpenGL version: " + glGetString(GL_VERSION));
		System.out.println("GLSL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
	}
}
