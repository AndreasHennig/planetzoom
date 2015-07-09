package planetZoooom.geometry;

import planetZoooom.engine.VertexArray;
import planetZoooom.graphics.Texture;

public class HUDText
{
	
	private static final int ROWCOUNT_STANDARD = 16;
	private static final int COLUMNCOUNT_STANDARD = 16;
	
	private Texture fontTexture;
	private int rowCount;
	private int colCount;
	
	private int startPositionX;
	private int startPositionY;
	private int cellSize;
	
	private VertexArray mesh;
	
	/**
	 * 
	 * @param text text to be rendered
	 * @param font name of the font bitmap (located in res.textures.fonts)
	 * @param x	x_cooridnate
	 * @param y y_coordinate
	 * @param size size of a character in the given bitmap
	 */
	public HUDText(String text, String font, int x, int y, int size)
	{
	    this(text, font, x, y, size, ROWCOUNT_STANDARD, COLUMNCOUNT_STANDARD);
	}
	
	/**
	 * 
	 * @param text text to be rendered
	 * @param font name of the font bitmap (located in res.textures.fonts)
	 * @param x x_cooridnate
	 * @param y y_coordinate
	 * @param size size of a character-cell in the given bitmap
	 * @param number of character rows in the given bitmap
	 * @param number of character columns in the given bitmap
	 */
	public HUDText(String text, String font, int x, int y, int size, int rowCount, int colCount)
	{
		super();
		this.fontTexture = new Texture("src/res/textures/fonts/" + font);
		this.rowCount = rowCount;
		this.colCount = colCount;
		this.startPositionX = x;
		this.startPositionY = y;
		this.cellSize = size;
		update(text);
	}
	
