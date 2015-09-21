
package server;

import app.ui.ServerConsole;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public final class ChatServer implements Runnable
{
    protected static final String STOP = "0";
    protected static final String CHAT = "1";
    
    private boolean stopped = true;
    private ServerSocket socket;
    private ArrayList<ChatService> services = new ArrayList<>(Server.MAX_CLIENTS);
    private static ChatServer instance;
    
    private ChatServer(){}
    
    public static ChatServer getInstance()
    {
        return instance;
    }
    
    protected static ChatServer create()
    {
        return instance = new ChatServer();
    }
    
    public boolean isRunning()
    {
        return !stopped;
    }

    protected void stop()
    {
        try 
        {
            for(ChatService service : services)
                service.disconnect();
            
            socket.close();
            services.clear();
            stopped = true;
        } 
        catch(IOException ex) 
        {
            ServerConsole.writeError("Failed to stop chat server.");
        }
    }
    
    protected void removeService(ChatService service)
    {
        services.remove(service);
    }
    
    protected ArrayList<ChatService> getServices()
    {
        return services;
    }

    @Override
    public void run() 
    {
        try 
        {
            socket = new ServerSocket(Server.CHAT_PORT);
            
            ServerConsole.writePrompt("Chat server initialized. Listening for connections at port "+Server.CHAT_PORT+".");
            
            stopped = false;
            
            while(!stopped)
            {
                if(services.size() < Server.MAX_CLIENTS) //begin accepting connections
                {
                    try
                    {
                        ChatService service = new ChatService(socket);

                        service.start();
                        services.add(service);

                        ServerConsole.writePrompt("Chat connection accepted from "+service.getClientName()+".");
                    }
                    catch(IOException ex) 
                    {
                        ServerConsole.writeError("Failed to accept a connection.");
                    }
                }
                else
                    ServerConsole.writeError("Maximum number of connections reached.");
            }
            
            ServerConsole.writePrompt("Chat server terminated.");
        }
        catch(IOException ex) 
        {
            ServerConsole.writeError("Failed to initialize chat server.");
        }
    }
}
