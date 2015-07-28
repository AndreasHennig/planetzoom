package planetZoooom.engine;

import static org.lwjgl.opengl.GL11.*;

public class Renderer 
{		
	public Renderer()
	{
		init();
	}		
		
	private void init()
    {
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glPointSize(3.0f);
    }    
}
