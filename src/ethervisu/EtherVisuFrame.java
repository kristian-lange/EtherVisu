package ethervisu;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class EtherVisuFrame extends Frame implements WindowListener
{
  
  private static final long serialVersionUID = 4845009354414996629L;

  public EtherVisuFrame(String title, Applet applet)
  {
    super(title);
    setLayout(new BorderLayout());
    addWindowListener( this );
    add(applet, BorderLayout.CENTER);
    pack();
    setVisible(true);
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
