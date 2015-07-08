package planetZoooom.geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import planetZoooom.graphics.Texture;

public class HUDText extends GameObject
{
	
	private static final int ROWCOUNT_STANDARD = 16;
	private static final int COLUMNCOUNT_STANDARD = 16;
	
	private Texture fontTexture;
	private int rowCount;
	private int colCount;
	
	private int startPositionX;
	private int startPositionY;
	private int cellSize;
	
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

		vertexData.clear();
		indices = new int[text.length() * 6];
		
		Vector3f[] positions = new Vector3f[4];
		Vector2f[] uvs = new Vector2f[4];
		Vector3f normal = new Vector3f(0, 0, 1);

		for (int i = 0; i < text.length(); i++)
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
				continue;
			}

			positions[0] = new Vector3f(x, (y + cellSize), 0.0f); 				//top left 
			positions[1] = new Vector3f(x + cellSize, (y + cellSize), 0.0f); 		//top right
			positions[2] = new Vector3f(x, y, 0.0f); 								//bottom left 
			positions[3] = new Vector3f(x + cellSize, y, 0.0f); 					//bottom right 

			uvs[0] = new Vector2f(uv_x, uv_y + uvCellHeight); 				//top left 
			uvs[1] = new Vector2f(uv_x + uvCellWidth, uv_y + uvCellHeight); //top right
			uvs[2] = new Vector2f(uv_x, uv_y); 								//bottom left
			uvs[3] = new Vector2f(uv_x + uvCellWidth, uv_y); 				//bottom right

			vertexData.add(new Vertex(positions[0], uvs[0], normal));
			vertexData.add(new Vertex(positions[1], uvs[1], normal));
			vertexData.add(new Vertex(positions[2], uvs[2], normal));
			vertexData.add(new Vertex(positions[3], uvs[3], normal));

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

		if (this.vao != null)
			this.vao.delete();
		
		createVAO();
	}
	
	/**
	 * @param mode render mode (GL_LENUM)
	 * Handels binding of font texture and calls GameObject2D's draw method
	 */
	@Override
	public void draw(int mode)
	{	
	    fontTexture.bind();
		super.draw(mode);
		fontTexture.unbind();
	}
	
	public void setFont(String font)
	{
	    this.fontTexture = new Texture("src/res/textures/fonts/" + font);
	}
}