package net.kylemc.kadmin;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public final class HorseTeleport implements Listener
{
 @org.bukkit.event.EventHandler(ignoreCancelled=true)
 public void onHorseTP(PlayerCommandPreprocessEvent event)
 {
   String input = event.getMessage();
   if ((input.startsWith("/teleport")) || (input.startsWith("/tp")) || (input.startsWith("/warp")) || (input.startsWith("/home")) || (input.startsWith("/spawn")))
   {
     Player p = event.getPlayer();
     if ((p.isInsideVehicle()) && (p.getVehicle().getType() == EntityType.HORSE))
     {
       Horse h = (Horse)p.getVehicle();
       if (h.isTamed())
       {
         p.leaveVehicle();
         String message = event.getMessage().substring(1);
         org.bukkit.World w = p.getWorld();
         p.performCommand(message);
         if (w == p.getWorld()) {
           h.teleport(p);
         }
         event.setCancelled(true);
         return;
       }
     }
   }
 }
}