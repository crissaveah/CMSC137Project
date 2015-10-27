
package game;

import app.logic.GameLogic;
import app.ui.ClientUI;
import client.ChatClient;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.concurrent.CopyOnWriteArrayList;

public final class GameEngine implements Runnable, MouseListener, MouseMotionListener, KeyListener
{
    private static final int SCROLL_SCALE = 1000;
    
    private int fps;
    private int frames;
    private int xView;
    private int yView;
    private int xMouse;
    private int yMouse;
    private int xPress;
    private int yPress;
    private int worldWidth;
    private int worldHeight;
    private int halfWidth;
    private int halfHeight;
    private float scrollSpeed;
    private float delta;
    private long prevFPSTime;
    private long currentTime;
    private long previousTime;
    private boolean stopped;
    private boolean dragged;
    private boolean dragging;
    private Rectangle dragArea;
    private Graphics2D graphics;
    private AffineTransform transform;
    private CopyOnWriteArrayList<GameUI> uis;
    private CopyOnWriteArrayList<GameObject> temp;
    private CopyOnWriteArrayList<GameObject> objects;
    private CopyOnWriteArrayList<GameListener> listeners;
    private BufferedImage framebuffer;
    
    private static GameEngine instance = new GameEngine();
    
    private GameEngine()
    {
        xView = 0;
        yView = 0;
        scrollSpeed = 0.75f;
        worldWidth = 2048;
        worldHeight = 2048;
        dragArea = new Rectangle();
        transform = new AffineTransform();
        halfWidth = ClientUI.getWidth()/2;
        halfHeight = ClientUI.getHeight()/2;
        temp = new CopyOnWriteArrayList<>();
        uis = new CopyOnWriteArrayList<>();
        objects = new CopyOnWriteArrayList<>();
        listeners = new CopyOnWriteArrayList<>();
    }
    
    public static GameEngine getInstance()
    {
        return instance;
    }
    
    public void start()
    {
        new Thread(instance).start();
    }
    
    public void setScrollSpeed(float speed)
    {
        scrollSpeed = speed;
    }
    
    public float getScrollSpeed()
    {
        return scrollSpeed;
    }
    
    public void setWorldSize(int width, int height)
    {
        worldWidth = width;
        worldHeight = height;
    }
    
    public int getWorldWidth()
    {
        return worldWidth;
    }
    
    public int getWorldHeight()
    {
        return worldHeight;
    }
    
    public void setViewport(int x, int y)
    {
        xView = x;
        yView = y;
    }
    
    public void stop()
    {
        stopped = true;
    }
    
    public boolean isRunning()
    {
        return !stopped;
    }
    
    public BufferedImage getFramebuffer()
    {
        return framebuffer;
    }
    
    public void addGameObject(GameObject object)
    {
        objects.add(object);
        listeners.add(object);
    }
    
    public void removeGameObject(GameObject object)
    {
        objects.remove(object);
        listeners.remove(object);
    }
    
    public GameObject getGameObject(int index)
    {
        return objects.get(index);
    }
    
    public GameObject getGameObject(int owner, int id)
    {
        for(GameObject object : objects)
            if(object.owner == owner && object.id == id)
                return object;
        
        return null;
    }
    
    public CopyOnWriteArrayList<GameObject> getGameObjects()
    {
        return objects;
    }
    
    public void addGameListener(GameListener listener)
    {
        listeners.add(listener);
    }
    
    public GameListener getGameListener(int index)
    {
        return listeners.get(index);
    }
    
    public CopyOnWriteArrayList<GameListener> getGameListeners()
    {
        return listeners;
    } 
    
    public boolean contains(int owner, int id)
    {
        for(GameObject object : objects)
            if(object.getOwner() == owner && object.getID() == id)
                return true;
        
        return false;
    }
    
    public void removeAllObjects(int owner)
    {
        temp.clear();
        
        for(GameObject object : objects)
            if(object.getOwner() == owner)
                temp.add(object);
        
        objects.removeAll(temp);
    }
    
    public void addUI(GameUI ui)
    {
        uis.add(ui);
        listeners.add(ui);
    }
    
    public void removeUI(GameUI ui)
    {
        uis.remove(ui);
        listeners.remove(ui);
    }
    
    public GameUI getUI(int index)
    {
        return uis.get(index);
    }
    
    public int getFPS()
    {
        return fps;
    }
    
    public float getDelta()
    {
        return delta;
    }
    
    public int getWorldX(int x)
    {
        return x - halfWidth + xView;
    }
    
    public int getWorldY(int y)
    {
        return halfHeight + yView - y;
    }
    
    public int getScreenX(int x)
    {
        return x + halfWidth - xView;
    }
    
    public int getScreenY(int y)
    {
        return halfHeight - y + yView;
    }
    
    public Rectangle getDragArea()
    {
        return dragArea;
    }
    
    public boolean consumeDrag()
    {
        boolean drag = this.dragged;
        dragged = false;
        return drag;
    }
    
    private void calculateDelta()
    {
        delta = (currentTime - previousTime) / 1000000000f;    
    }
    
    private void calculateFPS()
    {
        if(currentTime - prevFPSTime > 1000000000) //one second has passed, output accumulated frames
        {
            fps = frames;
            frames = 0;
            prevFPSTime = currentTime;
        }

        frames++;
    }
    
    private void checkScroll()
    {
        double scaledDelta = delta * SCROLL_SCALE;
        double scrollAmount = Math.floor(scrollSpeed * scaledDelta);
        
        if(xMouse == 0 && xView > (ClientUI.getWidth() - worldWidth)/2 + scaledDelta)
            xView -= scrollAmount;
        else if(xMouse == ClientUI.getWidth()-1 && xView < (worldWidth - ClientUI.getWidth())/2 - scaledDelta)
            xView += scrollAmount;

        if(yMouse == 0 && yView < (worldHeight - ClientUI.getHeight())/2 - scaledDelta)
            yView += scrollAmount;
        else if(yMouse == ClientUI.getHeight()-1 && yView > (ClientUI.getHeight() - worldHeight)/2 + scaledDelta)
            yView -= scrollAmount;
    }
    
