package net.kylemc.kadmin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Restart implements org.bukkit.command.CommandExecutor
{
  java.util.logging.Logger log = java.util.logging.Logger.getLogger("Minecraft");
  
  static Plugin plugin;
  private int remaining = 60;
  
  public Restart(Plugin plugin) {
    Restart.plugin = plugin;
  }
  


  @SuppressWarnings("deprecation")
protected final void initRestarts()
  {
    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new BukkitRunnable()
    {
      public void run()
      {
        Bukkit.getServer().broadcastMessage(ChatColor.RED + "Server Restarting in " + Restart.this.remaining + " minutes");
        Restart.this.remaining -= 10;
      }
    }, 792000L, 12000L);
    

    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new BukkitRunnable()
    {
      public void run() {
        Bukkit.getServer().broadcastMessage(ChatColor.RED + "Server Restarting in 5 minutes");
      }
    }, 858000L);
    

    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new BukkitRunnable()
    {
      public void run() {
        Bukkit.getServer().broadcastMessage(ChatColor.RED + "Server Restarting in 1 minute");
      }
    }, 862800L);
    

    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new BukkitRunnable()
    {
      public void run()
      {
        for (Player p1 : Bukkit.getServer().getOnlinePlayers()) {
          p1.kickPlayer("Server Is Restarting");
        }
        Bukkit.getServer().shutdown();
      }
    }, 864000L);
    
    System.out.println("Auto-Restart schedulers prepared.");
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if (cmd.getName().equalsIgnoreCase("restart")) {
      if (((sender instanceof Player)) && (!sender.hasPermission("kadmin.restart"))) {
        sender.sendMessage(ChatColor.RED + "You do not have permission to do this!");
        return true;
      }
      
      this.log.info("Restarting...");
      for (Player p1 : sender.getServer().getOnlinePlayers()) {
        p1.kickPlayer("Server Is Restarting");
      }
      sender.getServer().shutdown();
      return true;
    }
    
    if (cmd.getName().equalsIgnoreCase("ls")) {
      String players = "";
      int numPlayers = 0;
      
      for (Player p : Bukkit.getOnlinePlayers()) {
        numPlayers++;
        players = players + p.getName() + ", ";
      }
      if (players.length() > 0) {
        players = players.substring(0, players.length() - 2);
      }
      String online = String.format("Online Players (%d/%d): %s", new Object[] { Integer.valueOf(numPlayers), Integer.valueOf(Bukkit.getMaxPlayers()), players });
      sender.sendMessage(online);
      return true;
    }
    if (cmd.getName().equalsIgnoreCase("quartz")) {
      if ((sender instanceof Player)) {
        Player p = (Player)sender;
        if (p.getItemInHand() == null) {
          p.sendMessage(ChatColor.RED + "You must be holding NetherRack to use this command");
        }
        else {
          boolean hasroom = false;
          if ((p.getItemInHand().getType().equals(Material.NETHERRACK)) && (p.getInventory().getItemInHand().getAmount() > 9)) { ItemStack[] arrayOfItemStack;
            int j = (arrayOfItemStack = p.getInventory().getContents()).length; for (int i = 0; i < j; i++) { ItemStack item = arrayOfItemStack[i];
              if ((item == null) || ((item.getType().equals(Material.QUARTZ)) && (item.getAmount() < 64))) {
                hasroom = true;
                break;
              }
            }
            
            if (hasroom) {
              ItemStack i = new ItemStack(Material.QUARTZ, 1);
              ItemStack h = p.getItemInHand();
              p.getInventory().addItem(new ItemStack[] { i });
              h.setAmount(h.getAmount() - 10);
              
              if (h.getAmount() == 0) {
                p.setItemInHand(null);
              }
              else {
                p.setItemInHand(h);
              }
              p.sendMessage(ChatColor.GOLD + "10 NetherRack removed, 1 Quartz added");
            }
            else {
              p.sendMessage(ChatColor.RED + "You don't have room for any quartz!");
            }
          }
          else {
            p.sendMessage(ChatColor.RED + "You need to hold at least 10 NetherRack to use this command");
          }
        }
      }
      else {
        sender.sendMessage(ChatColor.RED + "Command can only be used by players");
      }
      return true;
    }
    return false;
  }
}