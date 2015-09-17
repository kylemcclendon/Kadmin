package net.kylemc.kadmin;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

final class RainbowObject
{
  private final HashMap<Location, Material> changed;
  private final World w;
  
  public RainbowObject(World world, HashMap<Location, Material> hs)
  {
    this.changed = hs;
    this.w = world;
  }
  
  public void undoRainbow() {
    for (Map.Entry<Location, Material> entry : this.changed.entrySet()) {
      this.w.getBlockAt((Location)entry.getKey()).setType((Material)entry.getValue());
    }
  }
}