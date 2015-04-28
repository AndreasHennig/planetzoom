package game;

import engine.CoreEngine;
import engine.IGame;
import input.Input;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


public class Game implements IGame
{
    public static void main(String[] args)
    {
        CoreEngine game = new CoreEngine(new Game());
        game.start();
    }

    @Override
    public void init()
    {
        printVersionInfo();
        glClearColor(0.7f, 0.7f, 0.8f, 1.0f);
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void update()
    {
        if(Input.keys[GLFW_KEY_SPACE])
        {
            System.out.println("Spacebar pressed!");
        }
    }

    @Override
    public void render()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }


    private void printVersionInfo()
    {
        System.out.println("GPU Vendor: " + glGetString(GL_VENDOR));
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));
    }
}
