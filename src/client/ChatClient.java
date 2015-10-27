
package client;

import app.ui.ClientUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import server.ConnectionManager;

public final class ChatClient implements Runnable
{
    private int id = -1;
    private int port = -1;
    private long timeout = 10;
    private boolean stopped;
    private String name;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private static ChatClient instance = new ChatClient();
    
    private ChatClient(){}
    
    public static ChatClient getInstance()
    {
        return instance;
    }
    
    public void start()
    {
        new Thread(instance).start();
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setID(int id)
    {
        this.id = id;
    }
    
    public int getID()
    {
        return id;
    }
    
    public int getGamePort()
    {
        return port;
    }
    
    public void stop()
    {
        sendMessage(Client.STOP);
        stopped = true;
    }
    
    public boolean isRunning()
    {
        return !stopped;
    }
    
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }
    
    public long getTimeout()
    {
        return timeout;
    }
    
    public boolean isConnected()
    {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
    
    public boolean connect(String host)
    {
        try 
        {
            socket = new Socket(host, ConnectionManager.CHAT_PORT);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            name = InetAddress.getLocalHost().getHostName();

            ClientUI.writePrompt("Connection successful.");
            ClientUI.setConsoleVisible(false);
            
            return true;
        } 
        catch(IOException ex) 
        {
            ClientUI.writeError("Failed to establish connection with server "+host+".");
        }
        
        return false;
    }
    
    public void sendMessage(String message)
    {
        if(isConnected() && writer != null)
            writer.println(message);
        else
            ClientUI.writeError("Disconnected from server.");
    }

    @Override
    public void run() 
    {
        stopped = false;
        
        while(!stopped)
        {
            try 
            {
                if(isConnected() && reader != null)
                {
                    String message = reader.readLine();

                    if(message != null)
                    {
                        if(message.startsWith(Client.CHAT))
                            ClientUI.writeChat(message.substring(2));
                        else if(message.startsWith(Client.ID))
                            id = Integer.valueOf(message.substring(2));
                        else if(message.startsWith(Client.PORT))
                            port = Integer.valueOf(message.substring(2));
                        else if(message.startsWith(Client.DC))
                        {
                            String[] info = message.split(" ");
                            GameClient.getInstance().playerDisconnected(Integer.parseInt(info[1]));
                            ClientUI.writeError(info[2]);
                        }
                        else if(message.equals(Client.STOP))
                        {
                            ClientUI.writeError("Disconnected from server.");
                            Client.stop();
                        }
                    }
                }
            } 
            catch(IOException ex) 
            {
            }
            
            try 
            {
                Thread.sleep(timeout);
            } 
            catch (InterruptedException ex) 
            {
                //ignore interrupt
            }
        }
        
        if(isConnected())
        {
            try 
            {
                socket.close();
                reader.close();
            } 
            catch (IOException ex) 
            {
                System.err.println("Failed to close connection.");
            }

            writer.close();
        }
    }
}
