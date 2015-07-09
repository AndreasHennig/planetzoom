package planetZoooom.engine;

import static org.lwjgl.opengl.GL11.*;
import planetZoooom.gameContent.BillBoard;
import planetZoooom.geometry.GameObject;
import planetZoooom.graphics.Texture;

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
	
	public void renderGameObject(BillBoard gameObject, Texture texture, int renderMode)
	{
		if(texture != null)
			texture.bind();
		
		gameObject.render(renderMode);
		
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
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glPointSize(4.0f);
    }    
}