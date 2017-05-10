package ethervisu.monitors;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.lan.Ethernet;

import ethervisu.IMonitorListener;

public class JNetPcapMonitor implements Runnable
{
  
  private IMonitorListener _monitorListener;
  
  public JNetPcapMonitor(IMonitorListener monitorListener)
  {
    _monitorListener = monitorListener;
  }
  
  @Override
  public void run()
  {
    StringBuilder errorBuffer = new StringBuilder(); // For any error msgs

    int snaplen = 1024; // Capture only the first 1024 bytes - should be enough to get MACs 
    int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
    int timeout = 10 * 1000; // 10 seconds in millis
    Pcap pcap = Pcap.openLive("wlp3s0", snaplen, flags, timeout, errorBuffer);

    if (pcap == null)
    {
      System.err.printf("Error while opening device for capture: "
          + errorBuffer.toString());
      return;
    }

    PcapPacketHandler<String> jPacketHandler = new PcapPacketHandler<String>()
    {
      public void nextPacket(PcapPacket packet, String user)
      {
        Ethernet ethernet = new Ethernet();
        if (packet.hasHeader(ethernet))
        {
          _monitorListener.nextPacket(ethernet);
        }
      }
    };

    pcap.loop(Pcap.LOOP_INFINATE, jPacketHandler, "jNetPcap rocks!");
    pcap.close();
  }
}
