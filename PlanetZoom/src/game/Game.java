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
	private Matrix4f projectionMatrix;
	private ICamera camera; 
	private Renderer renderer;
	
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
        initProjectionMatrix(45.0f);
        initCamera();
        initRenderer();
        
        planet = new Planet(1f, new Vector3f(0f, 0f, 0f));
    }

    @Override
    public void update()
    {
        ICameraControl cameraControl = camera.getCameraControl();
        this.camera = cameraControl.handleInput();
        
        FirstPersonCamera cam = (FirstPersonCamera)camera;
        
        float distance = cam.getDistanceToPlanetSurface(planet);
        int subdivisions;
        
        System.out.println(distance);
        
        if(distance < 1) {
        	subdivisions = 9;
        } else if(distance < 10) {
        	subdivisions = 8;
        } else if(distance < 20) {
        	subdivisions = 7;
        } else if(distance < 30) {
        	subdivisions = 6;
        } else if(distance < 40) {
        	subdivisions = 5;
        } else if(distance < 50) {
        	subdivisions = 4;
        } else if(distance < 60) {
        	subdivisions = 3;
        } else if(distance < 70) {
        	subdivisions = 2;
        } else if(distance < 80) {
        	subdivisions = 1;
        } else {
        	subdivisions = 1;
        } 
        // distance 1 -> 7
        // distance 10 -> 2
//        subdivisions = 1;
        
        planet.update(subdivisions);
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
    
    private void initProjectionMatrix(float fovParam)
	{
		projectionMatrix = new Matrix4f();
		float fov = fovParam;
		float zFar = 500.0f;
		float zNear = 0.1f;
		float aspectRatio = 4.0f/3.0f;				
		float frustumLength = zFar - zNear;
		float yScale = (float)(1.0f/Math.tan(Math.toRadians(fov/2.0f)));
		float xScale = yScale / aspectRatio;

		projectionMatrix.setZero();
		projectionMatrix.m00 = xScale;		
		projectionMatrix.m11 = yScale;								
		projectionMatrix.m22 =  -((zFar + zNear)/frustumLength);	
		projectionMatrix.m32 = -((2 * zNear * zFar) / frustumLength);
		projectionMatrix.m23 =  -1.0f;								
	}   
    private void initRenderer()
    {
    	renderer = new Renderer(projectionMatrix);
    }
    
    private void printVersionInfo()
    {
        System.out.println("GPU Vendor: " + glGetString(GL_VENDOR));
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));
        System.out.println("OpenGL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
    }
}