    private AffineTransform applyTransform(GameObject object)
    {   
        int radius = (int) object.getMaxExtent() * 2 + 6;
        double objHalfWidth = object.getSprite().getWidth()/2;
        double objHalfHeight = object.getSprite().getHeight()/2;
        double worldX = object.location.getX();
        double worldY = object.location.getY();
        double screenX = (worldX - objHalfWidth) + halfWidth - xView;
        double screenY = halfHeight - (worldY + objHalfHeight) + yView;

        transform.setToIdentity();
        transform.translate(screenX, screenY);
        transform.rotate(object.rotation, objHalfWidth, objHalfHeight);
        
        if(object.hitPoints > 0)
        {
            if(object.isSelected())
            {
                graphics.setColor(Color.WHITE);
                graphics.drawOval((int) screenX - 3, (int) screenY - 3, radius, radius);
            }
            else if(object.owner != ChatClient.getInstance().getID() && !(object instanceof Projectile))
            {
                graphics.setColor(Color.RED);
                graphics.drawOval((int) screenX - 3, (int) screenY - 3, radius, radius);
            }
        }
        
        return transform;
    }
    
    private void drawUtilities()
    {
        graphics.setColor(Color.WHITE);
        graphics.drawString("FPS: "+Integer.toString(fps), ClientUI.getWidth()-58, 20); //print fps
        graphics.drawString("Reserved Units: "+GameLogic.numChars, ClientUI.getWidth()-120, 40); //print available units left
        graphics.drawString("Remaining Units: "+GameLogic.numLives, ClientUI.getWidth()-128, 60); //print available units left

        if(dragging) //draw drag box
        {
            int x; 
            int y;
            int width;
            int height;

            if(xMouse >= xPress)
            {
                x = xPress;
                width = xMouse-xPress;
            }
            else
            {
                x = xMouse;
                width = xPress-xMouse;
            }

            if(yMouse >= yPress)
            {
                y = yPress;
                height = yMouse-yPress;
            }
            else
            {
                y = yMouse;
                height = yPress-yMouse;
            }

            graphics.drawRect(x, y, width, height);
            
            dragArea.setLocation(x, y);
            dragArea.setSize(width, height);
        }
    }
    
    private void drawUIs()
    {
        for(GameUI ui : uis)
        {
            if(ui.hasIcon()) //draw icon
            {
                transform.setToIdentity();
                transform.translate(ui.x, ui.y);
                graphics.drawImage(ui.getIcon(ui.getActiveIcon()), transform, null);
            }
            else
            {
                graphics.setColor(ui.color);
                graphics.fillRoundRect(ui.x, ui.y, ui.width, ui.height, ui.cornerRadius, ui.cornerRadius);
            }
        }
    }

    @Override
    public void run()
    {
        ClientUI.getDrawingSurface().addKeyListener(instance);
        ClientUI.getDrawingSurface().addMouseListener(instance);
        ClientUI.getDrawingSurface().addMouseMotionListener(instance);
        
        stopped = false;
        previousTime = System.nanoTime();
        
        framebuffer = new BufferedImage(ClientUI.getWidth(), ClientUI.getHeight(), BufferedImage.TYPE_INT_ARGB);
        graphics = framebuffer.createGraphics();
        
        while(!stopped)
        {
            checkScroll(); //update viewport transformation
            
            for(GameObject object : objects) //process transformation then render object to offscreen framebuffer
                if(object.isVisible())
                    graphics.drawImage(object.getSprite(), applyTransform(object), null);
            
            drawUIs();
            drawUtilities();
            
            ClientUI.getDrawingSurface().repaint(); //update onscreen framebuffer
            
            currentTime = System.nanoTime();
            
            calculateFPS();
            calculateDelta();
            
            previousTime = currentTime;
        }
        
        for(GameObject object : objects) //reset active state
            object.setActive(false);
        
        //clean up
        objects.clear();
        listeners.clear();
        
        graphics.clearRect(0, 0, ClientUI.getWidth(), ClientUI.getHeight());
        graphics.dispose();
    }

    @Override
    public void mouseClicked(MouseEvent e) 
    {
        for(GameListener listener : listeners)
            listener.processMouseEvent(e);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        xPress = e.getX();
        yPress = e.getY();
        dragged = false;
        
        for(GameListener listener : listeners)
            listener.processMouseEvent(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) 
    {
        dragging = false;
        
        for(GameListener listener : listeners)
            listener.processMouseEvent(e);
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        for(GameListener listener : listeners)
            listener.processMouseEvent(e);
    }

    @Override
    public void mouseExited(MouseEvent e) 
    {
        for(GameListener listener : listeners)
            listener.processMouseEvent(e);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) 
    {
        xMouse = e.getX();
        yMouse = e.getY();
        dragging = true;
        dragged = true;
        
        for(GameListener listener : listeners)
            listener.processMouseEvent(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) 
    {
        xMouse = e.getX();
        yMouse = e.getY();
        
        for(GameListener listener : listeners)
            listener.processMouseEvent(e);
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        for(GameListener listener : listeners)
            listener.processKeyEvent(e);
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        for(GameListener listener : listeners)
            listener.processKeyEvent(e);
    }

    @Override
    public void keyReleased(KeyEvent e) 
    {
        for(GameListener listener : listeners)
            listener.processKeyEvent(e);
    }
}
