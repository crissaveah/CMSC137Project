
package game;

import app.ui.ClientUI;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;

public abstract class GameObject extends Thread implements GameListener
{
    protected int hitPoints;
    protected long owner;
    protected long timeout;
    protected double bounds;
    protected double extentX;
    protected double extentY;
    protected double rotation;
    protected boolean move;
    protected boolean dirty;
    protected boolean active;
    protected boolean collidedOccupant;
    protected Vector2 location;
    protected Vector2 direction;
    protected Vector2 destination;
    protected GameObject occupant;
    protected BufferedImage currentSprite;
    protected CopyOnWriteArrayList<Long> durations;
    protected CopyOnWriteArrayList<BufferedImage> sprites;
    protected CopyOnWriteArrayList<GameObject> collisions;
    
    public GameObject()
    {
        dirty = true;
        owner = -1;
        timeout = 10;
        location = new Vector2();
        destination = new Vector2();
        direction = new Vector2(Vector2.UNIT_Y);
        sprites = new CopyOnWriteArrayList<>();
        durations = new CopyOnWriteArrayList<>();
        collisions = new CopyOnWriteArrayList<>();
    }
    
    public void addSprite(File file, long time)
    {
        try 
        {
            BufferedImage sprite = ImageIO.read(file);
            currentSprite = sprite;
            sprites.add(sprite);
            durations.add(time);
        }
        catch(IOException ex) 
        {
            ClientUI.writeError("Failed to load sprite: "+file.getPath());
        }
    }
    
    public void setOwner(long owner)
    {
        this.owner = owner;
    }
    
    public long getOwner()
    {
        return owner;
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
    
    public boolean intersects(GameObject object)
    {
        double dx = Math.abs(location.getX() - object.location.getX());
        double dy = Math.abs(location.getY() - object.location.getY());
        
        return dx < extentX + object.extentX && dy < extentY + object.extentY;
    }
    
    public boolean intersects(double x, double y)
    {
        return location.distance(x, y) <= bounds;
    }
    
    public boolean intersects(Vector2 point)
    {
        return intersects(point.getX(), point.getY());
    }
    
    public void setTranslation(double x, double y)
    {
        location.set(x, y);
        dirty = true;
    }
    
    public void setTranslation(Vector2 translation)
    {
        setTranslation(translation.getX(), translation.getY());
    }
    
    public void setRotation(double rot)
    {
        rotation = rot;
    }
    
    public void setRotation(double x, double y)
    {
        rotation = Math.atan2(x, y);
    }

    public BufferedImage getSprite()
    {
        return currentSprite;
    }
    
    public void setActive(boolean active)
    {
        this.active = active;
        
        if(active)
            start();
    }
    
    public boolean isActive()
    {
        return active;
    }
    
    public boolean isDirty()
    {
        return dirty;
    }
    
    protected void checkCollision()
    {
        if(dirty)
        {
            collisions.clear();

            for(GameObject object : GameEngine.getInstance().getGameObjects())
            {
                if(!object.equals(this) && !(object instanceof Terrain))
                {
                    //another object already reached the same destination
                    if(occupant == null && object.intersects(destination))
                        occupant = object;
                    
                    if(intersects(object))
                    {
                        if(object.equals(occupant) || object.collidedOccupant)
                            collidedOccupant = true;
                        
                        collisions.add(object);
                    }
                }
            }
        }
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
