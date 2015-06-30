package engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import engine.utils.Texture;

public class Renderer 
{		
	public Renderer()
	{
		init();
	}		
	
	public void renderGameObject(GameObject gameObject, Texture texture, int renderMode)
	{

		
		
		if(texture != null)
			texture.bind();
		
		gameObject.draw(renderMode);
		
        //Clean up
		if(texture != null)
			texture.unbind();
	}
	
	private void init()
    {
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }    
}
