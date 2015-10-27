
package game;

import app.logic.GameLogic;
import client.ChatClient;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import util.EnginePool;

public abstract class Character extends GameObject
{
    private int attackDelay;
    private int disposeDelay;
    
    public Character()
    {
        disposeDelay = 6000;
    }
    
    public void setMoveSpeed(int ms)
    {
        moveSpeed = ms;
    }
    
    public double getMoveSpeed()
    {
        return moveSpeed;
    }
    
    public void setAttackSpeed(int as)
    {
        attackSpeed = Math.max(Math.min(as, 100), 0);
    }
    
    public double getAttackSpeed()
    {
        return attackSpeed;
    }
    
    public void setAttackDamage(int ad)
    {
        attackDamage = ad;
    }
    
    public double getAttackDamage()
    {
        return attackDamage;
    }
    
    public void move(double x, double y)
    {
        destination.set(x, y);
        direction.set(destination).subtract(location).normalize();
        rotation = direction.angle();
        
        move = true;
        occupant = false;
    }
    
    public void move(Vector2 dest)
    {
        move(dest.getX(), dest.getY());
    }
    
    public boolean isMoving()
    {
        return move;
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
                if(selected && owner == ChatClient.getInstance().getID() && !checkPickTarget(e, x, y))
                    move(x, y);
            }
        }
    }
    
    @Override
    protected void processBehavior()
    {   
        double delta = GameEngine.getInstance().getDelta();
        double increment = moveSpeed * delta;
        
        if(move)
        {
            Vector2 locInc = EnginePool.Vector2.fetch().set(direction).multiply(increment);
            
            location.add(locInc);
            
            //move increment is greater than distance to destination so finalize move
            if(location.distance(destination) < increment)
            {
                move = false;
                occupant = true;
                location.set(destination);
            }
            
            EnginePool.Vector2.release(locInc);
        }
        
        checkCollision();
        
        if(!collisions.isEmpty()) //handle collision
        {
            Vector2 dirToCollider = EnginePool.Vector2.fetch();
            
            for(GameObject collider : collisions)
            {
                dirToCollider.set(collider.location).subtract(location).normalize().multiply(increment);

                //move away from collider
                if(location.equals(collider.location))
                    location.subtract(collider.getSprite().getWidth(), 0);
                else
                    location.subtract(dirToCollider);

                if(move)
                {
                    if(collider.occupant && collider.selected) //collided with an occupant, finalize move
                    {
                        move = false;
                        occupant = true;
                        continue;
                    }
                    
                    Vector2 newDir1 = EnginePool.Vector2.fetch().set(-dirToCollider.getY(), dirToCollider.getX());
                    Vector2 newDir2 = EnginePool.Vector2.fetch().set(dirToCollider.getY(), -dirToCollider.getX());

                    //decide which direction along the tangent to go
                    if(direction.dot(newDir1) > direction.dot(newDir2))
                        location.add(newDir1.normalize().multiply(increment));
                    else
                        location.add(newDir2.normalize().multiply(increment));

                    EnginePool.Vector2.release(newDir1);
                    EnginePool.Vector2.release(newDir2);

                    move(destination);
                }
            }
            
            collisions.clear();
            
            EnginePool.Vector2.release(dirToCollider);
        }
        
        if(target != null && target.getHitPoints() > 0)
        {
            if(attackDelay == 0)
            {
                if(withinAttackRange(target)) //target in range, attack
                {
                    //always face target
                    direction.set(target.location).subtract(location).normalize();
                    rotation = direction.angle();
                    move = false;
                    
                    Projectile projectile = new Projectile(this);
                    projectile.setTarget(target);
                    projectile.launch(target.location);
                }
                else //move towards target until within attack range
                    move(target.location);
                
                attackDelay = (int) ((2000 - attackSpeed) * delta);
            }
            else
                attackDelay--;
        }
        
        if(hitPoints <= 0) //unit is dead
        {
            move = false;
            
            if(owner == ChatClient.getInstance().getID())
                GameLogic.numLives--;
            
            setCollidable(false);
            setCurrentSprite(1);
            setOwner(-1);
                    
            if(disposeDelay == 0)
            {
                GameEngine.getInstance().removeGameObject(this);
                setActive(false);
            }
            else
                disposeDelay--;
        }
    }
}
