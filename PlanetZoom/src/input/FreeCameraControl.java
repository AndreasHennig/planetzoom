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
	private float inputSensitivity = 2.5f;
	
	public FreeCameraControl(FreeCamera cam)
	{
		this.cam = cam;
	}

	@Override
	public FreeCamera handleInput(int deltaTime)
	{
		if(Keyboard.isKeyPressed(GLFW_KEY_W))
			cam.moveForwards(inputSensitivity * deltaTime);
		
		if(Keyboard.isKeyPressed(GLFW_KEY_S))
			cam.moveBackwards(inputSensitivity * deltaTime);
		
		if(Keyboard.isKeyPressed(GLFW_KEY_A))
			cam.strafeLeft(inputSensitivity * deltaTime);

		if(Keyboard.isKeyPressed(GLFW_KEY_D))
			cam.strafeRight(inputSensitivity * deltaTime);
			
		if(Keyboard.isKeyPressed(GLFW_KEY_SPACE))
			cam.moveUp(inputSensitivity * deltaTime);
		
		if(Keyboard.isKeyPressed(GLFW_KEY_LEFT_CONTROL))
			cam.moveDown(inputSensitivity * deltaTime);
		
		if(Keyboard.isKeyPressed(GLFW_KEY_E))
			cam.addRoll(0.1f * inputSensitivity * deltaTime);
		
		if(Keyboard.isKeyPressed(GLFW_KEY_Q))
			cam.addRoll(-0.1f * inputSensitivity * deltaTime);
		
		cam.addYaw((float) Cursor.getDx() / 250.0f);
		cam.addPitch((float) Cursor.getDy() / 250.0f);
			
		return cam;	
	}
}
