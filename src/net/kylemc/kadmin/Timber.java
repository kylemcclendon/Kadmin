package net.kylemc.kadmin;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class Timber implements org.bukkit.event.Listener
{
  @EventHandler(ignoreCancelled=true)
  public void goldToolMining(BlockBreakEvent event)
  {
    Player p = event.getPlayer();
    Block b = event.getBlock();
    ItemStack item = p.getItemInHand();
    
    if (item.getType().equals(Material.GOLD_PICKAXE)) {
      if (b.getType().equals(Material.IRON_ORE)) {
        for (ItemStack i : b.getDrops()) {
          if (i.getType().equals(Material.IRON_ORE)) {
            b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.IRON_INGOT, 1));
          }
          else {
            b.getWorld().dropItemNaturally(b.getLocation(), i);
          }
        }
        afterMine(b, item, p);
        event.setCancelled(true);
        return;
      }
      if (b.getType().equals(Material.GOLD_ORE)) {
        for (ItemStack i : b.getDrops()) {
          if (i.getType().equals(Material.GOLD_ORE)) {
            b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GOLD_INGOT, 1));
          }
          else {
            b.getWorld().dropItemNaturally(b.getLocation(), i);
          }
        }
        afterMine(b, item, p);
        event.setCancelled(true);
        return;
      }
      if (b.getType().equals(Material.STONE)) {
        for (ItemStack i : b.getDrops()) {
          if (i.getType().equals(Material.COBBLESTONE)) {
            b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.STONE, 1));
          }
          else {
            b.getWorld().dropItemNaturally(b.getLocation(), i);
          }
        }
        afterMine(b, item, p);
        event.setCancelled(true);
        return;
      }
    }
    if (item.getType().equals(Material.GOLD_SPADE)) {
      if (b.getType().equals(Material.GRAVEL)) {
        for (ItemStack i : b.getDrops()) {
          if (i.getType().equals(Material.GRAVEL)) {
            b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.FLINT, 1));
          }
          else {
            b.getWorld().dropItemNaturally(b.getLocation(), i);
          }
        }
      }
      afterMine(b, item, p);
      event.setCancelled(true);
      return;
    }
    if ((item.getType().equals(Material.GOLD_AXE)) && ((b.getType().equals(Material.LOG)) || (b.getType().equals(Material.LOG_2)))) {
      List<Block> treeBlocks = new ArrayList<Block>();
      int logs = 0;
      
      if (treeWalk(treeBlocks, b, 2147483646))
      {
        for (Block bl : treeBlocks) {
          if ((bl.getType().equals(Material.LOG)) || (bl.getType().equals(Material.LOG_2))) {
            logs++;
          }
          if (bl != null) {
            bl.breakNaturally();
          }
        }
      }
      
      if (p.getGameMode() != GameMode.CREATIVE) {
        item.setDurability((short)(item.getDurability() + (logs / 20 + 1)));
        if (item.getDurability() >= 33) {
          p.setItemInHand(null);
        }
      }
    }
  }
  
  public final boolean treeWalk(List<Block> blocks, Block base, int max)
  {
    int count = 0;
    int radius = 5;
    boolean imatree = false;
    Queue<Block> myQueue = new java.util.LinkedList<Block>();
    myQueue.add(base);
    
    int x = base.getX();int z = base.getZ();
    while ((!myQueue.isEmpty()) && (count < max))
    {
      count++;
      base = (Block)myQueue.poll();
      blocks.add(base);
      if ((!imatree) && ((base.getType().equals(Material.LEAVES)) || (base.getType().equals(Material.LEAVES_2))))
      {
        imatree = true;
      }
      

      if ((Math.abs(base.getX() - x) <= radius) && (Math.abs(base.getZ() - z) <= radius))
      {

        if ((isTreePiece(base, BlockFace.UP)) && (!blocks.contains(base.getRelative(BlockFace.UP))) && (!myQueue.contains(base.getRelative(BlockFace.UP)))) {
          myQueue.add(base.getRelative(BlockFace.UP));
        }
        if ((Math.abs(base.getX() - x) <= radius) && (Math.abs(base.getZ() - z) <= radius))
        {

          if ((isTreePiece(base, BlockFace.NORTH)) && (!blocks.contains(base.getRelative(BlockFace.NORTH))) && (!myQueue.contains(base.getRelative(BlockFace.NORTH)))) {
            myQueue.add(base.getRelative(BlockFace.NORTH));
          }
          if ((Math.abs(base.getX() - x) <= radius) && (Math.abs(base.getZ() - z) <= radius))
          {

            if ((isTreePiece(base, BlockFace.SOUTH)) && (!blocks.contains(base.getRelative(BlockFace.SOUTH))) && (!myQueue.contains(base.getRelative(BlockFace.SOUTH)))) {
              myQueue.add(base.getRelative(BlockFace.SOUTH));
            }
            if ((Math.abs(base.getX() - x) <= radius) && (Math.abs(base.getZ() - z) <= radius))
            {

              if ((isTreePiece(base, BlockFace.EAST)) && (!blocks.contains(base.getRelative(BlockFace.EAST))) && (!myQueue.contains(base.getRelative(BlockFace.EAST)))) {
                myQueue.add(base.getRelative(BlockFace.EAST));
              }
              if ((Math.abs(base.getX() - x) <= radius) && (Math.abs(base.getZ() - z) <= radius))
              {

                if ((isTreePiece(base, BlockFace.WEST)) && (!blocks.contains(base.getRelative(BlockFace.WEST))) && (!myQueue.contains(base.getRelative(BlockFace.WEST)))) {
                  myQueue.add(base.getRelative(BlockFace.WEST));
                }
                if ((Math.abs(base.getX() - x) <= radius) && (Math.abs(base.getZ() - z) <= radius))
                {

                  if ((isTreePiece(base, BlockFace.DOWN)) && (!blocks.contains(base.getRelative(BlockFace.DOWN))) && (!myQueue.contains(base.getRelative(BlockFace.DOWN))))
                    myQueue.add(base.getRelative(BlockFace.DOWN)); }
              }
            } } } } }
    return imatree;
  }
  
  private final boolean isTreePiece(Block base, BlockFace f) {
    if ((base.getRelative(f) != null) && ((base.getRelative(f).getType().equals(Material.LOG)) || (base.getRelative(f).getType().equals(Material.LOG_2)) || (base.getRelative(f).getType().equals(Material.LEAVES)) || (base.getRelative(f).getType().equals(Material.LEAVES_2)))) {
      return true;
    }
    return false;
  }
  
  @EventHandler(ignoreCancelled=true)
  public void hoeLand(PlayerInteractEvent event) {
    if ((event.getPlayer().getItemInHand() != null) && (event.getPlayer().getItemInHand().getType().equals(Material.GOLD_HOE)) && (event.getClickedBlock() != null) && 
      ((event.getClickedBlock().getType().equals(Material.DIRT)) || (event.getClickedBlock().getType().equals(Material.GRASS)) || (event.getClickedBlock().getType().equals(Material.MYCEL))) && (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
      Location middle = event.getClickedBlock().getLocation();
      
      for (int x = -1; x < 2; x++) {
        for (int z = -1; z < 2; z++) {
          Location newblock = new Location(middle.getWorld(), middle.getX() + x, middle.getY(), middle.getZ() + z);
          if ((newblock.getBlock().getType().equals(Material.DIRT)) || (newblock.getBlock().getType().equals(Material.GRASS)) || (newblock.getBlock().getType().equals(Material.MYCEL))) {
            newblock.getBlock().setType(Material.SOIL);
          }
        }
      }
      middle.getWorld().playSound(middle, Sound.STEP_GRAVEL, 1.0F, 0.7F);
      if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
        event.getItem().setDurability((short)(event.getItem().getDurability() + 1));
        if (event.getItem().getDurability() >= 33) {
          event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
          event.getPlayer().setItemInHand(null);
        }
      }
      event.setCancelled(true);
      return;
    }
  }
  
  private void afterMine(Block block, ItemStack i, Player p)
  {
    block.getDrops().clear();
    block.setType(Material.AIR);
    if (p.getGameMode() != GameMode.CREATIVE) {
      i.setDurability((short)(i.getDurability() + 1));
      if (i.getDurability() >= 33) {
        p.getWorld().playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
        p.setItemInHand(null);
      }
    }
  }
}