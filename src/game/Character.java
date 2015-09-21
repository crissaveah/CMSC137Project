
package game;

import util.EnginePool;

public abstract class Character extends GameObject
{
    protected double attackSpeed;
    protected double attackDamage;
    protected double moveSpeed;
    protected GameObject target;
    
    public Character()
    {
        hitPoints = 100;
        moveSpeed = 0.2;
        attackSpeed = 2;
        attackDamage = 10;
    }
    
    public void setTarget(GameObject target)
    {
        this.target = target;
    }
    
    public GameObject getTarget()
    {
        return target;
    }
    
    public void setHitPoints(int hp)
    {
        hitPoints = hp;
    }
    
    public int getHitPoints()
    {
        return hitPoints;
    }
    
    public void setMoveSpeed(double ms)
    {
        moveSpeed = ms;
    }
    
    public double getMoveSpeed()
    {
        return moveSpeed;
    }
    
    public void setAttackSpeed(double as)
    {
        attackSpeed = as;
    }
    
    public double getAttackSpeed()
    {
        return attackSpeed;
    }
    
    public void setAttackDamage(double ad)
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
        direction.multiply(moveSpeed * GameEngine.getInstance().getDelta());
        rotation = direction.angle();
        move = true;
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
    protected void processBehavior()
    {
        double delta = GameEngine.getInstance().getDelta();
        double increment = moveSpeed * delta;
        
        if(move)
        {
            location.add(direction);
            dirty = true;
            
            //location increment is greater than distance to destination so finalize move
            if(location.distance(destination) < increment)
            {
                location.set(destination);
                move = false;
                dirty = false;
            }
        }
        
        if(!collisions.isEmpty()) //process collision
        {
            Vector2 dirToCollider = EnginePool.Vector2.fetch();
            
            for(GameObject collider : collisions)
            {
                dirToCollider.set(collider.location).subtract(location).normalize().multiply(increment);
                
                while(intersects(collider)) //move a unit away from collider
                {
                    if(location.equals(collider.location))
                        location.subtract(collider.getSprite().getWidth(), 0);
                    else
                        location.subtract(dirToCollider);
                }

                if(move)
                {
                    //an object already occupies the destination, don't try to squeeze in
                    //1. stop if collided with occupant
                    //2. stop if collided with an object which satisfies the previous or this condition
                    if(occupant != null && collidedOccupant)
                    {
                        move = false;
                        dirty = false;
                        collidedOccupant = false;
                        occupant =  null;
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
            
            EnginePool.Vector2.release(dirToCollider);
        }
    }
}
