package game;

import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import input.ICameraControl;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.CoreEngine;
import engine.FirstPersonCamera;
import engine.FreeCamera;
import engine.ICamera;
import engine.IGame;
import engine.Planet;
import engine.Renderer;
import engine.utils.GameUtils;

public class Game implements IGame
{
	private static CoreEngine game;
	private ICamera camera; 
	private Renderer renderer;

	float fovParam = 45.0f;

	private Planet planet;
	
    public static void main(String[] args)
    {
        game = new CoreEngine(new Game());
        game.start();
    }

    @Override
    public void init()
    {
        printVersionInfo();
        
        initCamera();
        initRenderer();
        
        planet = new Planet(3f, new Vector3f(0f, 0f, 0f));
    }

    @Override
    public void update(int deltaTime)
    {
        ICameraControl cameraControl = camera.getCameraControl();
        this.camera = cameraControl.handleInput(deltaTime);
        
        planet.update(3);
        //planet.update(camera);

//        planet.update(subdivisions);
        	

        float planetCamDistance = GameUtils.getDistanceBetween(planet.getPosition(), camera.getPosition()) - planet.getRadius();;
	
        planet.update(planetCamDistance, false);
    }

    @Override
    public void render()
    {
    	renderer.render(planet, camera);
    }

    private void initCamera()
    {
        camera = new FreeCamera(0.0f, 0.0f, 5.0f);
    }
    
    private void initRenderer()
    {
    	renderer = new Renderer(fovParam, game.getWindowWidth(), game.getWindowHeight());
    }
    
    private void printVersionInfo()
    {
        System.out.println("GPU Vendor: " + glGetString(GL_VENDOR));
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));
        System.out.println("GLSL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
    }
}
