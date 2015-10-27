
package game;

import client.ChatClient;
import client.GameClient;

public class Worker extends Thread
{
    private long timeout;
    private GameObject handler;
    
    public Worker(GameObject handler)
    {
        this.handler = handler;
        timeout = 17;
        
        setDaemon(true);
    }
    
    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }
    
    @Override
    public void run() 
    {
        while(handler.active)
        {
            if(handler.owner == ChatClient.getInstance().getID())
                GameClient.getInstance().send(handler.getObjectState());
            
            try 
            {
                Thread.sleep(timeout);
            } 
            catch(InterruptedException ex) 
            {
            }
        }
    }
}
