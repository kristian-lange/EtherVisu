package ethervisu.monitors;

import java.util.logging.Level;

import ethervisu.PcapNetGraphCreator;

import jgv.Logger;


public class PcapMonitor implements Runnable
{
  
  private PcapNetGraphCreator _graphCreator;

  public native void runMonitor();
  
  public PcapMonitor(PcapNetGraphCreator pcapNetGraphCreator)
  {
    _graphCreator = pcapNetGraphCreator;
  }

  static
  {
    System.loadLibrary("pcap_monitor");
  }
  
  public void nextPacket(String destMAC, String sourceMAC, String bssid, String ssid)
  {
    Logger.getLogger().log(Level.INFO, "Source MAC " + sourceMAC);
    Logger.getLogger().log(Level.INFO, "Destination MAC " + destMAC);
    Logger.getLogger().log(Level.INFO, "BSSID " + bssid);
    Logger.getLogger().log(Level.INFO, "SSID " + ssid);
    
    PcapData data = new PcapData();
    data.setSourceLabel(sourceMAC);
    data.setTargetLabel(destMAC);
    data.setSSID(ssid);
    data.setBSSID(bssid);
    
    _graphCreator.nextPacket(data);
  }

  @Override
  public void run()
  {
    runMonitor();
  }
}
