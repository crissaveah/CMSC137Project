
package app;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.ServerSocket;
import pcap.Sniffer;

public class PacketSniffer 
{
    private static final int APP_LOCK = 6983;
    
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
                        Sniffer sniffer = new Sniffer();
                        ServerSocket appLock = new ServerSocket(APP_LOCK);
                    }
                    catch(IOException ex) 
                    {
                        System.err.println("Only one instance of this application is allowed to run at a time.");
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            }
        );
    }
}
