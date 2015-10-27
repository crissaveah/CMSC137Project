
package server;

import app.ui.ServerConsole;

public final class Server implements Runnable
{
    public static final int MAX_CLIENTS = 4;
    public static final int DATA_LENGTH = 128;
    
    protected static final String CMD_EXIT = "exit";
    protected static final String CMD_STOP = "stop";
    protected static final String CMD_RESET = "reset";
    
    private long timeout = 10;
    private boolean exited = true;
    private boolean stopped = true;
    private String command = "";
    private static Server instance = new Server();
    
    private Server(){}
    
    public static Server initialize()
    {
        ServerConsole.initialize();
        return instance;
    }
    
    public static Server getInstance()
    {
        return instance;
    }
    
    public boolean isActive()
    {
        return !exited;
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
    
    public void runCommand(String command)
    {
        this.command = command;
    }
    
    public void exit()
    {
        exited = true;
    }
    
    public void stop()
    {
        ConnectionManager.getInstance().stop();
        stopped = true;
    }
    
    public void reset()
    {
        if(!stopped)
            stop();
        
        new Thread(ConnectionManager.create()).start();
        
        stopped = false;
    }

    @Override
    public void run() 
    {
        reset();

        exited = false;
        
        while(!exited)
        {
            if(command.equals("")) //ignore empty string
                continue;

            switch(command) //run command
            {
                case CMD_EXIT:
                {
                    stop();
                    exit();
                    ServerConsole.dispose();
                    break;
                }
                case CMD_STOP:
                {
                    stop();
                    break;
                }
                case CMD_RESET:
                {
                    reset();
                    break;
                }
                default:
                    ServerConsole.writeError("Unknown command.");
            }
            
            command = "";
            
            try 
            {
                Thread.sleep(timeout);
            } 
            catch(InterruptedException ex) 
            {
                //ignore interrupt
            }
        }
    }
}
