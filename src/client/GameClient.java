
package client;

import app.ui.ClientUI;
import game.GameEngine;
import game.GameListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import server.ConnectionManager;
import server.Server;

public final class GameClient implements Runnable
{
    private int timeout = 10;
    private boolean stopped = true;
    private boolean initialized;
    private byte[] dataBuffer;
    private InetAddress host;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private DatagramSocket socket;
    private static GameClient instance = new GameClient();
    
    private GameClient(){}
    
    public static GameClient getInstance()
    {
        return instance;
    }
    
    public void start()
    {
        new Thread(instance).start();
    }
    
    public void stop()
    {
        socket.close();
        stopped = true;
        initialized = false;
    }
    
    public boolean isRunning()
    {
        return !stopped;
    }
    
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }
    
    public int getTimeout()
    {
        return timeout;
    }
    
    public void send(String response)
    {
        if(initialized)
        {
            try 
            {
                byte[] data = response.getBytes("UTF-8");
                
                sendPacket.setData(data, 0, data.length);
                socket.send(sendPacket);
            }
            catch(IOException ex) 
            {
                System.err.println("Failed to send data.");
            }
        }
    }
    
    public void playerDisconnected(int id)
    {
        GameEngine.getInstance().removeAllObjects(id);
    }
    
    public boolean connect(String host)
    {
        try 
        {
            this.host = InetAddress.getByName(host);
        }
        catch(UnknownHostException ex) 
        {
            ClientUI.writeError("Host not recognized");
            return false;
        }
        
        try 
        {
            socket = new DatagramSocket(ConnectionManager.GAME_PORT);
        }
        catch(IOException ex) 
        {
            ClientUI.writeError("Failed to initialize game client.");
            return false;
        }
        
        dataBuffer = new byte[Server.DATA_LENGTH];
        receivePacket = new DatagramPacket(dataBuffer, Server.DATA_LENGTH);
        sendPacket = new DatagramPacket(new byte[Server.DATA_LENGTH], Server.DATA_LENGTH);
        
        sendPacket.setAddress(this.host);
        
        initialized = true;
        
        return initialized;
    }
    
    @Override
    public void run()
    {
        while(ChatClient.getInstance().getGamePort() == -1) //block until game port is established
        {
            try 
            {
                Thread.sleep(timeout);
            }
            catch(InterruptedException ex) 
            {
            }
        }
        
        sendPacket.setPort(ChatClient.getInstance().getGamePort());
        
        stopped = false;
        
        while(!stopped)
        {
            if(initialized)
            {
                try 
                {
                    Arrays.fill(dataBuffer, (byte) 0); //clear data buffer
                    socket.receive(receivePacket);
                    
                    String data = new String(receivePacket.getData(), "UTF-8").trim();

                    for(GameListener listener : GameEngine.getInstance().getGameListeners())
                        listener.update(data);
                    
                    receivePacket.setLength(Server.DATA_LENGTH);
                }
                catch(IOException ex)
                {
                }
            }
            
            try 
            {
                Thread.sleep(timeout);
            }
            catch(InterruptedException ex) 
            {
            }
        }
        
        initialized = false;
    }
}
