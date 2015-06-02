package engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.util.ArrayList;

import lenz.utils.ShaderProgram;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.util.vector.Matrix4f;

import engine.utils.MatrixUtils;

public class Renderer 
{
	private Matrix4f perspectiveProjectionMatrix;
	private Matrix4f orthographicProjectionMatrix;
	
	
	private static ShaderProgram testShader; 
	private static ShaderProgram hudShader; ;
	private static ShaderProgram shaderTestPete;
	
	private ArrayList<GameObject3D> gameObjects3D;
	private ArrayList<GameObject2D> gameObjects2D;
	
	
	public static int testShaderID = -1;
	public static int hudShaderID = -1;
	public static int testPeteShaderID = -1;
	
	public Renderer(float fovParam, int windowWidth, int windowHeight)
	{
//<<<<<<< HEAD
		perspectiveProjectionMatrix = MatrixUtils.perspectiveProjectionMatrix(fovParam, windowWidth, windowHeight);
		orthographicProjectionMatrix = MatrixUtils.orthographicProjectionMatrix(0.0f, -800.0f, -600.0f, 0.0f, -1.0f, 1.0f);
//=======
//		initProjectionMatrix(fovParam, windowWidth, windowHeight);
//		this.orthographicProjectionMatrix = Renderer.createOrthographicProjectionMatric(0.0f, -800.0f, -600.0f, 0.0f, -1.0f, 1.0f);
		gameObjects2D = new ArrayList<GameObject2D>();
		gameObjects3D = new ArrayList<GameObject3D>();
//>>>>>>> origin/45_Refactoring_Rendering
		init();
	}
	public static void initShader()
	{
		testShader = new ShaderProgram("testShader");
		hudShader = new ShaderProgram("HUDShader");
		shaderTestPete = new ShaderProgram("shaderTestPete");
		
		testShaderID = testShader.getId();
		hudShaderID = hudShader.getId();
		testPeteShaderID = shaderTestPete.getId();
	}
	
	private void loadShader(ShaderProgram shader, GameObject gameObject, ICamera camera)
	{
		Matrix4f viewMatrix = camera.getViewMatrix();
		Matrix4f modelViewMatrix = new Matrix4f();
		Matrix4f.mul(gameObject.getModelMatrix(), viewMatrix, modelViewMatrix);
		Matrix4f normalMatrix = new Matrix4f();
		Matrix4f.transpose(modelViewMatrix, normalMatrix);
		Matrix4f.invert(normalMatrix, normalMatrix);
		
		glUseProgram(shader.getId());
		
		ShaderProgram.loadUniformMat4f(shader.getId(), perspectiveProjectionMatrix, "projectionMatrix");
		ShaderProgram.loadUniformMat4f(shader.getId(), viewMatrix, "modelViewMatrix");
		ShaderProgram.loadUniformMat4f(shader.getId(), normalMatrix, "normalMatrix");
		ShaderProgram.loadUniformVec3f(shader.getId(), camera.getPosition(), "cameraPosition");
	}
	
	private ShaderProgram getShader(int shaderID)
	{
		if(shaderID == testPeteShaderID)
			return shaderTestPete;
		if(shaderID == hudShaderID)
			return hudShader;
		
		return testShader;
	}
	public void render(ICamera camera) 
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		for(GameObject3D gameObject3D : gameObjects3D)
		{
			loadShader(getShader(gameObject3D.shaderID), gameObject3D, camera);
			gameObject3D.draw(GL_TRIANGLES);
		}
		
		glClear(GL_DEPTH_BUFFER_BIT);
		
//		Vector3f camToPlanet = new Vector3f();
//		Vector3f.sub(planet.getPosition(), camera.getPosition(), camToPlanet);
//		float planetCamDistance = Math.abs(camToPlanet.length()) - planet.getRadius();
//
//		Matrix4f modelViewMatrix = new Matrix4f();
//		modelViewMatrix.setIdentity();
//		GameObject2D hud = new HeadsUpDisplay(0,0,"arial_nm.png", camera.getPosition(), new Vector3f(), planetCamDistance, 0).getText2D();
//		glClear(GL_DEPTH_BUFFER_BIT);
//		glUseProgram(hudShader.getId());
//		ShaderProgram.loadUniformMat4f(hudShader.getId(), orthographicProjectionMatrix, "projectionMatrix");
//		ShaderProgram.loadUniformMat4f(hudShader.getId(), modelViewMatrix, "modelViewMatrix");
//
//		hud.getTexture().bind();
//		VertexArrayObject text1 = new VertexArrayObject(hud);
//		renderVAO(text1, GL_TRIANGLES);
//		hud.getTexture().unbind();
		
