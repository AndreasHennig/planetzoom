package engine;

import input.ICameraControl;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public interface ICamera
{
	public Matrix4f getViewMatrix();
	public ICameraControl getCameraControl();
	public Vector3f getPosition();
}
