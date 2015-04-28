package engine;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
public class FirstPersonCamera
{
	private Matrix4f viewMatrix;
	private Vector3f position;
	
	private float yaw = 0.0f;
	private float pitch = 0.0f;
	
	public FirstPersonCamera()
	{
		position = new Vector3f(0.0f, 0.0f, 0.0f);
		viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
	}
	
	public FirstPersonCamera(float x, float y, float z)
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
	
	public void moveForwards(float distance)
	{
	    position.x -= distance * (float) Math.sin(yaw);
	    position.z += distance * (float) Math.cos(yaw);
	}
	 
	public void moveBackwards(float distance)
	{
	    position.x += distance * (float) Math.sin(yaw);
	    position.z -= distance * (float) Math.cos(yaw);
	}
	 
	public void strafeLeft(float distance)
	{
	    position.x -= distance * (float) Math.sin(yaw - (Math.PI / 2.0));
	    position.z += distance * (float) Math.cos(yaw - (Math.PI / 2.0));
	}
	 
	public void strafeRight(float distance)
	{
	    position.x -= distance * (float) Math.sin(yaw + (Math.PI / 2.0));
	    position.z += distance * (float) Math.cos(yaw + (Math.PI / 2.0));
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
		this.yaw = yaw;
	}
	
	public void setPitch(float pitch)
	{
		this.pitch = pitch;
	}
	
	public void addYaw(float amount)
	{
		yaw += amount;
	}
	
	public void addPitch(float amount)
	{
		pitch += amount;
	}
	
	public Matrix4f getViewMatrix()
	{
		updateViewMatrix();
		return viewMatrix;
	}
	
	private void updateViewMatrix()
	{
		viewMatrix.setIdentity();
		
        //pitch - x-axis
        viewMatrix.rotate(pitch, new Vector3f(1.0f, 0.0f, 0.0f));
        
        //yaw - y-axis
        viewMatrix.rotate(yaw, new Vector3f(0.0f, 1.0f, 0.0f));
        
        viewMatrix.translate(position);
	}
	
}
