package planetZoooom.utils;

import org.lwjgl.util.vector.Matrix4f;

public class MatrixUtils 
{
    public static Matrix4f perspectiveProjectionMatrix(float fovParam, int width, int height)
	{
		Matrix4f result = new Matrix4f();
		float fov = fovParam;
		float zFar = 90000.0f;
		float zNear = 1f;
		float aspectRatio = (float) width/height;		
		float frustumLength = zFar - zNear;
		float yScale = (float)(1.0f/Math.tan(Math.toRadians(fov/2.0f)));
		float xScale = yScale / aspectRatio;

		result.setZero();
		result.m00 = xScale;
		result.m11 = yScale;
		result.m22 =  -((zFar + zNear)/frustumLength);
		result.m32 = -((2 * zNear * zFar) / frustumLength);
		result.m23 =  -1.0f;	
		
		return result;
	}
	
	public static Matrix4f orthographicProjectionMatrix(float right, float left, float top, float bottom, float near, float far)
	{
		Matrix4f result = new Matrix4f();
		result.setZero();
		
		result.m00 = (2.0f / (right - left));
		result.m11 = (2.0f / (top - bottom));
		result.m22 = -(2.0f / (far - near));
		result.m33 = 1;
		result.m30 = (right + left) / (right - left);
		result.m31 = (top + bottom) / (top - bottom);
		result.m32 = (far + near) / (far - near);
		
		return result;
	}
}
