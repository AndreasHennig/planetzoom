package input;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import engine.FreeCamera;

public class FreeCameraControl implements ICameraControl
{
	private FreeCamera cam;

	public FreeCameraControl(FreeCamera cam)
	{
		this.cam = cam;
	}

	@Override
	public FreeCamera handleInput()
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
		
		if(Keyboard.isKeyPressed(GLFW_KEY_E))
			cam.addRoll(0.01f);
		
		if(Keyboard.isKeyPressed(GLFW_KEY_Q))
			cam.addRoll(-0.01f);
		
		cam.addYaw((float)  Cursor.getDx() /250.0f);
		cam.addPitch((float) Cursor.getDy() / 250.0f);
			
		return cam;	
	}
}