	public void update(String text)
	{
		int index = 0;
		char currentChar;
		float uv_x, uv_y;
		float uvCellHeight = 1.0f / colCount;
		float uvCellWidth = 1.0f / rowCount;
		int row, column;
		int x = startPositionX, y = startPositionY;

		float[] vertices = new float[12 * text.length()];	//4 vertices per quad * 3 floats(x,y,z) * chars
		float[] normals = new float[vertices.length];
		float[] uvCoords = new float[8 * text.length()];	//4 texCoords per quad * 2 floats(u,v) * chars
		int[] indices = new int[6 * text.length()];				//6 points to draw 2 triangles = 1 quad
		
		for (int i = 0; i < text.length() ; i++)
		{
			currentChar = text.charAt(i);
			row = (currentChar % rowCount);
			column = (currentChar / colCount);

			uv_x = row / (float) rowCount;
			uv_y = column / (float) colCount;

			if (currentChar == '\n')
			{
				y += cellSize;
				x = 0;
			}

			vertices[(i * 12) + 0] = x; 				//top left x 
			vertices[(i * 12) + 1] = (y + cellSize); 	//top left y
			vertices[(i * 12) + 2] = 0.0f; 				//top left z
			
			vertices[(i * 12) + 3] = (x + cellSize);	//top right x
			vertices[(i * 12) + 4] = (y + cellSize);	//top right y
			vertices[(i * 12) + 5] = 0.0f; 				//top right z
			
			vertices[(i * 12) + 6] = x; 				//bottom left x 
			vertices[(i * 12) + 7] = y;					//bottom left y
			vertices[(i * 12) + 8] = 0.0f;				//bottom left z
			
			vertices[(i * 12) + 9] = (x + cellSize);	//bottom right x
			vertices[(i * 12) + 10] = y; 				//bottom right y
			vertices[(i * 12) + 11] = 0.0f; 			//bottom right z
			
			uvCoords[(i * 8) + 0] = uv_x;						//top left u
			uvCoords[(i * 8) + 1] = (uv_y + uvCellHeight);	//top left v
			
			uvCoords[(i * 8) + 2] = (uv_x + uvCellWidth); 	//top right u
			uvCoords[(i * 8) + 3] = (uv_y + uvCellHeight); 	//top right v
			
			uvCoords[(i * 8) + 4] = (uv_x); 					//bottom left u
			uvCoords[(i * 8) + 5] = (uv_y); 					//bottom left v
	
			uvCoords[(i * 8) + 6] = (uv_x + uvCellWidth); 	//bottom right u
			uvCoords[(i * 8) + 7] = (uv_y); 				//bottom right v

			// First triangle
			indices[(i * 6) + 0] = index + 2;
			indices[(i * 6) + 1] = index;
			indices[(i * 6) + 2] = index + 3;

			// Second triangle
			indices[(i * 6) + 3] = index;
			indices[(i * 6) + 4] = index + 1;
			indices[(i * 6) + 5] = index + 3;

			index += 4;
			x += cellSize;
		}
		
		

		mesh = new VertexArray(vertices, normals, uvCoords, indices);
	}

	
//	public void update(String text)
//	{
//		int index = 0;
//		char currentChar;
//		float uv_x, uv_y;
//		float uvCellHeight = 1.0f / colCount;
//		float uvCellWidth = 1.0f / rowCount;
//		int row, column;
//		int x = startPositionX, y = startPositionY;
//
//		vertexData.clear();
//		indices = new int[text.length() * 6];
//		
//		Vector3f[] positions = new Vector3f[4];
//		Vector2f[] uvs = new Vector2f[4];
//		Vector3f normal = new Vector3f(0, 0, 1);
//
//		for (int i = 0; i < text.length(); i++)
//		{
//			currentChar = text.charAt(i);
//			row = (currentChar % rowCount);
//			column = (currentChar / colCount);
//
//			uv_x = row / (float) rowCount;
//			uv_y = column / (float) colCount;
//
//			if (currentChar == '\n')
//			{
//				y += cellSize;
//				x = 0;
//				continue;
//			}
//
//			positions[0] = new Vector3f(x, (y + cellSize), 0.0f); 				//top left 
//			positions[1] = new Vector3f(x + cellSize, (y + cellSize), 0.0f); 		//top right
//			positions[2] = new Vector3f(x, y, 0.0f); 								//bottom left 
//			positions[3] = new Vector3f(x + cellSize, y, 0.0f); 					//bottom right 
//
//			uvs[0] = new Vector2f(uv_x, uv_y + uvCellHeight); 				//top left 
//			uvs[1] = new Vector2f(uv_x + uvCellWidth, uv_y + uvCellHeight); //top right
//			uvs[2] = new Vector2f(uv_x, uv_y); 								//bottom left
//			uvs[3] = new Vector2f(uv_x + uvCellWidth, uv_y); 				//bottom right
//
//			vertexData.add(new Vertex(positions[0], uvs[0], normal));
//			vertexData.add(new Vertex(positions[1], uvs[1], normal));
//			vertexData.add(new Vertex(positions[2], uvs[2], normal));
//			vertexData.add(new Vertex(positions[3], uvs[3], normal));
//
//			// First triangle
//			indices[(i * 6) + 0] = index + 2;
//			indices[(i * 6) + 1] = index;
//			indices[(i * 6) + 2] = index + 3;
//
//			// Second triangle
//			indices[(i * 6) + 3] = index;
//			indices[(i * 6) + 4] = index + 1;
//			indices[(i * 6) + 5] = index + 3;
//
//			index += 4;
//			x += cellSize;
//		}
//
//		if (this.vao != null)
//			this.vao.delete();
//		
//		createVAO();
//	}
	
	/**
	 * @param mode render mode (GL_LENUM)
	 * Handels binding of font texture and calls GameObject2D's draw method
	 */

	public void render(int mode)
	{	
	    fontTexture.bind();
		mesh.render(mode);
		fontTexture.unbind();
	}
	
	public void setFont(String font)
	{
	    this.fontTexture = new Texture("src/res/textures/fonts/" + font);
	}
}