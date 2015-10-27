
package app;

import app.logic.GameLogic;
import app.ui.ClientUI;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URISyntaxException;

public final class ClientMain 
{
    private static final int APP_LOCK = 6981;
    
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
                        ClientUI.initialize();
                        GameLogic game = new GameLogic();
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
    
    public static File getFile(String path)
    {
        try 
        {
            return new File(ClientMain.class.getResource(path).toURI());
        } 
        catch(URISyntaxException ex)
        {
            ClientUI.writeError("Failed to load sprite: "+path);
        }
        
        return null;
    }
}
