package engine;

import input.FreeCameraControl;
import input.ICameraControl;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class FreeCamera implements ICamera
{
	private Vector3f target;
	private Vector3f direction;
	private Vector3f position;
	
	private Vector3f right;
	private Vector3f up;
	
	
	private float yaw;
	private float pitch;
	
	private Matrix4f view;
	
	public FreeCamera()
	{
		target = new Vector3f(0, 0, 0);
		direction = (Vector3f) Vector3f.add(position, target, direction).normalise();
		
		right = (Vector3f) Vector3f.cross(new Vector3f(0, 1, 0), direction, right).normalise();
		up = (Vector3f) Vector3f.cross(direction, right, up).normalise();
		view = new Matrix4f();
	}
	
	public FreeCamera(float x, float y, float z)
	{
		position = new Vector3f(x, y ,z);
		target = new Vector3f(0, 0, 0);
		direction = new Vector3f();
		Vector3f.add(position, target, direction);
		direction.normalise();
		
		right = new Vector3f();
		Vector3f.cross(new Vector3f(0, 1, 0), direction, right);
		right.normalise();
		
		up = new Vector3f();
		Vector3f.cross(direction, right, up);
		up.normalise();
		
		view = new Matrix4f();
	}
	
	
	public Matrix4f getViewMatrix()
	{
		view = new Matrix4f();
		view.setIdentity();	
		view.m00 = right.x;
		view.m10 = right.y;
		view.m20 = right.z;
		
		view.m01 = up.x;
		view.m11 = up.y;
		view.m21 = up.z;
		
		view.m02 = direction.x;
		view.m12 = direction.y;
		view.m22 = direction.z;
		
		view.translate(new Vector3f(-position.x, -position.y, -position.z));

		return view;
	}


	@Override
	public ICameraControl getCameraControl()
	{
		return new FreeCameraControl(this);
	}

	@Override
	public float getDistanceToPlanetSurface(Planet planet)
	{
		// TODO Auto-generated method stub
		return 0;
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
		updateOrientation();
	}
	
	public void setPitch(float pitch)
	{
		this.pitch = (float) (pitch % (2 * Math.PI));
		updateOrientation();
	}
	
	public void addYaw(float amount)
	{
		yaw = (float) ((yaw + amount) % (2 * Math.PI));
		updateOrientation();
	}
	
	public void addPitch(float amount)
	{
		pitch = (float) ((pitch + amount) % (2 * Math.PI));
		updateOrientation();
	}
	
	private void updateOrientation()
	{
		direction.x = (float) (Math.cos(pitch) * Math.cos(yaw));
		direction.y = (float) Math.sin(pitch);
		direction.z = (float) (Math.cos(pitch) * Math.sin(yaw));
		

		Vector3f.cross(new Vector3f(0, 1, 0), direction, right);
		right.normalise();
		
		if(pitch > Math.PI / 2.0 || pitch < - (Math.PI / 2.0))
			right.negate();
		
		up = (Vector3f) Vector3f.cross(direction, right, up).normalise();	
		
		System.out.println("direction: " + (direction));
		System.out.println("up: " + up);
		System.out.println("right: " + right);
	}
	
	public void moveForwards(float amount)
	{
		this.position.x -= direction.x * amount;
		this.position.y -= direction.y * amount;
		this.position.z -= direction.z * amount;
	}
	
	public void moveBackwards(float amount)
	{
		this.position.x += direction.x * amount;
		this.position.y += direction.y * amount;
		this.position.z += direction.z * amount;
	}
	
	public void strafeLeft(float amount)
	{
		this.position.x -= right.x * amount;
		this.position.y -= right.y * amount;
		this.position.z -= right.z * amount;
	}
	
	public void strafeRight(float amount)
	{
		this.position.x += right.x * amount;
		this.position.y += right.y * amount;
		this.position.z += right.z * amount;
	}
	
	public void moveUp(float amount)
	{
		this.position.x += up.x * amount;
		this.position.y += up.y * amount;
		this.position.z += up.z * amount;
	}
	
	public void moveDown(float amount)
	{
		this.position.x -= up.x * amount;
		this.position.y -= up.y * amount;
		this.position.z -= up.z * amount;
	}
}
