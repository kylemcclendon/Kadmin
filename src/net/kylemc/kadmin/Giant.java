package net.kylemc.kadmin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftGiant;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Giant implements org.bukkit.event.Listener
{
  private static ArrayList<ItemStack> dropTable = new ArrayList<ItemStack>();
  private static int listSize = 0;
  Plugin plugin;

  public Giant(Kadmin instance) {
    this.plugin = instance;
  }

  protected void initItems() {
	//Diamond Sword (Sharpness 4, Durability 3)
    final ItemStack dsword = new ItemStack(Material.DIAMOND_SWORD, 1);
    ItemMeta im = dsword.getItemMeta();
    im.setDisplayName("Giant Slaying Blade");
    dsword.setItemMeta(im);
    dsword.setItemMeta(im);
    dsword.addEnchantment(Enchantment.DAMAGE_ALL, 4);
    dsword.addEnchantment(Enchantment.DURABILITY, 3);

    //Diamond ChestPlate (Protection 3, Durability 3)
    final ItemStack darmor = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
    im = darmor.getItemMeta();
    im.setDisplayName("Giant's Armor");
    darmor.setItemMeta(im);
    darmor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
    darmor.addEnchantment(Enchantment.DURABILITY, 3);

    //Diamond Boots (Protection 3, Durability 3, "grip")
    final ItemStack dboots = new ItemStack(Material.DIAMOND_BOOTS, 1);
    im = darmor.getItemMeta();
    im.setDisplayName("Spiked Boots");
    final List<String> lore = java.util.Arrays.asList(new String[] { org.bukkit.ChatColor.DARK_PURPLE + "Boots infused with gripping iron spikes" });
    im.setLore(lore);
    dboots.setItemMeta(im);
    darmor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
    dboots.addEnchantment(Enchantment.DURABILITY, 3);

    //Diamonds (4)
    final ItemStack diamond = new ItemStack(Material.DIAMOND, 4);

    //Prismarine Crystals (6)
    final ItemStack crystals = new ItemStack(Material.PRISMARINE_CRYSTALS, 6);

    //Bow (Damage 4, Durability 3)
    final ItemStack bow = new ItemStack(Material.BOW, 1);
    im = darmor.getItemMeta();
    im.setDisplayName("Adamantium Bow");
    bow.setItemMeta(im);
    bow.addEnchantment(Enchantment.ARROW_DAMAGE, 4);
    bow.addEnchantment(Enchantment.DURABILITY, 3);

    //Prismarine Shards (10)
    final ItemStack shards = new ItemStack(Material.PRISMARINE_SHARD, 10);

    //Nether Star (1)
    final ItemStack netherstar = new ItemStack(Material.NETHER_STAR, 1);

    dropTable.add(dsword);
    dropTable.add(shards);
    dropTable.add(diamond);
    dropTable.add(dboots);
    dropTable.add(darmor);
    dropTable.add(crystals);
    dropTable.add(diamond);
    dropTable.add(bow);
    dropTable.add(shards);
    dropTable.add(diamond);
    dropTable.add(netherstar);

    listSize = dropTable.size();
  }

  @EventHandler
  private void playerHurtGiant(EntityDamageByEntityEvent event) {
    final Location l = event.getEntity().getLocation();
    event.getEntity().getWorld().playEffect(l, org.bukkit.Effect.STEP_SOUND, 152);

    if (event.getEntity().getType().equals(EntityType.GIANT) && event.getDamager() instanceof Player) {
      final Random random = new Random(System.currentTimeMillis());
      final int next = random.nextInt(15);

      final int sound = random.nextInt(3);
      if (sound == 2) {
        Bukkit.getWorld(event.getEntity().getWorld().getUID()).playSound(l, Sound.ZOMBIE_HURT, 6.0F, 0.1F);
      }

      if (event.getEntity().isDead()) {
        return;
      }

      giantAttackSelector((CraftGiant)event.getEntity(), event.getDamager());

      if (next == 3) {
        for (int i = 0; i < 3; i++) {
          ((Zombie)event.getEntity().getWorld().spawnEntity(l, EntityType.ZOMBIE)).setBaby(true);
        }
      }
    }
  }

  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH, ignoreCancelled=true)
  private void spawnGiant(CreatureSpawnEvent event) {
    if (event.getEntityType().equals(EntityType.GIANT)) {
      Bukkit.getWorld(event.getEntity().getWorld().getUID()).playSound(event.getEntity().getLocation(), Sound.ENDERDRAGON_GROWL, 6.0F, 0.7F);
      return;
    }
    if ((event.getEntityType().equals(EntityType.ZOMBIE)) && (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) &&
      (event.getEntity().getLocation().getBlock().getLightFromSky() == 15)) {
      final Random r = new Random(System.currentTimeMillis());
      int next;
      if ((event.getLocation().getBlock().getBiome().equals(Biome.EXTREME_HILLS_PLUS_MOUNTAINS)) || (event.getLocation().getBlock().getBiome().equals(Biome.EXTREME_HILLS_MOUNTAINS))) {
        next = r.nextInt(10);
      }
      else {
        next = r.nextInt(50);
      }

      if (next == 5) {
        final Location l = event.getLocation();
        final CraftGiant g = (CraftGiant)event.getEntity().getWorld().spawnEntity(l, EntityType.GIANT);
        g.getHandle().getAttributeInstance(net.minecraft.server.v1_8_R3.GenericAttributes.maxHealth).setValue(200.0D);
        g.setHealth(200.0D);
        Bukkit.getWorld(event.getEntity().getWorld().getUID()).playSound(l, Sound.ENDERDRAGON_GROWL, 6.0F, 1.0F);
        event.getEntity().remove();
      }
    }
  }

  @EventHandler
  private void giantDeath(EntityDeathEvent event)
  {
    if ((event.getEntityType().equals(EntityType.GIANT)) &&
      ((event.getEntity().getKiller() instanceof Player))) {
      final Random r = new Random(System.currentTimeMillis());

      Bukkit.getServer().getWorld(event.getEntity().getWorld().getUID()).playSound(event.getEntity().getLocation(), Sound.BLAZE_DEATH, 6.0F, 0.5F);

      int index = r.nextInt(listSize);

      if (index == listSize - 1) {
        index = r.nextInt(listSize);
      }
      if (index == listSize - 1) {
        index = r.nextInt(listSize);
      }
      Bukkit.getServer().getWorld(event.getEntity().getWorld().getUID()).dropItem(event.getEntity().getLocation(), dropTable.get(index));

      final ItemStack potion = new ItemStack(Material.POTION, 1, (short)8229);
      Bukkit.getServer().getWorld(event.getEntity().getWorld().getUID()).dropItem(event.getEntity().getLocation(), potion);

      event.setDroppedExp(200);
    }
  }

  private void giantAttackSelector(CraftGiant giant, Entity damager)
  {
    final Random rand = new Random(System.currentTimeMillis());
    final int attack = rand.nextInt(5);

    switch (attack)
    {
    case 0:
      break;
    case 1:
      giantLeap(giant.getLocation(), giant);
      break;
    case 2:
      giantRoar(giant.getLocation());
      break;
    case 3:
      giantStomp(giant.getLocation(), giant);
      break;
    case 4:
      giantToss(giant, damager);
    }
  }

  private void giantLeap(Location l, CraftGiant giant)
  {
    giant.setVelocity(new Vector(0, 0, 0));
    l.getWorld().playSound(l, Sound.ENDERDRAGON_WINGS, 6.0F, 0.1F);
    giant.getHandle().move(0.0D, 20.0D, 0.0D);
  }

  private void giantStomp(final Location l, final CraftGiant giant){
	  final List<Player> players = Bukkit.getServer().getWorld(l.getWorld().getUID()).getPlayers();
      final List<Player> close = new ArrayList<Player>();

      for (final Player p : players) {
        if (getDistance(l, p.getLocation()) <= 10.0D) {
          close.add(p);
        }
      }

      l.getWorld().playSound(l, Sound.ENDERDRAGON_GROWL, 6.0F, 1.5F);

      Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
      {
        @Override
		public void run()
        {
          l.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), 0.0F, false, false);
          l.getWorld().createExplosion(l.getX() + 5.0D, l.getY(), l.getZ(), 0.0F, false, false);
          l.getWorld().createExplosion(l.getX() - 5.0D, l.getY(), l.getZ(), 0.0F, false, false);
          l.getWorld().createExplosion(l.getX(), l.getY(), l.getZ() + 5.0D, 0.0F, false, false);
          l.getWorld().createExplosion(l.getX(), l.getY(), l.getZ() - 5.0D, 0.0F, false, false);
          l.getWorld().createExplosion(l.getX() + 5.0D, l.getY(), l.getZ() + 5.0D, 0.0F, false, false);
          l.getWorld().createExplosion(l.getX() - 5.0D, l.getY(), l.getZ() - 5.0D, 0.0F, false, false);
          l.getWorld().createExplosion(l.getX() - 5.0D, l.getY(), l.getZ() + 5.0D, 0.0F, false, false);
          l.getWorld().createExplosion(l.getX() + 5.0D, l.getY(), l.getZ() - 5.0D, 0.0F, false, false);

          for (final Player p : close) {
              p.damage(20.0D, giant);
              p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
              p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
              p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1));
              pushAway(p, 3.0D, giant);
            }
        }
      }, 20L);

      Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
      {
        @Override
		public void run()
        {
          l.getWorld().createExplosion(l.getX() + 10.0D, l.getY(), l.getZ(), 0.0F, false, false);
          l.getWorld().createExplosion(l.getX() - 10.0D, l.getY(), l.getZ(), 0.0F, false, false);
          l.getWorld().createExplosion(l.getX(), l.getY(), l.getZ() + 10.0D, 0.0F, false, false);
          l.getWorld().createExplosion(l.getX(), l.getY(), l.getZ() - 10.0D, 0.0F, false, false);
          l.getWorld().createExplosion(l.getX() + 10.0D, l.getY(), l.getZ() + 10.0D, 0.0F, false, false);
          l.getWorld().createExplosion(l.getX() - 10.0D, l.getY(), l.getZ() - 10.0D, 0.0F, false, false);
          l.getWorld().createExplosion(l.getX() + 10.0D, l.getY(), l.getZ() - 10.0D, 0.0F, false, false);
          l.getWorld().createExplosion(l.getX() - 10.0D, l.getY(), l.getZ() + 10.0D, 0.0F, false, false);
        }
      }, 25L);
  }

  private void giantRoar(Location l) {
    final List<Player> players = Bukkit.getServer().getWorld(l.getWorld().getUID()).getPlayers();

    for (final Player p : players)
      if (getDistance(l, p.getLocation()) <= 20.0D)
      {
        l.getWorld().playSound(l, Sound.ENDERDRAGON_GROWL, 6.0F, 0.7F);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 2));
        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 2));
      }
  }

  private void giantToss(CraftGiant giant, Entity damager) {
    if ((damager instanceof Player)) {
      final Player player = (Player)damager;
      final boolean dist = getDistance(player.getLocation(), giant.getLocation()) < 5.0D;

      if ((player.getInventory().getBoots() != null) && (player.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS) && (player.getInventory().getBoots().getItemMeta() != null) && (player.getInventory().getBoots().getItemMeta().getDisplayName() != null) && (player.getInventory().getBoots().getItemMeta().getDisplayName().equals("Spiked Boots"))) {
        if (dist) {
          player.setVelocity(new Vector(0, 1, 0));
        }
        return;
      }
      if (dist) {
        player.setVelocity(new Vector(0.0D, 1.5D, 0.0D));
      }
    }
  }

  @EventHandler
  private void giantFall(final EntityDamageEvent event)
    throws InterruptedException
  {
    if ((event.getEntityType().equals(EntityType.ARMOR_STAND)) &&
      (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE))) {
      event.setCancelled(true);
    }

    if (event.getEntityType().equals(EntityType.GIANT)) {
      if ((event.getCause().equals(EntityDamageEvent.DamageCause.FIRE)) || (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) || (event.getCause().equals(EntityDamageEvent.DamageCause.POISON))) {
        event.setCancelled(true);
      }
      if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
        event.setDamage(event.getDamage() / 4.0D);
      }
      if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
        event.setDamage(event.getDamage() / 2.0D);
      }
      if(event.getCause().equals(EntityDamageEvent.DamageCause.DROWNING)){
    	  event.setCancelled(true);
      }

      if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
        final Location l = event.getEntity().getLocation();
        final List<Player> players = Bukkit.getServer().getWorld(l.getWorld().getUID()).getPlayers();
        final List<Player> close = new ArrayList<Player>();

        for (final Player p : players) {
          if (getDistance(l, p.getLocation()) <= 20.0D) {
            close.add(p);
          }
        }

        event.getEntity().getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), 0.0F, false, false);


        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
        {
          @Override
		public void run()
          {
            event.getEntity().getWorld().createExplosion(l.getX() + 5.0D, l.getY(), l.getZ(), 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() - 5.0D, l.getY(), l.getZ(), 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX(), l.getY(), l.getZ() + 5.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX(), l.getY(), l.getZ() - 5.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() + 5.0D, l.getY(), l.getZ() + 5.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() - 5.0D, l.getY(), l.getZ() - 5.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() - 5.0D, l.getY(), l.getZ() + 5.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() + 5.0D, l.getY(), l.getZ() - 5.0D, 0.0F, false, false);
          }
        }, 3L);


        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
        {
          @Override
		public void run()
          {
            event.getEntity().getWorld().createExplosion(l.getX() + 10.0D, l.getY(), l.getZ(), 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() - 10.0D, l.getY(), l.getZ(), 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX(), l.getY(), l.getZ() + 10.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX(), l.getY(), l.getZ() - 10.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() + 10.0D, l.getY(), l.getZ() + 10.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() - 10.0D, l.getY(), l.getZ() - 10.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() + 10.0D, l.getY(), l.getZ() - 10.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() - 10.0D, l.getY(), l.getZ() + 10.0D, 0.0F, false, false);
          }
        }, 6L);


        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
        {
          @Override
		public void run()
          {
            event.getEntity().getWorld().createExplosion(l.getX() + 15.0D, l.getY(), l.getZ(), 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() - 15.0D, l.getY(), l.getZ(), 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX(), l.getY(), l.getZ() + 15.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX(), l.getY(), l.getZ() - 15.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() + 10.0D, l.getY(), l.getZ() - 15.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() + 15.0D, l.getY(), l.getZ() - 10.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() + 15.0D, l.getY(), l.getZ() + 10.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() + 10.0D, l.getY(), l.getZ() + 15.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() - 10.0D, l.getY(), l.getZ() - 15.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() - 15.0D, l.getY(), l.getZ() - 10.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() - 10.0D, l.getY(), l.getZ() + 15.0D, 0.0F, false, false);
            event.getEntity().getWorld().createExplosion(l.getX() - 15.0D, l.getY(), l.getZ() + 10.0D, 0.0F, false, false);
          }
        }, 9L);

        for (final Player p : close) {
          p.damage(35.0D, event.getEntity());
          p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
          p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
          p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1));
          pushAway(p, 7.0D, event.getEntity());
        }
        event.setCancelled(true);
      }
    }
  }

  private double getDistance(Location ent1, Location ent2)
  {
    final double x2 = Math.pow(ent2.getX() - ent1.getX(), 2.0D);
    final double y2 = Math.pow(ent2.getY() - ent1.getY(), 2.0D);
    final double z2 = Math.pow(ent2.getZ() - ent1.getZ(), 2.0D);

    return Math.sqrt(x2 + y2 + z2);
  }

  private void pushAway(Player player, double speed, Entity entity)
  {
    if ((player.getInventory().getBoots() != null) && (player.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS) && (player.getInventory().getBoots().getItemMeta() != null) && (player.getInventory().getBoots().getItemMeta().getDisplayName() != null) && (player.getInventory().getBoots().getItemMeta().getDisplayName().equals("Spiked Boots")) &&
      (player.getInventory().getBoots().getItemMeta().getLore() != null) && (player.getInventory().getBoots().getItemMeta().getLore().contains(org.bukkit.ChatColor.DARK_PURPLE + "Boots infused with gripping iron spikes"))) {
      return;
    }

    final Vector unitVector = player.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();

    player.setVelocity(unitVector.multiply(speed));
  }

  @EventHandler
  public void disableFirst(PluginDisableEvent event) {
    final Plugin plug = Bukkit.getPluginManager().getPlugin("Multiverse-Core");
    if (event.getPlugin().equals(plug)) {
      Bukkit.getPluginManager().disablePlugin(this.plugin);
    }
  }
}