
package client;

import app.logic.GameLogic;
import app.ui.ClientUI;
import game.Character;
import game.GameEngine;

public final class Client
{
    public static final String STOP = "0";
    public static final String CHAT = "1";
    public static final String DC = "2";
    public static final String ID = "3";
    public static final String PORT = "4";
    
    protected static final String CMD_EXIT = "exit";
    protected static final String CMD_CONNECT = "connect";
    protected static final String CMD_CHEAT = "cheat";
    
    protected static final String CHEAT_CODE0 = "brutebugsalot";
    protected static final String CHEAT_CODE1 = "quickbugsalot";
    
    private static boolean stopped = true;
    
    private Client(){}
    
    public static void stop()
    {
        if(!stopped)
        {
            GameEngine.getInstance().stop();
            GameClient.getInstance().stop();
            ChatClient.getInstance().stop();
            
            ClientUI.writePrompt("Client stopped. Send /connect <server name or ip> to reconnect.");
            stopped = true;
        }
    }
    
    public static void processCommand(String command)
    {
        String[] comSplit = command.split(" ");
        
        switch(comSplit[0])
        {
            case CMD_EXIT:
            {
                stop();
                ClientUI.dispose();
                break;
            }
            case CMD_CHEAT:
            {
                if(stopped)
                {
                    ClientUI.writePrompt("Cannot execute cheat commands while off-game.");
                    break;
                }
                
                if(comSplit.length < 2)
                {
                    ClientUI.writeError("Invalid command. Cheat code required.");
                    break;
                }
                
                switch(comSplit[1])
                {
                    case CHEAT_CODE0:
                    {
                        for(int i = 0; i < 100; i++)
                        {
                            Character character = GameLogic.createCharacter(GameLogic.BRUTEBUG, GameLogic.charID++, 0, 0);
                                
                            character.setOwner(ChatClient.getInstance().getID());
                            character.setActive(true);
                            GameEngine.getInstance().addGameObject(character);
                        }
                        
                        break;
                    }
                    default:
                        ClientUI.writeError("Unknown cheat code.");
                }
            }
            case CMD_CONNECT:
            {
                if(!stopped)
                {
                    ClientUI.writePrompt("Client is already connected to server.");
                    break;
                }
                
                if(comSplit.length < 2)
                {
                    ClientUI.writeError("Invalid command. Host name or ip required.");
                    break;
                }
                    
                if(ChatClient.getInstance().connect(comSplit[1]) && GameClient.getInstance().connect(comSplit[1]))
                {
                    ChatClient.getInstance().start();
                    GameClient.getInstance().start();
                    GameEngine.getInstance().start();
                    stopped = false;
                }
                
                break;
            }
            default:
                ClientUI.writeError("Unknown command.");
        }
    }
}
