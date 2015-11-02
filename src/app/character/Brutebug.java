
package app.character;

import app.logic.GameLogic;
import game.Character;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Brutebug extends Character
{
    public Brutebug()
    {
        type = GameLogic.BRUTEBUG;
        moveSpeed = 20;
        hitPoints = 500;
        attackRange = 60;
        attackDamage = 50;
        attackSpeed = 50;
    }
    
    @Override
    protected void processBehavior()
    {
        super.processBehavior();
    }

    @Override
    public void update(String data)
    {
    }

    @Override
    public void processKeyEvent(KeyEvent e) 
    {
        super.processKeyEvent(e);
    }

    @Override
    public void processMouseEvent(MouseEvent e) 
    {
        super.processMouseEvent(e);
    }
}
