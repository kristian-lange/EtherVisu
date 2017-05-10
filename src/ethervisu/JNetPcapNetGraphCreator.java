package ethervisu;


import jgv.nodes.JGVNode;
import jgv.physics.Coordinates;

import org.jnetpcap.protocol.lan.Ethernet;


public class JNetPcapNetGraphCreator extends NetGraphCreator
{

  @Override
  public void nextPacket(Object obj)
  {
    if (obj.getClass().equals(Ethernet.class))
    {
      Ethernet ethernet = (Ethernet) obj;
      byte[] sourceMAC = ethernet.source();
      byte[] targetMAC = ethernet.destination();
      String sourceLabel = macToString(sourceMAC);
      String targetLabel = macToString(targetMAC);
      JGVNode sourceNode = getNode(sourceLabel.hashCode());
      JGVNode targetNode = getNode(targetLabel.hashCode());
      addOrBounce(sourceMAC, targetMAC, sourceLabel, targetLabel,
          sourceNode, targetNode);
    }
  }
  
  private String macToString(byte[] mac)
  {
    StringBuilder buf = new StringBuilder();
    for (byte b : mac)
    {
      if (buf.length() > 0)
      {
        buf.append(':');
      }
      if (b >= 0 && b < 16)
      {
        buf.append('0');
      }
      buf.append(Integer.toHexString((b < 0) ? b + 256 : b).toUpperCase());
    }
    return buf.toString();
  }
  
  @Override
  protected Coordinates getNodeCoords(byte[] mac)
  {
    return getCoordinatesFromMAC(mac);
  }
  
  private static Coordinates getCoordinatesFromMAC(byte[] mac)
  {
    float x = (float) (((int) (mac[1] & 0xFF) + (int) (mac[2] & 0xFF) + (int) (mac[3] & 0xFF))) / 765; 
    float y = (float) (((int) (mac[3] & 0xFF) + (int) (mac[4] & 0xFF) + (int) (mac[5] & 0xFF))) / 765;
    return new Coordinates(x, y);
  }

}
