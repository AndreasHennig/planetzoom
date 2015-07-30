package planetZoooom.gameContent;

import planetZoooom.engine.MeshObject;
import planetZoooom.engine.VertexArray;

public class Rectangle2D extends MeshObject
{
	private float x;
	private float y;
	private float width;
	private float height;
	private float[] color;
	
	public Rectangle2D(float x, float y, float width, float height)
	{
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.color = new float[]{0,0,0,1};
		init();
	}
	
	private void init()
	{
		vertices = new float[12];
		indices = new int[6];	
		colors = new float[16];
		
		uvCoords = new float[]{0};
		normals = new float[]{0};
		createMesh();
		mesh = new VertexArray(vertices, normals, uvCoords, colors, indices);
		update();
	}
	
	private void createMesh()
	{
		vertices[0] = x;
		vertices[1] = y;
		vertices[2] = 0;
		
		vertices[3] = x + width;
		vertices[4] = y;
		vertices[5] = 0;
		
		vertices[6] = x + width;
		vertices[7] = y + height;
		vertices[8] = 0;
		
		vertices[9] = x;
		vertices[10] = y + height;
		vertices[11] = 0;
		
		//firstTriangle
		indices[0] = 2;
		indices[1] = 1;
		indices[2] = 0;

		indices[3] = 0;
		indices[4] = 3;
		indices[5] = 2;		
		
		for(int i = 0; i < 16; i+=4)
		{
			colors[i] = color[0];
			colors[i+1] = color[1];
			colors[i+2] = color[2];
			colors[i+3] = color[3];
		}
	}
	
	public void update()
	{		
		createMesh();
		mesh.update(vertices, normals, uvCoords, colors, indices);
	}
	
	public void setColor(float[] color)
	{
		this.color = color;
		updateColor();
	}
	
	private void updateColor()
	{
		for(int i = 0; i < 16; i+=4)
		{
			colors[i] = color[0];
			colors[i+1] = color[1];
			colors[i+2] = color[2];
			colors[i+3] = color[3];
		}
	}
	public void setWidth(float width)
	{
		this.width = width;
	}
	public void setHeight(float height)
	{
		this.height = height;
	}
	
}
