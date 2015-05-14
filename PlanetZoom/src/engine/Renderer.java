package engine;

import lenz.utils.ShaderProgram;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;

import org.lwjgl.util.vector.Matrix4f;

public class Renderer 
{
	private Matrix4f perspectiveProjectionMatrix;
	private Matrix4f orthographicProjectionMatrix;
	
	private ShaderProgram testShader = new ShaderProgram("testShader");
	private ShaderProgram hudShader = new ShaderProgram("HUDShader");
	private ShaderProgram toonShader = new ShaderProgram("toonShader");
	
	public Renderer(Matrix4f projectionMatrix)
	{
		this.perspectiveProjectionMatrix = projectionMatrix;
		this.orthographicProjectionMatrix = Renderer.createOrthographicProjectionMatric(0.0f, -800.0f, -600.0f, 0.0f, -1.0f, 1.0f);
		init();
	}
	
	public void render(Planet planet, Matrix4f viewMatrix) 
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		Matrix4f normalMatrix = new Matrix4f();
		Matrix4f.transpose(viewMatrix, normalMatrix);
		Matrix4f.invert(normalMatrix, normalMatrix);
		
		glUseProgram(testShader.getId());
		ShaderProgram.loadMatrix4f(testShader.getId(), perspectiveProjectionMatrix, "projectionMatrix");
		ShaderProgram.loadMatrix4f(testShader.getId(), viewMatrix, "modelViewMatrix");
		ShaderProgram.loadMatrix4f(testShader.getId(), normalMatrix, "normalMatrix");
		renderVAO(new VertexArrayObject(planet.getSphere()), GL_LINE_STRIP);	
		
		Matrix4f modelViewMatrix = new Matrix4f();
		modelViewMatrix.setIdentity();
		
		
		glClear(GL_DEPTH_BUFFER_BIT);
		
		glUseProgram(hudShader.getId());
		ShaderProgram.loadMatrix4f(hudShader.getId(), orthographicProjectionMatrix, "projectionMatrix");
		ShaderProgram.loadMatrix4f(hudShader.getId(), modelViewMatrix, "modelViewMatrix");
		GameObject2D t = TextRenderer2D.textToObject2D("Sample text", "arial_nm.png", 0, 0, 16);
		VertexArrayObject text = new VertexArrayObject(t);
		renderVAO(text, GL_TRIANGLES);
		
	}
	
	private void init()
    {
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }    
   
    public void renderVAO(VertexArrayObject vao, int mode)
	{						
    	vao.bindBuffers();
        GL30.glBindVertexArray(vao.getId());
        GL20.glEnableVertexAttribArray(0); //positions
        GL20.glEnableVertexAttribArray(1); //uvs
        GL20.glEnableVertexAttribArray(2); //normals
        GL20.glEnableVertexAttribArray(3); //color
         
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vao.getVboIndexHandle());
         
        // Draw vertices
        GL11.glDrawElements(mode, vao.getIndexCount() , GL11.GL_UNSIGNED_INT, 0);
         
        // Put everything back to default 
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
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
