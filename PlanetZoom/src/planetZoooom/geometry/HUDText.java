package planetZoooom.geometry;

import planetZoooom.engine.MeshObject;
import planetZoooom.engine.VertexArray;
import planetZoooom.graphics.Texture;

public class HUDText extends MeshObject
{
	
	private static final int ROWCOUNT_STANDARD = 16;
	private static final int COLUMNCOUNT_STANDARD = 16;
	
	private Texture fontTexture;
	private int rowCount;
	private int colCount;

	private int x,y, row, column, index;
	private float uvCellWidth, uvCellHeight, uv_x, uv_y;
	private char currentChar;
	
	private int startPositionX;
	private int startPositionY;
	private int cellSize;
	
	private int maxWidth;
	private int maxHeight;
	
	private int textLength;

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
		this.textLength = text.length();
		createFloatArrays();
		mesh = new VertexArray(vertices, normals, uvCoords, indices);
		update(text);
	}
	
	private void createFloatArrays()
	{
		vertices = new float[12 * textLength];	//4 vertices per quad * 3 floats(x,y,z) * chars
		normals = new float[vertices.length];
		uvCoords = new float[8 * textLength];	//4 texCoords per quad * 2 floats(u,v) * chars
		indices = new int[6 * textLength];		//6 points to draw 2 triangles = 1 quad
	}
	
	//int creations = 0;
	//int updates = 0;
	
	
	public void update(String text)
	{
		//updates++;
		//System.out.println("updates: " + updates + " / creations: " + creations);
		index = 0;
		maxWidth = 0;
		maxHeight = 0;
		uvCellHeight = 1.0f / colCount;
		uvCellWidth = 1.0f / rowCount;
		x = startPositionX; 
		y = startPositionY;

		if(text.length() != textLength)
		{
			this.textLength = text.length();
			createFloatArrays();
			//creations++;
		}

		for (int i = 0; i < textLength ; i++)
		{
			currentChar = text.charAt(i);
			row = (currentChar % rowCount);
			column = (currentChar / colCount);

			uv_x = row / (float) rowCount;
			uv_y = column / (float) colCount;

			if (currentChar == '\n')
			{
				y += cellSize;
				x = startPositionX - cellSize;
			}
			if(x > maxWidth - cellSize)
				maxWidth = x + cellSize;
			if(y > maxHeight - cellSize)
				maxHeight = y + cellSize;

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

		mesh.update(vertices, normals, uvCoords, indices);
	}

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
	public int getMaxWidth()
	{
		return maxWidth;
	}
	public int getMaxHeight()
	{
		return maxHeight;
	}
}