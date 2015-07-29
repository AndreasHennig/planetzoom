package planetZoooom.interfaces;


public interface ICameraControl 
{
	public final static float MIN_CAM_SPEED = 0.05f;
	public final static float MAX_CAM_SPEED = 5f;
	
	public ICamera handleInput(int deltaTime);
	public void setVelocity(float velocity);
}
