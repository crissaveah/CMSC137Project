
package client;

import app.ui.ClientUI;
import game.GameEngine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import server.Server;

public final class GameClient implements Runnable
{
    private int timeout = 10;
    private boolean stopped = true;
    private boolean initialized;
    private InetAddress host;
    private InetAddress group;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private MulticastSocket socket;
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
        try 
        {
            socket.leaveGroup(group);
        } 
        catch(IOException ex) 
        {
            System.err.println("Failed to leave group.");
        }
        finally
        {
            socket.close();
            stopped = true;
            initialized = false;
        }
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
    
    public void send(byte[] response)
    {
        try 
        {
            sendPacket.setData(response);
            sendPacket.setLength(response.length);
            socket.send(sendPacket);
        } 
        catch(IOException ex) 
        {
            System.err.println("Failed to send data.");
        }
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
            group = InetAddress.getByName(Server.JOIN_GROUP);
        } 
        catch(UnknownHostException ex) 
        {
            ClientUI.writeError("Join group not recognized.");
            return false;
        }
        
        try 
        {
            socket = new MulticastSocket(Server.GAME_PORT);
            socket.joinGroup(group);
        }
        catch(IOException ex) 
        {
            ClientUI.writeError("Failed to initialize game client.");
            return false;
        }
        
        receivePacket = new DatagramPacket(new byte[Server.DATA_LENGTH], Server.DATA_LENGTH);
        sendPacket = new DatagramPacket(new byte[Server.DATA_LENGTH], Server.DATA_LENGTH, this.host, Server.GAME_PORT);
        
        initialized = true;
        
        return initialized;
    }
    
    @Override
    public void run()
    {
        stopped = false;
int i = 0;
        while(!stopped)
        {
            if(initialized)
            {
                try 
                {
                    send(("Request"+i).getBytes());
                    i++;
                    socket.receive(receivePacket);
                    
                    System.out.println(new String(receivePacket.getData(), 0, receivePacket.getLength()));
                    
                    receivePacket.setLength(Server.DATA_LENGTH);
                    
                    GameEngine.getInstance().setGameStateDirty(true);
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
