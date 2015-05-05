
package input;



import static org.lwjgl.glfw.GLFW.*;

import engine.FirstPersonCamera;

public class FirstPersonCameraControl implements ICameraControl
{
	private FirstPersonCamera cam;

	public FirstPersonCameraControl(FirstPersonCamera cam)
	{
		this.cam = cam;
	}

	@Override
	public FirstPersonCamera handleInput()
	{
		if(Keyboard.isKeyPressed(GLFW_KEY_W))
			cam.moveForwards(0.1f);
		
		if(Keyboard.isKeyPressed(GLFW_KEY_S))
			cam.moveBackwards(0.1f);
		
		if(Keyboard.isKeyPressed(GLFW_KEY_A))
			cam.strafeLeft(0.1f);

		if(Keyboard.isKeyPressed(GLFW_KEY_D))
			cam.strafeRight(0.1f);
			
		if(Keyboard.isKeyPressed(GLFW_KEY_SPACE))
			cam.moveUp(0.1f);
		
		if(Keyboard.isKeyPressed(GLFW_KEY_LEFT_CONTROL))
			cam.moveDown(0.1f);

		cam.addYaw((float) - Cursor.getDx() /250.0f);
			
		cam.addPitch((float) Cursor.getDy() / 250.0f);

		

			
		return cam;	
	}
	
}
