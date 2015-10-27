
package app.character;

import app.logic.GameLogic;
import game.Character;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Quickbug extends Character
{
    public Quickbug()
    {
        type = GameLogic.QUICKBUG;
        moveSpeed = 50;
        hitPoints = 100;
        attackRange = 150;
        attackDamage = 10;
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
