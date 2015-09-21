
package app.test;

import app.ClientMain;
import app.character.Archer;
import game.GameEngine;
import game.GameListener;
import game.Terrain;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

public final class GameLogic implements GameListener
{
    private Terrain terrain;
    
    public GameLogic()
    {
        terrain = new Terrain();
        terrain.addSprite(ClientMain.getResource("sprite/terrain1.png"), 0);
        
        GameEngine.getInstance().addGameObject(terrain);
        GameEngine.getInstance().setWorldSize(terrain.getSprite().getWidth(), terrain.getSprite().getHeight());
        GameEngine.getInstance().addGameListener(this);
    }
    
    @Override
    public void update() 
    {
    }

    @Override
    public void processKeyEvent(KeyEvent e) 
    {
    }

    @Override
    public void processMouseEvent(final MouseEvent e) 
    {
        SwingUtilities.invokeLater
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    int x = GameEngine.getInstance().getWorldX(e.getX());
                    int y = GameEngine.getInstance().getWorldY(e.getY());
                    
                    if(e.getID() == MouseEvent.MOUSE_CLICKED)
                    {
                        if(e.getButton() == MouseEvent.BUTTON1) //left mouse button
                        {
                            Archer ladybug = new Archer();

                            ladybug.addSprite(ClientMain.getResource("sprite/ladybug.png"), 0);
                            ladybug.setBounds(ladybug.getSprite().getWidth()/2, ladybug.getSprite().getHeight()/2);
                            ladybug.setTranslation(x, y);

                            GameEngine.getInstance().addGameObject(ladybug);
                            ladybug.setActive(true);
                        }
                    }
                    
                    if(e.getID() == MouseEvent.MOUSE_PRESSED)
                    {
                        if(e.getButton() == MouseEvent.BUTTON3) //right mouse button
                        {
                            
                        }
                    }
                }
            }
        );
    }
}
