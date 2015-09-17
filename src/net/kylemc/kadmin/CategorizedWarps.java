package net.kylemc.kadmin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

public class CategorizedWarps implements Listener, CommandExecutor
{
  static Plugin plugin;
  public static File newWarps;
  public static HashMap<String, String[]> warps;
  public static HashMap<String, String> categories;
  String cats = ChatColor.GOLD + "Categories: worlds, hyrule, bigtowns, smalltowns, bigbuilds, utilities, old";
  String use = ChatColor.RED + "Usage: /cwarps [category] [page number]";
  public String[] Old = { "Old", "Old's Spawn", "Rapture", "Old's Waypoint Hub", "Cilly", "Great City of Old", "BMB", "Beyond the Mysterious Beyond", "RenekTown", "Melpy's Old Town", "RavineTown", "DD622's Old Town", "Arena", "PVP Arena", "CTF", "Capture the Flag", "Zombie", "Nazi Zombies", "Exziron", "Capture the Flag" };
  

  public String[] Hyrule = { "hyrule", "Hyrule hub", "forest", "Sacred Forest Meadow", "water", "Lake Hylia", "shadow", "Graveyard", "light", "Temple of Time" };
  public String[] BigTowns = { "PortSerenity/ps/Metro", "quicksilver20, DD622, SnowLeopard, Paradoxagent", "SkyCity", "Sundav", "Terra", "idmb", "Tulloch", "ERROR372", "Fuzz", "DD622", "Misteria", "ERROR372", "Zion", "Exziron", "HolOfSol", "Solsetur" };
  public String[] SmallTowns = { "Tenuto", "ERROR372 + DD622", "HolidayTown", "stella96", "Otok", "Solsetur", "Riverside", "SnowLeopard448", "TwoRivers", "Solsetur", "NewRussia", "Electricut, meeko305, aesusure", "Jehlan", "Myuufasa", "Cabins", "ERROR372", "Baragh", "Sundav", "Mooncrest", "ace714", "Bubble", "DD622", "IceHut", "oilsands5" };
  public String[] BigBuilds = { "Lotus", "DD622", "Travencal", "quicksilver20", "AstaShip", "Lioness", "Tanith", "quicksilver20 + DD622", "UFO", "DD622", "Winterland", "DD622", "mirkwood", "Sundav", "Bloodlust", "oilsands5", "LinkOverLook", "DD622", "zelda", "DD622", "Winterfell", "Sundav", "Ship", "DD622", "Tardis", "quicksilver20", "DDBook", "DD622", "Windfish", "DD622", "Ark", "Community Effort", "DPRanch", "DonPretzel", "Donut", "DD622", "Kanto", "fatrat92", "iTower", "unknown", "Itsumoniyoru", "unknown", "Andy", "ICU27", "Air", "Exziron", "Dragon", "DD622", "Maze", "DD622", "ddpark", "DD622" };
  public String[] Utilities = { "Tanith", "Main Spawn/Info", "TWC", "Tanith Warp Center", "cube", "Sheep Farm", "moo", "Sheep Farm", "Idaho", "Villager Farm", "sheep", "idmb's sheep farm" };
  public String[] Worlds = { "Tanith", "PVE Survival World", "Hyrule", "Ocarina of Time Recreation", "Temp", "Temporary World", "PVP", "PVP-based World", "Old", "Creative Alpha World" };
  
  public CategorizedWarps(Plugin plugin) {
    CategorizedWarps.plugin = plugin;
    initCategories();
  }
  
  private void initCategories() {
    warps = new HashMap<String, String[]>();
    warps.put("hyrule", this.Hyrule);
    
    warps.put("bigtowns", this.BigTowns);
    warps.put("smalltowns", this.SmallTowns);
    warps.put("bigbuilds", this.BigBuilds);
    warps.put("utilities", this.Utilities);
    
    warps.put("old", this.Old);
    warps.put("worlds", this.Worlds);
    
    categories = new HashMap<String, String>();
    categories.put("hyrule", "Hyrule");
    
    categories.put("bigtowns", "BigTowns");
    categories.put("smalltowns", "SmallTowns");
    categories.put("bigbuilds", "BigBuilds");
    categories.put("utilities", "Utilities");
    
    categories.put("old", "Old");
    categories.put("worlds", "Worlds");
    
    newWarps = new File(plugin.getDataFolder(), "newWarps.yml");
  }
  

