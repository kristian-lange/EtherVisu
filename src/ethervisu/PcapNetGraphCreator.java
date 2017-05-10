package ethervisu;

import java.math.BigInteger;

import ethervisu.monitors.PcapData;
import jgv.JGVConstants;
import jgv.nodes.JGVNode;
import jgv.physics.Coordinates;

public class PcapNetGraphCreator extends NetGraphCreator
{
  
  @Override
  public void nextPacket(Object obj)
  {
    if (obj.getClass().equals(PcapData.class))
    {
      PcapData ssidObj = (PcapData) obj;
      String sourceLabel = ssidObj.getSourceLabel();
      String targetLabel = ssidObj.getTargetLabel();
      byte[] sourceMAC = parseMACAddress(sourceLabel);
      byte[] targetMAC = parseMACAddress(targetLabel);
      JGVNode sourceNode = getNode(sourceLabel.hashCode());
      JGVNode targetNode = getNode(targetLabel.hashCode());
      addOrBounce(sourceMAC, targetMAC, sourceLabel, targetLabel,
          sourceNode, targetNode);
    }
  }

  private byte[] parseMACAddress(String macAddress)
  {
      String[] bytes = macAddress.split(":");
      byte[] parsed = new byte[bytes.length];

      for (int x = 0; x < bytes.length; x++)
      {
          BigInteger temp = new BigInteger(bytes[x], 16);
          byte[] raw = temp.toByteArray();
          parsed[x] = raw[raw.length - 1];
      }
      return parsed;
  }

  @Override
  protected Coordinates getNodeCoords(byte[] mac)
  {
    return getCoordsFromMACNearCentre(mac);
  }
  
  private static Coordinates getCoordsFromMACNearCentre(byte[] mac)
  {
    // starts somewhere near the centre (centroid)
    float x = getCoordsFromMACByte( mac[1], mac[3], mac[5]) + JGVConstants.CENTER_X;
    float y = getCoordsFromMACByte( mac[1], mac[2], mac[4]) + JGVConstants.CENTER_Y;
    
    return new Coordinates(x, y);
  }
  
  private static float getCoordsFromMACByte(byte a, byte b, byte c)
  {
    // Returns a small value around 0, positive or negative.
    // The value 260 is somehow empirical and reflects the MAC address distribution
    // of the place I'm in right now.
    return ((float) ((int) (a & 0xFF) + (int) (b & 0xFF) + (int) (c & 0xFF) + (int) - 260) / 10000);
  }

}