		for(GameObject2D gameObject2D : gameObjects2D)
		{
			glUseProgram(hudShader.getId());
			ShaderProgram.loadUniformMat4f(hudShader.getId(), orthographicProjectionMatrix, "projectionMatrix");
			ShaderProgram.loadUniformMat4f(hudShader.getId(), gameObject2D.getModelMatrix(), "modelViewMatrix");
			gameObject2D.draw(GL_TRIANGLES);
		}
	}
	
	private void init()
    {
		initShader();
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }    
   
    public void renderVAO(VertexArrayObject vao, int mode)
	{						
    	vao.bind();
        glBindVertexArray(vao.getId());
        glEnableVertexAttribArray(VertexArrayObject.POSITION_LOCATION);
        glEnableVertexAttribArray(VertexArrayObject.UV_LOCATION);
        glEnableVertexAttribArray(VertexArrayObject.NORMAL_LOCATION);
        glEnableVertexAttribArray(VertexArrayObject.COLOR_LOCATION);
         
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vao.getIndexHandle());
         
        // Draw vertices
        glDrawElements(mode, vao.getIndexCount() , GL_UNSIGNED_INT, 0);
         
        // Put everything back to default 
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(VertexArrayObject.POSITION_LOCATION);
        glDisableVertexAttribArray(VertexArrayObject.UV_LOCATION);
        glDisableVertexAttribArray(VertexArrayObject.NORMAL_LOCATION);
        glDisableVertexAttribArray(VertexArrayObject.COLOR_LOCATION);
        glBindVertexArray(0);
	}
    
//    private void initProjectionMatrix(float fovParam, int width, int height)
//	{
//		perspectiveProjectionMatrix = new Matrix4f();
//		float fov = fovParam;
//		float zFar = 500.0f;
//		float zNear = 0.1f;
//		float aspectRatio = (float)width/height;		
//		float frustumLength = zFar - zNear;
//		float yScale = (float)(1.0f/Math.tan(Math.toRadians(fov/2.0f)));
//		float xScale = yScale / aspectRatio;
//
//		perspectiveProjectionMatrix.setZero();
//		perspectiveProjectionMatrix.m00 = xScale;
//		perspectiveProjectionMatrix.m11 = yScale;
//		perspectiveProjectionMatrix.m22 =  -((zFar + zNear)/frustumLength);
//		perspectiveProjectionMatrix.m32 = -((2 * zNear * zFar) / frustumLength);
//		perspectiveProjectionMatrix.m23 =  -1.0f;								
//	}
	
//	public static Matrix4f createOrthographicProjectionMatric(float right, float left, float top, float bottom, float near, float far)
//	{
//		Matrix4f projectionMatrix = new Matrix4f();
//		projectionMatrix.setZero();
//		
//		projectionMatrix.m00 = (2.0f / (right - left));
//		projectionMatrix.m11 = (2.0f / (top - bottom));
//		projectionMatrix.m22 = -(2.0f / (far - near));
//		projectionMatrix.m33 = 1;
//		projectionMatrix.m30 = (right + left) / (right - left);
//		projectionMatrix.m31 = (top + bottom) / (top - bottom);
//		projectionMatrix.m32 = (far + near) / (far - near);
//		
//		return projectionMatrix;
//	}
	
	public void addGameObject3D(GameObject3D gameObject3D)
	{
		gameObjects3D.add(gameObject3D);
	}
	public void addGameObject2D(GameObject2D gameObject2D)
	{
		gameObjects2D.add(gameObject2D);
	}
	
	public void clearGameObjects()
	{
		gameObjects2D.clear();
		gameObjects3D.clear();
	}
}
