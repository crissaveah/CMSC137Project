
package pcap;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

public class PacketHandler implements PcapPacketHandler<Object>
{
    Tcp tcp = new Tcp();
    Udp udp = new Udp();
    
    @Override
    public void nextPacket(PcapPacket packet, Object object) 
    {
        if(packet.hasHeader(tcp))
            System.out.println(tcp.getPacket());
        else 
        if(packet.hasHeader(udp))
            System.out.println(udp.getPacket());
    }
}
