
package server;

import app.ui.ServerConsole;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ConnectionManager implements Runnable
{
    public static final int CHAT_PORT = 2853;
    public static final int GAME_PORT = 2854;
    
    protected static int clientIDs = 0;
    protected static int gamePorts = 2855;
    
    private boolean stopped = true;
    private ServerSocket socket;
    private CopyOnWriteArrayList<ChatService> chatServices = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<GameService> gameServices = new CopyOnWriteArrayList<>();
    private static ConnectionManager instance;
    
    private ConnectionManager(){}
    
    public static ConnectionManager getInstance()
    {
        return instance;
    }
    
    protected static ConnectionManager create()
    {
        return instance = new ConnectionManager();
    }
    
    public boolean isRunning()
    {
        return !stopped;
    }

    protected void stop()
    {
        try 
        {
            for(ChatService service : chatServices)
                service.disconnect();
            
            for(GameService service : gameServices)
                service.disconnect();
            
            socket.close();
            chatServices.clear();
            stopped = true;
        } 
        catch(IOException ex) 
        {
            ServerConsole.writeError("Failed to stop chat server.");
        }
    }
    
    protected void removeChatService(ChatService service)
    {
        chatServices.remove(service);
    }
    
    protected CopyOnWriteArrayList<ChatService> getChatServices()
    {
        return chatServices;
    }
    
    protected void removeGameService(GameService service)
    {
        gameServices.remove(service);
    }
    
    protected CopyOnWriteArrayList<GameService> getGameServices()
    {
        return gameServices;
    }

    @Override
    public void run() 
    {
        try 
        {
            socket = new ServerSocket(CHAT_PORT);
            
            ServerConsole.writePrompt("Server initialized. Listening for connections at port "+CHAT_PORT+".");
            
            stopped = false;
            
            while(!stopped) //begin accepting connections
            {
                if(chatServices.size() < Server.MAX_CLIENTS) 
                {
                    try
                    {
                        ChatService chatService = new ChatService(socket); //blocks until a client connects
                        GameService gameService = new GameService(chatService.getClientAddress(), chatService.getGamePort());
                        
                        chatService.start();
                        gameService.start();
                        
                        chatServices.add(chatService);
                        gameServices.add(gameService);

                        ServerConsole.writePrompt("Connection accepted from "+chatService.getClientAddress().getHostName()+".");
                    }
                    catch(IOException ex) 
                    {
                        ServerConsole.writeError("Failed to accept a connection.");
                    }
                }
                else
                    ServerConsole.writeError("Maximum number of connections reached.");
            }
        }
        catch(IOException ex) 
        {
            ServerConsole.writeError("Failed to initialize server.");
        }
    }
}
