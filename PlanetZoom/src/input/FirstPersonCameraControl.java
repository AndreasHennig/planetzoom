package input;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

import engine.FirstPersonCamera;

public class FirstPersonCameraControl implements ICameraControl
{
	private FirstPersonCamera cam;
	
	private  GLFWKeyCallback keyCallback;
	private  GLFWCursorPosCallback cursorPosCollback;
	private final long windowHandle;
	
	public FirstPersonCameraControl(FirstPersonCamera cam, long windowHandle)
	{
		this.windowHandle = windowHandle;
		this.cam = cam;
		init();
	}

	@Override
	public FirstPersonCamera handleInput()
	{
		return cam;	
	}
	
	@Override
	public void releaseCallbacks()
	{
		release();
		
	}
	
	private void release()
	{
		keyCallback.release();
		cursorPosCollback.release();
	}
	private void init()
	{
		keyCallback = new GLFWKeyCallback()
		{
			
			@Override
			public void invoke (long window, int key, int scancode, int action, int mods) 
			{				
				//if(action == GLFW_PRESS)
					//System.out.println("Key Pressed: " + key);
				
				if(action == GLFW_PRESS || action == GLFW_REPEAT)
				{
					if(key == GLFW_KEY_W)
					{
						cam.moveForwards(0.1f);
					}
					
					else if(key == GLFW_KEY_A)
					{
						cam.strafeLeft(0.1f);
					}
					
					else if(key == GLFW_KEY_S)
					{
						cam.moveBackwards(0.1f);
					}
					
					else if(key == GLFW_KEY_D)
					{
						cam.strafeRight(0.1f);
					}
					
					else if(key == GLFW_KEY_SPACE)
					{
						
					}
					
					else if(key == GLFW_KEY_LEFT_CONTROL)
					{
						
					}
				}
				
			}
		};
		
		
		cursorPosCollback = new GLFWCursorPosCallback()
		{
			
			@Override
			public void invoke(long window, double xpos, double ypos)
			{
					
			}
		};
		
		
		glfwSetKeyCallback(windowHandle, keyCallback);
		glfwSetCursorPosCallback(windowHandle, cursorPosCollback);
		
	}	
}
