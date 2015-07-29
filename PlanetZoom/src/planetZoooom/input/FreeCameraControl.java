package planetZoooom.input;

import static org.lwjgl.glfw.GLFW.*;
import planetZoooom.gameContent.FreeCamera;
import planetZoooom.interfaces.ICameraControl;

public class FreeCameraControl implements ICameraControl
{
	private FreeCamera cam;
	private float velocity = ICameraControl.MAX_CAM_SPEED;
	private final static float rollSpeed = 0.00025f * ICameraControl.MAX_CAM_SPEED;
	private boolean boostEnabled;
	
	public FreeCameraControl(FreeCamera cam)
	{
		this.cam = cam;
	}

	@Override
	public FreeCamera handleInput(int deltaTime)
	{
		boostEnabled = Keyboard.isKeyPressed(GLFW_KEY_LEFT_SHIFT);

		if(boostEnabled)
			velocity *= 2;
		
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
		
		if(Keyboard.isKeyPressed(GLFW_KEY_Q))
			cam.addRoll(-rollSpeed * deltaTime);
		
		if(Keyboard.isKeyPressed(GLFW_KEY_E))
			cam.addRoll(rollSpeed * deltaTime);

		
		cam.addYaw((float) Cursor.getDx() / 250.0f);
		cam.addPitch((float) Cursor.getDy() / 250.0f);
			
		if(boostEnabled)
			velocity /= 2;
		
		return cam;	
	}

	@Override
	public void setVelocity(float velocity) 
	{
		this.velocity = velocity;
	}
}
