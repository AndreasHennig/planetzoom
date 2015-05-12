package engine;

import geometry.Vertex2D;

import java.util.ArrayList;

import lenz.utils.ShaderProgram;
import lenz.utils.Texture;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class TextRenderer2D
{
	
	private static void setUpTextVBO(String text, int x, int y, int size, int length)
	{
		
	}
	
	/**
	 * 
	 * @param text text to be rendered
	 * @param font name of the font bitmap (located in res.textures.fonts)
	 * @param x	x_cooridnate
	 * @param y y_coordinate
	 * @param size size of a character in the given bitmap
	 */
	public static Object2D textToObject2D(String text, String font, int x, int y, int size)
	{
		
		byte index = 0;
		char currentChar;
		float uv_x, uv_y;
		float f = 1.0f / 16.0f;
		
		byte[] indices = new byte[text.length() * 6];
		ArrayList<Vector2f> positions = new ArrayList<>();;
		ArrayList<Vector2f> uvs = new ArrayList<>();
		ArrayList<Vertex2D> vertices = new ArrayList<>();
        Vector3f normal = new Vector3f(0, 0, 1);
        
		for(int i = 0; i < text.length(); i++)
		{
			positions.add(new Vector2f(x + (i * size), y + size)); 		 // top left		index
			positions.add(new Vector2f(x + ((i + 1) * size), y + size)); // top right		index + 1
			positions.add(new Vector2f(x + (i * size), y)); 			 // bottom left		index + 2
			positions.add(new Vector2f(x + ((i + 1) * size), y)); 		 // bottom right	index + 3
			
			currentChar = text.charAt(i);
			uv_x = (currentChar % 16) / 16.0f;
			uv_y = (currentChar / 16) / 16.0f;

			uvs.add(new Vector2f(uv_x, 1 - uv_y));			  // top left			index
			uvs.add(new Vector2f(uv_x + f, 1 - uv_y));		  // top right			index + 1
			uvs.add(new Vector2f(uv_x + f, (1 - uv_y) + f));  // bottom left		index + 2
			uvs.add(new Vector2f(uv_x, (1 - uv_y) + f)); 	  // bottom right		index + 3
			
			//First triangle
			indices[(i * 6)] = index;
			indices[(i * 6) + 1] = (byte) (index + 3);
			indices[(i * 6) + 2] = (byte) (index + 1);
			//Second triangle
			indices[(i * 6) + 3] = (byte) (index + 2);
			indices[(i * 6) + 4] = (byte) (index + 1);
			indices[(i * 6) + 5] = (byte) (index + 3);
			
						
			index += 4;
		}
		
		Texture textTexture = new Texture("fonts/" + font);

		Object2D obj =  new Object2D(vertices, indices);
		obj.setTexture(textTexture);
		obj.setShader(new ShaderProgram("textShader"));
		return obj;
	}
	
	
}
