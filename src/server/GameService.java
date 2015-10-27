
package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public final class GameService extends Thread
{
    private boolean initialized;
    private byte[] dataBuffer;
    private DatagramSocket socket;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private InetAddress clientAddress;
    
    protected GameService(InetAddress address, int port) throws IOException
    {
        dataBuffer = new byte[Server.DATA_LENGTH];
        socket = new DatagramSocket(port);
        receivePacket = new DatagramPacket(dataBuffer, Server.DATA_LENGTH);
        sendPacket = new DatagramPacket(new byte[Server.DATA_LENGTH], Server.DATA_LENGTH, address, ConnectionManager.GAME_PORT);
        clientAddress = address;
        
        initialized = true;
    }
    
    private void send(String message, InetAddress address)
    {
        if(initialized)
        {
            try
            {
                byte[] data = message.getBytes("UTF-8");

                sendPacket.setData(data);
                sendPacket.setLength(data.length);
                sendPacket.setAddress(address);
                socket.send(sendPacket);
            }
            catch(IOException ex) 
            {
                System.err.println("Failed to send data.");
            }
        }
    }
    
    private void broadcast(String response)
    {
        for(GameService service : ConnectionManager.getInstance().getGameServices())
        {
            if(!service.equals(this))
            {
                send(response, service.clientAddress);
            }
        }
    }
    
    protected void disconnect()
    {
        socket.close();
    }
    
    @Override
    public void run()
    {
        while(Server.getInstance().isRunning())
        {
            try
            {
                Arrays.fill(dataBuffer, (byte) 0); //clear data buffer
                socket.receive(receivePacket);
                String data = new String(receivePacket.getData(), "UTF-8").trim();

                broadcast(data);
            }
            catch (IOException ex)
            {
            }
        }
        
        initialized = false;
    }
}
