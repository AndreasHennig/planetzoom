package input;

import org.lwjgl.glfw.GLFWCursorPosCallback;



public class Cursor extends GLFWCursorPosCallback
{
	private static double lastXPos;
	private static double lastYPos;
	
	private static double xPos;
	private static double yPos;
	
	
	public static double getDx()
	{
		double dx =  xPos - lastXPos;
		lastXPos = xPos;
		return dx;
	}
	
	public static double getDy()
	{
		double dy =  yPos - lastYPos;
		lastYPos = yPos;
		return dy;
	}
	
	public static double getPositionX()
	{
		return xPos;
	}
	
	public static double getPositionY()
	{
		return yPos;
	}

	@Override
	public void invoke(long window, double xpos, double ypos)
	{
		Cursor.xPos = xpos;
		Cursor.yPos = ypos;
	}
}
