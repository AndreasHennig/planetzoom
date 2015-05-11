package engine;

import org.lwjgl.glfw.GLFW;

/**
 * This whole class will propably be merged into the core engine later on
 * @author Jonas
 */
public class Timer {

	long lastFrame;
	
	public Timer(){
		 lastFrame = getTimeInMs();
	}
	
	public long getTimeInMs(){
		return System.nanoTime() / 1_000_000;
	}
	
	public long getTime(){
		//glfwGetTime() returns time in seconds as a double value
		return (long) (GLFW.glfwGetTime() * 1_000);
	}
	
	/**
	 * can only be called once per frame (both zero and two calls would break this shit)
	 * @return the time since the last frame in ms
	 */
	public int getDeltaTime() {
	    long currentTime = getTimeInMs();
	    int delta = (int) (currentTime - lastFrame);
	    lastFrame = currentTime;
	         
	    return delta;
	}
}