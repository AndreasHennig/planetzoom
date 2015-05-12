package engine;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import lenz.utils.ShaderProgram;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

public class Renderer 
{
	private Matrix4f perspectiveProjectionMatrix;
	private Matrix4f orthographicProjectionMatrix;
	
	
	public Renderer(Matrix4f projectionMatrix)
	{
		this.perspectiveProjectionMatrix = projectionMatrix;
		this.orthographicProjectionMatrix = Renderer.createOrthographicProjectionMatric(4.0f/3.0f, -4.0f/3.0f, 1.0f, -1.0f, -1.0f, 1.0f);
		init();
	}
	
	public void render(Planet planet, Matrix4f viewMatrix) 
	{
		renderTest(viewMatrix);
	}
	
	private void init()
    {
		glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    }
    
    private void renderTest(Matrix4f viewMatrix)
    {
       // glUseProgram(testShader.getId());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        //renderObject2D(TextRenderer2D.textToObject2D("My Text yo", "arial_noMetrices.png", 10, 10, 32));
        renderObject3D(Object3D.getTestObject3D(), viewMatrix);
        renderObject2D(Object2D.getTestObject2D());
    }
    
   
    public void renderObject2D(Object2D object2D)
	{		
		VertexArrayObject vao = new VertexArrayObject(object2D);
		
		Matrix4f modelViewMatrix = new Matrix4f();
		Matrix4f.setIdentity(modelViewMatrix);
		
		GL20.glUseProgram(object2D.getShader().getId());
		
		ShaderProgram.loadMatrix4f(object2D.getShader().getId(), orthographicProjectionMatrix, "projectionMatrix");
		ShaderProgram.loadMatrix4f(object2D.getShader().getId(), modelViewMatrix, "modelViewMatrix");
		
    	vao.bindBuffers();
        GL30.glBindVertexArray(vao.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
         
        // Bind to the index VBO that has all the information about the order of the vertices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vao.getVboIndexHandle());
         
        // Draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLES, vao.getIndexCount() , GL11.GL_UNSIGNED_BYTE, 0);
         
        // Put everything back to default (deselect)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
	}
    
    public void renderObject3D(Object3D object3D, Matrix4f viewMatrix)
	{		
		VertexArrayObject vao = new VertexArrayObject(object3D);
		
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.setIdentity();
		
		Matrix4f modelViewMatrix = new Matrix4f();
		Matrix4f.mul(viewMatrix, modelMatrix, modelViewMatrix);
		
		Matrix4f normalMatrix = new Matrix4f();
		Matrix4f.transpose(modelViewMatrix, normalMatrix);
		Matrix4f.invert(normalMatrix, normalMatrix);
		

		
		GL20.glUseProgram(object3D.getShader().getId());
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix = perspectiveProjectionMatrix;
		
		ShaderProgram.loadMatrix4f(object3D.getShader().getId(), projectionMatrix, "projectionMatrix");
		ShaderProgram.loadMatrix4f(object3D.getShader().getId(), modelViewMatrix, "modelViewMatrix");
		
    	vao.bindBuffers();
        GL30.glBindVertexArray(vao.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
         
        // Bind to the index VBO that has all the information about the order of the vertices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vao.getVboIndexHandle());
         
        // Draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLES, vao.getIndexCount() , GL11.GL_UNSIGNED_BYTE, 0);
         
        // Put everything back to default (deselect)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
	}
    
    
	
	public static Matrix4f createOrthographicProjectionMatric(float right, float left, float top, float bottom, float near, float far)
	{
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.setZero();
		
		projectionMatrix.m00 = (2.0f / (right - left));
		projectionMatrix.m11 = (2.0f / (top - bottom));
		projectionMatrix.m22 = -(2.0f / (far - near));
		projectionMatrix.m33 = 1;
		projectionMatrix.m30 = (right + left) / (right - left);
		projectionMatrix.m31 = (top + bottom) / (top - bottom);
		projectionMatrix.m32 = (far + near) / (far - near);
		
		return projectionMatrix;
	}
}
