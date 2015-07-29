package planetZoooom.input;

import static org.lwjgl.glfw.GLFW.*;
import planetZoooom.gameContent.FirstPersonCamera;
import planetZoooom.interfaces.ICameraControl;

public class FirstPersonCameraControl implements ICameraControl
{
	private FirstPersonCamera cam;
	private float velocity = 0.01f;

	public FirstPersonCameraControl(FirstPersonCamera cam)
	{
		this.cam = cam;
	}

	@Override
	public FirstPersonCamera handleInput(int deltaTime)
	{
		if(Keyboard.isKeyPressed(GLFW_KEY_W))
			cam.moveForwards(velocity * deltaTime);

		if(Keyboard.isKeyPressed(GLFW_KEY_S))
			cam.moveBackwards(velocity * deltaTime);

		if(Keyboard.isKeyPressed(GLFW_KEY_A))
			cam.strafeLeft(velocity * deltaTime);

		if(Keyboard.isKeyPressed(GLFW_KEY_D))
			cam.strafeRight(velocity * deltaTime);

		if(Keyboard.isKeyPressed(GLFW_KEY_SPACE))
			cam.moveUp(velocity * deltaTime);

		if(Keyboard.isKeyPressed(GLFW_KEY_LEFT_CONTROL))
			cam.moveDown(velocity * deltaTime);

		cam.addYaw((float) -Cursor.getDx() / 250.0f);
		cam.addPitch((float) Cursor.getDy() / 250.0f);

		return cam;
	}

	@Override
	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}
}