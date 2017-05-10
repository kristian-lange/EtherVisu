package ethervisu;


import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import ethervisu.monitors.JNetPcapMonitor;

public class EtherVisu extends Frame implements WindowListener
{
  
  private static final long serialVersionUID = -8149980331174651850L;

  private static Applet _etherVisuApplet;
  
  public EtherVisu(String title, Applet applet)
  {
    super(title);
    setLayout(new BorderLayout());
    addWindowListener( this );
    add(applet, BorderLayout.CENTER);
  }
  
  public static void main(String[] args)
  {
    JNetPcapNetGraphCreator graphCreator = new JNetPcapNetGraphCreator();
    _etherVisuApplet = graphCreator.getApplet(800, 800);
    EtherVisu etherVisu = new EtherVisu("EtherVisu", _etherVisuApplet);
    _etherVisuApplet.setStub(new MyAppletStub(args));
    _etherVisuApplet.init();
    etherVisu.pack();
    etherVisu.setVisible(true);
    
    JNetPcapMonitor monitor = new JNetPcapMonitor(graphCreator);
    new Thread(monitor, "Monitor").start();
  }
  
  @Override
  public void windowOpened(WindowEvent e)
  {
    // nothing to do
  }
  
  @Override
  public void windowIconified(WindowEvent e)
  {
    // nothing to do
  }
  
  @Override
  public void windowDeiconified(WindowEvent e)
  {
    // nothing to do
  }
  
  @Override
  public void windowDeactivated(WindowEvent e)
  {
    // nothing to do
  }
  
  @Override
  public void windowClosing(WindowEvent e)
  {
    _etherVisuApplet.stop();
    _etherVisuApplet.destroy();
    System.exit(0);
  }
  
  @Override
  public void windowClosed(WindowEvent e)
  {
    // nothing to do
  }
  
  @Override
  public void windowActivated(WindowEvent e)
  {
    // nothing to do
  }
  
  
}

class MyAppletStub implements AppletStub
{
  private Hashtable<String, String> _properties;
  
  /**
   * Creates a new MyAppletStub instance and initializes thei nit parameters
   * from the command line. Arguments are passed in as name=value pairs.
   * Reading the command line arguments can be made more sophisciated
   * depending on your needs, but the basic idea will likely remain the same.
   * Also, this particular implementation doesn't deal very well with invalid
   * name=value pairs.
   * 
   * @param argv
   *          [] Command line arguments passed to Main
   * @param an
   *          Applet instance.
   */
  public MyAppletStub(String argv[])
  {
    _properties = new Hashtable<String, String>();
    for (int i = 0; i < argv.length; i++)
    {
      try
      {
        StringTokenizer parser = new StringTokenizer(argv[i], "=");
        String name = parser.nextToken().toString();
        String value = parser.nextToken("\"").toString();
        value = value.substring(1);
        _properties.put(name, value);
      }
      catch (NoSuchElementException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Calls the applet's resize
   * 
   * @param width
   * @param height
   * @return void
   */
  public void appletResize(int width, int height)
  {
  }
  
  /**
   * Returns the applet's context, which is null in this case. This is an area
   * where more creative programming work can be done to try and provide a
   * context
   * 
   * @return AppletContext Always null
   */
  public AppletContext getAppletContext()
  {
    return null;
  }
  
  /**
   * Returns the CodeBase. If a host parameter isn't provided in the command
   * line arguments, the URL is based on InetAddress.getLocalHost(). The
   * protocol is "file:"
   * 
   * @return URL
   */
  public java.net.URL getCodeBase()
  {
    String host;
    if ((host = getParameter("host")) == null)
    {
      try
      {
        host = InetAddress.getLocalHost().getHostName();
      }
      catch (UnknownHostException e)
      {
        e.printStackTrace();
      }
    }
    
    java.net.URL u = null;
    try
    {
      u = new java.net.URL("file://" + host);
    }
    catch (Exception e)
    {
    }
    return u;
  }
  
  /**
   * Returns getCodeBase
   * 
   * @return URL
   */
  public java.net.URL getDocumentBase()
  {
    return getCodeBase();
  }
  
  /**
   * Returns the corresponding command line value
   * 
   * @return String
   */
  public String getParameter(String p)
  {
    return (String) _properties.get(p);
  }
  
  /**
   * Applet is always true
   * 
   * @return boolean True
   */
  public boolean isActive()
  {
    return true;
  }

}
