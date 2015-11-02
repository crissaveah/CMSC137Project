
package app.logic;

import app.ClientMain;
import app.character.Brutebug;
import app.character.Quickbug;
import app.ui.ClientUI;
import client.ChatClient;
import game.Button;
import game.Character;
import game.GameEngine;
import game.GameListener;
import game.GameObject;
import game.GameUI;
import game.GameUIListener;
import game.Terrain;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

public final class GameLogic implements GameListener, GameUIListener
{   
    public static final int MAX_CHARS = 20;
    
    public static final int BRUTEBUG = 0;
    public static final int QUICKBUG = 1;
    
    public static int charID = 0;
    public static int numChars = MAX_CHARS;
    public static int numLives = MAX_CHARS;
    
    private Terrain terrain;
    private Button quickbugButton;
    private Button brutebugButton;
    
    public GameLogic()
    {
        terrain = new Terrain();
        quickbugButton = new Button();
        brutebugButton = new Button();
        
        terrain.addSprite(ClientMain.getFile("sprite/terrain.png"), 0);
        terrain.setCurrentSprite(0);
        
        quickbugButton.addIcon(ClientMain.getFile("sprite/quickbug_button.png"));
        quickbugButton.addIcon(ClientMain.getFile("sprite/quickbug_button_hover.png"));
        quickbugButton.addIcon(ClientMain.getFile("sprite/quickbug_button_press.png"));
        quickbugButton.setLocation(ClientUI.getWidth()-60, 80);
        quickbugButton.setDefaultIcon(0);
        quickbugButton.setHoverIcon(1);
        quickbugButton.setPressIcon(2);
        quickbugButton.addGameUIListener(this);
        
        brutebugButton.addIcon(ClientMain.getFile("sprite/brutebug_button.png"));
        brutebugButton.addIcon(ClientMain.getFile("sprite/brutebug_button_hover.png"));
        brutebugButton.addIcon(ClientMain.getFile("sprite/brutebug_button_press.png"));
        brutebugButton.setLocation(ClientUI.getWidth()-60, 130);
        brutebugButton.setDefaultIcon(0);
        brutebugButton.setHoverIcon(1);
        brutebugButton.setPressIcon(2);
        brutebugButton.addGameUIListener(this);
        
        GameEngine.getInstance().addUI(quickbugButton);
        GameEngine.getInstance().addUI(brutebugButton);
        
        GameEngine.getInstance().addGameObject(terrain);
        GameEngine.getInstance().setWorldSize(terrain.getSprite().getWidth(), terrain.getSprite().getHeight());
        GameEngine.getInstance().addGameListener(this);
    }
    
    public static Character createCharacter(int type, int id, int x, int y)
    {
        Character character = null;
        
        switch(type)
        {
            case BRUTEBUG:
            {
                character = new Brutebug();
                character.addSprite(ClientMain.getFile("sprite/brutebug.png"), 0);
                character.addSprite(ClientMain.getFile("sprite/splat.png"), 0);
                character.setCurrentSprite(0);
                break;
            }
            case QUICKBUG:
            {
                character = new Quickbug();
                character.addSprite(ClientMain.getFile("sprite/quickbug.png"), 0);
                character.addSprite(ClientMain.getFile("sprite/splat.png"), 0);
                character.setCurrentSprite(0);
                break;
            }
        }
        
        if(character != null)
        {
            character.setID(id);
            character.setBounds(character.getSprite().getWidth()/2, character.getSprite().getHeight()/2);
            character.setTranslation(x, y);
        }
        
        return character;
    }
    
    public boolean checkPick(MouseEvent e, int x, int y)
    {
        boolean picked = false;
        
        for(GameObject object : GameEngine.getInstance().getGameObjects())
        {
            if(object instanceof Character && object.getOwner() == ChatClient.getInstance().getID())
            {
                if(object.intersects(x, y))
                {
                    picked = true;
                    object.setSelected(true);
                }
                else if(!e.isControlDown())
                    object.setSelected(false);
            }
        }
        
        return picked;
    }
    
    public boolean checkDragPick(MouseEvent e, Rectangle dragArea)
    {
        boolean picked = false;
        
        for(GameObject object : GameEngine.getInstance().getGameObjects())
        {
            if(object instanceof Character && object.getOwner() == ChatClient.getInstance().getID())
            {
                if(object.intersects(dragArea))
                {
                    object.setSelected(true);
                    picked = true;
                }
                else if(!e.isControlDown())
                    object.setSelected(false);
            }
        }
        
        return picked;
    }
    
