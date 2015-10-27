
package game;

import app.ClientMain;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import util.EnginePool;

public class Projectile extends GameObject
{
    private boolean launch;
    private int hitSprite;
    private int decayDelay;
    private GameObject launcher;
    
    public Projectile(GameObject launcher)
    {
        this.launcher = launcher;
        moveSpeed = 100;
        visible = false;
        decayDelay = 10;
        hitSprite = 1;
        
        addSprite(ClientMain.getFile("sprite/goo.png"), 0);
        addSprite(ClientMain.getFile("sprite/pak.png"), 0);
        setCurrentSprite(0);
    }
    
    public void launch(double x, double y)
    {
        launch = true;
        destination.set(x, y);
        location.set(launcher.location);
        
        setActive(true);
        GameEngine.getInstance().addGameObject(this);
    }
    
    public void launch(Vector2 target)
    {
        launch(target.getX(), target.getY());
    }
    
    public void setHitSprite(int index)
    {
        hitSprite = index;
    }
    
    @Override
    protected void processBehavior() 
    {
        if(launch) //move to target
        {
            double delta = GameEngine.getInstance().getDelta();
            double increment = moveSpeed * delta;
            
            setVisible(true);
            
            direction.set(destination).subtract(location).normalize();
            rotation = direction.angle();
            
            Vector2 locInc = EnginePool.Vector2.fetch().set(direction).multiply(increment);
            
            location.add(locInc);
            
            //move increment is greater than distance to destination so finalize move
            if(location.distance(destination) < increment)
            {
                setCurrentSprite(hitSprite);
                
                if(target != null)
                    target.hitPoints -= launcher.attackDamage;
                
                launch = false;
            }
            
            EnginePool.Vector2.release(locInc);
        }
        else
        {
            if(decayDelay == 0)
            {
                GameEngine.getInstance().removeGameObject(this);
                setActive(false);
            }
            else
                decayDelay--;
        }
    }

    @Override
    public void update(String data) 
    {
    }

    @Override
    public void processKeyEvent(KeyEvent e) 
    {
    }

    @Override
    public void processMouseEvent(MouseEvent e) 
    {
    }
}
