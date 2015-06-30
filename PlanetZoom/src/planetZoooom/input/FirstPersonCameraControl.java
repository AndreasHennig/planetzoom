package planetZoooom.input;

import static org.lwjgl.glfw.GLFW.*;
import planetZoooom.gameContent.FirstPersonCamera;
import planetZoooom.interfaces.ICameraControl;

public class FirstPersonCameraControl implements ICameraControl
{
	private FirstPersonCamera cam;
	private float inputSensitivity = 0.01f;

	public FirstPersonCameraControl(FirstPersonCamera cam)
	{
		this.cam = cam;
	}

	@Override
	public FirstPersonCamera handleInput(int deltaTime)
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

		cam.addYaw((float) -Cursor.getDx() / 250.0f);
		cam.addPitch((float) Cursor.getDy() / 250.0f);

		return cam;
	}
}