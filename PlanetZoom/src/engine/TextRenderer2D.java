package engine;

import geometry.Vertex2D;

import java.util.ArrayList;

import lenz.utils.ShaderProgram;
import lenz.utils.Texture;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class TextRenderer2D
{
	
	private static final int ROWCOUNT_STANDARD = 16;
	private static final int COLUMNCOUNT_STANDARD = 16;
	/**
	 * 
	 * @param text text to be rendered
	 * @param font name of the font bitmap (located in res.textures.fonts)
	 * @param x	x_cooridnate
	 * @param y y_coordinate
	 * @param size size of a character in the given bitmap
	 */
	public static GameObject2D textToObject2D(String text, String font, int x, int y, int size)
	{
		return textToObject2D(text, font, x, y, size, ROWCOUNT_STANDARD, COLUMNCOUNT_STANDARD);
	}
	
	/**
	 * 
	 * @param text text to be rendered
	 * @param font name of the font bitmap (located in res.textures.fonts)
	 * @param x	x_cooridnate
	 * @param y y_coordinate
	 * @param size size of a character-cell in the given bitmap
	 * @param number of character rows in the given bitmap
	 * @param number of character columns in the given bitmap
	 */
	public static GameObject2D textToObject2D(String text, String font, int x, int y, int size, int rowCount, int colCount)
	{
		int index = 0;
		char currentChar;
		float uv_x, uv_y;
		
		float cellHeight = 1.0f / colCount;
		float cellWidth = 1.0f / rowCount;
		
		int row, column;
		
		int[] indices = new int[text.length() * 6];
		ArrayList<Vector2f> positions = new ArrayList<>();;
		ArrayList<Vector2f> uvs = new ArrayList<>();
		ArrayList<Vertex2D> vertices = new ArrayList<>();
        Vector3f normal = new Vector3f(0, 0, 1);
        
		for(int i = 0; i < text.length(); i++)
		{
			positions.add(new Vector2f(x + (i * size), (y + size))); 		 	// top left			index
			positions.add(new Vector2f(x + ((i + 1) * size), (y + size))); 		// top right		index + 1
			positions.add(new Vector2f(x + (i * size), y)); 			 		// bottom left		index + 2
			positions.add(new Vector2f(x + ((i + 1) * size), y)); 		 		// bottom right		index + 3
			
			
			currentChar = text.charAt(i);
			int ascii = (int) text.charAt(i);
			
			row = (currentChar % rowCount);
			column = (currentChar / colCount);
			
			uv_x =  row / (float) rowCount;
			uv_y =  column / (float) colCount;

//			uvs.add(new Vector2f(uv_x, 1 - uv_y));			  // top left			index
//			uvs.add(new Vector2f(uv_x + cellWidth, 1 - uv_y));		  // top right			index + 1
//			uvs.add(new Vector2f(uv_x + cellWidth, (1 - uv_y) + cellHeight));  // bottom left		index + 2
//			uvs.add(new Vector2f(uv_x, (1 - uv_y) + cellHeight)); 	  // bottom right		index + 3
			
			uvs.add(new Vector2f(uv_x, uv_y));			  					// bottom left		index
			uvs.add(new Vector2f(uv_x + cellWidth, uv_y));		  			// bottom right		index + 1
			uvs.add(new Vector2f(uv_x + cellWidth, uv_y + cellHeight));  	// top right		index + 2
			uvs.add(new Vector2f(uv_x, uv_y + cellHeight)); 	  			// top left			index + 3
			
			
			vertices.add(new Vertex2D(positions.get(index), uvs.get(index + 3), normal));
			vertices.add(new Vertex2D(positions.get(index + 1), uvs.get(index + 2), normal));
			vertices.add(new Vertex2D(positions.get(index + 2), uvs.get(index), normal));
			vertices.add(new Vertex2D(positions.get(index + 3), uvs.get(index + 1), normal));
			
			//First triangle
			indices[(i * 6)] = index + 3;
			indices[(i * 6) + 1] = index;
			indices[(i * 6) + 2] = index + 2;
			
			//Second triangle
			indices[(i * 6) + 3] = index + 3;
			indices[(i * 6) + 4] = index + 1;
			indices[(i * 6) + 5] = index;
			
						
			index += 4;
		}
		
		Texture textTexture = new Texture("fonts/" + font);

		GameObject2D obj =  new GameObject2D(vertices, indices);
		obj.setTexture(textTexture);
		return obj;
	}
	
	
}
