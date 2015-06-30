package planetZoooom.utils;

import org.lwjgl.glfw.GLFW;

/**
 * This whole class will propably be merged into the core engine later on
 * @author Jonas
 */
public class Timer {

	long lastFrame;
	long lastFPStimestamp;
	int currentFPS;
	int fps = 0; // counts frames every second
	int expectedFPS = 0; // calculates frames based on time difference between two following(?) frames
	
	public Timer(){
		 lastFrame = getTime();
		 lastFPStimestamp = getTime();
	}
	
	//in case this method should be used it would be simpler to rename this to getTime() and the current getTime() to something else 
	private long getTime2(){
		return System.nanoTime() / 1_000_000;
	}
	
	private long getTime(){
		//glfwGetTime() returns time in seconds as a double value
		return (long) (GLFW.glfwGetTime() * 1_000);
	}
	
	/**
	 * can only be called once per frame (both zero and two calls would break this shit)
	 * @return the time since the last frame in ms
	 */
	public int getDeltaTime() {
	    long currentTime = getTime();
	    int delta = (int) (currentTime - lastFrame);
	    updateFPS();
	    updateExpectedFPS();
	    lastFrame = currentTime;
	    
	    return delta;
	}
	
	private void updateFPS() {
		if(getTime() - lastFPStimestamp > 1_000)
		{
			currentFPS = fps;
			fps = 0;
			lastFPStimestamp += 1000;
		}
		fps++;
	}
	
	public int getFPS() {
		return currentFPS;
	}
	
	private void updateExpectedFPS(){
		long timeSinceLastFrame = getTime()- lastFrame;
		expectedFPS = (int) (1_000.0f / timeSinceLastFrame);
	}
	
	public int getExpectedFPS() {
		return expectedFPS;
	}
}