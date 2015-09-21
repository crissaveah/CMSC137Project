
package server;

import app.ui.ServerConsole;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public final class GameServer implements Runnable
{
    private int timeout = 10;
    private boolean stopped = true;
    private InetAddress group;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private MulticastSocket socket;
    private static GameServer instance;
    
    private GameServer(){}
    
    public static GameServer getInstance()
    {
        return instance;
    }
    
    protected static GameServer create()
    {
        return instance = new GameServer();
    }
    
    protected void stop()
    {
        socket.close();
        stopped = true;
    }
    
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }
    
    public int getTimeout()
    {
        return timeout;
    }
    
    private void send(byte[] response)
    {
        try 
        {
            sendPacket.setData(response);
            sendPacket.setLength(response.length);
            socket.send(sendPacket);
        } 
        catch (IOException ex) 
        {
            System.err.println("Failed to send data.");
        }
    }

    @Override
    public void run()
    {
        try 
        {
            socket = new MulticastSocket(Server.GAME_PORT);
            group = InetAddress.getByName(Server.JOIN_GROUP);
            receivePacket = new DatagramPacket(new byte[Server.DATA_LENGTH], Server.DATA_LENGTH);
            sendPacket = new DatagramPacket(new byte[Server.DATA_LENGTH], Server.DATA_LENGTH, group, Server.GAME_PORT);
            
            ServerConsole.writePrompt("Game server initialized.");
            
            stopped = false;

            while(!stopped)
            {
                socket.receive(receivePacket);
                receivePacket.setLength(Server.DATA_LENGTH);
                send(receivePacket.getData());

                try 
                {
                    Thread.sleep(timeout);
                }
                catch(InterruptedException ex) 
                {
                }
            }
        } 
        catch (IOException ex) 
        {
            ServerConsole.writePrompt("Game server terminated.");
        }
    }
}
