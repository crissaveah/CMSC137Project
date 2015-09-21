
package client;

import app.ui.ClientUI;
import game.GameEngine;

public final class Client
{
    public static final String STOP = "0";
    public static final String CHAT = "1";
    
    protected static final String CMD_EXIT = "exit";
    protected static final String CMD_STOP = "stop";
    protected static final String CMD_CONNECT = "connect";
    
    private long clientID;
    private static boolean stopped = true;
    
    private Client(){}
    
    public void setID(long id)
    {
        clientID = id;
    }
    
    public long getID()
    {
        return clientID;
    }
    
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
            case CMD_STOP:
            {
                stop();
                break;
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
                    GameClient.getInstance().start();
                    ChatClient.getInstance().start();
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
