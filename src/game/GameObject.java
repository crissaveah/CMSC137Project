
package game;

import app.ui.ClientUI;
import client.ChatClient;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;

public abstract class GameObject extends Thread implements GameListener
{
    protected int id;
    protected int type;
    protected int owner;
    protected int hitPoints;
    protected int attackSpeed;
    protected int attackDamage;
    protected int attackRange;
    protected int moveSpeed;
    protected long timeout;
    protected double extentX;
    protected double extentY;
    protected double rotation;
    protected boolean move;
    protected boolean active;
    protected boolean visible;
    protected boolean selected;
    protected boolean occupant;
    protected boolean collidable;
    protected GameObject target;
    protected Worker worker;
    protected StringBuilder builder;
    protected Vector2 location;
    protected Vector2 direction;
    protected Vector2 destination;
    protected BufferedImage currentSprite;
    protected CopyOnWriteArrayList<Long> durations;
    protected CopyOnWriteArrayList<BufferedImage> sprites;
    protected CopyOnWriteArrayList<GameObject> collisions;
    
    public GameObject()
    {
        owner = -1;
        timeout = 10;
        visible = true;
        collidable = true;
        location = new Vector2();
        destination = new Vector2();
        direction = new Vector2(Vector2.UNIT_Y);
        sprites = new CopyOnWriteArrayList<>();
        durations = new CopyOnWriteArrayList<>();
        collisions = new CopyOnWriteArrayList<>();
    }
    
    public void setCollidable(boolean collidable)
    {
        this.collidable = collidable;
    }
    
    public boolean isCollidable()
    {
        return collidable;
    }
    
    public void addSprite(File file, long time)
    {
        try 
        {
            BufferedImage sprite = ImageIO.read(file);
            sprites.add(sprite);
            durations.add(time);
        }
        catch(IOException ex) 
        {
            ClientUI.writeError("Failed to load sprite: "+file.getPath());
        }
    }
    
    public void setID(int id)
    {
        this.id = id;
    }
    
    public int getID()
    {
        return id;
    }
    
    public void setOwner(int owner)
    {
        this.owner = owner;
    }
    
    public int getOwner()
    {
        return owner;
    }
    
    public int getType()
    {
        return type;
    }
    
    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }
    
    public long getTimeout()
    {
        return timeout;
    }
    
    public void setBounds(double x, double y)
    {
        extentX = x;
        extentY = y;
    }
    
    public double getBoundsX()
    {
        return extentX;
    }
    
    public double getBoundsY()
    {
        return extentY;
    }
    
    public double getMaxExtent()
    {
        return Math.max(extentX, extentY);
    }
    
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
    
    public boolean isSelected()
    {
        return selected;
    }
    
    public boolean intersects(GameObject object)
    {
        double dx = Math.abs(location.getX() - object.location.getX());
        double dy = Math.abs(location.getY() - object.location.getY());
        
        return dx <= extentX + object.extentX && dy <= extentY + object.extentY;
    }
    
    public boolean intersects(double x, double y)
    {
        double dx = Math.abs(location.getX() - x);
        double dy = Math.abs(location.getY() - y);
        
        return dx <= extentX && dy <= extentY;
    }
    
    public boolean intersects(Rectangle rect)
    {
        double x = GameEngine.getInstance().getScreenX((int) location.getX());
        double y = GameEngine.getInstance().getScreenY((int) location.getY());
        double width = extentX * 2;
        double height = extentY * 2;
        
        return rect.intersects(x, y, width, height);
    }
    
    public boolean intersects(Vector2 point)
    {
        return intersects(point.getX(), point.getY());
    }
    
    public boolean withinAttackRange(GameObject object)
    {
        return location.distance(object.location) <= attackRange;
    }
    
    public void setHitPoints(int hp)
    {
        hitPoints = hp;
    }
    
    public int getHitPoints()
    {
        return hitPoints;
    }
    
    public void setTranslation(double x, double y)
    {
        location.set(x, y);
    }
    
    public void setTranslation(Vector2 translation)
    {
        setTranslation(translation.getX(), translation.getY());
    }
    
    public double getTranslationX()
    {
        return location.getX();
    }
    
    public double getTranslationY()
    {
        return location.getY();
    }
    
    public void setRotation(double rot)
    {
        rotation = rot;
    }
    
    public void setRotation(double x, double y)
    {
        rotation = Math.atan2(x, y);
    }
    
    public double getRotation()
    {
        return rotation;
    }
    
    public void setCurrentSprite(int index)
    {
        currentSprite = sprites.get(index);
    }

    public BufferedImage getSprite()
    {
        return currentSprite;
    }
    
    public void setTarget(GameObject target)
    {
        this.target = target;
    }
    
    public GameObject getTarget()
    {
        return target;
    }
    
    public void setActive(boolean active)
    {
        this.active = active;
        
        if(active)
        {
            start();
            
            if(ChatClient.getInstance().getID() == owner)
            {
                worker = new Worker(this);
                builder = new StringBuilder();
                
                worker.start();
            }  
        }
    }
    
    public boolean isActive()
    {
        return active;
    }
    
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }
    
    public boolean isVisible()
    {
        return visible;
    }
    
    public String getObjectState()
    {
        builder.delete(0, builder.length());
        
        builder.append(id).append(" ");
        builder.append(type).append(" ");
        builder.append(owner).append(" ");
        builder.append(Math.round(location.getX())).append(" ");
        builder.append(Math.round(location.getY())).append(" ");
        builder.append(rotation).append(" ");
        builder.append(hitPoints).append(" ");
        builder.append(moveSpeed).append(" ");
        builder.append(attackSpeed).append(" ");
        builder.append(attackDamage).append(" ");
        
        if(target != null)
        {
            builder.append(target.id).append(" ");
            builder.append(target.owner).append(" ");
            builder.append(target.hitPoints).append(" ");
        }
        else
        {
            builder.append(-1).append(" ");
            builder.append(-1).append(" ");
            builder.append(-1).append(" ");
        }
        
        return builder.toString();
    }
    
    protected void checkCollision()
    {
        if(collidable)
            for(GameObject object : GameEngine.getInstance().getGameObjects())
                if(!object.equals(this) && !(object instanceof Terrain)  && !(object instanceof Projectile) && object.collidable)
                    if(intersects(object))
                        collisions.add(object);
    }
    
    protected boolean checkPickTarget(MouseEvent e, int x, int y)
    {
        boolean picked = false;
        target = null;
        
        for(GameObject object : GameEngine.getInstance().getGameObjects())
        {
            if(object.getOwner() != ChatClient.getInstance().getID() && object.intersects(x, y))
            {
                picked = true;
                target = object;
                break;
            }
        }
        
        return picked;
    }
    
    @Override
    public void run()
    {
        while(active)
        {   
            processBehavior();
            
            try 
            {
                Thread.sleep(timeout);
            }
            catch(InterruptedException ex) 
            {
            }
        }
    }
    
    protected abstract void processBehavior();
}
