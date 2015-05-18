package game;

import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import geometry.Sphere;
import input.ICameraControl;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

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
    }

    @Override
    public void update()
    {
        ICameraControl cameraControl = camera.getCameraControl();
        this.camera = cameraControl.handleInput();
        
    }

    @Override
    public void render()
    {
        //initRenderer();
    	renderer.render(new Planet(new Sphere(4, new Vector4f(1, 1, 1, 1))), camera.getViewMatrix());
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
