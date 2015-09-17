package net.kylemc.kadmin;

import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

public final class Maintenance implements org.bukkit.event.Listener
{
  Logger log = Logger.getLogger("Minecraft");
  private final Kadmin plugin;
  
  public Maintenance(Kadmin instance) {
    this.plugin = instance;
    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
  }
  
  @org.bukkit.event.EventHandler(priority=EventPriority.LOWEST)
  public void login(PlayerLoginEvent event) {
    Player p = event.getPlayer();
    if ((org.bukkit.Bukkit.hasWhitelist()) && (!p.isOp())) {
      event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server Under Maintenance.\n Watch from direct.kylemc.net/map");
    }
  }
}