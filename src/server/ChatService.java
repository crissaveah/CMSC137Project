
package server;

import app.ui.ServerConsole;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public final class ChatService extends Thread
{
    private boolean initialized;
    private Socket socket;
    private String clientName;
    private PrintWriter writer;
    private BufferedReader reader;
    
    protected ChatService(ServerSocket serverSocket) throws IOException
    {
        socket = serverSocket.accept();
        clientName = socket.getInetAddress().getHostName();
        
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        initialized = true;
    }
    
    protected void disconnect()
    {
        try 
        {
            writer.println(ChatServer.STOP);
            
            writer.close();
            reader.close();
            socket.close();
        }
        catch(IOException ex) 
        {
            ServerConsole.writeError("Failed to close connection for client "+clientName+".");
        }
    }
    
    protected void sendMessage(String response)
    {
        if(initialized)
            writer.println(response);
    }
    
    protected String getClientName()
    {
        return clientName;
    }
    
    private void broadcast(String message)
    {
        for(ChatService service : ChatServer.getInstance().getServices())
            service.sendMessage(message);
    }
    
    @Override
    public void run()
    {
        try 
        {
            while(ChatServer.getInstance().isRunning()) 
            {
                String message = reader.readLine();
                
                if(message != null) //process messages from client
                {
                    if(message.equals(ChatServer.STOP))
                    {
                        disconnect();
                        ChatServer.getInstance().removeService(this);
                        broadcast("Player "+clientName+" has disconnected.");
                        ServerConsole.writePrompt("Client "+clientName+" has disconnected.");
                        break;
                    }
                    else if(message.startsWith(ChatServer.CHAT))
                    {
                        broadcast(message);
                    }
                }
            }
            
            writer.close();
            reader.close();
            socket.close();
            
            ChatServer.getInstance().removeService(this);
            
            initialized = false;
        } 
        catch(IOException ex) 
        {
            ServerConsole.writeError("Failed to receive message from client "+clientName+".");
        }
    }
}
