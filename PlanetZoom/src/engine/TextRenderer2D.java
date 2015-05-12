package engine;

import geometry.Vertex2D;

import java.util.ArrayList;

import lenz.utils.ShaderProgram;
import lenz.utils.Texture;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class TextRenderer2D
{
	
	private static final int rowCountStandard = 16;
	private static final int colCountStandard = 16;
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
		return textToObject2D(text, font, x, y, size, rowCountStandard, colCountStandard);
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
		float r = 1.0f / (float) rowCount;
		float c = 1.0f / (float) colCount;
		
		int[] indices = new int[text.length() * 6];
		ArrayList<Vector2f> positions = new ArrayList<>();;
		ArrayList<Vector2f> uvs = new ArrayList<>();
		ArrayList<Vertex2D> vertices = new ArrayList<>();
        Vector3f normal = new Vector3f(0, 0, 1);
        
		for(int i = 0; i < text.length(); i++)
		{
			positions.add(new Vector2f(x + (i * size) / 80.0f, (y + size) / 60.0f)); 		 	// top left			index
			positions.add(new Vector2f(x + ((i + 1) * size) / 800.0f, (y + size) / 60.0f)); 	// top right		index + 1
			positions.add(new Vector2f(x + (i * size) / 80.0f, y / 600.0f)); 			 		// bottom left		index + 2
			positions.add(new Vector2f(x + ((i + 1) * size) / 800.0f, y / 60.0f)); 		 		// bottom right		index + 3
			
			currentChar = text.charAt(i);
			uv_x = (currentChar % rowCount) / (float) rowCount;
			uv_y = (currentChar / colCount) / (float) colCount;

			uvs.add(new Vector2f(uv_x, 1 - uv_y));			  // top left			index
			uvs.add(new Vector2f(uv_x + r, 1 - uv_y));		  // top right			index + 1
			uvs.add(new Vector2f(uv_x + r, (1 - uv_y) + c));  // bottom left		index + 2
			uvs.add(new Vector2f(uv_x, (1 - uv_y) + c)); 	  // bottom right		index + 3
			
			vertices.add(new Vertex2D(positions.get(index), uvs.get(i), normal));
			vertices.add(new Vertex2D(positions.get(index + 1), uvs.get(index + 1), normal));
			vertices.add(new Vertex2D(positions.get(index + 2), uvs.get(index + 2), normal));
			vertices.add(new Vertex2D(positions.get(index + 3), uvs.get(index + 3), normal));
			
			//First triangle
			indices[(i * 6)] = index;
			indices[(i * 6) + 1] = index + 3;
			indices[(i * 6) + 2] = index + 1;
			//Second triangle
			indices[(i * 6) + 3] = index + 2;
			indices[(i * 6) + 4] = index + 1;
			indices[(i * 6) + 5] = index + 3;
			
						
			index += 4;
		}
		
		for(int i = 0; i < vertices.size(); i++)
		{
		}
		Texture textTexture = new Texture("fonts/" + font);

		GameObject2D obj =  new GameObject2D(vertices, indices);
		obj.setTexture(textTexture);
		return obj;
	}
	
	
}
