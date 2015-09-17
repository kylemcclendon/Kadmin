package net.kylemc.kadmin;

import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

public final class FeatherClip implements org.bukkit.event.Listener
{
  @EventHandler(ignoreCancelled=true)
  public void ClipWing(PlayerInteractAtEntityEvent event)
  {
    Player player = event.getPlayer();
    if (player.hasPermission("permissions.restrict.nointeract")) {
      event.setCancelled(true);
      return;
    }
    Entity e = event.getRightClicked();
    Location eloc = e.getLocation();
    if ((player.getItemInHand().getType() == Material.SHEARS) && (e.getType() == EntityType.CHICKEN))
    {
      Chicken c = (Chicken)e;
      ItemStack i = new ItemStack(Material.FEATHER, 1);
      eloc.getWorld().dropItemNaturally(eloc, i);
      if (player.getGameMode() != GameMode.CREATIVE)
      {
        player.getItemInHand().setDurability((short)(player.getItemInHand().getDurability() + 1));
        if (player.getItemInHand().getDurability() >= 238)
        {
          player.getWorld().playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
          player.setItemInHand(null);
        }
      }
      if (c.getHealth() > 0.0D)
      {
        c.setHealth(c.getHealth() - 1.0D);
        c.playEffect(EntityEffect.HURT);
        if (c.getAge() >= 0)
        {
          player.getWorld().playSound(eloc, Sound.CHICKEN_HURT, 10.0F, 1.0F);
        }
        else
        {
          player.getWorld().playSound(eloc, Sound.CHICKEN_HURT, 10.0F, 2.0F);
        }
        
        if (c.getHealth() <= 0.0D) {
          c.damage(200.0D);
          eloc.getWorld().dropItemNaturally(eloc, new ItemStack(Material.RAW_CHICKEN, 1));
        }
      }
    }
  }
}