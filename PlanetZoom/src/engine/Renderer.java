package engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import lenz.utils.ShaderProgram;

import static engine.utils.SimplexNoise.*;

import org.lwjgl.util.vector.Matrix4f;

public class Renderer 
{
	private ShaderProgram testShader;
	private Matrix4f projectionMatrix;
	
	
	public Renderer(Matrix4f projectionMatrix, Matrix4f viewMatrix)
	{
		this.projectionMatrix = projectionMatrix;
		init();
	}
	
	public void render(Planet planet, Matrix4f viewMatrix) 
	{
		renderTest();

		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.setIdentity();
		
		Matrix4f modelViewMatrix = new Matrix4f();
		Matrix4f.mul(viewMatrix, modelMatrix, modelViewMatrix);
		
		Matrix4f normalMatrix = new Matrix4f();
		Matrix4f.transpose(modelViewMatrix, normalMatrix);
		Matrix4f.invert(normalMatrix, normalMatrix);
		
		loadMatricesToShader(modelViewMatrix, normalMatrix);
	}
	
	private void init()
    {
        testShader = new ShaderProgram("testShader");
        
		glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    }
	
    private void loadMatricesToShader(Matrix4f modelViewMatrix, Matrix4f normalMatrix)
    {
    	ShaderProgram.loadMatrix4f(testShader.getId(), projectionMatrix, "projectionMatrix");
    	ShaderProgram.loadMatrix4f(testShader.getId(), modelViewMatrix, "modelViewMatrix");
    	ShaderProgram.loadMatrix4f(testShader.getId(), normalMatrix, "normalMatrix");
    }
    
    private void renderTest()
    {
        glUseProgram(testShader.getId());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        drawCube(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void drawCube(float sizeX, float sizeY, float sizeZ, float texTiling)
  	{		
  		float halfHeight = sizeY / 2.0f; 
  		float halfWidth  = sizeX / 2.0f;
  		float halfDepth  = sizeZ / 2.0f;
  		float texMul = texTiling;
  		
  		glBegin(GL_QUADS);
  			//TOP
  			glColor4f(1, 0, 0, 1);
  			glNormal3f(0,1,0);
  			glTexCoord2f(0.0f,0.0f);
  			glVertex3f(-halfWidth, halfHeight, -halfDepth);         
  			glTexCoord2f(0.0f,sizeZ*texMul);
  			glVertex3f(-halfWidth, halfHeight, halfDepth);         
  			glTexCoord2f(sizeX*texMul,sizeZ*texMul);
  			glVertex3f(halfWidth, halfHeight, halfDepth);         
  			glTexCoord2f(sizeX*texMul,0.0f);
  			glVertex3f(halfWidth, halfHeight, -halfDepth);         
  				
  			//FRONT
  			glColor4f(1, 1, 1, 1);
  			glNormal3f(0,0,1);
  			glTexCoord2f(0.0f,0.0f);
  			glVertex3f( -halfWidth, halfHeight, halfDepth);   
  			glTexCoord2f(0.0f,sizeY*texMul);
  			glVertex3f(-halfWidth, -halfHeight, halfDepth);   
  			glTexCoord2f(sizeX*texMul,sizeY*texMul);
  			glVertex3f(halfWidth, -halfHeight, halfDepth);    
  			glTexCoord2f(sizeX*texMul,0.0f);
  			glVertex3f( halfWidth, halfHeight, halfDepth);      
  				
  			//BACK

  			glNormal3f(0,0,-1);
  			glTexCoord2f(sizeX*texMul,0.0f);
  			glVertex3f( -halfWidth, halfHeight, -halfDepth);   
  			glTexCoord2f(0.0f,0.0f);
  			glVertex3f( halfWidth, halfHeight, -halfDepth);     
  			glTexCoord2f(0.0f,sizeY*texMul);
  			glVertex3f( halfWidth, -halfHeight, -halfDepth);    
  			glTexCoord2f(sizeX*texMul,sizeY*texMul);
  			glVertex3f( -halfWidth, -halfHeight, -halfDepth);      

  			//LEFT
  			glColor4f(0, 1, 0, 1);
  			glNormal3f(-1,0,0);
  			glTexCoord2f(sizeZ*texMul,0.0f);
  			glVertex3f(-halfWidth, halfHeight, halfDepth);   
  			glTexCoord2f(0.0f,0.0f);
  			glVertex3f(-halfWidth, halfHeight, -halfDepth);   
  			glTexCoord2f(0.0f,sizeY*texMul);
  			glVertex3f(-halfWidth, -halfHeight, -halfDepth);   
  			glTexCoord2f(sizeZ*texMul,sizeY*texMul);
  			glVertex3f(-halfWidth, -halfHeight, halfDepth);      

  			//RIGHT  
  			glColor4f(0, 1, 0, 1);
  			glNormal3f(1,0,0);
  			glTexCoord2f(sizeZ*texMul,0.0f);
  			glVertex3f( halfWidth, halfHeight, -halfDepth);
  			glTexCoord2f(0.0f,0.0f);
  		    glVertex3f( halfWidth, halfHeight, halfDepth);  
  		    glTexCoord2f(0.0f,sizeY*texMul);
  		    glVertex3f( halfWidth, -halfHeight, halfDepth);         
  		    glTexCoord2f(sizeZ*texMul,sizeY*texMul);
  		    glVertex3f( halfWidth, -halfHeight, -halfDepth);     
  			    
  		    
  			//BOTTOM
  			glColor4f(1, 0, 0, 1);
  		    glNormal3f(0,-1,0);
  		    glTexCoord2f(0.0f,0.0f);
  			glVertex3f( -halfWidth, -halfHeight, -halfDepth);  
  			glTexCoord2f(0.0f,sizeZ*texMul);
  			glVertex3f( halfWidth, -halfHeight, -halfDepth);     
  			glTexCoord2f(sizeX*texMul,sizeZ*texMul);
  			glVertex3f( halfWidth, -halfHeight, halfDepth);  
  			glTexCoord2f(sizeX*texMul,0.0f);
  			glVertex3f( -halfWidth, -halfHeight, halfDepth);	
  		glEnd();
  	}
}
