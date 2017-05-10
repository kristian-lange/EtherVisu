package ethervisu;

import jgv.edges.EdgeStd;
import jgv.edges.JGVEdge;
import jgv.edges.graphics.EGBroadcast;
import jgv.edges.graphics.JGVEdgeGraphics;
import jgv.edges.graphics.EGLineAndBlob;
import jgv.edges.physics.JGVEdgePhysics;
import jgv.edges.physics.EPBasic;
import jgv.edges.physics.EPJelly;
import jgv.graph.JGVGraph;
import jgv.graphics.Color;
import jgv.graphics.JGVGraphics;
import jgv.item.moduls.ModuleColorFillBouncing;
import jgv.item.moduls.ModuleColorStrokeBouncing;
import jgv.item.moduls.ModuleSizeBouncing;
import jgv.item.moduls.ModuleSizeBouncingRate;
import jgv.nodes.NodeBroadcast;
import jgv.nodes.JGVNode;
import jgv.nodes.NodeStd;
import jgv.nodes.graphics.NGCircle;
import jgv.nodes.graphics.JGVNodeGraphics;
import jgv.nodes.physics.NPJelly;
import jgv.nodes.physics.JGVNodePhysics;
import jgv.physics.Coordinates;
import jgv.physics.PhysicsJelly;

public abstract class NetGraphCreator extends JGVGraph implements IMonitorListener
{
  
  public NetGraphCreator()
  {
    super(new JGVGraphics(false), new PhysicsJelly(true), 2000, 1000);
    addBroadcast();
  }

  private void addBroadcast()
  {
    String label = "FF:FF:FF:FF:FF:FF";
    JGVNode broadcastNode = new NodeBroadcast(label.hashCode());
    broadcastNode.setLabel(label);
    addNode(broadcastNode);
  }
  
  public abstract void nextPacket(Object obj);
  
  private void buildAndAddEdge(JGVNode sourceNode, JGVNode targetNode)
  {
    JGVEdgeGraphics jGVEdgeGraphics;
    JGVEdgePhysics jGVEdgePhysics;
    int fadeAlphaStart, fadeAlphaEnd;
    float fadeFactor;
    
    if (targetNode instanceof NodeBroadcast)
    {
      Color color = new Color(50, 50, 50, 150);
      jGVEdgeGraphics = new EGBroadcast(color);
      jGVEdgePhysics = new EPBasic();
      fadeAlphaStart = 150;
      fadeAlphaEnd = 0;
      fadeFactor = 0.8f;
    }
    else
    {
      Color color = new Color(0, 0, 150, 150);
      jGVEdgeGraphics = new EGLineAndBlob(2.0f, color, 5, 0.05f);
      jGVEdgePhysics = new EPJelly();
      fadeAlphaStart = 150;
      fadeAlphaEnd = 50;
      fadeFactor = 0.93f;
    }
    EdgeStd edge = new EdgeStd(sourceNode, targetNode,
        jGVEdgeGraphics, jGVEdgePhysics);
    edge.setMaxIdleTime(20);
    edge.addModul(new ModuleColorStrokeBouncing(fadeAlphaStart, fadeAlphaEnd,
        fadeFactor, true));
    addEdge(edge);
  }
  
  
  protected void addOrBounce(byte[] sourceMAC, byte[] targetMAC,
      String sourceLabel, String targetLabel,
      JGVNode sourceNode, JGVNode targetNode)
  {
    boolean sourceKnown = (sourceNode != null);
    boolean targetKnown = (targetNode != null);
    if (sourceKnown && targetKnown)
    {
      sourceNode.bounce();
      targetNode.bounce();
      JGVEdge[] knownEdges= getDirectedEdges(sourceNode, targetNode);
      if (knownEdges.length == 0)
      {
        buildAndAddEdge(sourceNode, targetNode);
      }
      else
      {
        for (JGVEdge edge : knownEdges)
        {
          edge.bounce();
        }
      }
    }
    else if (sourceKnown && !targetKnown)
    {
      sourceNode.bounce();
      targetNode = buildAndAddNode(targetMAC, targetLabel);
      buildAndAddEdge(sourceNode, targetNode);
    }
    else if (!sourceKnown && targetKnown)
    {
      sourceNode = buildAndAddNode(sourceMAC, sourceLabel);
      targetNode.bounce();
      buildAndAddEdge(sourceNode, targetNode);
    }
    else
    {
      sourceNode = buildAndAddNode(sourceMAC, sourceLabel);
      targetNode = buildAndAddNode(targetMAC, targetLabel);
      buildAndAddEdge(sourceNode, targetNode);
    }
  }
  
  private JGVNode buildAndAddNode(byte[] mac, String label)
  {
    Color fillColor = getNodeColor(mac);
    fillColor.setAlpha(50);
    JGVNodeGraphics jGVNodeGraphics = new NGCircle(fillColor);

    Coordinates coords = getNodeCoords(mac);
    JGVNodePhysics jGVNodePhysics = new NPJelly(coords);
    
    NodeStd jGVNode = new NodeStd(label.hashCode(), jGVNodeGraphics, jGVNodePhysics);
    jGVNode.setMaxIdleTime(100);
    jGVNode.setComment(label);
    jGVNode.addModul(new ModuleColorFillBouncing(150, 75, 0.9f, true));
    jGVNode.addModul(new ModuleSizeBouncingRate());
    jGVNode.addModul(new ModuleSizeBouncing());
    addNode(jGVNode);
    
    return jGVNode;
  }
  
  private Color getNodeColor(byte[] mac)
  {
    return getColorFromMAC(mac);
  }
  
  private static Color getColorFromMAC(byte[] mac)
  {
    int red = ((int) (mac[0] & 0xFF) + (int) (mac[1] & 0xFF)) / 2;
    int green = ((int) (mac[2] & 0xFF) + (int) (mac[3] & 0xFF)) / 2;
    int blue = ((int) (mac[4] & 0xFF) + (int) (mac[5] & 0xFF)) / 2;
    return new Color(red, green, blue);
  }

  abstract protected Coordinates getNodeCoords(byte[] mac);

}