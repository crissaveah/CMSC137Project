
package game;

import app.ui.ClientUI;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;
    
public abstract class GameUI extends Rectangle implements GameListener
{
    protected int activeIcon;
    protected int cornerRadius;
    protected boolean inside;
    protected Color color;
    protected CopyOnWriteArrayList<BufferedImage> icons;
    protected CopyOnWriteArrayList<GameUIListener> listeners;
    
    public GameUI()
    {
        cornerRadius = 0;
        color = Color.WHITE;
        icons = new CopyOnWriteArrayList<>();
        listeners = new CopyOnWriteArrayList<>();
        
        setSize(64, 32);
        setLocation(0, 0);
    }
    
    public void addIcon(File file)
    {
        try 
        {
            BufferedImage sprite = ImageIO.read(file);
            icons.add(sprite);
        }
        catch(IOException ex) 
        {
            ClientUI.writeError("Failed to load sprite: "+file.getPath());
        }
    }
    
    public void addGameUIListener(GameUIListener listener)
    {
        listeners.add(listener);
    }
    
    public void removeGameUIListener(GameUIListener listener)
    {
        listeners.remove(listener);
    }
    
    public void setColor(Color color)
    {
        this.color = color;
    }
    
    public Color getColor()
    {
        return color;
    }
    
    public void setCornerRadius(int radius)
    {
        cornerRadius = radius;
    }
    
    public int getCornerRadius()
    {
        return cornerRadius;
    }
    
    public void setActiveIcon(int index)
    {
        activeIcon = index;
    }
    
    public int getActiveIcon()
    {
        return activeIcon;
    }
    
    public BufferedImage getIcon(int index)
    {
        return icons.get(index);
    }
    
    public boolean hasIcon()
    {
        return !icons.isEmpty();
    }
    
    public boolean mouseOver()
    {
        return inside;
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
        switch(e.getID())
        {
            case MouseEvent.MOUSE_PRESSED: 
            {
                if(contains(e.getPoint()))
                {
                    for(GameUIListener listener : listeners)
                        listener.pressed(this);
                        
                    pressed();
                }
                
                break;
            }
            case MouseEvent.MOUSE_RELEASED:
            {
                if(contains(e.getPoint()))
                {
                    for(GameUIListener listener : listeners)
                        listener.released(this);
                    
                    released();
                }
                
                break;
            }
            case MouseEvent.MOUSE_MOVED:
            {
                if(!inside && contains(e.getPoint()))
                {
                    for(GameUIListener listener : listeners)
                        listener.entered(this);
                    
                    entered();
                    inside = true;
                }
                else if(inside && !contains(e.getPoint()))
                {
                    for(GameUIListener listener : listeners)
                        listener.exited(this);
                    
                    exited();
                    inside = false;
                }
                
                break;
            }
        }
    }
    
    public abstract void pressed();
    public abstract void released();
    public abstract void entered();
    public abstract void exited();
}