  @SuppressWarnings("deprecation")
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if (cmd.getName().equalsIgnoreCase("cwarps")) {
      if (args.length < 1) {
        sender.sendMessage(this.cats);
        sender.sendMessage(this.use);
        return true;
      }
      
      if (args.length == 1) {
        String cat = args[0].toLowerCase();
        
        if (warps.containsKey(cat)) {
          String[] h = (String[])warps.get(cat);
          String category = (String)categories.get(cat);
          sender.sendMessage(ChatColor.GOLD + "---------------" + category + " Pages: " + (int)Math.ceil(h.length / 10.0D) + "---------------");
          sender.sendMessage(ChatColor.GOLD + "----------Format: Warp - Builder(s)/Description----------");
          for (int i = 1; i < 10; i += 2) {
            if (i >= h.length) {
              return true;
            }
            sender.sendMessage(ChatColor.AQUA + h[(i - 1)] + " - " + h[i]);
          }
        }
        else {
          sender.sendMessage(ChatColor.RED + "Invalid Category");
        }
      }
      else if (args.length == 2)
      {
        if ((args[0].equalsIgnoreCase("info")) && (sender.getName().equals("@")))
        {
          String x = args[1];
          Player p = org.bukkit.Bukkit.getServer().getPlayer(x);
          onCommand(p, cmd, "", new String[0]);
          return true;
        }
        if (args[1].matches("[0-9]+")) {
          try {
            int num = Integer.parseInt(args[1]);
            
            int pnum = (num - 1) * 10;
            String cat = args[0].toLowerCase();
            
            if (warps.containsKey(cat)) {
              String[] h = (String[])warps.get(cat);
              String category = (String)categories.get(cat);
              sender.sendMessage(ChatColor.GOLD + "---------------" + category + " Page: " + num + "/" + (int)Math.ceil(h.length / 10.0D) + "---------------");
              sender.sendMessage(ChatColor.GOLD + "----------Format: Warp - Builder(s)/Description----------");
              for (int i = 0; i < 10; i += 2) {
                if (pnum + i >= h.length) {
                  return true;
                }
                sender.sendMessage(ChatColor.AQUA + h[(pnum + i)] + " - " + h[(pnum + (i + 1))]);
              }
            }
            else {
              sender.sendMessage(ChatColor.RED + "Invalid Category");
            }
          }
          catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Number is too large!");
            return true;
          }
        }
        
        sender.sendMessage(ChatColor.RED + "Invalid Number");
      }
      else
      {
        sender.sendMessage(ChatColor.RED + "Usage: /cwarps [category] [page number]");
      }
      return true;
    }
    
    return false;
  }
  
  @EventHandler
  public final void setWarpListen(PlayerCommandPreprocessEvent event) {
    String msg = event.getMessage();
    String[] parts = msg.split(" ");
    if ((!event.getPlayer().isOp()) && (parts.length > 1) && (parts[0].equalsIgnoreCase("/warps")) && (parts[1].equalsIgnoreCase("list"))) {
      event.getPlayer().sendMessage(ChatColor.RED + "Please use '/cwarps' to access the warps list");
      event.setCancelled(true);
    }
    else if ((event.getPlayer().isOp()) && (parts.length == 2) && (parts[0].equalsIgnoreCase("/setwarp"))) {
      String warp = parts[1];
      try
      {
        FileWriter fStream = new FileWriter(newWarps, true);
        fStream.append(warp);
        fStream.append(System.getProperty("line.separator"));
        fStream.flush();
        fStream.close();
      }
      catch (IOException ex) {
        System.out.println("Could not write to text file!");
      }
    }
  }
}