package ethervisu.monitors;

public class PcapData
{
  private String _sourceLabel;
  private String _targetName;
  private String _bssid;
  private String _ssid;
  
  public void setSourceLabel(String sourceLabel)
  {
    _sourceLabel = sourceLabel.toUpperCase();
  }
  
  public void setTargetLabel(String targetName)
  {
    _targetName = targetName.toUpperCase();
  }
  
  public void setBSSID(String bssid)
  {
    _bssid = bssid;
  }
  
  public void setSSID(String ssid)
  {
    _ssid = ssid;
  }
  
  public String getSourceLabel()
  {
    return _sourceLabel;
  }
  
  public String getTargetLabel()
  {
    return _targetName;
  }
  
  public String getBSSID()
  {
    return _bssid;
  }
  
  public String getSSID()
  {
    return _ssid;
  }
  
}
