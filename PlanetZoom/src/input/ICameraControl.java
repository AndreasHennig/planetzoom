package input;

import engine.ICamera;

public interface ICameraControl 
{
	public ICamera handleInput();
	public void releaseCallbacks();
}
