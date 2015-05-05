package engine;

import input.FirstPersonCameraControl;
import input.ICameraControl;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
public class FirstPersonCamera implements ICamera
{
	private Matrix4f viewMatrix;
	private Vector3f position;
	
	private float yaw = 0.0f;
	private float pitch = 0.0f;
	private boolean invertYAxis;
	
	public FirstPersonCamera()
	{
		position = new Vector3f(0.0f, 0.0f, 0.0f);
		viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
	}
	
	public FirstPersonCamera(long windowHandle, float x, float y, float z)
	{	
		position = new Vector3f(x, y , z);
		viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
	}
	
	public FirstPersonCamera(Vector3f position)
	{	
		this.position = position;
		viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
	}
	
	@Override
	public ICameraControl getCameraControl()
	{
		return new FirstPersonCameraControl(this);	
	}
	
	public void moveForwards(float distance)
	{
		float x = distance * (float) Math.sin(yaw);
		float z = distance * (float) Math.cos(yaw);
		
		System.out.println("z: " + z);
		System.out.println("x: " + x);
		
	    position.x += distance * (float) Math.sin(yaw);
	    position.z += distance * (float) Math.cos(yaw);
	}
	 
	public void moveBackwards(float distance)
	{
	    position.x -= distance * (float) Math.sin(yaw);
	    position.z -= distance * (float) Math.cos(yaw);
	}
	
	public void moveUp(float distance)
	{
		position.y -= distance;
	}
	
	public void moveDown(float distance)
	{
		position.y += distance;
	}
	 
	public void strafeLeft(float distance)
	{
		float x = distance * (float) Math.sin(yaw);
		float z = distance * (float) Math.cos(yaw);
		
		System.out.println("z: " + z);
		System.out.println("x: " + x);
		
	    position.x += distance * (float) Math.sin(yaw + (Math.PI / 2.0));
	    position.z += distance * (float) Math.cos(yaw + (Math.PI / 2.0));
	}
	 
	public void strafeRight(float distance)
	{
	    position.x -= distance * (float) Math.sin(yaw + (Math.PI / 2.0));
	    position.z -= distance * (float) Math.cos(yaw + (Math.PI / 2.0));
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	public void setPosition(Vector3f position)
	{
		this.position = position;
	}
	
	public void setPosition(float x, float y, float z)
	{
		position = new Vector3f(x, y, z);
	}
	
	public float getYaw()
	{
		return yaw;
	}
	
	public float getPitch()
	{
		return pitch;
	}
	
	public void setYaw(float yaw)
	{
		this.yaw = (float) (yaw % (2 * Math.PI));
	}
	
	public void setPitch(float pitch)
	{
		this.pitch = (float) (pitch % (2 * Math.PI));
	}
	
	public void addYaw(float amount)
	{
		yaw = (float) ((yaw + amount) % (2 * Math.PI));
	}
	
	public void addPitch(float amount)
	{
		pitch = (float) ((pitch + amount) % (2 * Math.PI));
	}
	
	public Matrix4f getViewMatrix()
	{
		updateViewMatrix();
		return viewMatrix;
	}
	
	public void invertYAxis()
	{
		invertYAxis = true;
	}
	public void setInvertYAxis(boolean b)
	{
		invertYAxis = b;
	}
	public boolean getYAxisInverted()
	{
		return invertYAxis;
	}
	
	private void updateViewMatrix()
	{
		viewMatrix.setIdentity();
		
        //pitch - x-axis
        viewMatrix.rotate(pitch, new Vector3f(1.0f, 0.0f, 0.0f));
        
        //yaw - y-axis
        if(invertYAxis)
        	viewMatrix.rotate(yaw, new Vector3f(0.0f, 1.0f, 0.0f));
        else
        	viewMatrix.rotate(-yaw, new Vector3f(0.0f, 1.0f, 0.0f));
        
        viewMatrix.translate(position);
	}
}
