
package pcap;

import app.ui.PcapUI;
import java.util.ArrayList;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

public class Sniffer extends Thread
{
    protected static final int TIMEOUT = 1000;
    protected static final int CAPTURE_LENGTH = 1024 * 64;
    
    private boolean initialized;
    private ArrayList<PcapIf> devices;
    private StringBuilder errorBuffer;
    private PacketHandler handler;
    private Pcap pcap;
    
    public Sniffer()
    {
        devices = new ArrayList<>();
        errorBuffer = new StringBuilder();
        
        if(Pcap.findAllDevs(devices, errorBuffer) != Pcap.OK)
        {
            PcapUI.writeError("Cannot find network devices ready for packet capture.");
        }
        else
        {
            PcapUI.writePrompt("Available Network Devices\n");
            
            for(int i = 0; i < devices.size(); i++)
            {    
                PcapIf device = devices.get(i);
                PcapUI.writePrompt(i+": "+device.getName()+": "+device.getDescription());
            }
            
            PcapUI.writePrompt("\nEnter /open <device index> to initialize device for packet capture.");
            
            initialized = true;
        }
    }
    
    public boolean open(int devIndex)
    {
        if(initialized)
        {
            pcap = Pcap.openLive(devices.get(devIndex).getName(), CAPTURE_LENGTH, Pcap.MODE_NON_PROMISCUOUS, TIMEOUT, errorBuffer);
            
            if(pcap == null)
            {
                PcapUI.writeError("Failed to open device for packet capture.");
                return false;
            }
            
            return true;
        }
        else
            PcapUI.writeError("Device is not ready for packet capture.");
        
        return false;
    }
    
    public ArrayList<PcapIf> getDevices()
    {
        return devices;
    }
    
    public void close()
    {
        pcap.breakloop();
        pcap.close();
    }
    
    @Override
    public void run()
    {
        handler = new PacketHandler();
        pcap.loop(Pcap.LOOP_INFINITE, handler, System.out);
    }
}
