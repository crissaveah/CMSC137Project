
package app.character;

import game.Character;
import game.GameEngine;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class GameCharacter extends Character
{
    @Override
    public void update() 
    {
    }

    @Override
    public void processKeyEvent(KeyEvent e) 
    {
        
    }

    @Override
    public void processMouseEvent(MouseEvent e) 
    {   
        int x = GameEngine.getInstance().getWorldX(e.getX());
        int y = GameEngine.getInstance().getWorldY(e.getY());
        
        if(e.getID() == MouseEvent.MOUSE_PRESSED)
        {
            if(e.getButton() == MouseEvent.BUTTON3)
            {
                move(x, y);
            }
        }
    }
    
    @Override
    protected void processBehavior()
    {
        super.processBehavior();
    }
}
