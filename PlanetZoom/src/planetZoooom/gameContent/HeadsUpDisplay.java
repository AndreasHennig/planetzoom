package planetZoooom.gameContent;

import org.lwjgl.util.vector.Matrix4f;

import planetZoooom.geometry.HUDText;

public class HeadsUpDisplay
{
	private Matrix4f modelMatrix = new Matrix4f();
	
	private static final int STANDARD_POSITION_X = 0;
	private static final int STANDARD_POSITION_Y = 0;
	private static final String  STANDARD_FONT = "arial_nm.png";
	
	private int position_x;
	private int position_y;
	private String font;
	private HUDText text;
	
	public HeadsUpDisplay()
	{
		position_x = STANDARD_POSITION_X;
		position_y = STANDARD_POSITION_Y;
		font = STANDARD_FONT;
		this.text = new HUDText("", font, position_x, position_y, 16);
	}
	
	public HeadsUpDisplay(int x, int y, String font)
	{
		position_x = x;
		position_y = y;
		this.font = font;
		this.text = new HUDText("", font, position_x, position_y, 16);

	}
	
	public void update(String text)
	{
		this.text.update(text);
	}
	
	public HUDText getTextMesh()
	{
	    return text;
	}
	
	public Matrix4f getModelMatrix()
	{
		return modelMatrix;
	}
	public void setFont(String font)
	{
	    this.font = font;
	    text.setFont(font);
	}
}