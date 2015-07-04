package planetZoooom.graphics;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

import static org.lwjgl.opengl.GL11.*;

public class Texture 
{
	private int width;
	private int height;
	private int textureID;
	
	public Texture(String path)
	{
		load(path);
	}
	
	private void load(String path)
	{
		InputStream in;

		try
		{
			in = new FileInputStream(path);
			PNGDecoder decoder = new PNGDecoder(in);
			
			width = decoder.getWidth();
			height = decoder.getHeight();
			
			ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			
			textureID = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, textureID);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glBindTexture(GL_TEXTURE_2D, 0);
			
			in.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void bind()
	{
		glBindTexture(GL_TEXTURE_2D, textureID);
	}

	public void unbind()
	{
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	
}

