package engine;

import input.ICameraControl;

import org.lwjgl.util.vector.Matrix4f;

public interface ICamera
{
	public Matrix4f getViewMatrix();
	public ICameraControl getCameraControl();
}
