package game;

import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import input.ICameraControl;

import org.lwjgl.util.vector.*;

import engine.CoreEngine;
import engine.FirstPersonCamera;
import engine.ICamera;
import engine.IGame;
import engine.Planet;
import engine.Renderer;

public class Game implements IGame
{
	private ICamera camera; 
	private Renderer renderer;
	float fovParam = 45.0f;
	
	private long windowHandle;
	private Planet planet;
	
    public static void main(String[] args)
    {
        CoreEngine game = new CoreEngine(new Game());
        game.start();
    }

    @Override
    public void init(long windowHandle)
    {
        this.windowHandle = windowHandle;
        printVersionInfo();
        
        initCamera();
        initRenderer();
        
        planet = new Planet(1f, new Vector3f(0f, 0f, 0f));
    }

    @Override
    public void update()
    {
        ICameraControl cameraControl = camera.getCameraControl();
        this.camera = cameraControl.handleInput();
        
        planet.update(1);
    }

    @Override
    public void render()
    {
    	renderer.render(planet, camera.getViewMatrix());
    }

    private void initCamera()
    {
        camera = new FirstPersonCamera(windowHandle, 0.0f, 0.0f, -2f);
    }
    
    private void initRenderer()
    {
    	renderer = new Renderer(fovParam);
    }
    
    private void printVersionInfo()
    {
        System.out.println("GPU Vendor: " + glGetString(GL_VENDOR));
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));
        System.out.println("OpenGL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
    }
}
