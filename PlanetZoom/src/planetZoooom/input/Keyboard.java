package planetZoooom.input;

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
public class Keyboard extends GLFWKeyCallback
{

	private static boolean[] keys = new boolean[348];
	
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods)
	{
		if(!(key >= keys.length) && key > 0)
			keys[key] = (action != GLFW_RELEASE);	
	}
	
	public static boolean isKeyPressed(int key)
	{
		if(key < keys.length)
			return keys[key];
		
		return false;
	}
	
	public static boolean isKeyPressedWithReset(int key)
	{	
		if(key < keys.length)
		{
			if(keys[key])
			{
				keys[key] = false;
				return true;
			}
			
			return false;
		}
		return false;
	}

}