    @Override
    public void update(String data) 
    {
        String[] split = data.split(" ");
        int id = Integer.parseInt(split[0]);
        int type = Integer.parseInt(split[1]);
        int owner = Integer.parseInt(split[2]);
        int x = Integer.parseInt(split[3]);
        int y = Integer.parseInt(split[4]);
        float rot = Float.parseFloat(split[5]);
        int hp = Integer.parseInt(split[6]);
        int as = Integer.parseInt(split[7]);
        int ad = Integer.parseInt(split[8]);
        int targetID = Integer.parseInt(split[9]);
        int targetOwner = Integer.parseInt(split[10]);
        int targetHP = Integer.parseInt(split[11]);
        
        if(owner != ChatClient.getInstance().getID() && owner != -1)
        {
            if(!GameEngine.getInstance().contains(owner, id))
            {
                Character character = createCharacter(type, id, x, y);
                
                character.setOwner(owner);
                character.setRotation(rot);
                character.setHitPoints(hp);
                character.setAttackSpeed(as);
                character.setAttackDamage(ad);
                character.setActive(true);
                
                GameEngine.getInstance().addGameObject(character);
            }
            else
            {
                Character character = (Character) GameEngine.getInstance().getGameObject(owner, id);
                
                if(character.getTranslationX() != x && character.getTranslationY() != y)
                    character.move(x, y);
                
                if(character.getRotation() != rot)
                    character.setRotation(rot);
                
                if(character.getHitPoints() != hp)
                    character.setHitPoints(hp);
                
                if(character.getAttackSpeed() != as)
                    character.setAttackSpeed(as);
                
                if(character.getAttackDamage() != ad)
                    character.setAttackDamage(ad);
                
                if(targetID == -1)
                    character.setTarget(null);
                else
                {
                    GameObject target = GameEngine.getInstance().getGameObject(targetOwner, targetID);
                    
                    if(target != null)
                    {
                        character.setTarget(target);
                        target.setHitPoints(targetHP);
                    }
                }
            }
        }
    }

    @Override
    public void processKeyEvent(final KeyEvent e) 
    {
        
    }

    @Override
    public void processMouseEvent(final MouseEvent e) 
    {
        SwingUtilities.invokeLater
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    int x = GameEngine.getInstance().getWorldX(e.getX());
                    int y = GameEngine.getInstance().getWorldY(e.getY());
                    
                    if(e.getID() == MouseEvent.MOUSE_CLICKED)
                    {
                        if(e.getButton() == MouseEvent.BUTTON1) //left mouse button
                        {
                            if(!checkPick(e, x, y)) //create unit
                            {
                                if(numChars > 0)
                                {
                                    if(brutebugButton.isToggleOn() && !brutebugButton.mouseOver())
                                    {
                                        Brutebug character = (Brutebug) createCharacter(BRUTEBUG, charID++, x, y);

                                        character.setOwner(ChatClient.getInstance().getID());
                                        character.setActive(true);
                                        character.setSelected(true);
                                        
                                        GameEngine.getInstance().addGameObject(character);
                                        
                                        numChars--;
                                    }
                                    else if(quickbugButton.isToggleOn() && !quickbugButton.mouseOver())
                                    {
                                        Quickbug character = (Quickbug) createCharacter(QUICKBUG, charID++, x, y);

                                        character.setOwner(ChatClient.getInstance().getID());
                                        character.setActive(true);
                                        character.setSelected(true);

                                        GameEngine.getInstance().addGameObject(character);
                                        
                                        numChars--;
                                    }
                                }
                            }
                        }
                    }
                    else if(e.getID() == MouseEvent.MOUSE_RELEASED)
                    {
                        if(e.getButton() == MouseEvent.BUTTON1)
                        {
                            if(GameEngine.getInstance().consumeDrag()) //process drag picking
                            {
                                checkDragPick(e, GameEngine.getInstance().getDragArea());
                            }
                        }
                    }
                }
            }
        );
    }

    @Override
    public void pressed(GameUI ui)
    {
        if(ui.equals(brutebugButton))
            quickbugButton.setToggle(false);
        else if(ui.equals(quickbugButton))
            brutebugButton.setToggle(false);
    }

    @Override
    public void released(GameUI ui) 
    {
    }

    @Override
    public void entered(GameUI ui) 
    {
    }

    @Override
    public void exited(GameUI ui) 
    {
    }
}
