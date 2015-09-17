package net.kylemc.kadmin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PVP implements org.bukkit.event.Listener
{
  @EventHandler(ignoreCancelled=true)
  private void onSnowballHit(EntityDamageByEntityEvent event)
  {
    if (event.getDamager() instanceof org.bukkit.entity.Snowball) {
      final Player p = (Player)event.getEntity();
      p.setHealth(Math.min(0, p.getHealth() - 1.0D));
    }
  }

  @EventHandler(ignoreCancelled=true)
  private void onEat(PlayerItemConsumeEvent event) {
    final Material mat = event.getItem().getType();
    if ((mat.equals(Material.ROTTEN_FLESH)) || (mat.equals(Material.SPIDER_EYE)) || (mat.equals(Material.POTION))) {
      return;
    }
    final double min = Math.min(20.0D, event.getPlayer().getHealth() + 1.0D);
    event.getPlayer().setHealth(min);
  }

  @EventHandler(ignoreCancelled=true)
  private void preventTele(PlayerTeleportEvent event)
  {
    if ((event.getTo().getWorld().getName().equals("PVP")) && (event.getCause().equals(PlayerTeleportEvent.TeleportCause.COMMAND)) && (!event.getPlayer().isOp())) {
      event.setCancelled(true);
      return;
    }
  }
}