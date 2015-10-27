
package server;

import app.ui.ServerConsole;
import client.Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public final class ChatService extends Thread
{
    private int gamePort;
    private int clientID;
    private boolean initialized;
    private Socket socket;
    private InetAddress clientAddress;
    private PrintWriter writer;
    private BufferedReader reader;
    
    protected ChatService(ServerSocket serverSocket) throws IOException
    {
        socket = serverSocket.accept();
        clientAddress = socket.getInetAddress();
        
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        clientID = ConnectionManager.clientIDs++;
        gamePort = ConnectionManager.gamePorts++;
        
        initialized = true;
    }
    
    protected void disconnect()
    {
        try 
        {
            writer.println(Client.STOP);
            broadcast(Client.DC+" "+clientID+" Player "+clientAddress.getHostName()+" has disconnected.");
            
            writer.close();
            reader.close();
            socket.close();
        }
        catch(IOException ex) 
        {
            ServerConsole.writeError("Failed to close connection for client "+clientAddress.getHostName()+".");
        }
    }
    
    protected void sendMessage(String response)
    {
        if(initialized)
            writer.println(response);
    }
    
    protected InetAddress getClientAddress()
    {
        return clientAddress;
    }
    
    protected int getGamePort()
    {
        return gamePort;
    }
    
    protected int getClientID()
    {
        return clientID;
    }
    
    private void broadcast(String message)
    {
        for(ChatService service : ConnectionManager.getInstance().getChatServices())
            service.sendMessage(message);
    }
    
    @Override
    public void run()
    {
        try 
        {
            //server generated client id and game port
            sendMessage(Client.ID+" "+clientID);
            sendMessage(Client.PORT+" "+gamePort);
            
            while(Server.getInstance().isRunning())
            {
                String message = reader.readLine();
                
                if(message != null) //process messages from client
                {
                    if(message.equals(Client.STOP))
                    {
                        disconnect();
                        ConnectionManager.getInstance().removeChatService(this);
                        ServerConsole.writePrompt("Client "+clientAddress.getHostName()+" has disconnected.");
                        break;
                    }
                    else if(message.startsWith(Client.CHAT))
                    {
                        broadcast(message);
                    }
                }
            }
            
            writer.close();
            reader.close();
            socket.close();
            
            ConnectionManager.getInstance().removeChatService(this);
            
            initialized = false;
        } 
        catch(IOException ex) 
        {
            ServerConsole.writeError("Failed to receive message from client "+clientAddress.getHostName()+".");
        }
    }
}
