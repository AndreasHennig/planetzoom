package engine;

import lenz.utils.ShaderProgram;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Peter.TextureUsingPNGDecoder;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Renderer 
{
	private Matrix4f perspectiveProjectionMatrix;
	private Matrix4f orthographicProjectionMatrix;
	
	private ShaderProgram testShader = new ShaderProgram("testShader");
	private ShaderProgram hudShader = new ShaderProgram("HUDShader");
	private ShaderProgram shaderTestPete = new ShaderProgram("shaderTestPete");
	
	private TextureUsingPNGDecoder texture = new TextureUsingPNGDecoder("src/res/textures/crypt_wall.png");
	
	public Renderer(float fovParam, int windowWidth, int windowHeight)
	{
		initProjectionMatrix(fovParam, windowWidth, windowHeight);
		this.orthographicProjectionMatrix = Renderer.createOrthographicProjectionMatric(0.0f, -800.0f, -600.0f, 0.0f, -1.0f, 1.0f);
		init();
	}
	
	public void render(Planet planet, ICamera camera) 
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		Matrix4f viewMatrix = camera.getViewMatrix();
		Matrix4f normalMatrix = new Matrix4f();
		Matrix4f.transpose(viewMatrix, normalMatrix);
		Matrix4f.invert(normalMatrix, normalMatrix);
		
		
		
		texture.bind();
		glUseProgram(shaderTestPete.getId());

		ShaderProgram.loadUniformMat4f(testShader.getId(), perspectiveProjectionMatrix, "projectionMatrix");
		ShaderProgram.loadUniformMat4f(testShader.getId(), viewMatrix, "modelViewMatrix");
		ShaderProgram.loadUniformMat4f(testShader.getId(), normalMatrix, "normalMatrix");
		ShaderProgram.loadUniformVec3f(testShader.getId(), camera.getPosition(), "cameraPosition");

		renderVAO(new VertexArrayObject(planet.getMesh()), GL_TRIANGLES);	
		texture.unbind();
		
		
		
		Vector3f camToPlanet = new Vector3f();
		Vector3f.sub(planet.getPosition(), camera.getPosition(), camToPlanet);
		float planetCamDistance = Math.abs(camToPlanet.length()) - planet.getRadius();


		Matrix4f modelViewMatrix = new Matrix4f();
		modelViewMatrix.setIdentity();
		GameObject2D hud = new HeadsUpDisplay(0,0,"arial_nm.png", camera.getPosition(), new Vector3f(), planetCamDistance, 0).getText2D();
		glClear(GL_DEPTH_BUFFER_BIT);
		glUseProgram(hudShader.getId());
		ShaderProgram.loadUniformMat4f(hudShader.getId(), orthographicProjectionMatrix, "projectionMatrix");
		ShaderProgram.loadUniformMat4f(hudShader.getId(), modelViewMatrix, "modelViewMatrix");

		hud.getTexture().bind();
		VertexArrayObject text1 = new VertexArrayObject(hud);
		renderVAO(text1, GL_TRIANGLES);
		hud.getTexture().unbind();
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
    	vao.bind();
        GL30.glBindVertexArray(vao.getId());
        GL20.glEnableVertexAttribArray(VertexArrayObject.POSITION_LOCATION);
        GL20.glEnableVertexAttribArray(VertexArrayObject.UV_LOCATION);
        GL20.glEnableVertexAttribArray(VertexArrayObject.NORMAL_LOCATION);
        GL20.glEnableVertexAttribArray(VertexArrayObject.COLOR_LOCATION);
         
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vao.getIndexHandle());
         
        // Draw vertices
        GL11.glDrawElements(mode, vao.getIndexCount() , GL11.GL_UNSIGNED_INT, 0);
         
        // Put everything back to default 
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(VertexArrayObject.POSITION_LOCATION);
        GL20.glDisableVertexAttribArray(VertexArrayObject.UV_LOCATION);
        GL20.glDisableVertexAttribArray(VertexArrayObject.NORMAL_LOCATION);
        GL20.glDisableVertexAttribArray(VertexArrayObject.COLOR_LOCATION);
        GL30.glBindVertexArray(0);
	}
    
    private void initProjectionMatrix(float fovParam, int width, int height)
	{
		perspectiveProjectionMatrix = new Matrix4f();
		float fov = fovParam;
		float zFar = 500.0f;
		float zNear = 0.1f;
		float aspectRatio = (float)width/height;		
		float frustumLength = zFar - zNear;
		float yScale = (float)(1.0f/Math.tan(Math.toRadians(fov/2.0f)));
		float xScale = yScale / aspectRatio;

		perspectiveProjectionMatrix.setZero();
		perspectiveProjectionMatrix.m00 = xScale;
		perspectiveProjectionMatrix.m11 = yScale;
		perspectiveProjectionMatrix.m22 =  -((zFar + zNear)/frustumLength);
		perspectiveProjectionMatrix.m32 = -((2 * zNear * zFar) / frustumLength);
		perspectiveProjectionMatrix.m23 =  -1.0f;								
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
