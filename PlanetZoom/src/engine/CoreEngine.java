package engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;

import input.Input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GLContext;

public class CoreEngine
{
    private final IGame game;
    public boolean running;
    public long windowHandle;
    private GLFWKeyCallback keyCallback;

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
    }

    public void init()
    {
        if(glfwInit() != GL_TRUE)
        {
            System.err.println("can't initialize GLFW");
        }

        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
    	GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
    	GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    	GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
    	
        
        
        windowHandle = glfwCreateWindow(800,600, "Stare into it device", NULL, NULL);

        if(windowHandle == NULL)
        {
            System.err.println("Window creation failed");
        }

        ByteBuffer vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(windowHandle, 100, 100);

        glfwMakeContextCurrent(windowHandle);

        glfwShowWindow(windowHandle);
        glfwSetKeyCallback(windowHandle, keyCallback = new Input());

        GLContext.createFromCurrent();

        game.init();
    }

    public void update()
    {
        glfwPollEvents();

        game.update();
    }

    public void render()
    {
        game.render();

        glfwSwapBuffers(windowHandle);
    }

}
