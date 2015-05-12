package lenz.utils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

public class Texture {
	private int id;

	public Texture(String resourceName) 
	{
		this(resourceName, 1, false);
	}

	public Texture(String resourceName, int numberOfMipMapLevels) 
	{
		this(resourceName, numberOfMipMapLevels, numberOfMipMapLevels != 1);
	}

	public Texture(String resourceName, int numberOfMipMapLevels, boolean autoGenerateMipMaps) {
		try {
			createTextureFromImage(ImageIO.read(createInputStreamFromResourceName(resourceName)), 0);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, numberOfMipMapLevels - 1);
			if (autoGenerateMipMaps) {
				glGenerateMipmap(GL_TEXTURE_2D);
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to read texture from stream", e);
		}
	}

	private InputStream createInputStreamFromResourceName(String resourceName) 
	{
		if (!resourceName.startsWith("/")) {
			resourceName = "/res/textures/" + resourceName;
		}
		return getClass().getResourceAsStream(resourceName);
	}

	private void createTextureFromImage(BufferedImage image, int mipMapLevel) 
	{
		int width = image.getWidth();
		int height = image.getHeight();
		boolean hasAlpha = image.getColorModel().hasAlpha();

		ByteBuffer buffer = ByteBuffer.allocateDirect((hasAlpha ? 4 : 3) * width * height);

		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int argb = image.getRGB(x, y);
				buffer.put((byte) ((argb >> 16) & 0xff));
				buffer.put((byte) ((argb >> 8) & 0xff));
				buffer.put((byte) (argb & 0xff));
				if (hasAlpha) {
					buffer.put((byte) ((argb >> 24) & 0xff));
				}
			}
		}
		buffer.flip();

		if (mipMapLevel == 0) {
			id = glGenTextures();
		}
		glBindTexture(GL_TEXTURE_2D, id);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

		glTexImage2D(GL_TEXTURE_2D, mipMapLevel, hasAlpha ? GL_RGBA : GL_RGB, width, height, 0, hasAlpha ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, buffer);
	}

	public void addMipMapLevel(String resourceName, int level) {
		try {
			glBindTexture(GL_TEXTURE_2D, id);
			createTextureFromImage(ImageIO.read(createInputStreamFromResourceName(resourceName)), level);
		} catch (IOException e) {
			throw new RuntimeException("Unable to read texture from stream", e);
		}
	}

	public void delete() {
		glDeleteTextures(id);
	}

	public int getId() {
		return id;
	}
}
