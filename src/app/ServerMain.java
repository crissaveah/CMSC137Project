
package app;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.ServerSocket;
import server.Server;

public final class ServerMain
{
    private static final int APP_LOCK = 5209;
    
    public static void main(String args[])
    {
        EventQueue.invokeLater
        (
            new Runnable()
            {
                @Override
                public void run() 
                {
                    try 
                    {
                        ServerSocket appLock = new ServerSocket(APP_LOCK);
                        new Thread(Server.initialize()).start();
                    }
                    catch (IOException ex) 
                    {
                        System.err.println("Only one instance of this application is allowed to run at a time.");
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            }
        );
    }
}
