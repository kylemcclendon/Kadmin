package net.kylemc.kadmin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public final class Rainbow implements Listener, CommandExecutor
{
  List<Byte> colors = Arrays.asList(new Byte[] { Byte.valueOf((byte) 14), Byte.valueOf((byte) 6), Byte.valueOf((byte) 2), Byte.valueOf((byte) 10), Byte.valueOf((byte) 3), Byte.valueOf((byte) 9), Byte.valueOf((byte) 5), Byte.valueOf((byte) 4), Byte.valueOf((byte) 1) });
  HashMap<String, Stack<RainbowObject>> ps = new HashMap();
  
  @EventHandler(ignoreCancelled=true)
  public void brush(PlayerInteractEvent e)
  {
    Player player = e.getPlayer();
    ItemStack item = player.getItemInHand();
    
    if (item == null) {
      return;
    }
    if ((e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getType().equals(Material.DIAMOND_HOE)) && (player.isOp())) {
      int radius = 0;
      boolean changeGrass = false;
      boolean randomColors = false;
      
      String itemName = null;
      
      if (item.getItemMeta() != null)
      {
        itemName = item.getItemMeta().getDisplayName();
      }
      
      if (itemName == null)
      {
        return;
      }
      
      if (itemName.contains("tree"))
      {
        Block block = e.getClickedBlock();
        Location loc = block.getLocation();
        loc = loc.add(0.0D, 1.0D, 0.0D);
        String biome = block.getBiome().toString();
        TreeType tree = null;
        int tree_rand = (int)(10.0D * Math.random());
        
        if (biome.contains("SWAMP")) {
          tree = TreeType.SWAMP;
        }
        else if (biome.contains("MEGA")) {
          if (tree_rand < 2) {
            tree = TreeType.REDWOOD;
          } else if (tree_rand < 4) {
            tree = TreeType.TALL_REDWOOD;
          } else {
            tree = TreeType.MEGA_REDWOOD;
          }
        }
        else if ((biome.contains("TAIGA")) || (biome.contains("FROZEN")) || (biome.contains("ICE"))) {
          if (tree_rand < 4) {
            tree = TreeType.TALL_REDWOOD;
          } else {
            tree = TreeType.REDWOOD;
          }
        }
        else if (biome.contains("BIRCH")) {
          if (tree_rand < 3) {
            tree = TreeType.TALL_BIRCH;
          } else {
            tree = TreeType.BIRCH;
          }
        }
        else if (biome.contains("JUNGLE")) {
          if (tree_rand == 4) {
            tree = TreeType.JUNGLE_BUSH;
          } else if (tree_rand == 5) {
            tree = TreeType.COCOA_TREE;
          } else if (tree_rand < 5) {
            tree = TreeType.JUNGLE;
          } else {
            tree = TreeType.SMALL_JUNGLE;
          }
        }
        else if (biome.contains("MUSHROOM")) {
          if (tree_rand < 5) {
            tree = TreeType.RED_MUSHROOM;
          } else {
            tree = TreeType.BROWN_MUSHROOM;
          }
        } else {
          if (biome.contains("DESERT")) {
            if (tree_rand < 3) {
              loc.getBlock().setType(Material.DEAD_BUSH);
            } else {
              loc.getBlock().setType(Material.CACTUS);
            }
            return;
          }
          if (biome.contains("ROOFED")) {
            if (tree_rand == 5) {
              tree = TreeType.RED_MUSHROOM;
            } else if (tree_rand == 4) {
              tree = TreeType.BROWN_MUSHROOM;
            } else {
              tree = TreeType.DARK_OAK;
            }
          }
          else if ((biome.contains("SAVANNA")) || (biome.contains("MESA")) || (biome.contains("PLAINS"))) {
            tree = TreeType.ACACIA;
          }
          else if (biome.contains("FLOWER")) {
            if (tree_rand < 5) {
              tree = TreeType.BIRCH;
            } else {
              tree = TreeType.TREE;
            }
            
          }
          else if (tree_rand == 5) {
            tree = TreeType.BIG_TREE;
          } else if (tree_rand < 2) {
            tree = TreeType.BIRCH;
          } else {
            tree = TreeType.TREE;
          }
        }
        
        if (tree == null) {
          e.getPlayer().sendMessage(ChatColor.RED + "No valid tree type.");
          return;
        }
        

        loc.getWorld().generateTree(loc, tree);
        e.setCancelled(true);
        return;
      }
      
      if (itemName.contains("grass")) {
        changeGrass = true;
      }
      if (itemName.contains("random")) {
        randomColors = true;
      }
      
      int spacepos = itemName.indexOf(" ");
      if (spacepos == -1) {
        if (itemName.matches("[0-9]+"))
        {
          radius = Integer.parseInt(itemName);
          if (radius > 30)
          {
            radius = 30;
          }
        }
        else {
          e.getPlayer().sendMessage(ChatColor.RED + "Name: <radius> [grass] [random]");
        }
        
      }
      else if (itemName.substring(0, spacepos).matches("[0-9]+")) {
        radius = Integer.parseInt(itemName.substring(0, spacepos));
        if (radius > 30)
        {
          radius = 30;
        }
      }
      else {
        e.getPlayer().sendMessage(ChatColor.RED + "Name: <radius> [grass] [random]");
        return;
      }
      

      Block block = e.getClickedBlock();
      Location loc = block.getLocation();
      
      double x = loc.getX();
      double y = loc.getY();
      double z = loc.getZ();
      World w = loc.getWorld();
      
      HashMap<Location, Material> hs = new HashMap();
      
      for (int i = -2 * radius; i < 2 * radius + 1; i++)
      {
        for (int k = -2 * radius; k < 2 * radius + 1; k++)
        {
          for (int j = -2 * radius; j < 2 * radius + 1; j++)
          {
            Location temp = new Location(w, x + i, y + j, z + k);
            block = temp.getBlock();
            Material m = block.getType();
            
            if ((m.equals(Material.DIRT)) || (m.equals(Material.STONE)) || (m.equals(Material.WOOL)) || ((changeGrass) && (m.equals(Material.GRASS))))
            {
              boolean visible = false;
              if ((block.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) || 
                (block.getRelative(BlockFace.UP).getType().equals(Material.AIR)) || 
                (block.getRelative(BlockFace.EAST).getType().equals(Material.AIR)) || 
                (block.getRelative(BlockFace.WEST).getType().equals(Material.AIR)) || 
                (block.getRelative(BlockFace.NORTH).getType().equals(Material.AIR)) || 
                (block.getRelative(BlockFace.SOUTH).getType().equals(Material.AIR))) {
                visible = true;
              }
              
              if (visible) {
                Location t = block.getLocation();
                hs.put(t, block.getType());
                
                block.setType(Material.WOOL);
                
                if (!randomColors)
                {
                  block.setData(((Byte)this.colors.get(Math.abs(block.getX() + block.getY() + block.getZ()) % 9)).byteValue());
                }
                else
                {
                  block.setData(((Byte)this.colors.get((int)(Math.random() * (this.colors.size() - 1)))).byteValue());
                }
              }
            }
          }
        }
      }
      
      RainbowObject rO = new RainbowObject(e.getPlayer().getWorld(), hs);
      if (this.ps.get(e.getPlayer().getName().toString()) == null) {
        Stack<RainbowObject> newStack = new Stack();
        this.ps.put(e.getPlayer().getName().toString(), newStack);
      }
      Stack<RainbowObject> s = (Stack)this.ps.get(e.getPlayer().getName().toString());
      s.push(rO);
    }
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if ((sender instanceof Player)) {
      if ((sender.isOp()) && (cmd.getName().equalsIgnoreCase("rename"))) {
        if (args.length < 1) {
          sender.sendMessage(ChatColor.RED + "Usage: /rename <name>");
          return true;
        }
        
        if (((Player)sender).getItemInHand() == null) {
          sender.sendMessage(ChatColor.RED + "Must have item in hand to rename");
          return true;
        }
        
        ItemStack item = ((Player)sender).getItemInHand();
        String name = "";
        String[] arrayOfString; int j = (arrayOfString = args).length; for (int i = 0; i < j; i++) { String x = arrayOfString[i];
          name = name + x;
        }
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        item.setItemMeta(im);
        return true;
      }
      if ((sender.isOp()) && (cmd.getName().equalsIgnoreCase("hoe_undo"))) {
        if (!this.ps.containsKey(sender.getName().toString())) {
          sender.sendMessage(ChatColor.RED + "Nothing to undo");
          return true;
        }
        
        Stack<RainbowObject> rStack = (Stack)this.ps.get(sender.getName().toString());
        if (!rStack.isEmpty()) {
          RainbowObject stored = (RainbowObject)rStack.pop();
          stored.undoRainbow();
          sender.sendMessage(ChatColor.GOLD + "Rainbow action undone");
          return true;
        }
        sender.sendMessage(ChatColor.RED + "Nothing to undo");
        return true;
      }
      if ((sender.isOp()) && (cmd.getName().equalsIgnoreCase("hoe_help"))) {
        sender.sendMessage(ChatColor.RED + "Name Syntax: <# or tree> [random] [grass]\n/hoe_undo: Undo a rainbow action.\n /hoe <name>: name a hoe you are holding");
        return true;
      }
      if ((sender.isOp()) && (cmd.getName().equalsIgnoreCase("hoe"))) {
        Player p = (Player)sender;
        PlayerInventory inv = p.getInventory();
        int index = 0;
        
        for (index = 0; index < 36; index++) {
          if (inv.getItem(index) == null) {
            break;
          }
        }
        
        if (index > 35) {
          sender.sendMessage(ChatColor.RED + "You do not have space for this");
          return true;
        }
        
        ItemStack item = new ItemStack(Material.DIAMOND_HOE, 1);
        

        String name = "";
        for (int i = 0; i < args.length; i++) {
          name = name + args[i] + " ";
        }
        
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        item.setItemMeta(im);
        p.getInventory().setItem(index, item);
        p.sendMessage(ChatColor.GOLD + "Hoe named to: " + name);
        return true;
      }
      return true;
    }
    
    sender.sendMessage(ChatColor.RED + "Command can only be used by players!");
    return true;
  }
}