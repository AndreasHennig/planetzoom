package engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;
import input.Cursor;
import input.Keyboard;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GLContext;

public class CoreEngine
{
    private final IGame game;
    public boolean running;
    public long windowHandle;
  
    boolean fullscreen = false;
    
    int windowWidth = 800; 
    int windowHeight = 600;
    
    private GLFWKeyCallback keyCallback;
    private GLFWCursorPosCallback cursorCallback;

    public Timer timer;
    
    public CoreEngine(IGame game)
    {
        this.game = game;
    }

    public void start()
    {
        running = true;

        init();

        while(running)
        {
            update();
            render();

            if(glfwWindowShouldClose(windowHandle) == GL_TRUE)
            {
                running = false;
            }
        }

        keyCallback.release();
        cursorCallback.release();
    }

    public void init()
    {
        if(glfwInit() != GL_TRUE)
        {
            System.err.println("can't initialize GLFW");
        }

        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        
        // necessary for OpenGL 3/4:
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
    	GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
    	GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    	GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

    	
    	if(fullscreen)
    	{
    		long monitor = glfwGetPrimaryMonitor();
    		GLFWvidmode mode = new GLFWvidmode(glfwGetVideoMode(monitor));
    		
    		windowWidth = mode.getWidth();
    		windowHeight = mode.getHeight();
    		
    		//System.out.println("width: " + windowWidth + " | height: " + windowHeight);
    		
    		windowHandle = glfwCreateWindow(windowWidth, windowHeight, "Stare into it device: " + windowHandle, monitor, NULL);
       	} 
    	else 
    	{
    		windowHandle = glfwCreateWindow(windowWidth, windowHeight, "Stare into it device: " + windowHandle, NULL, NULL);	
    	}
        
        if(windowHandle == NULL)
        {
            System.err.println("Window creation failed");
        }
        
        glfwSetWindowPos(windowHandle, 100, 100);

        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(1); //vSync

        glfwShowWindow(windowHandle);

        GLContext.createFromCurrent();

        glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetKeyCallback(windowHandle, keyCallback = new Keyboard());
        glfwSetCursorPosCallback(windowHandle, cursorCallback = new Cursor());

        game.init();
        
        timer = new Timer(); //not sure if best here
    }

    public void update()
    {
        glfwPollEvents();
        
        int deltaTime = timer.getDeltaTime();
        /*
            int fps = timer.getFPS();
        	int fps2 = timer.getExpectedFPS();
        	System.out.println("dt: " + deltaTime + " | fps: " + fps + " | efps: " + fps2);
         */
        game.update(deltaTime);
        
        if(Keyboard.isKeyPressed(GLFW_KEY_ESCAPE))
        	glfwSetWindowShouldClose(windowHandle, GL_TRUE);
        
        

    }

    public void render()
    {
    	game.render();
                
        glfwSwapBuffers(windowHandle);
    }
    
}
