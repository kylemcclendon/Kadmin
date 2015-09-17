package net.kylemc.kadmin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class Exploration implements Runnable
{
  boolean OOB = false;

  @Override
  public void run()
  {
    do{
      try
      {
        Thread.sleep(10000L);
      }
      catch (final InterruptedException ex) {
        ex.printStackTrace();
      }

      for(final Player p : Bukkit.getServer().getOnlinePlayers()){
      if (!p.isOp()) {
        final Location location = p.getLocation();
        final double x = location.getX();
        final double z = location.getZ();
        int newX = (int)x;
        int newZ = (int)z;

        if (location.getWorld().getName().equals("Hyrule")) {
          if ((x > 510.0D) || (x < -880.0D) || (z > 990.0D) || (z < -400.0D))
          {
            this.OOB = true;
            if (x > 510.0D) {
              newX = 510;
            }
            else if (x < -880.0D) {
              newX = 64656;
            }
            if (z > 990.0D) {
              newZ = 990;
            }
            else if (z < -400.0D) {
              newZ = 65136;
            }
          }
        }
        else if ((x > 15000.0D) || (x < -15000.0D) || (z > 15000.0D) || (z < -15000.0D))
        {
          this.OOB = true;
          if (x > 15000.0D) {
            newX = 15000;
          }
          else if (x < -15000.0D) {
            newX = 50536;
          }
          if (z < -15000.0D) {
            newZ = 50536;
          }
          else if (z > 15000.0D) {
            newZ = 15000;
          }
        }

        if (this.OOB)
        {
          Entity e = null;
          if (p.isInsideVehicle())
          {
            e = p.getVehicle();
          }
          p.leaveVehicle();

          final World world = p.getWorld();
          final int newY = world.getHighestBlockYAt(newX, newZ);

          p.teleport(new Location(world, newX, newY, newZ, location.getYaw(), location.getPitch()));

          if (e != null)
          {
            e.teleport(p);
          }
          p.sendMessage(org.bukkit.ChatColor.RED + "You've reached the edge of the explorable world!");
          this.OOB = false;
        }
      }
      }
    }while(Kadmin.threadVar);
  }
}