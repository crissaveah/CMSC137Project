
package game;

import app.ui.ClientUI;
import java.awt.Graphics2D;
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
    private int fps;
    private int frames;
    private int xView;
    private int yView;
    private int xMouse;
    private int yMouse;
    private int worldWidth;
    private int worldHeight;
    private int halfWidth;
    private int halfHeight;
    private int scrollSpeed;
    private float delta;
    private long prevFPSTime;
    private long currentTime;
    private long previousTime;
    private boolean stopped;
    private boolean gameStateDirty;
    private AffineTransform transform;
    private CopyOnWriteArrayList<GameObject> objects;
    private CopyOnWriteArrayList<GameListener> listeners;
    private BufferedImage framebuffer;
    
    private static GameEngine instance = new GameEngine();
    
    private GameEngine()
    {
        xView = 0;
        yView = 0;
        scrollSpeed = 1;
        worldWidth = 2048;
        worldHeight = 2048;
        transform = new AffineTransform();
        halfWidth = ClientUI.getWidth()/2;
        halfHeight = ClientUI.getHeight()/2;
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
    
    public void setScrollSpeed(int speed)
    {
        scrollSpeed = speed;
    }
    
    public int getScrollSpeed()
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
    
    public GameObject getGameObject(int index)
    {
        return objects.get(index);
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
    
    public void setGameStateDirty(boolean dirty)
    {
        gameStateDirty = dirty;
    }
    
    public boolean isGameStateDirty()
    {
        return gameStateDirty;
    }
    
    private void calculateDelta()
    {
        delta = (currentTime - previousTime)/1000000f;    
    }
    
    private void calculateFPS()
    {
        if(currentTime - prevFPSTime > 1000000000)
        {
            fps = frames;
            frames = 0;
            prevFPSTime = currentTime;
        }

        frames++;
    }
    
    private void checkScroll()
    {
        double scrollAmount = Math.floor(scrollSpeed * delta);
        
        if(xMouse == 0 && xView > (ClientUI.getWidth() - worldWidth)/2 + delta)
            xView -= scrollAmount;
        else if(xMouse == ClientUI.getWidth()-1 && xView < (worldWidth - ClientUI.getWidth())/2 - delta)
            xView += scrollAmount;

        if(yMouse == 0 && yView < (worldHeight - ClientUI.getHeight())/2 - delta)
            yView += scrollAmount;
        else if(yMouse == ClientUI.getHeight()-1 && yView > (ClientUI.getHeight() - worldHeight)/2 + delta)
            yView -= scrollAmount;
    }
    
    private AffineTransform applyTransform(GameObject object)
    {   
        double objHalfWidth = object.getSprite().getWidth()/2;
        double objHalfHeight = object.getSprite().getHeight()/2;
        double worldX = object.location.getX();
        double worldY = object.location.getY();
        double screenX = (worldX - objHalfWidth) + halfWidth - xView;
        double screenY = halfHeight - (worldY + objHalfHeight) + yView;

        transform.setToIdentity();
        transform.translate(screenX, screenY);
        transform.rotate(object.rotation, objHalfWidth, objHalfHeight);
        
        return transform;
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
        
        Graphics2D graphics = framebuffer.createGraphics();
        graphics.clipRect(0, 0, ClientUI.getWidth(), ClientUI.getHeight());
        
        while(!stopped)
        {
            checkScroll(); //update viewport transformation
            
            for(GameListener listener : listeners)
            {
                if(gameStateDirty) //update game state
                {
                    listener.update();
                    gameStateDirty = false;
                }
            }
            
            for(GameObject object : objects) //process collision detection and transformation
            {
                object.checkCollision();
                graphics.drawImage(object.getSprite(), applyTransform(object), null);
            }
            
            ClientUI.getDrawingSurface().repaint(); //update graphics
            
            currentTime = System.nanoTime();
            
            calculateFPS();
            calculateDelta();
            
            previousTime = currentTime;
        }
        
        for(GameObject object : objects) //reset object active state
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
        for(GameListener listener : listeners)
            listener.processMouseEvent(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) 
    {
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
