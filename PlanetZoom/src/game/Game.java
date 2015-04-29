package game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import input.Input;

import java.nio.FloatBuffer;

import lenz.utils.ShaderProgram;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.CoreEngine;
import engine.FirstPersonCamera;
import engine.IGame;


public class Game implements IGame
{
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Matrix4f modelViewMatrix = new Matrix4f();
	private Matrix4f normalMatrix = new Matrix4f();
	private Matrix4f modelMatrix  = new Matrix4f();
	
	private ShaderProgram testShader;
	private FirstPersonCamera camera; 
	
	private FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer(16);
	private FloatBuffer modelViewBuffer = BufferUtils.createFloatBuffer(16);
	private FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(16);
	
	
    public static void main(String[] args)
    {
        CoreEngine game = new CoreEngine(new Game());
        game.start();
    }

    @Override
    public void init()
    {
        printVersionInfo();
     
        camera = new FirstPersonCamera(0.0f, 0.0f, -15f);
        
        initProjectionMatrix(90.0f);
        projectionMatrix.store(projectionBuffer);
		projectionBuffer.flip();
		
		viewMatrix = camera.getViewMatrix();
       
		glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        
        testShader = new ShaderProgram("testShader");
    }

    @Override
    public void update()
    {
        if(Input.keys[GLFW_KEY_SPACE])
        {
            System.out.println("Spacebar pressed!");
        }
    }

    @Override
    public void render()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glUseProgram(testShader.getId());
        
        modelMatrix.setIdentity();
        
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        drawCube(1f,1f,1f,1f);
    	
        viewMatrix = camera.getViewMatrix();
        
		Matrix4f.mul(viewMatrix,modelMatrix,modelViewMatrix);
		modelViewMatrix.store(modelViewBuffer);
		modelViewBuffer.flip();
		
		Matrix4f.invert(modelViewMatrix, normalMatrix);
		normalMatrix.store(normalBuffer);
		normalBuffer.flip();
			
		updateShader(testShader.getId());
        
    }

    private void updateShader(int shaderId)
	{
		glUniformMatrix4fv(glGetUniformLocation(shaderId, "projectionMatrix"), false, projectionBuffer);
		glUniformMatrix4fv(glGetUniformLocation(shaderId, "modelViewMatrix"), false, modelViewBuffer);
		glUniformMatrix4fv(glGetUniformLocation(shaderId, "normalMatrix"), true, normalBuffer);
	}

    
    private void initProjectionMatrix(float fovParam)
	{
		projectionMatrix = new Matrix4f();
		float fov = fovParam;
		float zFar = 500.0f;
		float zNear = 0.1f;
		float aspectRatio = 4.0f/3.0f;				
		float frustumLength = zFar - zNear;
		float yScale = (float)(1.0f/Math.tan(Math.toRadians(fov/2.0f)));
		float xScale = yScale / aspectRatio;

		projectionMatrix.m00 = xScale;	projectionMatrix.m10 = 0.0f;	projectionMatrix.m20 =  0.0f;								projectionMatrix.m30 = 0.0f;
		projectionMatrix.m01 = 0.0f;	projectionMatrix.m11 = yScale;	projectionMatrix.m21 =  0.0f;								projectionMatrix.m31 = 0.0f;
		projectionMatrix.m02 = 0.0f;	projectionMatrix.m12 = 0.0f;	projectionMatrix.m22 =  -((zFar + zNear)/frustumLength);	projectionMatrix.m32 = -((2 * zNear * zFar) / frustumLength);
		projectionMatrix.m03 = 0.0f;	projectionMatrix.m13 = 0.0f;	projectionMatrix.m23 =  -1.0f;								projectionMatrix.m33 = 0.0f;
	}
    
    private void printVersionInfo()
    {
        System.out.println("GPU Vendor: " + glGetString(GL_VENDOR));
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));
        System.out.println("OpenGL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
    }
    
    public void drawCube(float sizeX, float sizeY, float sizeZ, float texTiling)
	{		
		float halfHeight = sizeY / 2.0f; 
		float halfWidth  = sizeX / 2.0f;
		float halfDepth  = sizeZ / 2.0f;
		float texMul = texTiling;
		
		glBegin(GL_QUADS);
			//TOP
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
