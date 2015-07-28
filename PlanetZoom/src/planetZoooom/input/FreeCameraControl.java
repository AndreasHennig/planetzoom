package planetZoooom.input;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import planetZoooom.gameContent.FreeCamera;
import planetZoooom.interfaces.ICameraControl;

public class FreeCameraControl implements ICameraControl
{
	private FreeCamera cam;
	private float velocity = ICameraControl.MAX_CAM_SPEED;
	
	public FreeCameraControl(FreeCamera cam)
	{
		this.cam = cam;
	}

	@Override
	public FreeCamera handleInput(int deltaTime)
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
		
		if(Keyboard.isKeyPressed(GLFW_KEY_E))
			cam.addRoll(0.1f * velocity * deltaTime);
		
		if(Keyboard.isKeyPressed(GLFW_KEY_Q))
			cam.addRoll(-0.1f * velocity * deltaTime);
		
		cam.addYaw((float) Cursor.getDx() / 250.0f);
		cam.addPitch((float) Cursor.getDy() / 250.0f);
			
		return cam;	
	}

	@Override
	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}
}
